package com.epam.jvmmagic.white;

import org.openjdk.jmh.annotations.*;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 25, time = 400, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 20, time = 400, timeUnit = TimeUnit.MILLISECONDS)
@Fork(1)
public class SortingBenchmark {

    @Param({"4", "16", "64", "128", "256", "512", "1024"})
    private int size;

    private int[] intArray;

    @Setup(Level.Iteration)
    public void setup() {
        intArray = ThreadLocalRandom.current().ints().limit(size).toArray();
        //        System.out.println("Generating " + Arrays.toString(intArray));
    }

    @Benchmark
    public int[] measureInsertionSort() {
        insertionSort(intArray);
        return intArray;
    }

    @Benchmark
    public int[] measureQuickSort() {
        quickSort(intArray, 0, intArray.length - 1);
        return intArray;
    }

    @Benchmark
    public int[] measureQuickSortIterative() {
        quickSortIterative(intArray, 0, intArray.length - 1);

        return intArray;
    }

    private static void insertionSort(int arr[]) {
        int n = arr.length;
        for (int i = 1; i < n; ++i) {
            int key = arr[i];
            int j = i - 1;

            while (j >= 0 && arr[j] > key) {
                arr[j + 1] = arr[j];
                j = j - 1;
            }
            arr[j + 1] = key;
        }
    }

    private static void quickSort(int arr[], int low, int high) {
        if (low < high) {
            int pi = partition(arr, low, high);

            quickSort(arr, low, pi - 1);
            quickSort(arr, pi + 1, high);
        }
    }

    private static void quickSortIterative(int arr[], int l, int h) {
        int[] stack = new int[h - l + 1];

        int top = -1;

        stack[++top] = l;
        stack[++top] = h;

        while (top >= 0) {
            h = stack[top--];
            l = stack[top--];

            int p = partition(arr, l, h);

            if (p - 1 > l) {
                stack[++top] = l;
                stack[++top] = p - 1;
            }

            if (p + 1 < h) {
                stack[++top] = p + 1;
                stack[++top] = h;
            }
        }
    }

    private static int partition(int arr[], int low, int high) {
        int pivot = arr[high];
        int i = (low - 1);
        for (int j = low; j <= high - 1; j++) {
            if (arr[j] <= pivot) {
                i++;

                int temp = arr[i];
                arr[i] = arr[j];
                arr[j] = temp;
            }
        }

        int temp = arr[i + 1];
        arr[i + 1] = arr[high];
        arr[high] = temp;

        return i + 1;
    }
}
