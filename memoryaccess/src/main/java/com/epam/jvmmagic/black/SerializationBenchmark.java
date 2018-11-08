package com.epam.jvmmagic.black;

import com.epam.jvmmagic.black.serialization.JavaSerializer;
import com.epam.jvmmagic.black.serialization.Serializer;
import com.epam.jvmmagic.black.serialization.UnsafeDirectSerializer;
import com.epam.jvmmagic.black.serialization.UnsafeSerializer;
import org.openjdk.jmh.annotations.*;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
@State(Scope.Thread)
@Warmup(time = 3000, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(time = 3000, timeUnit = TimeUnit.MILLISECONDS)
@Fork(2)
public class SerializationBenchmark {

    @Param({"10", "1000", "100000"})
    private int dataSize;

    private int bufferSize;
    private ObjectToBeSerialized objectToBeSerialized;
    private UnsafeSerializer serializer;
    private ByteBuffer buffer;
    private ByteBuffer directBuffer;
    private UnsafeDirectSerializer directSerializer;

    @Setup
    public void setup() {
        bufferSize = 16 * dataSize + 64;
        objectToBeSerialized = new ObjectToBeSerialized(
                1010L, true, 777, 99,
                IntStream.range(0, dataSize).mapToDouble(i -> i / 10d).toArray(),
                LongStream.range(0, dataSize).toArray()
        );
        serializer = new UnsafeSerializer(bufferSize);
        directSerializer = new UnsafeDirectSerializer(bufferSize);
        buffer = ByteBuffer.allocate(bufferSize);
        directBuffer = ByteBuffer.allocateDirect(bufferSize);
    }

    @Benchmark
    public byte[] writeSerialization() {
        JavaSerializer serializer = new JavaSerializer(bufferSize);
        return serializer.write(objectToBeSerialized);
    }

    @Benchmark
    public ObjectToBeSerialized copySerialization() {
        JavaSerializer serializer = new JavaSerializer(bufferSize);
        byte[] bytes = serializer.write(objectToBeSerialized);
        return (ObjectToBeSerialized) serializer.read(bytes);
    }

    @Benchmark
    public byte[] writeByteBuffer() {
        buffer.clear();
        objectToBeSerialized.write(buffer);
        return buffer.array();
    }

    @Benchmark
    public ObjectToBeSerialized copyByteBuffer() {
        buffer.clear();
        objectToBeSerialized.write(buffer);
        buffer.flip();
        return ObjectToBeSerialized.read(buffer);
    }

    @Benchmark
    public byte[] writeDirectByteBuffer() {
        directBuffer.clear();
        objectToBeSerialized.write(directBuffer);
        return buffer.array();
    }

    @Benchmark
    public ObjectToBeSerialized copyDirectByteBuffer() {
        directBuffer.clear();
        objectToBeSerialized.write(directBuffer);
        directBuffer.flip();
        return ObjectToBeSerialized.read(directBuffer);
    }

    @Benchmark
    public byte[] writeUnsafe() {
        serializer.reset();
        objectToBeSerialized.write(serializer);
        return serializer.getBuffer();
    }

    @Benchmark
    public ObjectToBeSerialized copyUnsafe() {
        serializer.reset();
        objectToBeSerialized.write(serializer);
        serializer.reset();
        return ObjectToBeSerialized.read(serializer);
    }

    @Benchmark
    public ByteBuffer writeDirectUnsafe() {
        directSerializer.reset();
        objectToBeSerialized.write(directSerializer);
        return directSerializer.getBuffer();
    }

    @Benchmark
    public ObjectToBeSerialized copyDirectUnsafe() {
        directSerializer.reset();
        objectToBeSerialized.write(directSerializer);
        directSerializer.reset();
        return ObjectToBeSerialized.read(directSerializer);
    }

    public static class ObjectToBeSerialized implements Serializable {

        private static final long serialVersionUID = 10275539472837495L;

        private final long sourceId;
        private final boolean special;
        private final int orderCode;
        private final int priority;
        private final double[] prices;
        private final long[] quantities;

        public ObjectToBeSerialized(final long sourceId, final boolean special,
                                    final int orderCode, final int priority,
                                    final double[] prices, final long[] quantities) {
            this.sourceId = sourceId;
            this.special = special;
            this.orderCode = orderCode;
            this.priority = priority;
            this.prices = prices;
            this.quantities = quantities;
        }

        public void write(final ByteBuffer byteBuffer) {
            byteBuffer.putLong(sourceId);
            byteBuffer.put((byte) (special ? 1 : 0));
            byteBuffer.putInt(orderCode);
            byteBuffer.putInt(priority);

            byteBuffer.putInt(prices.length);
            for (final double price : prices) {
                byteBuffer.putDouble(price);
            }

            byteBuffer.putInt(quantities.length);
            for (final long quantity : quantities) {
                byteBuffer.putLong(quantity);
            }
        }

        public static ObjectToBeSerialized read(final ByteBuffer byteBuffer) {
            final long sourceId = byteBuffer.getLong();
            final boolean special = 0 != byteBuffer.get();
            final int orderCode = byteBuffer.getInt();
            final int priority = byteBuffer.getInt();

            final int pricesSize = byteBuffer.getInt();
            final double[] prices = new double[pricesSize];
            for (int i = 0; i < pricesSize; i++) {
                prices[i] = byteBuffer.getDouble();
            }

            final int quantitiesSize = byteBuffer.getInt();
            final long[] quantities = new long[quantitiesSize];
            for (int i = 0; i < quantitiesSize; i++) {
                quantities[i] = byteBuffer.getLong();
            }

            return new ObjectToBeSerialized(sourceId, special, orderCode,
                    priority, prices, quantities
            );
        }

        public void write(final Serializer buffer) {
            buffer.putLong(sourceId);
            buffer.putBoolean(special);
            buffer.putInt(orderCode);
            buffer.putInt(priority);
            buffer.putDoubleArray(prices);
            buffer.putLongArray(quantities);
        }

        public static ObjectToBeSerialized read(final Serializer buffer) {
            final long sourceId = buffer.getLong();
            final boolean special = buffer.getBoolean();
            final int orderCode = buffer.getInt();
            final int priority = buffer.getInt();
            final double[] prices = buffer.getDoubleArray();
            final long[] quantities = buffer.getLongArray();

            return new ObjectToBeSerialized(sourceId, special, orderCode,
                    priority, prices, quantities
            );
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            final ObjectToBeSerialized that = (ObjectToBeSerialized) o;

            if (orderCode != that.orderCode) {
                return false;
            }
            if (priority != that.priority) {
                return false;
            }
            if (sourceId != that.sourceId) {
                return false;
            }
            if (special != that.special) {
                return false;
            }
            if (!Arrays.equals(prices, that.prices)) {
                return false;
            }
            if (!Arrays.equals(quantities, that.quantities)) {
                return false;
            }

            return true;
        }
    }
}
