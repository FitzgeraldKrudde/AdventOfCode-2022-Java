package aoc2022;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

enum MonkeyOperation {
    PLUS, MIN, MULT, DIV;

    static MonkeyOperation of(String operation) {
        return switch (operation) {
            case "+" -> PLUS;
            case "-" -> MIN;
            case "*" -> MULT;
            case "/" -> DIV;
            default -> throw new IllegalStateException("Unexpected value: " + operation);
        };
    }
}

record ReverseResult(String monkeyName, long value) {
}

class MathMonkey implements Serializable {
    @Getter
    private final String name;
    @Getter
    @Setter
    boolean hasResult;
    @Getter
    @Setter
    long result;
    @Getter
    private String operationNameMonkey1;
    @Getter
    private boolean gotOperationValue1;
    @Getter
    @Setter
    private long operationValue1;
    private MonkeyOperation operation;
    @Getter
    private String operationNameMonkey2;
    @Getter
    private boolean gotOperationValue2;
    @Getter
    @Setter
    private long operationValue2;

    static public MathMonkey of(String line) {
        String[] split = line.split(":");
        String name = split[0];
        String[] operands = split[1].trim().split("\\s+");

        if (operands.length == 1) {
            return new MathMonkey(name, Long.parseLong(operands[0]));
        } else {
            return new MathMonkey(name, operands[0], MonkeyOperation.of(operands[1]), operands[2]);
        }
    }

    public MathMonkey(String name, long result) {
        this.name = name;
        hasResult = true;
        this.result = result;
    }

    public MathMonkey(String name, String operationNameMonkey1, MonkeyOperation operation, String operationNameMonkey2) {
        this.name = name;
        this.operation = operation;
        this.operationNameMonkey1 = operationNameMonkey1;
        this.operationNameMonkey2 = operationNameMonkey2;
    }

    @Override
    public String toString() {
        return "MathMonkey{" +
                "name='" + name + '\'' +
                ", hasResult=" + hasResult +
                ", result=" + result +
                ", operationNameMonkey1='" + operationNameMonkey1 + '\'' +
                ", gotOperationValue1=" + gotOperationValue1 +
                ", operationValue1=" + operationValue1 +
                ", operation=" + operation +
                ", operationNameMonkey2='" + operationNameMonkey2 + '\'' +
                ", gotOperationValue2=" + gotOperationValue2 +
                ", operationValue2=" + operationValue2 +
                '}';
    }

    void processMonkeyShout(String monkeyName, long value) {
        if (hasResult) {
            return;
        }

        if (operationNameMonkey1.equals(monkeyName)) {
            operationValue1 = value;
            gotOperationValue1 = true;
        }
        if (operationNameMonkey2.equals(monkeyName)) {
            operationValue2 = value;
            gotOperationValue2 = true;
        }
        if (gotOperationValue1 && gotOperationValue2) {
            result = switch (operation) {
                case PLUS -> operationValue1 + operationValue2;
                case MIN -> operationValue1 - operationValue2;
                case MULT -> operationValue1 * operationValue2;
                case DIV -> operationValue1 / operationValue2;
            };
            hasResult = true;
        }
    }

    ReverseResult processResult(long value) {
        result = value;

        if (gotOperationValue1) {
            return new ReverseResult(getOperationNameMonkey2(),
                    switch (operation) {
                        case PLUS -> result - operationValue1;
                        case MIN -> operationValue1 - result; // fooled me..
                        case MULT -> result / operationValue1;
                        case DIV -> result * operationValue1;
                    }
            );
        }
        if (gotOperationValue2) {
            return new ReverseResult(getOperationNameMonkey1(),
                    switch (operation) {
                        case PLUS -> result - operationValue2;
                        case MIN -> result + operationValue2;
                        case MULT -> result / operationValue2;
                        case DIV -> result * operationValue2;
                    }
            );
        }
        throw new RuntimeException("cannot process result");
    }
}

record Riddle(List<MathMonkey> monkeys) {
    long getRootShout() {
        MathMonkey root = monkeys.stream()
                .filter(mathMonkey -> mathMonkey.getName().equals("root"))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("no root"));

        while (!root.isHasResult()) {
            List<MathMonkey> shoutedMonkeys = monkeys.stream()
                    .filter(MathMonkey::isHasResult)
                    .toList();

            shoutedMonkeys.forEach(shoutedMonkey -> monkeys
                    .forEach(mathMonkey -> mathMonkey.processMonkeyShout(shoutedMonkey.getName(), shoutedMonkey.getResult())));

            monkeys.removeIf(shoutedMonkeys::contains);
        }

        return root.getResult();
    }

    public long getHumanShoutValue() {
        // do the shouting and we will have partial result for root
        // then use the result to solve "from root to human"
        MathMonkey root = monkeys.stream()
                .filter(mathMonkey -> mathMonkey.getName().equals("root"))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("no root"));

        // remove the human
        monkeys.removeIf(mathMonkey -> mathMonkey.getName().equals("humn"));

        long nrShoutingMonkeys = monkeys.stream()
                .filter(MathMonkey::isHasResult)
                .count();
        while (nrShoutingMonkeys > 0) {
            List<MathMonkey> shoutedMonkeys = monkeys.stream()
                    .filter(MathMonkey::isHasResult)
                    .toList();

            shoutedMonkeys.forEach(shoutedMonkey -> monkeys
                    .forEach(mathMonkey -> mathMonkey.processMonkeyShout(shoutedMonkey.getName(), shoutedMonkey.getResult())));

            monkeys.removeIf(shoutedMonkeys::contains);
            nrShoutingMonkeys = shoutedMonkeys.size();
        }

        // now root should have a value for operand 1 or operand2
        assert root.isGotOperationValue1() || root.isGotOperationValue2();

        // use a small DTO with the monkey name (which has a result value) and the actual value
        ReverseResult reverseResult;
        if (root.isGotOperationValue1()) {
            reverseResult = new ReverseResult(root.getOperationNameMonkey2(), root.getOperationValue1());
        } else {
            reverseResult = new ReverseResult(root.getOperationNameMonkey1(), root.getOperationValue2());
        }
        monkeys.removeIf(mathMonkey -> mathMonkey.getName().equals("root"));

        // keep substituting
        while (monkeys.size() > 1) {
            String monkeyNameWithResult = reverseResult.monkeyName();
            MathMonkey newMonkeyWithResult = monkeys.stream()
                    .filter(mathMonkey -> mathMonkey.getName().equals(monkeyNameWithResult))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("no reverse shouting monkey"));

            reverseResult = newMonkeyWithResult.processResult(reverseResult.value());
            monkeys.removeIf(mathMonkey -> mathMonkey.getName().equals(monkeyNameWithResult));
        }
        return monkeys.get(0).processResult(reverseResult.value()).value();
    }
}

public class Day21 extends Day {
    @Override
    public String doPart1(List<String> inputRaw) {
        Riddle riddle = new Riddle(inputRaw.stream()
                .map(MathMonkey::of)
                .collect(Collectors.toList()));

        long result = riddle.getRootShout();

        return String.valueOf(result);
    }

    @Override
    public String doPart2(List<String> inputRaw) {
        Riddle riddle = new Riddle(inputRaw.stream()
                .map(MathMonkey::of)
                .collect(Collectors.toList()));

        long result = riddle.getHumanShoutValue();

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
