package aoc2022;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static java.util.Comparator.comparingLong;

public class Day08 extends Day {
    @Override
    public String doPart1(List<String> inputRaw) {
        Forest forest = Forest.of(inputRaw);

        long result = forest.getNrVisibleTrees();

        return String.valueOf(result);
    }

    record Tree(int x, int y, int height) {
    }

    @Override
    public String doPart2(List<String> inputRaw) {
        Forest forest = Forest.of(inputRaw);

        long result = forest.getHighestScenicScore();

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

enum Direction {
    L, R, U, D;

    Direction reverse() {
        return switch (this) {
            case L -> R;
            case R -> L;
            case U -> D;
            case D -> U;
        };
    }
}

record Forest(List<Day08.Tree> trees, int gridLength) {
    static Forest of(List<String> input) {
        List<Day08.Tree> trees = new ArrayList<>();
        int gridLength = input.get(0).length();
        IntStream.range(0, gridLength)
                .boxed()
                .forEach(x -> IntStream.range(0, gridLength)
                        .forEach(y -> trees.add(new Day08.Tree(x, y, input.get(y).charAt(x) - 48))));

        return new Forest(trees, gridLength);
    }

    long getNrVisibleTrees() {
        return trees.parallelStream()
                .filter(this::isVisible)
                .count();
    }

    boolean isVisible(Day08.Tree tree) {
        boolean isVisibleFromTheLeft = trees.stream()
                .filter(t -> t.y() == tree.y())
                .filter(t -> t.x() < tree.x())
                .noneMatch(t -> t.height() >= tree.height());
        boolean isVisibleFromTheRight = trees.stream()
                .filter(t -> t.y() == tree.y())
                .filter(t -> t.x() > tree.x())
                .noneMatch(t -> t.height() >= tree.height());
        boolean isVisibleFromTheTop = trees.stream()
                .filter(t -> t.x() == tree.x())
                .filter(t -> t.y() < tree.y())
                .noneMatch(t -> t.height() >= tree.height());
        boolean isVisibleFromTheBottom = trees.stream()
                .filter(t -> t.x() == tree.x())
                .filter(t -> t.y() > tree.y())
                .noneMatch(t -> t.height() >= tree.height());

        return isVisibleFromTheTop || isVisibleFromTheLeft || isVisibleFromTheRight || isVisibleFromTheBottom;
    }

    Optional<Day08.Tree> getNeighbourTree(Day08.Tree tree, Direction direction) {
        return trees.stream()
                .filter(t -> t.x() == switch (direction) {
                            case U, D -> tree.x();
                            case L -> tree.x() - 1;
                            case R -> tree.x() + 1;
                        } && t.y() ==
                                switch (direction) {
                                    case U -> tree.y() - 1;
                                    case D -> tree.y() + 1;
                                    case L, R -> tree.y();
                                }
                )
                .findFirst();
    }

    long getHighestScenicScore() {
        return trees.parallelStream()
                .map(this::getScenicScore)
                .max(comparingLong(l -> l))
                .orElseThrow(() -> new RuntimeException("no solution"));
    }

    long getScenicScore(Day08.Tree tree) {
        return Arrays.stream(Direction.values())
                .map(direction -> getNrVisibleTrees(tree, direction))
                .reduce(1L, (l1, l2) -> l1 * l2);
    }

    private long getNrVisibleTrees(Day08.Tree tree, Direction direction) {
        long visibleTrees = 0;

        Optional<Day08.Tree> neighbour = getNeighbourTree(tree, direction);
        while (neighbour.isPresent()) {
            visibleTrees++;
            if (neighbour.get().height() < tree.height()) {
                neighbour = getNeighbourTree(neighbour.get(), direction);
            } else {
                neighbour = Optional.empty();
            }
        }

        return visibleTrees;
    }
}
