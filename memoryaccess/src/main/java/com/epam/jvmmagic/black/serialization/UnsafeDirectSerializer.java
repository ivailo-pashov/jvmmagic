package com.epam.jvmmagic.black.serialization;

import com.epam.jvmmagic.black.util.UnsafeProvider;
import com.esotericsoftware.kryo.unsafe.UnsafeByteBufferInput;
import com.esotericsoftware.kryo.unsafe.UnsafeByteBufferOutput;
import sun.misc.Unsafe;

import java.nio.ByteBuffer;

public class UnsafeDirectSerializer implements Serializer {

    private static final Unsafe UNSAFE = UnsafeProvider.getUnsafe();
    private static final long LONG_ARRAY_OFFSET = UNSAFE.arrayBaseOffset(long[].class);
    private static final long DOUBLE_ARRAY_OFFSET = UNSAFE.arrayBaseOffset(double[].class);

    private static final int SIZE_OF_BOOLEAN = 1;
    private static final int SIZE_OF_INT = 4;
    private static final int SIZE_OF_LONG = 8;

    private int pos = 0;
    private final UnsafeByteBufferInput input;
    private final UnsafeByteBufferOutput output;

    public UnsafeDirectSerializer(int size) {
        ByteBuffer buffer = ByteBuffer.allocateDirect(size);
        input = new UnsafeByteBufferInput(buffer);
        output = new UnsafeByteBufferOutput();
        output.setBuffer(buffer, size);
    }

    public ByteBuffer getBuffer() {
        return input.getByteBuffer();
    }

    @Override
    public void reset() {

    }

    @Override
    public void putBoolean(final boolean value) {
        output.writeBoolean(value);
    }

    @Override
    public boolean getBoolean() {
        return input.readBoolean();
    }

    @Override
    public void putInt(final int value) {
        output.writeInt(value);
    }

    @Override
    public int getInt() {
        return input.readInt();
    }

    @Override
    public void putLong(final long value) {
        output.writeLong(value);
    }

    @Override
    public long getLong() {
       return input.readLong();
    }

    @Override
    public void putLongArray(final long[] values) {
        putInt(values.length);
        output.writeLongs(values, 0, values.length);
    }

    @Override
    public long[] getLongArray() {
        int arraySize = getInt();
        return input.readLongs(arraySize);
    }

    @Override
    public void putDoubleArray(final double[] values) {
        putInt(values.length);
        output.writeDoubles(values, 0, values.length);
    }

    @Override
    public double[] getDoubleArray() {
        int arraySize = getInt();
        return input.readDoubles(arraySize);
    }


}
