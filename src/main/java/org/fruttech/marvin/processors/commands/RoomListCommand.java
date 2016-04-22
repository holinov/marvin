package org.fruttech.marvin.processors.commands;

import org.fruttech.marvin.MessageContext;
import org.fruttech.marvin.processors.BotCommand;
import org.fruttech.marvin.processors.CommandProcessor;

import java.util.Map;

public class RoomListCommand implements BotCommand {
    @Override public String getName() {
        return CommandProcessor.ROOM_LIST_COMMAND;
    }

    @Override public void execute(MessageContext ctx) {
        final StringBuilder rooms = new StringBuilder("Known rooms:" + System.lineSeparator());
        for (Map.Entry<String, String> room : ctx.getBot().getKnownRooms().entrySet()) {
            rooms.append(room.getKey()).append(" : ").append(room.getValue()).append(System.lineSeparator());
        }
        ctx.reply(rooms.toString());
    }

    @Override public String help() {
        return "List known chat groups";
    }
}
