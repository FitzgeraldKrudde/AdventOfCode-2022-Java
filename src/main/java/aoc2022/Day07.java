package aoc2022;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static aoc2022.Day07.Filesystem.ROOT;

public class Day07 extends Day {
    @Override
    public String doPart1(List<String> inputRaw) {
        Filesystem filesystem = Filesystem.of(inputRaw);

        HashMap<Node, Long> directorySizes = filesystem.calculateSizes();

        long result = directorySizes.values().stream()
                .filter(size -> size <= 100000)
                .reduce(0L, Long::sum);

        return String.valueOf(result);
    }

    record Node(String name, Node parent, Set<Node> nodes, long size) {
        void addDirectory(String directory) {
            nodes.add(new Node(directory, this, new HashSet<>(), 0));
        }

        void addFile(String filename, long size) {
            nodes.add(new Node(filename, this, new HashSet<>(), size));
        }

        long calculateSize(HashMap<Node, Long> sizes) {
            long totalSize = size + nodes().stream()
                    .map(node -> node.calculateSize(sizes))
                    .reduce(0L, Long::sum);
            if (size == 0) {
                // i.e. a directory
                // if files with length 0 would exist (and be a problem) then an explicit flag in the Node for file/directory would be needed
                sizes.put(this, totalSize);
            }

            return totalSize;
        }

        @Override
        public String toString() {
            return "Node{name='" + name + '\'' + ", nodes=" + nodes + ", size=" + size + '}';
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, nodes, size);
        }
    }

    record Filesystem(Node root) {
        final static String ROOT = "/";

        static Filesystem of(List<String> lines) {
            Node root = new Node(ROOT, null, new HashSet<>(), 0);
            Filesystem filesystem = new Filesystem(root);

            Prompt prompt = new Prompt(filesystem, root);
            for (String line : lines) {
                prompt = prompt.processLine(line);
            }

            return filesystem;
        }

        public HashMap<Node, Long> calculateSizes() {
            HashMap<Node, Long> directorySizes = new HashMap<>();
            root.calculateSize(directorySizes);

            return directorySizes;
        }
    }

    record Prompt(Filesystem filesystem, Node currentDir) {
        Prompt processLine(String line) {
            return switch (line) {
                case String cmd when cmd.startsWith("$ ls") -> this;
                case String cmd when cmd.startsWith("$ cd") -> changeDirectory(cmd.split("\\s+")[2]);
                case String node when node.startsWith("dir ") -> addDirectory(node.split("\\s+")[1]);
                default -> {
                    String[] words = line.split("\\s+");
                    yield addFile(words[1], Long.parseLong(words[0]));
                }
            };
        }

        Prompt cdRoot() {
            Node currentDir = this.currentDir;
            while (currentDir.parent() != null) {
                currentDir = currentDir.parent();
            }
            return new Prompt(filesystem, currentDir);
        }

        Prompt changeDirectory(String dir) {
            return switch (dir) {
                case ROOT -> cdRoot();
                case ".." -> new Prompt(filesystem, currentDir.parent());
                default -> new Prompt(filesystem, currentDir.nodes.stream()
                        .filter(node -> node.name().equals(dir))
                        .findFirst()
                        .orElseThrow(() -> new RuntimeException("unknown dir")));
            };
        }

        Prompt addDirectory(String directory) {
            currentDir.addDirectory(directory);
            return this;
        }

        Prompt addFile(String filename, long size) {
            currentDir.addFile(filename, size);
            return this;
        }
    }

    @Override
    public String doPart2(List<String> inputRaw) {
        Filesystem filesystem = Filesystem.of(inputRaw);

        HashMap<Node, Long> directorySizes = filesystem.calculateSizes();

        long FILESYTEM_SIZE = 70_000_000;
        long TOTAL_UNUSED_SPACE_NEEDED = 30_000_000;

        long spaceUsed = directorySizes.entrySet().stream()
                .filter(nodeSize -> nodeSize.getKey().name().equals(ROOT))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("no root"))
                .getValue();
        long freeSpace = FILESYTEM_SIZE - spaceUsed;
        long spaceNeeded = TOTAL_UNUSED_SPACE_NEEDED - freeSpace;

        long result = directorySizes.values().stream()
                .filter(size -> size >= spaceNeeded)
                .min(Long::compareTo)
                .orElseThrow(() -> new IllegalStateException("no root"));

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
