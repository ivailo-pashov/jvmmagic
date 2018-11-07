package com.epam.jvmmagic.black.calculate;

public class EquilateralTriangleCalculator implements SurfaceCalculator {

    private static final double RATIO = Math.sqrt(3) / 4;

    @Override
    public double calculate(int side) {
        return RATIO * side * side;
    }
}
