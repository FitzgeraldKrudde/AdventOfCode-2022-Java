package aoc2022;

import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class Day06Test {

    @Test
    void doPart1() throws Exception {
        Day day = getDay();

        assertThat(day.doPart1(List.of(new String[]{"bvwbjplbgvbhsrlpgdmjqwftvncz"}))).isEqualTo("5");
        assertThat(day.doPart1(List.of(new String[]{"nppdvjthqldpwncqszvftbrmjlhg"}))).isEqualTo("6");
        assertThat(day.doPart1(List.of(new String[]{"nznrnfrfntjfmvfwmzdfjlvtqnbhcprsg"}))).isEqualTo("10");
        assertThat(day.doPart1(List.of(new String[]{"zcfzfwzzqfrljwzlrfnpqdbhtmscgvjw"}))).isEqualTo("11");
    }

    @Test
    void doPart2() throws Exception {
        Day day = getDay();

        assertThat(day.doPart2(List.of(new String[]{"mjqjpqmgbljsphdztnvjfqwrcgsmlb"}))).isEqualTo("19");
        assertThat(day.doPart2(List.of(new String[]{"bvwbjplbgvbhsrlpgdmjqwftvncz"}))).isEqualTo("23");
        assertThat(day.doPart2(List.of(new String[]{"nppdvjthqldpwncqszvftbrmjlhg"}))).isEqualTo("23");
        assertThat(day.doPart2(List.of(new String[]{"nznrnfrfntjfmvfwmzdfjlvtqnbhcprsg"}))).isEqualTo("29");
        assertThat(day.doPart2(List.of(new String[]{"zcfzfwzzqfrljwzlrfnpqdbhtmscgvjw"}))).isEqualTo("26");
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
