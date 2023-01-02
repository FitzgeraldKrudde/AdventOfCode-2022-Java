package aoc2022;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static java.util.stream.Collectors.toList;

record Element(long value, double random) {
}

class EncryptedFile {
    private final HashMap<Integer, Element> hashMapElementsForInputPosition;
    private final List<Element> elementList;
    private final int nrElements;

    public EncryptedFile(List<Integer> input) {
        this(input, 1);
    }

    public EncryptedFile(List<Integer> input, long encryptionKey) {
        nrElements = input.size();
        hashMapElementsForInputPosition = new HashMap<>();
        for (int i = 0; i < nrElements; i++) {
            // mark value 0 (unique element) with a non-random
            if (input.get(i) != 0) {
                hashMapElementsForInputPosition.put(i, new Element(input.get(i) * encryptionKey, Math.random()));
            } else {
                hashMapElementsForInputPosition.put(i, new Element(input.get(i), 0D));
            }
        }
        elementList = new ArrayList<>(hashMapElementsForInputPosition.values());
    }

    EncryptedFile mix() {
        for (int i = 0; i < nrElements; i++) {
            Element currentElement = hashMapElementsForInputPosition.get(i);
            if (currentElement.value() == 0) {
                continue;
            }

            int currentPosition = elementList.indexOf(currentElement);
            // as list.add moves the items from that index to the right an extra +1 is needed
            long newPosition = (Math.floorMod(currentElement.value(), nrElements - 1) + currentPosition + 1) % (nrElements);
            if (newPosition == 0) {
                // just for readability, not required as this does not change the circular list
                elementList.remove(currentPosition);
                elementList.add(currentElement);
            } else {
                if (newPosition > currentPosition) {
                    elementList.add((int) newPosition, currentElement);
                    elementList.remove(currentPosition);
                } else {
                    elementList.remove(currentPosition);
                    elementList.add((int) newPosition, currentElement);
                }
            }
        }

        return this;
    }

    public long sumGroveCoordinates() {
        int indexZero = elementList.indexOf(new Element(0, 0D));

        long number1000 = elementList.get((indexZero + 1000) % nrElements).value();
        long number2000 = elementList.get((indexZero + 2000) % nrElements).value();
        long number3000 = elementList.get((indexZero + 3000) % nrElements).value();

        return number1000 + number2000 + number3000;
    }
}

public class Day20 extends Day {
    @Override
    public String doPart1(List<String> inputRaw) {
        List<Integer> input = parseInput(inputRaw);

        EncryptedFile encryptedFile = new EncryptedFile(input);

        long result = encryptedFile.mix().sumGroveCoordinates();

        return String.valueOf(result);
    }

    @Override
    public String doPart2(List<String> inputRaw) {
        List<Integer> input = parseInput(inputRaw);

        EncryptedFile encryptedFile = new EncryptedFile(input, 811589153);

        for (int i = 0; i < 10; i++) {
            encryptedFile.mix();
        }

        long result = encryptedFile.sumGroveCoordinates();

        return String.valueOf(result);
    }

    private List<Integer> parseInput(List<String> inputRaw) {
        return inputRaw.stream()
                .map(Integer::valueOf)
                .collect(toList());
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
