package org.fruttech.marvin.processors.commands;

import org.fruttech.marvin.MessageContext;
import org.fruttech.marvin.processors.BotCommand;
import org.fruttech.marvin.processors.CommandProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RoomRemoveCommand implements BotCommand {
    private static final Logger log = LoggerFactory.getLogger(RoomRemoveCommand.class);

    @Override public String getName() {
        return CommandProcessor.ROOM_REM_COMMAND;
    }

    @Override public boolean isCommand(String msg) {
        return msg.startsWith(CommandProcessor.ROOM_REM_COMMAND);
    }

    @Override public void execute(MessageContext ctx) {
        final String messageText = ctx.getMessageText();
        final String roomName = messageText.substring(CommandProcessor.ROOM_ADD_COMMAND.length())
                .trim()
                .replace(' ', '_');

        final String roomId = ctx.getChat().getIdentity();

        if (!ctx.getBot().getKnownRooms().containsKey(roomName)) {
            ctx.reply("Room " + roomName + " not exists.");
        } else {

            if (ctx.isBotControlRoom()) {
                ctx.getBot().getKnownRooms().remove(roomName);
                ctx.getBot().saveConfig();
                final String msg = String.format("User %s removed room %s with id: %s", ctx.getMessage().getSender().getUsername(), roomName, roomId);
                log.info(msg);
                ctx.reply(msg);
            } else {
                ctx.reply("Operation permitted only from bot control room");
            }
        }
    }

    @Override public String help() {
        return "Remove chat group to list";
    }
}
