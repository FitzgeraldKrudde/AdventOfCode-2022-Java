package aoc2022;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.List;

public class Day10 extends Day {
    @Override
    public String doPart1(List<String> inputRaw) {
        Device device = Device.of(inputRaw);

        long result = device.getSumSignalStrengthSamples();

        return String.valueOf(result);
    }

    enum Instruction {
        noop, addx
    }

    record Statement(Instruction instruction, long value) {
        static Statement of(String line) {
            String[] words = line.split("\\s+");
            return new Statement(Instruction.valueOf(words[0]), words.length > 1 ? Long.parseLong(words[1]) : 0);
        }
    }

    @RequiredArgsConstructor
    static class Device {
        @NonNull
        private final List<Statement> statements;
        private long register = 1;
        private int cycle = 1;
        private long sumSignalStrengthSamples = 0;
        private int crtPos = 0;

        static Device of(List<String> lines) {
            return new Device(lines.stream()
                    .map(Statement::of)
                    .toList());
        }

        private long getSignalStrength() {
            return register * cycle;
        }

        void cycle() {
            if ((cycle % 20 == 0 && cycle % 40 != 0)) {
                sumSignalStrengthSamples += getSignalStrength();
            }
            cycle++;
        }

        public long getSumSignalStrengthSamples() {
            for (Statement statement : statements) {
                cycle();
                if (Instruction.addx.equals(statement.instruction)) {
                    cycle();
                    register += statement.value();
                }
            }

            return sumSignalStrengthSamples;
        }

        public String drawCRT() {
            StringBuilder sb = new StringBuilder("\n");

            for (Statement statement : statements) {
                sb.append(drawCrtPixel());
                if (Instruction.addx.equals(statement.instruction)) {
                    sb.append(drawCrtPixel());
                    register += statement.value();
                }
            }

            return sb.toString();
        }

        private String drawCrtPixel() {
            StringBuilder sb = new StringBuilder();

            if (Math.abs(crtPos - register) <= 1) {
                sb.append('#');
            } else {
                sb.append('.');
            }

            crtPos++;
            if (crtPos < 40 && crtPos % 5 == 0) {
                // for readability between the letters
                sb.append("  ");
            }

            if (crtPos == 40) {
                sb.append('\n');
                crtPos = 0;
            }

            return sb.toString();
        }
    }

    @Override
    public String doPart2(List<String> inputRaw) {
        Device device = Device.of(inputRaw);

        String result = device.drawCRT();

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
