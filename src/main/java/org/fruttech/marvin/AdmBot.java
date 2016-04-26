package org.fruttech.marvin;

import com.samczsun.skype4j.Skype;
import com.samczsun.skype4j.SkypeBuilder;
import com.samczsun.skype4j.Visibility;
import com.samczsun.skype4j.chat.Chat;
import com.samczsun.skype4j.chat.messages.ReceivedMessage;
import com.samczsun.skype4j.events.EventHandler;
import com.samczsun.skype4j.events.Listener;
import com.samczsun.skype4j.events.chat.message.MessageReceivedEvent;
import com.samczsun.skype4j.exceptions.ChatNotFoundException;
import com.samczsun.skype4j.exceptions.ConnectionException;
import com.samczsun.skype4j.exceptions.InvalidCredentialsException;
import com.samczsun.skype4j.exceptions.NotParticipatingException;
import org.eclipse.jetty.server.Server;
import org.fruttech.marvin.http.NotificationHandler;
import org.fruttech.marvin.processors.CommandProcessor;
import org.fruttech.marvin.processors.MessageProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class AdmBot {
    static final String MAIN_BOT_CHAT = "bot_room";
    private static final String CFG_FILE = "pix.bot.dat";

    private static final Logger log = LoggerFactory.getLogger(AdmBot.class);
    private static final long KEEPALIVE_INTERVAL_MS = 10000;
    private static Map<String, String> chatGroups;
    private final List<MessageProcessor> processors = Collections.singletonList(new CommandProcessor());
    private final Server server;
    private final BotParams params;
    private Skype skype;
    private KeepAliveThread keepAliveThread;
    private boolean started;
    private Lock lock = new ReentrantLock();

    AdmBot(BotParams params) {
        this.params = params;
        server = new Server(params.httpPort);
    }

    public Map<String, String> getKnownRooms() {
        return chatGroups;
    }

    void run() {
        lock.lock();

        try {
            //start skype
            skype = new SkypeBuilder(params.username, params.password)
                    .withAllResources()
                    .build();

            try {
                skype.login();
                skype.subscribe();

                //run keepalive thread
                keepAliveThread = new KeepAliveThread();
                keepAliveThread.start();

                skype.setVisibility(Visibility.ONLINE);

                //init debug logging
                skype.getEventDispatcher().registerListener(new Listener() {
                    @EventHandler
                    public void onMessage(MessageReceivedEvent e) {
                        final ReceivedMessage message = e.getMessage();
                        final Chat chat = message.getChat();
                        final MessageContext ctx = new MessageContext(AdmBot.this, chat, message);

                        processors.stream()
                                .filter(processor -> processor.needToProcess(ctx))
                                .forEach(processor -> processor.process(ctx));
                    }
                });

            } catch (ConnectionException | NotParticipatingException | InvalidCredentialsException e) {
                log.error("error starting skype", e);
                System.exit(1);
            }

            //start http server
            try {
                server.setHandler(new NotificationHandler(this));
                server.start();
            } catch (Exception e) {
                log.error("Error starting HTTP server", e);
                System.exit(1);
            }

            log.info("Bot started");
            this.started = true;
        }finally {
            lock.unlock();
        }
    }

    public void sendMessage(String msg) {
        try {
            sendMessage(getChat(MAIN_BOT_CHAT), msg);
        } catch (ChatNotFoundException | ConnectionException e) {
            log.error("Error initializing bot");
        }
    }

    private Chat getChat(String mainBotChat) throws ConnectionException, ChatNotFoundException {
        return skype.getOrLoadChat(chatGroups.get(mainBotChat));
    }

    public boolean sendMessage(String group, String msg) {
        if (chatGroups.containsKey(group)) {
            try {
                sendMessage(getChat(group), msg);
                return true;
            } catch (ChatNotFoundException | ConnectionException e) {
                log.error("Error sending message", e);
                return false;
            }
        }
        return false;
    }

    void sendMessage(Chat chat, String msg) {
        lock.lock();
        try {
            try {
                chat.sendMessage(msg);
            } catch (ConnectionException e) {
                log.warn("Cant send message to chat", e);
            }
        } finally {
            lock.unlock();
        }

    }

    void stop() {
        lock.lock();
        try {
            if (started) {
                log.info("Stopping bot");
                keepAliveThread.interrupt();

                try {
                    skype.setVisibility(Visibility.AWAY);
                    skype.logout();
                } catch (ConnectionException e) {
                    log.error("error logging out from skype", e);
                }

                try {
                    server.stop();
                } catch (Exception e) {
                    log.error("error stopping http server", e);
                }
                started = false;
            }
        } finally {
            lock.unlock();
        }
    }

    public void saveConfig() {
        BotPersistentCfg cfg = new BotPersistentCfg();
        cfg.chatGroups = chatGroups;
        if (!cfg.chatGroups.containsKey(MAIN_BOT_CHAT))
            cfg.chatGroups.put(MAIN_BOT_CHAT, "19:d84e8f5732914bf9a5335444e93496a0@thread.skype");
        cfg.save(CFG_FILE);
    }

    private class KeepAliveThread extends Thread {
        KeepAliveThread() {
            super();
        }

        @Override
        public void run() {
            while (!isInterrupted()) {
                try {
                    skype.setVisibility(Visibility.ONLINE);
                } catch (ConnectionException e) {
                    log.warn("Error sending keep alive", e);
                    restart();
                }
                try {
                    Thread.sleep(KEEPALIVE_INTERVAL_MS);
                } catch (InterruptedException e) {
                    interrupt();
                }
            }
        }
    }

    private void restart() {
        lock.lock();
        try {
            stop();
            run();
        }finally {
            lock.unlock();
        }
    }

    //static ctor
    static {
        final File cfgFile = new File(CFG_FILE);
        BotPersistentCfg cfg = new BotPersistentCfg();
        if (!cfgFile.exists()) {
            cfg.chatGroups.put(MAIN_BOT_CHAT, "19:d84e8f5732914bf9a5335444e93496a0@thread.skype");
            cfg.save(CFG_FILE);
        }
        cfg.load(CFG_FILE);
        chatGroups = cfg.chatGroups;
    }


}


