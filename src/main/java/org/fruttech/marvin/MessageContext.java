package org.fruttech.marvin;

import com.samczsun.skype4j.chat.Chat;
import com.samczsun.skype4j.chat.messages.ReceivedMessage;

public class MessageContext {
    private final AdmBot bot;
    private final Chat chat;
    private final ReceivedMessage message;

    MessageContext(AdmBot bot, Chat chat, ReceivedMessage message) {
        this.bot = bot;
        this.chat = chat;
        this.message = message;
    }

    public AdmBot getBot() {
        return bot;
    }

    public Chat getChat() {
        return chat;
    }

    public ReceivedMessage getMessage() {
        return message;
    }

    public String getMessageText() {
        return message.getContent().toString().trim();
    }

    public void reply(String msg) {
        bot.sendMessage(chat, msg);
    }

    public boolean isBotControlRoom() {
        final String botControlRoomId = bot.getKnownRooms().get(AdmBot.MAIN_BOT_CHAT);
        return getChat().getIdentity().equals(botControlRoomId);
    }
}
