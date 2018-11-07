package com.epam.jvmmagic.white;

import java.util.Objects;
import java.util.function.IntSupplier;
import java.util.function.ToIntFunction;

public class LambdaPlayground {

    public static void main(String[] args) {
        test("CAFE");
        test("BABE");
        System.out.println();
        testEquality();
    }

    private static void test(String str) {
        ToIntFunction<String> nonCapturingMethodReference = String::length;

        IntSupplier capturingMethodReference = str::length;

        ToIntFunction<String> nonCapturingLambda = string -> string.length();

        IntSupplier capturingLambda = () -> str.length();

        ToIntFunction<String> anonymous = new ToIntFunction<String>() {

            @Override
            public int applyAsInt(String value) {
                return value.length();
            }
        };

        System.out.println(str + ":" +
                           "\n\t REFERENCE NC: " + nonCapturingMethodReference.applyAsInt(str) + " - " + nonCapturingMethodReference +
                           "\n\t REFERENCE C:  " + capturingMethodReference.getAsInt() + " - " + capturingMethodReference +
                           "\n\t LAMBDA NC:    " + nonCapturingLambda.applyAsInt(str) + " - " + nonCapturingLambda +
                           "\n\t LAMBDA C:     " + capturingLambda.getAsInt() + " - " + capturingLambda +
                           "\n\t ANONYMOUS:    " + anonymous.applyAsInt(str) + " - " + anonymous
        );
    }

    private static void testEquality() {
        ToIntFunction<String> first = String::length;
        ToIntFunction<String> second = String::length;
        System.out.println("Method references same: " + (first == second));
        System.out.println("Method references equal: " + Objects.equals(first, second));
    }
}
