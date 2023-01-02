package aoc2022;

import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;

import static org.assertj.core.api.Assertions.assertThat;

class Day13Test {

    @Test
    void doPart1() throws Exception {
        Day day = getDay();

        assertThat(new PacketPair(0,"[1,1,3,1,1]", "[1,1,5,1,1]").rightOrder()).isTrue();
        assertThat(new PacketPair(0,"[[1],[2,3,4]]", "[[1],4]").rightOrder()).isTrue();
        assertThat(new PacketPair(0,"[9]", "[[8,7,6]]").rightOrder()).isFalse();
        assertThat(new PacketPair(0,"[[4,4],4,4]", "[[4,4],4,4,4").rightOrder()).isTrue();
        assertThat(new PacketPair(0,"[7,7,7,7]", "[7,7,7]").rightOrder()).isFalse();
        assertThat(new PacketPair(0,"[]", "[3]").rightOrder()).isTrue();
        assertThat(new PacketPair(0,"[[[]]]", "[[]]").rightOrder()).isFalse();
        assertThat(new PacketPair(0,"[1,[2,[3,[4,[5,6,7]]]],8,9]", "[1,[2,[3,[4,[5,6,0]]]],8,9]").rightOrder()).isFalse();

        assertThat(day.doPart1(day.readInput(getInputFilename()))).isEqualTo("13");
    }

    @Test
    void doPart2() throws Exception {
        Day day = getDay();

        assertThat(day.doPart2(day.readInput(getInputFilename()))).isEqualTo("140");
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
