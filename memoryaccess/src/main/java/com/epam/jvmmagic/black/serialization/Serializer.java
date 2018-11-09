package com.epam.jvmmagic.black.serialization;

public interface Serializer {

    void reset();

    void putBoolean(boolean value);

    boolean getBoolean();

    void putInt(int value);

    int getInt();

    void putLong(long value);

    long getLong();

    void putLongArray(long[] values);

    long[] getLongArray();

    void putDoubleArray(double[] values);

    double[] getDoubleArray();
}
