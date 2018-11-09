package com.epam.jvmmagic.black.util;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

public class UnsafeProvider {

    private static final Unsafe INSTANCE = hackUnsafe();

    public static Unsafe getUnsafe() {
        return INSTANCE;
    }

    public static Unsafe hackUnsafe() {
        try {
            Field f = Unsafe.class.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            return (Unsafe) f.get(null);
        } catch (Exception e) {
            e.printStackTrace();

        }
        return null;
    }

}
