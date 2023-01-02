package aoc2022;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.stream.Collectors;

import static java.util.Comparator.comparingInt;
import static java.util.stream.Collectors.toList;

enum Turn {
    L, R;

    static Turn of(String s) {
        return valueOf(s);
    }
}

enum Tile {
    WALL, OPEN;

    static Tile of(char c) {
        return switch (c) {
            case '#' -> WALL;
            case '.' -> OPEN;
            default -> throw new IllegalStateException("Unexpected value: " + c);
        };
    }

}

record BoardPosition(Point point, Direction direction) {
}

record Board(Map<Point, Tile> map, List<PathInstruction> pathInstructions) {
    static Board of(List<String> mapLines, String path) {
        Map<Point, Tile> map = new HashMap<>();
        int maxX = mapLines.stream()
                .map(String::length)
                .max(comparingInt(value -> value))
                .orElseThrow(() -> new RuntimeException("no lines"));
        for (int y = 0; y < mapLines.size(); y++) {
            for (int x = 0; x < maxX; x++) {
                if (mapLines.get(y).length() > x && mapLines.get(y).charAt(x) != ' ') {
                    map.put(new Point(x, y), Tile.of(mapLines.get(y).charAt(x)));
                }
            }
        }

        StringTokenizer instructions = new StringTokenizer(path, "[LR]", true);
        List<PathInstruction> pathInstructions = Collections.list(instructions).stream()
                .map(token -> (String) token)
                .map(PathInstruction::of)
                .toList();

        return new Board(map, pathInstructions);
    }

    long password() {
        BoardPosition currentPosition = new BoardPosition(topLeftEmptyPosition(), Direction.R);

        for (PathInstruction pathInstruction :
                pathInstructions) {
            currentPosition = pathInstruction.move(this, currentPosition);
        }

        return 4L * (currentPosition.point().x() + 1) + 1000L * (currentPosition.point().y() + 1) +
                switch (currentPosition.direction()) {
                    case R -> 0;
                    case D -> 1;
                    case L -> 2;
                    case U -> 3;
                };
    }

    private Point topLeftEmptyPosition() {
        return map().keySet().stream()
                .filter(point -> point.y() == 0)
                .min(comparingInt(Point::x))
                .orElseThrow(() -> new RuntimeException("no starting point"));
    }

    public BoardPosition move(BoardPosition currentPosition) {
        Point newPoint = currentPosition.point().move(currentPosition.direction());
        if (!map.containsKey(newPoint)) {
            return wrapOrStay(currentPosition);
        }
        if (map.get(newPoint).equals(Tile.WALL)) {
            return currentPosition;
        }
        if (map.get(newPoint).equals(Tile.OPEN)) {
            return new BoardPosition(newPoint, currentPosition.direction());
        }
        throw new RuntimeException("move error");
    }

    BoardPosition wrapOrStay(BoardPosition currentPosition) {
        // move in reverse opposite direction, find last position of this part of the structure
        Point currentPoint = currentPosition.point();
        Direction reverseDirection = currentPosition.direction().reverse();
        Point lastPoint = currentPoint.move(reverseDirection);
        while (map.containsKey(lastPoint) && (map.get(lastPoint).equals(Tile.WALL) || map.get(lastPoint).equals(Tile.OPEN))) {
            lastPoint = lastPoint.move(reverseDirection);
        }

        // we always move 1 too far
        lastPoint = lastPoint.move(currentPosition.direction());

        // check the last position is not a wall
        if (map.get(lastPoint).equals(Tile.WALL)) {
            return currentPosition;
        } else {
            return new BoardPosition(lastPoint, currentPosition.direction());
        }
    }
}

interface PathInstruction {
    static PathInstruction of(String s) {
        return switch (s) {
            case "R", "L" -> new TurnInstruction(Turn.of(s));
            default -> new Moves(Integer.parseInt(s));
        };
    }

    BoardPosition move(Board board, BoardPosition position);
}

record TurnInstruction(Turn turn) implements PathInstruction {
    @Override
    public BoardPosition move(Board board, BoardPosition position) {
        return switch (turn) {
            case L -> new BoardPosition(position.point(), switch (position.direction()) {
                case L -> Direction.D;
                case R -> Direction.U;
                case D -> Direction.R;
                case U -> Direction.L;
            });
            case R -> new BoardPosition(position.point(), switch (position.direction()) {
                case L -> Direction.U;
                case R -> Direction.D;
                case D -> Direction.L;
                case U -> Direction.R;
            });
        };
    }
}

record Moves(int nrMoves) implements PathInstruction {
    @Override
    public BoardPosition move(Board board, BoardPosition position) {
        for (int i = 0; i < nrMoves; i++) {
            position = board.move(position);
        }

        return position;
    }
}

public class Day22 extends Day {
    @Override
    public String doPart1(List<String> inputRaw) {
        Board board = inputRaw.stream()
                .collect(Collectors.teeing(
                        Collectors.filtering(line -> line.contains(".") || line.contains("#"), toList()),
                        Collectors.filtering(line -> line.contains("R"), Collectors.joining()),
                        Board::of
                ));

        long result = board.password();

        return String.valueOf(result);
    }

    @Override
    public String doPart2(List<String> inputRaw) {

        long result = 0;

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
