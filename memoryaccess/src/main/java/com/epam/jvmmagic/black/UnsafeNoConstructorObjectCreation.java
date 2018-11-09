package com.epam.jvmmagic.black;

import com.epam.jvmmagic.black.util.UnsafeProvider;

import java.lang.reflect.InvocationTargetException;

public class UnsafeNoConstructorObjectCreation {

    public static void main(String[] args) throws NoSuchFieldException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {

        // Instantiate object with Unsafe without calling its constructor
        System.out.println("Creating an object with Unsafe without calling its constructor");
        ClassWithExpensiveConstructor instance = (ClassWithExpensiveConstructor)
                UnsafeProvider.getUnsafe().allocateInstance(ClassWithExpensiveConstructor.class);
        System.out.println("Is object null: " + (instance == null));
        System.out.println("Instance value = " + instance.getValue());
        System.out.println();
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
