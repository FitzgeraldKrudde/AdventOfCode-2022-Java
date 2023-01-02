package aoc2022;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;

public class Day03 extends Day {
    private final AtomicInteger ai = new AtomicInteger(0);

    @Override
    public String doPart1(List<String> inputRaw) {
        Long result = inputRaw.stream()
                .map(this::readRucksack)
                .map(Rucksack::commonItem)
                .map(this::getPriority)
                .reduce(0L, Long::sum);

        return String.valueOf(result);
    }

    private long getPriority(char item) {
        if (Character.isLowerCase(item)) {
            return item - 96;
        } else {
            return item - 38;
        }
    }

    private Rucksack readRucksack(String line) {
        int lengthCompartment = line.length() / 2;
        return new Rucksack(line.substring(0, lengthCompartment), line.substring(lengthCompartment));
    }

    @Override
    public String doPart2(List<String> inputRaw) {
        Long result = inputRaw.stream()
                .map(this::readRucksack)
                .collect(groupingBy(this::getKey, Collectors.toList()))
                .values().stream()
                .map(Group::new)
                .map(Group::findBadge)
                .map(this::getPriority)
                .reduce(0L, Long::sum);

        return String.valueOf(result);
    }

    private int getKey(Rucksack rucksack) {
        return ai.getAndIncrement() / 3;
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

record Group(List<Rucksack> listRucksack) {
    char findBadge() {
        return listRucksack.stream()
                .flatMap(Rucksack::getAllItems)
                .collect(groupingBy(identity(),
                        counting()))
                .entrySet()
                .stream()
                .filter(kv -> kv.getValue() == 3)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("no solution"))
                .getKey();
    }
}

record Rucksack(String compartment1, String compartment2) {
    Stream<Character> getAllItems() {
        return Stream.concat(compartment1.chars().distinct().mapToObj(c -> (char) c), compartment2.chars().distinct().mapToObj(c -> (char) c));
    }

    char commonItem() {
        return getAllItems()
                .collect(groupingBy(identity(),
                        counting()))
                .entrySet()
                .stream()
                .filter(kv -> kv.getValue() == 2)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("no solution"))
                .getKey();
    }
}