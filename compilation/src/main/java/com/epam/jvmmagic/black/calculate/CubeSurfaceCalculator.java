package com.epam.jvmmagic.black.calculate;

public class CubeSurfaceCalculator implements SurfaceCalculator {

    @Override
    public double calculate(int side) {
        return 6 * side * side;
    }

}
