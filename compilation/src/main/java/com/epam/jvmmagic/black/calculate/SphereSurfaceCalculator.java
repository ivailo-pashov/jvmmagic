package com.epam.jvmmagic.black.calculate;

public class SphereSurfaceCalculator implements SurfaceCalculator {

    @Override
    public double calculate(int radius) {
        return 4 * Math.PI * radius * radius;
    }
}