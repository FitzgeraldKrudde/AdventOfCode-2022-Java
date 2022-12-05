package aoc2022;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import static java.util.stream.Collectors.toList;

public class Day05 extends Day {
    @Override
    public String doPart1(List<String> inputRaw) {
        Drawing drawing = inputRaw.stream()
                .collect(Collectors.teeing(
                        Collectors.filtering(line -> line.contains("["), toList()),
                        Collectors.filtering(line -> line.startsWith("move"), Collectors.mapping(RearrangementStep::of, toList())),
                        Drawing::of
                ));

        drawing.doRearrangementProcedure();

        return drawing.getTopCrates();
    }

    record RearrangementStep(int nrCrates, int from, int to) {
        static RearrangementStep of(String line) {
            String[] words = line.trim().split("\\s+");
            return new RearrangementStep(Integer.parseInt(words[1]), Integer.parseInt(words[3]), Integer.parseInt(words[5]));
        }
    }

    record Drawing(List<Stack<String>> listCrateStacks, List<RearrangementStep> rearrangementProcedure) {
        public void doRearrangementProcedure() {
            rearrangementProcedure.forEach(this::doRearrangementStep);
        }

        public void doRearrangementProcedureMultipleCrates() {
            rearrangementProcedure.forEach(this::doRearrangementStepWithMultipleCrates);
        }

        private void doRearrangementStep(RearrangementStep step) {
            for (int i = 0; i < step.nrCrates(); i++) {
                listCrateStacks.get(step.to - 1).push(listCrateStacks.get(step.from() - 1).pop());
            }
        }

        private void doRearrangementStepWithMultipleCrates(RearrangementStep step) {
            Stack<String> fromCrateStack = listCrateStacks.get(step.from() - 1);
            Stack<String> toCrateStack = listCrateStacks.get(step.to - 1);

            ArrayList<String> crates = new ArrayList<>();
            for (int i = 0; i < step.nrCrates(); i++) {
                crates.add(fromCrateStack.pop());
            }

            Collections.reverse(crates);
            crates.forEach(toCrateStack::push);
        }


        public String getTopCrates() {
            return listCrateStacks.stream()
                    .filter(stack -> !stack.isEmpty())
                    .map(Stack::peek)
                    .collect(Collectors.joining());
        }

        static Drawing of(List<String> stackLines, List<RearrangementStep> listRearrangementSteps) {
            Collections.reverse(stackLines);
            long nrStacks = Pattern.compile("[A-Z]").matcher(stackLines.get(0)).results().count();

            List<Stack<String>> listCrateStacks = new ArrayList<>();
            LongStream.rangeClosed(1, nrStacks).forEach(value -> listCrateStacks.add(new Stack<>()));

            stackLines.stream()
                    .map(Drawing::getCratesFromInputLine)
                    .forEach(line -> addCratesToStacks(listCrateStacks, line));

            return new Drawing(listCrateStacks, listRearrangementSteps);
        }

        private static void addCratesToStacks(List<Stack<String>> listCrateStacks, String line) {
            String[] crates = line.split("");
            for (int i = 0; i < line.length(); i++) {
                String crate = crates[i];
                if (StringUtils.isNotBlank(crate)) {
                    listCrateStacks.get(i).push(crate);
                }
            }
        }

        static String getCratesFromInputLine(String s) {
            StringBuilder sb = new StringBuilder();
            for (int i = 1; i < s.length(); i += 4) {
                sb.append(s.charAt(i));
            }
            return sb.toString();
        }
    }

    @Override
    public String doPart2(List<String> inputRaw) {
        Drawing drawing = inputRaw.stream()
                .collect(Collectors.teeing(
                        Collectors.filtering(line -> line.contains("["), toList()),
                        Collectors.filtering(line -> line.startsWith("move"), Collectors.mapping(RearrangementStep::of, toList())),
                        Drawing::of
                ));

        drawing.doRearrangementProcedureMultipleCrates();

        return drawing.getTopCrates();
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
