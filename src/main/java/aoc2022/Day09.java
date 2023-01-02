package aoc2022;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

public class Day09 extends Day {
    @Override
    public String doPart1(List<String> inputRaw) {
        List<Motion> motions = inputRaw.stream()
                .map(Motion::of)
                .toList();

        Position startPosition = new Position(0, 0);

        Rope rope = new Rope(startPosition, startPosition);
        Set<Position> tailPositions = new HashSet<>(List.of(startPosition));

        List<Direction> steps = motions.stream()
                .flatMap(motion -> Stream.generate(motion::direction)
                        .limit(motion.nrSteps()))
                .toList();

        for (Direction direction : steps) {
            rope = rope.move(direction);
            tailPositions.add(rope.tailPosition());
        }

        long result = tailPositions.size();

        return String.valueOf(result);
    }

    @Override
    public String doPart2(List<String> inputRaw) {
        List<Motion> motions = inputRaw.stream()
                .map(Motion::of)
                .toList();

        Position startPosition = new Position(0, 0);

        RopeWith10Knots rope = new RopeWith10Knots(Stream.generate(() -> startPosition)
                .limit(10)
                .toList());
        Set<Position> tailPositions = new HashSet<>((List.of(startPosition)));

        List<Direction> steps = motions.stream()
                .flatMap(motion -> Stream.generate(motion::direction)
                        .limit(motion.nrSteps()))
                .toList();

        for (Direction direction : steps) {
            rope = rope.move(direction);
            tailPositions.add(rope.getTailPosition());
        }

        long result = tailPositions.size();

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

record Position(int x, int y) {
    Position move(Direction direction) {
        return switch (direction) {
            case U -> new Position(x, y - 1);
            case D -> new Position(x, y + 1);
            case L -> new Position(x - 1, y);
            case R -> new Position(x + 1, y);
        };
    }
}

record Rope(Position headPosition, Position tailPosition) {
    Rope move(Direction direction) {
        Position newHeadPosition = headPosition.move(direction);

        // overlap before or after the move
        if (tailPosition.equals(headPosition) || tailPosition.equals(newHeadPosition)) {
            return new Rope(newHeadPosition, tailPosition);
        }

        // check if the distance is still OK (<=1)
        if (Math.abs(tailPosition.x() - newHeadPosition.x()) <= 1 &&
                Math.abs(tailPosition.y() - newHeadPosition.y()) <= 1) {
            return new Rope(newHeadPosition, tailPosition);
        }

        //  when front and back are on a straight line -> move the tail in the same direction
        if (headPosition.x() == tailPosition.x() || headPosition.y() == tailPosition.y()) {
            return new Rope(newHeadPosition, tailPosition.move(direction));
        }

        // diagonal move i.e. the new tail position is the old head position
        return new Rope(newHeadPosition, headPosition);
    }
}

record Motion(Direction direction, int nrSteps) {
    public static Motion of(String line) {
        String[] words = line.split("\\s+");
        return new Motion(Direction.valueOf(words[0]), Integer.parseInt(words[1]));
    }
}

record RopeWith10Knots(List<Position> knots) {
    RopeWith10Knots move(Direction direction) {
        List<Position> newKnots = new ArrayList<>(knots.size());

        Position oldPositionKnotBefore = knots.get(0);
        Position newPositionKnotBefore = knots.get(0).move(direction);
        newKnots.add(newPositionKnotBefore);

        Position newPositionKnotAfter;

        for (int i = 1; i < knots.size(); i++) {
            Position oldPositionKnotAfter = knots.get(i);

            // overlap before or after the move
            if (newPositionKnotBefore.equals(oldPositionKnotAfter) || oldPositionKnotBefore.equals(oldPositionKnotAfter)) {
                newKnots.add(oldPositionKnotAfter);
                newPositionKnotBefore = oldPositionKnotAfter;
            } else {
                // check if the distance is still OK (<=1)
                if (Math.abs(newPositionKnotBefore.x() - oldPositionKnotAfter.x()) <= 1 &&
                        Math.abs(newPositionKnotBefore.y() - oldPositionKnotAfter.y()) <= 1) {
                    newKnots.add(oldPositionKnotAfter);
                    newPositionKnotBefore = oldPositionKnotAfter;
                } else {
                    //  when front and back are on a straight line -> move the 2nd knot closer
                    if (newPositionKnotBefore.x() == oldPositionKnotAfter.x() || newPositionKnotBefore.y() == oldPositionKnotAfter.y()) {
                        newPositionKnotAfter = new Position((newPositionKnotBefore.x() + oldPositionKnotAfter.x()) / 2, (newPositionKnotBefore.y() + oldPositionKnotAfter.y()) / 2);
                    } else {
                        // we need a diagonal move but this is different from part 1
                        // first check if they are on a diagonal line
                        if (Math.abs(newPositionKnotBefore.x() - oldPositionKnotAfter.x()) > 1 &&
                                Math.abs(newPositionKnotBefore.y() - oldPositionKnotAfter.y()) > 1) {
                            newPositionKnotAfter = new Position((newPositionKnotBefore.x() + oldPositionKnotAfter.x()) / 2, (newPositionKnotBefore.y() + oldPositionKnotAfter.y()) / 2);
                        } else {
                            // for the axis with difference 2 we take the middle coordinate
                            // and the other axis is the coordinate of the front knot
                            if (Math.abs(newPositionKnotBefore.x() - oldPositionKnotAfter.x()) > 1) {
                                newPositionKnotAfter = new Position((newPositionKnotBefore.x() + oldPositionKnotAfter.x()) / 2, newPositionKnotBefore.y());
                            } else {
                                newPositionKnotAfter = new Position(newPositionKnotBefore.x(), (newPositionKnotBefore.y() + oldPositionKnotAfter.y()) / 2);
                            }
                        }
                    }
                    newKnots.add(newPositionKnotAfter);
                    newPositionKnotBefore = newPositionKnotAfter;
                }
            }
        }
        return new RopeWith10Knots(newKnots);
    }

    public Position getTailPosition() {
        return knots.get(knots.size() - 1);
    }
}