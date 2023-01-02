package aoc2022;

import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

public class Day11 extends Day {
    @Override
    public String doPart1(List<String> inputRaw) {
        KeepAwayGame keepAwayGame = KeepAwayGame.of(inputRaw);

        IntStream.range(0, 20).forEach(l -> keepAwayGame.doRound(3));

        long result = keepAwayGame.getMonkeyBusiness();

        return String.valueOf(result);
    }

    @Override
    public String doPart2(List<String> inputRaw) {
        KeepAwayGame keepAwayGame = KeepAwayGame.of(inputRaw);

        IntStream.range(0, 10000).forEach(l -> keepAwayGame.doRound(1));

        long result = keepAwayGame.getMonkeyBusiness();

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

@Builder
@Getter
class Monkey {
    private List<Long> items;
    private BiFunction<Long, Long, Long> operation;
    private long operationValue;
    private Function<Long, Integer> targetMonkeyFunction;
    private long divisor;
    private long inspections;

    void addInspections(long nrOfInspections) {
        inspections += nrOfInspections;
    }
}

record KeepAwayGame(List<Monkey> monkeys, long lcd) {
    public static KeepAwayGame of(List<String> lines) {
        List<Monkey> monkeys = new ArrayList<>();
        int nrMonkeys = (lines.size() + 1) / 7;
        for (int i = 0; i < nrMonkeys; i++) {
            monkeys.add(Monkey.builder()
                    .items(Arrays.stream(lines.get(7 * i + 1).split(":")[1].split(","))
                            .map(String::trim)
                            .map(Long::valueOf)
                            .collect(toList()))
                    .operation(createOperation(lines.get(7 * i + 2)))
                    .operationValue(getOperationValue((lines.get(7 * i + 2))))
                    .divisor(createDivisor(lines.get(7 * i + 3)))
                    .targetMonkeyFunction(createTargetMonkeyFunction(
                            lines.get(7 * i + 3),
                            lines.get(7 * i + 4),
                            lines.get(7 * i + 5)))
                    .build());
        }

        long lcd = monkeys.stream().map(Monkey::getDivisor).reduce(1L, (l1, l2) -> l1 * l2);

        return new KeepAwayGame(monkeys, lcd);
    }

    private static long createDivisor(String line) {
        return Long.parseLong(line.trim().split("\\s+")[3]);
    }

    private static long getOperationValue(String line) {
        String value = line.trim().split("\\s+")[5];
        return value.equals("old") ? 0 : Long.parseLong(value);
    }

    private static Function<Long, Integer> createTargetMonkeyFunction(String linePredicate, String lineTrue, String lineFalse) {
        Predicate<Long> testPredicate = createTestPredicate(linePredicate);
        int monkeyWhenTrue = Integer.parseInt(lineTrue.trim().split("\\s+")[5]);
        int monkeyWhenFalse = Integer.parseInt(lineFalse.trim().split("\\s+")[5]);

        return worryValue -> testPredicate.test(worryValue) ? monkeyWhenTrue : monkeyWhenFalse;
    }

    private static Predicate<Long> createTestPredicate(String line) {
        long testValue = Long.parseLong(line.trim().split("\\s+")[3]);
        return l1 -> l1 % testValue == 0;
    }

    private static BiFunction<Long, Long, Long> createOperation(String line) {
        String[] operands = line.trim().split("\\s+");
        return switch (operands[4]) {
            case "*" -> operands[5].equals("old") ? (l1, l2) -> l1 * l1 : (l1, l2) -> l1 * l2;
            case "+" -> operands[5].equals("old") ? (l1, l2) -> l1 + l1 : Long::sum;
            default -> throw new IllegalStateException("Unexpected value: " + operands[4]);
        };
    }

    void doRound(int worryLevelDivider) {
        monkeys.forEach(monkey -> takeTurn(monkey, worryLevelDivider));
    }

    public void takeTurn(Monkey monkey, int worryLevelDivider) {
        monkey.getItems().stream()
                .map(item -> monkey.getOperation().apply(item, monkey.getOperationValue()))
                .map(worryLevel -> worryLevel / worryLevelDivider)
                .forEach(worryLevel -> throwToMonkey(monkey, worryLevel));

        monkey.addInspections(monkey.getItems().size());
        monkey.getItems().clear();
    }

    private void throwToMonkey(Monkey monkeyFrom, long worryLevel) {
        Monkey targetMonkey = monkeys.get(monkeyFrom.getTargetMonkeyFunction().apply(worryLevel));
        worryLevel %= lcd;
        targetMonkey.getItems().add(worryLevel);
    }

    long getMonkeyBusiness() {
        return monkeys.stream()
                .map(Monkey::getInspections)
                .sorted(Comparator.reverseOrder())
                .limit(2)
                .reduce(1L, (l1, l2) -> l1 * l2);
    }
}
