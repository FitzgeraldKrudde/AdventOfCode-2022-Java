package aoc2022;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

record PacketPair(int index, String list1, String list2) {
    boolean rightOrder() {
        return compare(list1, list2) < 0;
    }

    int order() {
        return compare(list1, list2);
    }


    int compare(String list1, String list2) {
        // check if both lists are empty
        if (list1.equals("[]") && list2.equals("[]")) {
            return 0;
        }

        // check if one of the lists is empty
        if (list1.equals("[]")) {
            return -1;
        }
        if (list2.equals("[]")) {
            return 1;
        }

        // both are numbers
        if (StringUtils.isNumeric(list1) && StringUtils.isNumeric(list2)) {
            return Integer.compare(Integer.parseInt(list1), Integer.parseInt(list2));
        }

        // only first is a number
        if (StringUtils.isNumeric(list1) && !StringUtils.isNumeric(list2)) {
            return compare("[" + list1 + "]", list2);
        }

        // only second is a number
        if (!StringUtils.isNumeric(list1) && StringUtils.isNumeric(list2)) {
            return compare(list1, "[" + list2 + "]");
        }

        // both are lists, check all elements
        List<String> elements1 = split(list1.substring(1, list1.length() - 1));
        List<String> elements2 = split(list2.substring(1, list2.length() - 1));
        // check the number of elements both have
        int nrElementsInBothList = Math.min(elements1.size(), elements2.size());
        for (int i = 0; i < nrElementsInBothList; i++) {
            int compare = compare(elements1.get(i), elements2.get(i));
            if (compare != 0) {
                return compare;
            }
        }

        // still equal..
        return elements1.size() - elements2.size();
    }

    public List<String> split(String s) {
        List<String> elements = new ArrayList<>();

        StringBuilder element = new StringBuilder();
        int balance = 0;
        for (int j = 0; j < s.length(); j++) {
            switch (s.charAt(j)) {
                case '[':
                    balance++;
                    element.append('[');
                    break;
                case ']':
                    balance--;
                    element.append(']');
                    break;
                case ',':
                    if (balance == 0) {
                        elements.add(element.toString());
                        element.setLength(0);
                    } else {
                        element.append(',');
                    }
                    break;
                default:
                    element.append(s.charAt(j));
            }
        }

        // add last element
        elements.add(element.toString());

        return elements;
    }
}

record ReceivedPackets(List<PacketPair> packetPairs) {
    static ReceivedPackets of(List<String> input) {
        List<PacketPair> pairs = new ArrayList<>();

        List<String> lines = input.stream().filter(s -> !StringUtils.isBlank(s))
                .toList();
        for (int i = 0; i < lines.size(); i += 2) {
            pairs.add(new PacketPair(i / 2 + 1, lines.get(i), lines.get(i + 1)));
        }

        return new ReceivedPackets(pairs);
    }

    public long sumIndicesPacketsInRightOrder() {
        return packetPairs.stream()
                .filter(PacketPair::rightOrder)
                .map(PacketPair::index)
                .reduce(0, Integer::sum);
    }

    public long decoderKey() {
        String dividerPacket1 = "[[2]]";
        String dividerPacket2 = "[[6]]";

        List<String> orderedPackets =
                Stream.concat(Stream.of(new PacketPair(0, dividerPacket1, dividerPacket2)), packetPairs.stream())
                        .flatMap(packetPair -> Stream.of(packetPair.list1(), packetPair.list2()))
                        .sorted((o1, o2) -> new PacketPair(0, o1, o2).order())
                        .toList();

        long decoderKey = 1L;
        for (int i = 0; i < orderedPackets.size(); i++) {
            if (orderedPackets.get(i).equals(dividerPacket1) || orderedPackets.get(i).equals(dividerPacket2)) {
                decoderKey *= (i+1);
            }
        }

        return decoderKey;
    }
}

public class Day13 extends Day {
    @Override
    public String doPart1(List<String> inputRaw) {
        ReceivedPackets receivedPackets = ReceivedPackets.of(inputRaw);

        long result = receivedPackets.sumIndicesPacketsInRightOrder();

        return String.valueOf(result);
    }

    @Override
    public String doPart2(List<String> inputRaw) {
        ReceivedPackets receivedPackets = ReceivedPackets.of(inputRaw);

        long result = receivedPackets.decoderKey();

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
