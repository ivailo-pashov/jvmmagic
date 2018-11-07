package com.epam.jvmmagic.black.calculate;

public class CircleSurfaceCalculator implements SurfaceCalculator {

    @Override
    public double calculate(int radius) {
        return Math.PI * radius * radius;
    }
}
