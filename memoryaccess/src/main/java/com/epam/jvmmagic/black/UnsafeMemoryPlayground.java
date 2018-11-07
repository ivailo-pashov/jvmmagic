package com.epam.jvmmagic.black;

import com.epam.jvmmagic.black.util.UnsafeProvider;

public class UnsafeMemoryPlayground {

    public static void main(String[] args) {
        UnsafeProvider.getUnsafe();
    }

    static long toAddress(Object obj) {
        Object[] array = new Object[] {obj};
        long baseOffset = UnsafeProvider.getUnsafe().arrayBaseOffset(Object[].class);
        return normalize(UnsafeProvider.getUnsafe().getInt(array, baseOffset));
    }

    static Object fromAddress(long address) {
        Object[] array = new Object[] {null};
        long baseOffset = UnsafeProvider.getUnsafe().arrayBaseOffset(Object[].class);
        UnsafeProvider.getUnsafe().putLong(array, baseOffset, address);
        return array[0];
    }

    private static long normalize(int value) {
        if(value >= 0) return value;
        return (~0L >>> 32) & value;
    }
}
