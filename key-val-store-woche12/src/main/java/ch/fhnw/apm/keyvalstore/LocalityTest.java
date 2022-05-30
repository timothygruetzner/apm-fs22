package ch.fhnw.apm.keyvalstore;

import java.util.stream.IntStream;

public class LocalityTest {

    public static void main(String[] args) {
        final int n = 10_000;
        int[][] array = new int[n][n];

        for(int i = 0; i < n; i++) {
            for(int j = 0; j < n; j++) {
                array[i][j] = (int)System.currentTimeMillis();
            }
        }

        long start = System.nanoTime();
        long sum = 0L;
        for (int i = 0; i < n; i++) {
            for(int j = 0; j < n; j++) {
                sum += array[i][j];
            }
        }
        long duration = System.nanoTime() - start;
        System.out.printf("Variante 1: took %d nanos%n", duration);

        start = System.nanoTime();
        sum = 0L;
        for (int j = 0; j < n; j++) {
            for(int i = 0; i < n; i++) {
                sum += array[i][j];
            }
        }
        long duration2 = System.nanoTime() -  start;
        System.out.printf("Variante 2: took %d nanos%n", duration2);

        System.out.printf("speedup: %f", (float)duration2 / duration);
    }

}
