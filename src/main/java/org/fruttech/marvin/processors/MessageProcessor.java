package org.fruttech.marvin.processors;

import org.fruttech.marvin.MessageContext;

public interface MessageProcessor {
    boolean needToProcess(MessageContext ctx);

    void process(MessageContext ctx);
}

