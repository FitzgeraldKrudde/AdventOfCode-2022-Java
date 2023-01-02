package aoc2022;

import java.util.List;

public class Day02 extends Day {
    @Override
    public String doPart1(List<String> inputRaw) {
        long result = inputRaw.stream()
                .map(line -> new PairShape(line.charAt(0), line.charAt(2)))
                .map(PairShape::getScore)
                .reduce(0L, Long::sum);

        return String.valueOf(result);
    }

    @Override
    public String doPart2(List<String> inputRaw) {
        long result = inputRaw.stream()
                .map(line -> new ShapeResult(line.charAt(0), line.charAt(2)))
                .map(ShapeResult::chooseMyShape)
                .map(PairShape::getScore)
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

record PairShape(char shape1, char shape2) {
    long getScore() {
        int shapeValue = shape2 - 'W';

        if (shape1 == (shape2 - 23)) {
            return shapeValue + 3;
        }

        return shapeValue + switch (shape2) {
            case 'X' -> shape1 == 'C' ? 6 : 0;
            case 'Y' -> shape1 == 'A' ? 6 : 0;
            case 'Z' -> shape1 == 'B' ? 6 : 0;
            default -> throw new RuntimeException("unknown shape");
        };
    }
}

record ShapeResult(char shape1, char result) {
    PairShape chooseMyShape() {
        char myShape = switch (result) {
            case 'X' -> switch (shape1) {
                case 'A' -> 'Z';
                case 'B' -> 'X';
                case 'C' -> 'Y';
                default -> throw new RuntimeException("unknown shape");
            };
            case 'Y' -> switch (shape1) {
                case 'A' -> 'X';
                case 'B' -> 'Y';
                case 'C' -> 'Z';
                default -> throw new RuntimeException("unknown shape");
            };
            case 'Z' -> switch (shape1) {
                case 'A' -> 'Y';
                case 'B' -> 'Z';
                case 'C' -> 'X';
                default -> throw new RuntimeException("unknown shape");
            };
            default -> throw new RuntimeException("unknown shape");
        };

        return new PairShape(shape1, myShape);
    }
}