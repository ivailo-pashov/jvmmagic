package com.epam.jvmmagic.black.calculate;

public class SquareSurfaceCalculator implements SurfaceCalculator {

    @Override
    public double calculate(int side) {
        return side * side;
    }
}
