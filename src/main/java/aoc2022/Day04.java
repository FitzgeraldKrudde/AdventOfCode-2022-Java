package aoc2022;

import java.util.List;

public class Day04 extends Day {
    @Override
    public String doPart1(List<String> inputRaw) {
        long result = inputRaw.stream()
                .map(AssignmentPair::of)
                .filter(AssignmentPair::fullyContained)
                .count();

        return String.valueOf(result);
    }

    @Override
    public String doPart2(List<String> inputRaw) {
        long result = inputRaw.stream()
                .map(AssignmentPair::of)
                .filter(AssignmentPair::partlyContained)
                .count();

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

record Assignment(int from, int to) {
    static Assignment of(String s) {
        String[] indices = s.split("-");
        return new Assignment(Integer.parseInt(indices[0]), Integer.parseInt(indices[1]));
    }
}

record AssignmentPair(Assignment assignment1, Assignment assignment2) {
    static AssignmentPair of(String line) {
        String[] assignments = line.split(",");
        return new AssignmentPair(Assignment.of(assignments[0]), Assignment.of(assignments[1]));
    }

    boolean fullyContained() {
        if (assignment1.from() >= assignment2.from() && assignment1.to() <= assignment2.to()) {
            return true;
        }
        return assignment2.from() >= assignment1.from() && assignment2.to() <= assignment1.to();
    }
    boolean partlyContained() {
        if (assignment1.from() >= assignment2.from() && assignment1.from() <= assignment2.to()) {
            return true;
        }
        if (assignment1.to() >= assignment2.from() && assignment1.to() <= assignment2.to()) {
            return true;
        }

        if (assignment2.from() >= assignment1.from() && assignment2.from() <= assignment1.to()) {
            return true;
        }
        return assignment2.to() >= assignment1.from() && assignment2.to() <= assignment1.to();
    }

}