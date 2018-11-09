package com.epam.jvmmagic.black;

import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

public class UnsafeNoConstructorObjectCreation {

    public static void main(String[] args) throws NoSuchFieldException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {

        // Instantiate object with Unsafe without calling its constructor
        System.out.println("Creating an object with Unsafe without calling its constructor");
        Unsafe unsafe = getUnsafe();
        ClassWithExpensiveConstructor instance = (ClassWithExpensiveConstructor)
                unsafe.allocateInstance(ClassWithExpensiveConstructor.class);
        System.out.println("Is object null: " + (instance == null));
        System.out.println("Instance value = " + instance.getValue());
        System.out.println();
    }

    private static Unsafe getUnsafe() throws NoSuchFieldException, IllegalAccessException {
        Field f = Unsafe.class.getDeclaredField("theUnsafe");
        f.setAccessible(true);
        Unsafe unsafe = (Unsafe) f.get(null);
        return unsafe;
    }

    private static class ClassWithExpensiveConstructor {

        private final int value;

        private ClassWithExpensiveConstructor() {
            System.out.println("ClassWithExpensiveConstructor constructor called");
            value = doExpensiveLookup();
        }

        private int doExpensiveLookup() {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return 1;
        }

        public int getValue() {
            return value;
        }
    }

}
