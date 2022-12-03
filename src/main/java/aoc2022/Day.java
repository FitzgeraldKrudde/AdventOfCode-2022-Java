package aoc2022;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Stream;

import static java.nio.file.Files.lines;

public abstract class Day {
    abstract String doPart1(List<String> input);

    abstract String doPart2(List<String> input);

    final public void main(String filename) throws IOException, URISyntaxException {
        List<String> input = readInput(filename);

        // part 1
        LocalTime start = LocalTime.now();
        String result = doPart1(input);
        LocalTime finish = LocalTime.now();

        System.out.println("\npart 1: " + result);
        System.out.println("duration (ms): " + Duration.between(start, finish).toMillis());
        System.out.println();

        // part 2
        start = LocalTime.now();
        result = doPart2(input);
        finish = LocalTime.now();

        System.out.println("\npart 2: " + result);
        System.out.println("duration (ms): " + Duration.between(start, finish).toMillis());
    }

    public List<String> readInput(String filename) throws IOException, URISyntaxException {
        System.out.println("reading file: " + filename);

        // get the input lines
        URL url = getClass().getClassLoader().getResource(filename);
        if (url == null) {
            throw new RuntimeException("cannot read input file: " + filename);
        }

        try (Stream<String> input = lines(Paths.get(url.toURI()))) {
            List<String> lines = input.toList();
            System.out.printf("read file: %s (#lines: %d)%n", filename, lines.size());

            return lines;
        }
    }
}
