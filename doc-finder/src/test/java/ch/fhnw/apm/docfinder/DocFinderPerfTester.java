package ch.fhnw.apm.docfinder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.stream.DoubleStream;

public class DocFinderPerfTester {
    private static final int REPETITIONS = 30;
    private static final int MAX_PROCESSORS = Runtime.getRuntime().availableProcessors() * 2;
    private static final Path RESULT_FILE = Path.of("perf-tests/result.csv").toAbsolutePath();
    public static final String SEARCH_TEXT = "woman friend flower food";

    public static void main(String[] args) throws IOException {
        var booksDir = Path.of("perf-tests/books").toAbsolutePath();
        if (!Files.isDirectory(booksDir)) {
            System.err.println("Directory perf-tests/books not found. " +
                    "Make sure to run this program in the doc-finder directory.");
            System.exit(1);
        }
        Files.deleteIfExists(RESULT_FILE);
        Files.createFile(RESULT_FILE);

        var finder = new DocFinder(booksDir);
        for(int n = 1; n <= MAX_PROCESSORS+1; n++){
            System.out.println("Running with " + n + " threads.");
            runWithNProcessors(finder, n);
        }

    }

    private static void runWithNProcessors(DocFinder finder, int numOfProcessors) throws IOException {
        var latencies = new double[REPETITIONS];
        for (int i = 0; i < REPETITIONS; i++) {
            var startTime = System.nanoTime();

            finder.findDocs(SEARCH_TEXT, numOfProcessors);

            var latency = System.nanoTime() - startTime;
            latencies[i] = latency / 1_000_000.0; // convert to ms

            // print progress to err
            if ((i + 1) % 10 == 0) {
                //System.err.println(i + 1 + "/" + REPETITIONS + " repetitions");
            }
        }
        //for (int i = 0; i < REPETITIONS; i++) {
        //    System.out.printf("%.1f\n", latencies[i]);
        //}

        var stats = DoubleStream.of(latencies).summaryStatistics();
        double throughput = 1 / (stats.getAverage()/1000);
        Files.writeString(RESULT_FILE, String.format("%.3f,", throughput), StandardOpenOption.APPEND);

        System.out.printf("Average: %.1f ms\n", stats.getAverage());
        System.out.printf("Min: %.1f ms\n", stats.getMin());
        System.out.printf("Max: %.1f ms\n", stats.getMax());
        System.out.println();
    }
}
