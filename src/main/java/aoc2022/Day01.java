package aoc2022;

import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.summingLong;

public class Day01 extends Day {
    private final AtomicInteger ai = new AtomicInteger(1);

    @Override
    public String doPart1(List<String> inputRaw) {
        // the getKet is a bit of a hack (to squeeze some state in the lambda)
        long result = inputRaw.stream()
                .collect(groupingBy(this::getKey,
                        summingLong(this::getCalories)))
                .values()
                .stream()
                .max(Long::compareTo)
                .orElseThrow(() -> new RuntimeException("no solution"));

        return String.valueOf(result);
    }

    private long getCalories(String calories) {
        if (StringUtils.isBlank(calories)) {
            return 0;
        }else {
            return Integer.parseInt(calories);
        }
    }

    private Integer getKey(String calories) {
        if (StringUtils.isBlank(calories)) {
            ai.getAndIncrement();
        }
        return ai.get();
    }

    @Override
    public String doPart2(List<String> inputRaw) {
        long result = inputRaw.stream()
                .collect(groupingBy(this::getKey,
                        summingLong(this::getCalories)))
                .values()
                .stream()
                .sorted((aLong, anotherLong) -> -aLong.compareTo(anotherLong))
                .limit(3)
                .reduce(0L, Long::sum);

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
