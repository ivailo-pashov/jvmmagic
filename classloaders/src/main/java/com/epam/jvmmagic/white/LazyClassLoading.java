package com.epam.jvmmagic.white;

/**
 * A class or interface type T will be initialized immediately before the first occurrence of any one of the following:
 *
 * T is a class and an instance of T is created.
 * A static method declared by T is invoked.
 * A static field declared by T is assigned.
 * A static field declared by T is used and the field is not a constant variable (§4.12.4).
 * T is a top level class (§7.6) and an assert statement (§14.10) lexically nested within T (§8.1.3) is executed.
 *
 * Invocation of certain reflective methods in class Class and in package java.lang.reflect also causes class or interface initialization.
 * A class or interface will not be initialized under any other circumstance.
 *
 * https://docs.oracle.com/javase/specs/jls/se8/html/jls-12.html#jls-12.4
 */

public class LazyClassLoading {

    public static void main(String[] args) throws ClassNotFoundException {
        System.out.println("CustomClassloaders started");

        System.out.println("Creating a reference to class");
        ClassToLoad instance = null;
        System.out.println(instance);

        System.out.println("Accessing static variable in class");
        System.out.println(ClassToLoad.i);
    }

    private static class ClassToLoad {
        static int i;

        static {
            System.out.println("Class loaded");
            i = 10;
        }
    }

}
