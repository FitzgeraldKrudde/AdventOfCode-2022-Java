package aoc2022;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Day06 extends Day {
    @Override
    public String doPart1(List<String> inputRaw) {
        String input = inputRaw.get(0);

        long result = IntStream.range(4, input.length())
                .mapToObj(i -> new Pair(i, input.substring(i - 4, i)))
                .filter(pair -> pair.isStartOfMarker(4))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("no solution"))
                .position();

        return String.valueOf(result);
    }

    record Pair(int position, String potentialMarker) {
        boolean isStartOfMarker(int markerLength) {
            return potentialMarker.chars()
                    .boxed()
                    .collect(Collectors.toSet())
                    .size() == markerLength;
        }
    }

    @Override
    public String doPart2(List<String> inputRaw) {
        String input = inputRaw.get(0);

        long result = IntStream.range(14, input.length())
                .mapToObj(i -> new Pair(i, input.substring(i - 14, i)))
                .filter(pair -> pair.isStartOfMarker(14))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("no solution"))
                .position();

        return String.valueOf(result);
    }

    // @formatter:off
    static public void main(String[] args) throws Exception {
        // get our class
        final Class<?> clazz = new Object() {
        }.getClass().getEnclosingClass();

        // construct filename with input
        final String filename = clazz.getSimpleName().toLowerCase().replace("day0", "day") + ".txt";

        // get the classname
        final String fullClassName = clazz.getCanonicalName();

        // create instance
        Day day = (Day) Class.forName(fullClassName).getDeclaredConstructor().newInstance();

        // invoke "main" from the base Day class
        day.main(filename);
    }
    // @formatter:on
}
