package org.fruttech.marvin.processors;

import org.fruttech.marvin.MessageContext;
import org.fruttech.marvin.processors.commands.RoomAddCommand;
import org.fruttech.marvin.processors.commands.RoomListCommand;
import org.fruttech.marvin.processors.commands.RoomRemoveCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class CommandProcessor implements MessageProcessor {

    public static final String ROOM_LIST_COMMAND = "list";
    public static final String ROOM_ADD_COMMAND = "room add";
    public static final String ROOM_REM_COMMAND = "room del";
    private static final String HELP_COMMAND = "help";
    private static final Logger log = LoggerFactory.getLogger(CommandProcessor.class);

    private List<BotCommand> commands = Arrays.asList(
            new BotCommand() {
                @Override public String getName() {
                    return HELP_COMMAND;
                }

                @Override public void execute(MessageContext ctx) {
                    final StringBuilder helpBuilder = new StringBuilder("Known commands:\r\n");
                    for (BotCommand command : commands) {
                        helpBuilder.append(command.getName()).append(" - ").append(command.help()).append(System.lineSeparator());
                    }
                    ctx.reply(helpBuilder.toString());
                }

                @Override public String help() {
                    return "Show help message";
                }
            },

            new RoomListCommand(),
            new RoomAddCommand(),
            new RoomRemoveCommand()
    );

    @Override
    public boolean needToProcess(MessageContext ctx) {
        final String messageText = ctx.getMessageText();
        return commands.stream().anyMatch(botCommand -> botCommand.isCommand(messageText));
    }

    @Override
    public void process(MessageContext ctx) {
        final String messageText = ctx.getMessageText();
        final Optional<BotCommand> command = commands.stream()
                .filter(botCommand -> botCommand.isCommand(messageText))
                .findFirst();

        if (command.isPresent()) {
            command.get().execute(ctx);
        }
    }

}


