package aoc2022;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Comparator.comparingInt;
import static java.util.stream.IntStream.rangeClosed;

record Cube3D(int x, int y, int z) {
    static Cube3D of(String line) {
        String[] coordinates = line.split(",");
        return new Cube3D(Integer.parseInt(coordinates[0]), Integer.parseInt(coordinates[1]), Integer.parseInt(coordinates[2]));
    }

    boolean isAdjacent(Cube3D otherCube) {
        int xDistance = x - otherCube.x();
        int yDistance = y - otherCube.y();
        int zDistance = z - otherCube.z();

        return
                (xDistance == 0 && yDistance == 0 && Math.abs(zDistance) == 1) ||
                        (xDistance == 0 && Math.abs(yDistance) == 1 && zDistance == 0) ||
                        (Math.abs(xDistance) == 1 && yDistance == 0 && zDistance == 0);
    }

    Stream<Cube3D> adjacentCubes() {
        return Stream.of(new Cube3D(x, y, z + 1)
                , new Cube3D(x, y, z - 1)
                , new Cube3D(x, y + 1, z)
                , new Cube3D(x, y - 1, z)
                , new Cube3D(x + 1, y, z)
                , new Cube3D(x - 1, y, z));
    }
}

record Scan(List<Cube3D> cube3DList) {
    int surfaceArea() {
        List<Cube3D> adjacents = cube3DList.stream()
                .flatMap(cube3D -> cube3DList.stream()
                        .filter(cube3DOther -> !cube3DOther.equals(cube3D))
                        .filter(cube3D::isAdjacent)
                        .flatMap(cube3DOther -> Stream.of(cube3D)))
                .toList();

        return 6 * cube3DList.size() - adjacents.size();
    }

    int exteriorSurfaceArea() {
        // create "box" around object and find all cubes where air can flow to
        // other cubes are then trapped
        int minX = cube3DList.stream().min(comparingInt(Cube3D::x)).orElseThrow(() -> new RuntimeException("no solution")).x() - 1;
        int maxX = cube3DList.stream().max(comparingInt(Cube3D::x)).orElseThrow(() -> new RuntimeException("no solution")).x() + 1;
        int minY = cube3DList.stream().min(comparingInt(Cube3D::y)).orElseThrow(() -> new RuntimeException("no solution")).y() - 1;
        int maxY = cube3DList.stream().max(comparingInt(Cube3D::y)).orElseThrow(() -> new RuntimeException("no solution")).y() + 1;
        int minZ = cube3DList.stream().min(comparingInt(Cube3D::z)).orElseThrow(() -> new RuntimeException("no solution")).z() - 1;
        int maxZ = cube3DList.stream().max(comparingInt(Cube3D::z)).orElseThrow(() -> new RuntimeException("no solution")).z() + 1;

        // create cubes for all the 6 edges of the "box"
        // this should be simpler..
        List<Cube3D> airCubesZAxis = rangeClosed(minX, maxX)
                .boxed()
                .flatMap(x -> rangeClosed(minY, maxY)
                        .boxed()
                        .flatMap(y -> Stream.of(new Cube3D(x, y, minZ), new Cube3D(x, y, maxZ)))
                )
                .distinct()
                .collect(Collectors.toList());
        List<Cube3D> airCubesYAxis = rangeClosed(minX, maxX)
                .boxed()
                .flatMap(x -> rangeClosed(minZ, maxZ)
                        .boxed()
                        .flatMap(z -> Stream.of(new Cube3D(x, minY, z), new Cube3D(x, maxY, z)))
                )
                .distinct()
                .toList();
        List<Cube3D> airCubesXAxis = rangeClosed(minY, maxY)
                .boxed()
                .flatMap(y -> rangeClosed(minZ, maxZ)
                        .boxed()
                        .flatMap(z -> Stream.of(new Cube3D(minX, y, z), new Cube3D(maxX, y, z)))
                )
                .distinct()
                .toList();

        List<Cube3D> airCubes = Stream.of(airCubesZAxis, airCubesYAxis, airCubesXAxis)
                .flatMap(Collection::stream)
                .distinct()
                .filter(cube3D -> !cube3DList.contains(cube3D))
                .toList();

        int nrAirCubes = airCubes.size();
        List<Cube3D> expandedAirCubes = getExpandedAirCubes(airCubes, minX, maxX, minY, maxY, minZ, maxZ);

        while (expandedAirCubes.size() > nrAirCubes) {
            nrAirCubes = expandedAirCubes.size();
            expandedAirCubes = getExpandedAirCubes(expandedAirCubes, minX, maxX, minY, maxY, minZ, maxZ);
        }

        List<Cube3D> finalExpandedAirCubes = expandedAirCubes;
        List<Cube3D> trappedCubes = rangeClosed(minX, maxX)
                .boxed()
                .flatMap(x -> rangeClosed(minY, maxY)
                        .boxed()
                        .flatMap(y -> rangeClosed(minZ, maxZ).mapToObj(z -> new Cube3D(x, y, z)))
                )
                .filter(cube3D -> !cube3DList.contains(cube3D))
                .filter(cube3D -> !finalExpandedAirCubes.contains(cube3D))
                .toList();

        return surfaceArea() - new Scan(trappedCubes).surfaceArea();
    }

    private List<Cube3D> getExpandedAirCubes(List<Cube3D> expandedAirCubes, int minX, int maxX, int minY, int maxY, int minZ, int maxZ) {
        return expandedAirCubes.stream()
                .flatMap(Cube3D::adjacentCubes)
                .filter(cube3D -> cube3D.x() >= minX && cube3D.x() <= maxX && cube3D.y() >= minY && cube3D.y() <= maxY && cube3D.z() >= minZ && cube3D.z() <= maxZ)
                .filter(cube3D -> !cube3DList.contains(cube3D))
                .distinct()
                .toList();
    }

    static Scan of(List<String> input) {
        return new Scan(input.stream()
                .map(Cube3D::of)
                .toList());
    }
}

public class Day18 extends Day {
    @Override
    public String doPart1(List<String> inputRaw) {
        Scan scan = Scan.of(inputRaw);

        long result = scan.surfaceArea();

        return String.valueOf(result);
    }

    @Override
    public String doPart2(List<String> inputRaw) {
        Scan scan = Scan.of(inputRaw);

        long result = scan.exteriorSurfaceArea();

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
