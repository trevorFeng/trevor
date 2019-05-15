package com.trevor.util;

import java.io.*;
import java.sql.Blob;

/**
 * @author trevor
 * @date 05/15/19 13:18
 */
public class ByteToBlobUtil {

    /**
     * byte转对象
     * @param bytes
     * @return
     */
    private Object ByteToObject(byte[] bytes) {
        Object obj = null;
        try {
            // bytearray to object
            ByteArrayInputStream bi = new ByteArrayInputStream(bytes);
            ObjectInputStream oi = new ObjectInputStream(bi);

            obj = oi.readObject();
            bi.close();
            oi.close();
        } catch (Exception e) {
            System.out.println("translation" + e.getMessage());
            e.printStackTrace();
        }
        return obj;
    }

    /**
     * 对象转byte
     * @param obj
     * @return
     */
    public byte[] ObjectToByte(Object obj) {
        byte[] bytes = null;
        try {
            // object to bytearray
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            ObjectOutputStream oo = new ObjectOutputStream(bo);
            oo.writeObject(obj);

            bytes = bo.toByteArray();

            bo.close();
            oo.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bytes;
    }

    public static byte[] blobToBytes(Blob blob) {
        BufferedInputStream is = null;
        try {
            is = new BufferedInputStream(blob.getBinaryStream());
            byte[] bytes = new byte[(int) blob.length()];
            int len = bytes.length;
            int offset = 0;
            int read = 0;
            while (offset < len && (read = is.read(bytes, offset, len - offset)) >= 0) {
                offset += read;
            }
            return bytes;
        } catch (Exception e) {
            try {
                is.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            is = null;
            return null;
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                return null;
            }
        }
    }

    public static byte[] bytesToBlob(byte[] bytes) {
        InputStream is = new ByteArrayInputStream(bytes);
    }
}
