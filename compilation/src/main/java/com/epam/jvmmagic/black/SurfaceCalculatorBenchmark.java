package com.epam.jvmmagic.black;

import com.epam.jvmmagic.black.calculate.*;
import org.openjdk.jmh.annotations.*;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 10, time = 1000, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 10, time = 1000, timeUnit = TimeUnit.MILLISECONDS)
@Fork(1)
public class SurfaceCalculatorBenchmark {

    private int side;

    @Param({"1", "2", "3", "4", "5"})
    private int modes;

    private SurfaceCalculator[] calculators;

    @Setup(Level.Trial)
    public void setup() throws Exception {
        side = 7;
        int size = 128;
        calculators = new SurfaceCalculator[size];
        List<Class<? extends SurfaceCalculator>> classes = Arrays.asList(
                CircleSurfaceCalculator.class,
                EquilateralTriangleCalculator.class,
                CubeSurfaceCalculator.class,
                SphereSurfaceCalculator.class,
                SquareSurfaceCalculator.class
        );
        for (int i = 0; i < size; i++) {
            calculators[i] = classes.get(i % modes).getDeclaredConstructor().newInstance();
        }
    }

    @Benchmark
    public double sum() {
        double sum = 0;
        for (SurfaceCalculator calculator : calculators) {
            sum += calculator.calculate(side);
        }
        return sum;
    }
}
