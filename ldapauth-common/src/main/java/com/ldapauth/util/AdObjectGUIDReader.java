package com.ldapauth.util;

import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.util.UUID;

@Slf4j
public class AdObjectGUIDReader {

    public static byte[] objectToByteArray(Object obj) {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(obj);
            oos.flush();
            oos.close();
            bos.close();
            return bos.toByteArray();
        } catch (IOException e){
            log.error("error",e);
        }
        return null;
    }

    public static String convertBytesToUUID(byte[] objectGuid) {
        ByteBuffer bb = ByteBuffer.wrap(objectGuid);
        long high = bb.getLong();
        long low = bb.getLong();
        UUID uuid = new UUID(high, low);
        return uuid.toString();
    }
}
