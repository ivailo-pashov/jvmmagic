package com.epam.jvmmagic.black;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

/*
 *  Demo is assuming it's executed on 32-bit Java 8
 */
public class MultipleInheritance {

    public static void main(String[] args) throws NoSuchFieldException, IllegalAccessException {
        Object o = new Object();
        printClassAddresses(o);

        A a = new A();

        Unsafe unsafe = getUnsafe();
        print(a, "variable a1", normalize(unsafe.getInt(a, 8L)));
        print(a,"variable a2", normalize(unsafe.getInt(a, 12L)));

        printClassAddresses(a);

        B b = new B();

        print(b, "variable b1", normalize(unsafe.getInt(b, 8L)));

        printClassAddresses(b);

        print("Starting multiple inheritance transformation");
        print("Shifting " + b.getClass().getSimpleName() + " class pointer and changing super check offset");
        final long bClassAddress = getClassAddress(b);
        unsafe.putAddress(bClassAddress + 32L, bClassAddress);
        unsafe.putAddress(bClassAddress + 8L, 32L);
        printClassAddresses(b);

        print("Adding " + a.getClass().getSimpleName() + " class pointer as supertype");
        final long aClassAddress = getClassAddress(a);
        unsafe.putAddress(bClassAddress + 28L, aClassAddress);
        printClassAddresses(b);

        A ba = (A)(Object)b;

        print("b instanceof A: " + Boolean.toString(ba instanceof A));
        print("b instanceof B: " + Boolean.toString(b instanceof B));
        print("");

        print("Setting value of ba.a1 to 10");
        ba.a1 = 10;
        print("ba.a1=" + ba.a1);
        print("ba.a2=" + ba.a2);
        print("b.b1=" + b.b1);

        print("Setting value of ba.a1 to 11 through memory");
        unsafe.putInt(ba, 8L, 11);
        print("ba.a1=" + ba.a1);
        print("ba.a2=" + ba.a2);
        print("b.b1=" + b.b1);

        methodWhichTakesA(ba);
    }

    private static void methodWhichTakesA(A a) {
        print("Value printed from method which takes a different parameter: " + ((B)(Object)a).b1);
    }

    private static long normalize(int value) {
        if(value >= 0) return value;
        return (~0L >>> 32) & value;
    }

    private static Unsafe getUnsafe() throws NoSuchFieldException, IllegalAccessException {
        Field f = Unsafe.class.getDeclaredField("theUnsafe");
        f.setAccessible(true);
        Unsafe unsafe = (Unsafe) f.get(null);
        return unsafe;
    }

    private static <T> void printClassAddresses(T instance) throws NoSuchFieldException, IllegalAccessException {
        Unsafe unsafe = getUnsafe();
        final long classAddress = getClassAddress(instance);
        print(instance,"super check offset", unsafe.getAddress(classAddress + 8L));
        print(instance,"instance class address", classAddress);
        print(instance,"supertype 0 class address", unsafe.getAddress(classAddress + 24L));
        print(instance,"supertype 1 class address", unsafe.getAddress(classAddress + 28L));
        print(instance,"supertype 2 class address", unsafe.getAddress(classAddress + 32L));
        print("");
    }

    private static <T> long getClassAddress(T instance) throws NoSuchFieldException, IllegalAccessException {
        return normalize(getUnsafe().getInt(instance, 4L));
    }

    private static <T> void print(T instance, String description, long value) {
        System.out.println(instance.getClass().getSimpleName() + " " + description + ": " + value);
    }

    private static void print(String message) {
        System.out.println(message);
    }

    private static final class A {
        private int a1 = 7;
        private int a2 = 8;
    }

    private static class B {
        private int b1 = 9;
    }
}
