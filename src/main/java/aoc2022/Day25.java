package aoc2022;

import java.util.Arrays;
import java.util.List;

import static java.lang.Integer.*;

record SnafuDigit(char snafu) {
    static final List<Character> SNAFU_DIGITS = Arrays.asList('=', '-', '0', '1', '2');

    long valueOf() {
        return SNAFU_DIGITS.indexOf(snafu) - 2;
    }

    static char of(int n) {
        return SNAFU_DIGITS.get(n + 2);
    }

}

record SnafuNumber(String snafuNumber) {
    SnafuNumber add(SnafuNumber sn2) {
        String snafuNumber2 = sn2.snafuNumber();
        StringBuilder result = new StringBuilder();
        int carry = 0;

        for (int i = 0; i < Math.max(snafuNumber.length(), snafuNumber2.length()); i++) {
            if (snafuNumber.length() > i) {
                carry += new SnafuDigit(snafuNumber.charAt(snafuNumber.length() - 1 - i)).valueOf();
            }
            if (snafuNumber2.length() > i) {
                carry += new SnafuDigit(snafuNumber2.charAt(snafuNumber2.length() - 1 - i)).valueOf();
            }

            switch (valueOf(carry)) {
                case Integer n when n > 2 -> {
                    result.append(SnafuDigit.of(carry - 5));
                    carry = 1;
                }
                case Integer n when n < -2 -> {
                    result.append(SnafuDigit.of(carry + 5));
                    carry = -1;
                }
                default -> {
                    result.append(SnafuDigit.of(carry));
                    carry = 0;
                }
            }
        }
        if (carry != 0) {
            result.append(carry);
        }

        return new SnafuNumber(result.reverse().toString());
    }
}

public class Day25 extends Day {
    @Override
    public String doPart1(List<String> inputRaw) {
        SnafuNumber sum = inputRaw.stream()
                .map(SnafuNumber::new)
                .reduce(new SnafuNumber("0"), SnafuNumber::add);

        return sum.snafuNumber();
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
