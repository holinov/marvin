package org.fruttech.marvin.processors;

import org.fruttech.marvin.MessageContext;

public interface BotCommand {
    String getName();

    default boolean isCommand(String msg) {
        return msg.equals(getName());
    }

    void execute(MessageContext ctx);

    String help();
}
