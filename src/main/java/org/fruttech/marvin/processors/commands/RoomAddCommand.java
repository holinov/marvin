package org.fruttech.marvin.processors.commands;

import org.fruttech.marvin.MessageContext;
import org.fruttech.marvin.processors.BotCommand;
import org.fruttech.marvin.processors.CommandProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RoomAddCommand implements BotCommand {
    private static final Logger log = LoggerFactory.getLogger(RoomAddCommand.class);

    @Override public String getName() {
        return CommandProcessor.ROOM_ADD_COMMAND;
    }

    @Override public boolean isCommand(String msg) {
        return msg.startsWith(CommandProcessor.ROOM_ADD_COMMAND);
    }

    @Override public void execute(MessageContext ctx) {
        final String messageText = ctx.getMessageText();
        final String roomName = messageText.substring(CommandProcessor.ROOM_ADD_COMMAND.length())
                .trim()
                .replace(' ', '_');

        final String roomId = ctx.getChat().getIdentity();

        //DON'T CARE IF SOME ONE ADDS FAKE ROOM IT WILL NOT SHOW ANY NOTIFICATIONS
        if (ctx.getBot().getKnownRooms().containsKey(roomName)) {
            ctx.reply("Room " + roomName + " already exists. Please remove it.");
        } else {
            ctx.getBot().getKnownRooms().put(roomName, roomId);
            ctx.getBot().saveConfig();
            final String msg = String.format("User %s added room %s with id: %s", ctx.getMessage().getSender().getUsername(), roomName, roomId);
            log.info(msg);
            ctx.reply(msg);
        }
    }

    @Override public String help() {
        return "Add chat group to list";
    }
}
