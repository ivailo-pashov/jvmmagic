package com.epam.jvmmagic.black;

import sun.misc.Unsafe;
import sun.reflect.ReflectionFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

public class NoConstructorObjectCreation {

    public static void main(String[] args) throws NoSuchFieldException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {

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
        ClassWithExpensiveConstructor instance = silentConstructor2.newInstance();
        Unsafe unsafe = getUnsafe();
        System.out.println("Instance value = " + instance.getValue());
        System.out.println("Memory value1 = " + unsafe.getInt(instance, 12L));
        System.out.println("Memory value2 = " + unsafe.getInt(instance, 16L));
        System.out.println("Are classes the same: " + (ClassWithExpensiveConstructor.class == instance.getClass()));
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
