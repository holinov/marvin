package org.fruttech.marvin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

class BotPersistentCfg implements Externalizable {
    private static final Logger log = LoggerFactory.getLogger(BotPersistentCfg.class);
    Map<String, String> chatGroups = new HashMap<>();

    @Override public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(chatGroups.size());
        for (Map.Entry<String, String> entry : chatGroups.entrySet()) {
            out.writeUTF(entry.getKey());
            out.writeUTF(entry.getValue());
        }
    }

    @Override public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        chatGroups = new HashMap<>();
        int cnt = in.readInt();
        for (int i = 0; i < cnt; i++) {
            String key = in.readUTF();
            String val = in.readUTF();
            chatGroups.put(key, val);
        }
    }

    void save(String file) {
        try {
            FileOutputStream fos = new FileOutputStream(file);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            writeExternal(oos);
            oos.close();
        } catch (IOException e) {
            log.error("Error saving config", e);
        }
    }

    void load(String file) {
        try {
            FileInputStream fis = new FileInputStream(file);
            ObjectInputStream ois = new ObjectInputStream(fis);
            readExternal(ois);
            ois.close();
        } catch (IOException | ClassNotFoundException e) {
            log.error("Error loading config", e);
        }
    }
}
