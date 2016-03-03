package com.igortyulkanov.routebuildersample.utils;

import java.util.Random;

public class RandomUtils {

    private RandomUtils() {
    }

    public static Random newRandom() {
        return new Random();
    }

    public static int newRandomAndNextInt(int min, int max) {
        return newRandom().nextInt(max - min) + min;
    }

}
