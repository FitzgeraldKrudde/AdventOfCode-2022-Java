package aoc2022;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.Comparator.comparingInt;
import static java.util.stream.Collectors.toMap;

public class Day12 extends Day {
    @Override
    public String doPart1(List<String> inputRaw) {
        HeightMap heightMap = HeightMap.of(inputRaw);

        long result = heightMap.nrStepsShortestPath();

        return String.valueOf(result);
    }

    @Override
    public String doPart2(List<String> inputRaw) {
        HeightMap heightMap = HeightMap.of(inputRaw);

        long result = heightMap.nrStepsShortestPathFromLowestLevel();

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

record Point(int x, int y) {
    List<Point> getNeighbours() {
        return List.of(new Point(x + 1, y), new Point(x - 1, y), new Point(x, y - 1), new Point(x, y + 1));
    }
    Point move(Direction direction){
        return switch (direction){
            case L->new Point(x - 1, y);
            case R->new Point(x + 1, y);
            case U->new Point(x, y - 1);
            case D->new Point(x, y + 1);
        };
    }
}

record Square(Point point, char height) {
}

record HeightMap(List<Square> squares, Square start, Square end) {
    private final static int UNREACHABLE = 999999;

    public int nrStepsShortestPath() {
        return calculateShortestPath(start, end);
    }

    public int nrStepsShortestPathFromLowestLevel() {
        return squares.parallelStream()
                .filter(square -> square.height() == 'a')
                .map(square -> calculateShortestPath(square, end))
                .min(comparingInt(Integer::valueOf))
                .orElseThrow(() -> new RuntimeException(" no solution"));
    }

    private int calculateShortestPath(Square src, Square dst) {
        return calculateDistanceMap(src).get(dst);
    }

    private Map<Square, Integer> calculateDistanceMap(Square src) {
        // use the Dijkstra algorithm

        // create a set with unvisited squares
        Set<Square> unvisitedSquares = new HashSet<>(squares);

        // Map with squares and (minimum) distance, initially unreachable
        Map<Square, Integer> mapSquareWithDistance = unvisitedSquares.stream()
                .collect(toMap(square -> square, square -> UNREACHABLE));

        // set the distance for the source to 0
        mapSquareWithDistance.put(src, 0);

        // start with source
        Square currentSquare = src;

        while (!unvisitedSquares.isEmpty()) {
            List<Square> reachableNeighbours = getReachableNeighbours(currentSquare).stream()
                    .filter(unvisitedSquares::contains)
                    .toList();

            // update the distance to these neighbours if closer through this node
            int currentDistanceToNeighbour = mapSquareWithDistance.get(currentSquare) + 1;
            reachableNeighbours.forEach(reachableNeighbour -> {
                if (currentDistanceToNeighbour < mapSquareWithDistance.get(reachableNeighbour)) {
                    mapSquareWithDistance.put(reachableNeighbour, currentDistanceToNeighbour);
                }
            });

            // remove current point from unvisited set
            unvisitedSquares.remove(currentSquare);

            // set next best point
            currentSquare = unvisitedSquares.stream()
                    .min(comparingInt(mapSquareWithDistance::get))
                    .orElse(null);
        }

        return mapSquareWithDistance;
    }

    static HeightMap of(List<String> input) {
        List<Square> squareList = IntStream.range(0, input.get(0).length())
                .boxed()
                .flatMap(x -> IntStream.range(0, input.size())
                        .mapToObj(y -> new Square(new Point(x, y), input.get(y).charAt(x))))
                .collect(Collectors.toList());

        // fix the height of the start ('S' -> 'a') and the end: 'E' -> 'z'
        Square start = getStart(squareList);
        Square end = getEnd(squareList);
        squareList.removeIf(square -> square.equals(start) || square.equals(end));

        Square newStart = new Square(start.point(), 'a');
        squareList.add(newStart);
        Square newEnd = new Square(end.point(), 'z');
        squareList.add(newEnd);

        return new HeightMap(squareList, newStart, newEnd);
    }

    List<Square> getNeighbours(Square square) {
        List<Point> neighbourPoints = square.point().getNeighbours();

        return squares.stream()
                .filter(potentialNeighbour -> neighbourPoints.contains(potentialNeighbour.point()))
                .toList();
    }

    List<Square> getReachableNeighbours(Square square) {
        return getNeighbours(square)
                .stream()
                .filter(squareTo -> isReachable(square, squareTo))
                .toList();
    }

    private boolean isReachable(Square squareFrom, Square squareTo) {
        if (squareFrom.equals(start)) {
            return true;
        }
        if (squareTo.equals(start)) {
            return false;
        }
        return squareTo.height() - squareFrom.height() < 2;
    }

    static Square getStart(List<Square> squares) {
        return squares.stream()
                .filter(square -> square.height() == 'S')
                .findFirst()
                .orElseThrow(() -> new RuntimeException("start not found"));
    }

    static Square getEnd(List<Square> squares) {
        return squares.stream()
                .filter(square -> square.height() == 'E')
                .findFirst()
                .orElseThrow(() -> new RuntimeException("end not found"));
    }

}
