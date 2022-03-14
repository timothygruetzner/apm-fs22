package ch.fhnw.apm.docfinder;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.SampleTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 2)
@Measurement(iterations = 3, time = 5)
public class DocFinderBenchmarks {

    private Path booksDir;

    @Param({"12"})
    public int threads;

    @Param({"mädchen zug", "mädchen zug biergarten hochspannungsleitung"})
    public String searchText;

    @Param({"true", "false"})
    public boolean caseSensitive;


    @Setup
    public void setup() {
        booksDir = Path.of("perf-tests/books").toAbsolutePath();
        if (!Files.isDirectory(booksDir)) {
            System.err.println("Directory perf-tests/books not found. " +
                    "Make sure to run this program in the doc-finder directory.");
            System.exit(1);
        }
    }

    @Benchmark
    public List<Result> runDocFinder() throws IOException {
        DocFinder docFinder = new DocFinder(this.booksDir, this.threads);
        docFinder.setIgnoreCase(!caseSensitive);
        return docFinder.findDocs(searchText);
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(DocFinderBenchmarks.class.getSimpleName())
                .forks(1)
                .build();
        new Runner(opt).run();
    }
}
