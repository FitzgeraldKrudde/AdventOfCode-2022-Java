package aoc2022;

import java.util.List;

import static java.util.stream.Collectors.toList;

public class Dayxx extends Day {
    @Override
    public String doPart1(List<String> inputRaw) {
        List<Long> input = parseInput(inputRaw);


        long result = input.size();

        return String.valueOf(result);
    }

    @Override
    public String doPart2(List<String> inputRaw) {
//        List<Long> input = parseInput(inputRaw);


        long result = 0;

        return String.valueOf(result);
    }

    private List<Long> parseInput(List<String> inputRaw) {
        return inputRaw.stream()
                .map(Long::valueOf)
                .collect(toList());
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
