package com.epam.jvmmagic.black;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

public class MultipleInheritance {

    public static void main(String[] args) throws NoSuchFieldException, IllegalAccessException {
        Object o = new Object();
        printClassAddresses(o);

        A a = new A();

        Unsafe unsafe = getUnsafe();
        print(a, "variable i", normalize(unsafe.getInt(a, 8L)));
        print(a,"variable j", normalize(unsafe.getInt(a, 12L)));

        printClassAddresses(a);

        B b = new B();

        printClassAddresses(b);

        C c = new C();

        print(c, "variable k", normalize(unsafe.getInt(c, 8L)));

        printClassAddresses(c);

        print("Starting multiple inheritance transformation");
        print("Shifting " + c.getClass().getSimpleName() + " class pointer and changing super check offset");
        final long cClassAddress = getClassAddress(c);
        unsafe.putAddress(cClassAddress + 32L, cClassAddress);
        unsafe.putAddress(cClassAddress + 8L, 32L);
        printClassAddresses(c);

        print("Adding " + a.getClass().getSimpleName() + " class pointer as supertype");
        final long aClassAddress = getClassAddress(a);
        unsafe.putAddress(cClassAddress + 28L, aClassAddress);
        printClassAddresses(c);

//        A ac = (A)(Object)c;
//        ac.i = 12;
//        print(c.k + "");
//        print(normalize(unsafe.getInt(c, 12L)) + "");
//        B bc = (B)(Object)c;

//        print("c instanceof A: " + Boolean.toString((A)(Object)c instanceof A));
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
        print(instance,"instance class address", classAddress);
        print(instance,"super check offset", unsafe.getAddress(classAddress + 8L));
        print(instance,"supertype 0 class address", unsafe.getAddress(classAddress + 24L));
        print(instance,"supertype 1 class address", unsafe.getAddress(classAddress + 28L));
        print(instance,"supertype 2 class address", unsafe.getAddress(classAddress + 32L));
        print(instance,"supertype 3 class address", unsafe.getAddress(classAddress + 36L));
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

    private static class A {
        int i = 7;
        int j = 8;
    }

    private static class B {

    }

    private static class C {
        int k = 9;
    }
}
