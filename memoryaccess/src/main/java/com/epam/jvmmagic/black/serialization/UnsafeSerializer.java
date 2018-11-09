package com.epam.jvmmagic.black.serialization;

import com.epam.jvmmagic.black.util.UnsafeProvider;
import sun.misc.Unsafe;

public class UnsafeSerializer implements Serializer {

    private static final Unsafe UNSAFE = UnsafeProvider.getUnsafe();
    private static final long BYTE_ARRAY_OFFSET = UNSAFE.arrayBaseOffset(byte[].class);
    private static final long LONG_ARRAY_OFFSET = UNSAFE.arrayBaseOffset(long[].class);
    private static final long DOUBLE_ARRAY_OFFSET = UNSAFE.arrayBaseOffset(double[].class);

    private static final int SIZE_OF_BOOLEAN = 1;
    private static final int SIZE_OF_INT = 4;
    private static final int SIZE_OF_LONG = 8;

    private int pos = 0;
    private final byte[] buffer;

    public UnsafeSerializer(int size) {
        this.buffer = new byte[size];
    }

    public byte[] getBuffer() {
        return buffer;
    }

    @Override
    public void reset() {
        this.pos = 0;
    }

    @Override
    public void putBoolean(final boolean value) {
        UNSAFE.putBoolean(buffer, BYTE_ARRAY_OFFSET + pos, value);
        pos += SIZE_OF_BOOLEAN;
    }

    @Override
    public boolean getBoolean() {
        boolean value = UNSAFE.getBoolean(buffer, BYTE_ARRAY_OFFSET + pos);
        pos += SIZE_OF_BOOLEAN;

        return value;
    }

    @Override
    public void putInt(final int value) {
        UNSAFE.putInt(buffer, BYTE_ARRAY_OFFSET + pos, value);
        pos += SIZE_OF_INT;
    }

    @Override
    public int getInt() {
        int value = UNSAFE.getInt(buffer, BYTE_ARRAY_OFFSET + pos);
        pos += SIZE_OF_INT;

        return value;
    }

    @Override
    public void putLong(final long value) {
        UNSAFE.putLong(buffer, BYTE_ARRAY_OFFSET + pos, value);
        pos += SIZE_OF_LONG;
    }

    @Override
    public long getLong() {
        long value = UNSAFE.getLong(buffer, BYTE_ARRAY_OFFSET + pos);
        pos += SIZE_OF_LONG;

        return value;
    }

    @Override
    public void putLongArray(final long[] values) {
        putInt(values.length);

        long bytesToCopy = values.length << 3;
        UNSAFE.copyMemory(values, LONG_ARRAY_OFFSET,
                buffer, BYTE_ARRAY_OFFSET + pos,
                bytesToCopy
        );
        pos += bytesToCopy;
    }

    @Override
    public long[] getLongArray() {
        int arraySize = getInt();
        long[] values = new long[arraySize];

        long bytesToCopy = values.length << 3;
        UNSAFE.copyMemory(buffer, BYTE_ARRAY_OFFSET + pos,
                values, LONG_ARRAY_OFFSET,
                bytesToCopy
        );
        pos += bytesToCopy;

        return values;
    }

    @Override
    public void putDoubleArray(final double[] values) {
        putInt(values.length);

        long bytesToCopy = values.length << 3;
        UNSAFE.copyMemory(values, DOUBLE_ARRAY_OFFSET,
                buffer, BYTE_ARRAY_OFFSET + pos,
                bytesToCopy
        );
        pos += bytesToCopy;
    }

    @Override
    public double[] getDoubleArray() {
        int arraySize = getInt();
        double[] values = new double[arraySize];

        long bytesToCopy = values.length << 3;
        UNSAFE.copyMemory(buffer, BYTE_ARRAY_OFFSET + pos,
                values, DOUBLE_ARRAY_OFFSET,
                bytesToCopy
        );
        pos += bytesToCopy;

        return values;
    }
}
