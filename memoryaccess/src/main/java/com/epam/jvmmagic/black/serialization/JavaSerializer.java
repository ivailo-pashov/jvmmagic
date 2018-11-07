package com.epam.jvmmagic.black.serialization;

import java.io.*;

public class JavaSerializer {

    private final int size;
    private ObjectOutputStream os;

    public JavaSerializer(int size) {
        this.size = size;
    }

    public byte[] write(Object object) {
        try {
            ByteArrayOutputStream byteOut = new ByteArrayOutputStream(size);
            ObjectOutputStream os = new ObjectOutputStream(byteOut);
            os.writeObject(object);
            return byteOut.toByteArray();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }


    public Object read(byte[] bytes) {
        try {
            ObjectInputStream is = new ObjectInputStream(new ByteArrayInputStream(bytes));
            return is.readObject();
        }catch (IOException | ClassNotFoundException e) {
            throw new IllegalStateException(e);
        }
    }

}
