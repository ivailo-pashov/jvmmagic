package com.epam.jvmmagic.black;

import sun.misc.Unsafe;
import sun.reflect.ReflectionFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

public class NoConstructorObjectCreation {

    public static void main(String[] args) throws NoSuchFieldException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {

        // Instantiate object with Unsafe without calling its constructor
        System.out.println("Creating an object with Unsafe without calling its constructor");
        Unsafe unsafe = getUnsafe();
        ClassWithExpensiveConstructor instance = (ClassWithExpensiveConstructor)
                unsafe.allocateInstance(ClassWithExpensiveConstructor.class);
        System.out.println("Is object null: " + (instance == null));
        System.out.println("Instance value = " + instance.getValue());
        System.out.println();

        // Instantiate object with ReflectionFactory without calling its constructor
        System.out.println("Creating an object with ReflectionFactory without calling its constructor");
        Constructor<ClassWithExpensiveConstructor> silentConstructor = (Constructor<ClassWithExpensiveConstructor>) ReflectionFactory.getReflectionFactory()
                .newConstructorForSerialization(ClassWithExpensiveConstructor.class, Object.class.getConstructor());
        silentConstructor.setAccessible(true);
        System.out.println("Instance value = " + silentConstructor.newInstance().getValue());
        System.out.println();

        System.out.println("Creating an object with ReflectionFactory by calling a different class's constructor");
        Constructor<ClassWithExpensiveConstructor> silentConstructor2 = (Constructor<ClassWithExpensiveConstructor>) ReflectionFactory.getReflectionFactory()
                .newConstructorForSerialization(ClassWithExpensiveConstructor.class, OtherClass.class.getDeclaredConstructor());
        silentConstructor2.setAccessible(true);
        ClassWithExpensiveConstructor instance2 = silentConstructor2.newInstance();
        System.out.println("Instance value = " + instance2.getValue());
        System.out.println("Memory value1 = " + unsafe.getInt(instance2, 8L));
        System.out.println("Memory value2 = " + unsafe.getInt(instance2, 12L));
        System.out.println("Are classes the same: " + (ClassWithExpensiveConstructor.class == instance2.getClass()));
        System.out.println("ClassWithExpensiveConstructor super class: " + instance.getClass().getSuperclass());
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

    private static class OtherClass {

        private final int value;
        private final int unknownValue;

        private OtherClass() {
            System.out.println("OtherClass constructor called");
            value = 10;
            unknownValue = 20;
        }
    }

}
