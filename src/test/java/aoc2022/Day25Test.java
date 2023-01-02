package aoc2022;

import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;

import static org.assertj.core.api.Assertions.assertThat;

class Day25Test {

    @Test
    void doPart1() throws Exception {
        Day day = getDay();

        assertThat(new SnafuNumber("1").add(new SnafuNumber("2"))).isEqualTo(new SnafuNumber("1="));
        assertThat(new SnafuNumber("1=").add(new SnafuNumber("1="))).isEqualTo(new SnafuNumber("11"));
        assertThat(new SnafuNumber("10").add(new SnafuNumber("10"))).isEqualTo(new SnafuNumber("20"));
        assertThat(new SnafuNumber("20").add(new SnafuNumber("20"))).isEqualTo(new SnafuNumber("1-0"));

        assertThat(day.doPart1(day.readInput(getInputFilename()))).isEqualTo("2=-1=0");
    }

    @Test
    void doPart2() throws Exception {
        Day day = getDay();

        assertThat(day.doPart2(day.readInput(getInputFilename()))).isEqualTo("0");
    }

    // @formatter:off
    private String getInputFilename() {
        // get our class
        final Class<?> clazz = new Object() {
        }.getClass().getEnclosingClass();

        // construct filename with input
        return clazz.getSimpleName().toLowerCase().replace("test", "").replace("day0", "day") + ".txt";
        // @formatter:on
    }

    private Day getDay() throws NoSuchMethodException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException {
        // get our Test class
        final Class<?> clazz = new Object() {
        }.getClass().getEnclosingClass();

        // get the classname of the class under test
        final String fullClassName = clazz.getCanonicalName().replace("Test", "");

        // create instance
        return (Day) Class.forName(fullClassName).getDeclaredConstructor().newInstance();
    }
    // @formatter:on
}
