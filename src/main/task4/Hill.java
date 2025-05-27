package task4;

import java.util.*;
import java.util.AbstractMap.SimpleEntry;

public class Hill {
    private static HillMap hillMap;
    private static final int[] xAxisPossibleMoves = {0, 1, 0, -1};
    private static final int[] yAxisPossibleMoves = {1, 0, -1, 0};

    public static void main(String[] args) {
        readInput();

        boolean runLift = false;
        boolean runPiste = false;

        if (args.length == 0) {
            runLift = true;
            runPiste = true;
        } else {
            switch (args[0]) {
                case "lift":
                    runLift = true;
                    break;
                case "piste":
                    runPiste = true;
                    break;
                default:
                    System.err.println("Error: Unknown option: " + args[0]);
                    System.exit(1);
            }
        }

        if (runLift) {
            List<Integer> lPath = findLiftPath();
            if (lPath == null) {
                System.err.println("Error: Cesta neexistuje!");
                System.exit(1);
            }
            printPath(lPath);
        }
        if (runPiste) {
            List<Integer> pPath = findPistePath();
            if (pPath == null) {
                System.err.println("Error: Cesta neexistuje!");
                System.exit(1);
            }
            printPath(pPath);
        }
    }

    public static List<Integer> findLiftPath() {
        PathInfo[][] fromStart = findPathForLift(0, 0);
        PathInfo[][] fromEnd = findPathForLift(hillMap.getY() - 1, hillMap.getX() - 1);
        return findBestPath(fromStart, fromEnd, true);
    }

    private static List<Integer> findPistePath() {
        PathInfo[][] fromStart = findPathForPiste(0, 0);
        PathInfo[][] fromEnd = findPathForPiste(
                hillMap.getY() - 1, hillMap.getX() - 1
        );
        return findBestPath(fromStart, fromEnd, false);
    }

    private static PathInfo[][] findPathForLift(int startCoordinateX, int startCoordinateY) {
        int[][] terrain = hillMap.getStorage();
        int i = hillMap.getY();
        int j = hillMap.getX();

        PathInfo[][] liftPath = initializeMatrix(i, j);
        liftPath[startCoordinateX][startCoordinateY].setLength(1);

        Queue<SimpleEntry<Integer, Integer>> queue = new LinkedList<>();
        queue.add(new SimpleEntry<>(startCoordinateX, startCoordinateY));

        while (! queue.isEmpty()) {
            SimpleEntry<Integer, Integer> actual = queue.poll();
            int x = actual.getKey();
            int y = actual.getValue();

            for (int move = 0; move < 4; move++) {
                int newX = x + xAxisPossibleMoves[move];
                int newY = y + yAxisPossibleMoves[move];
                if (newX >= 0 && newX < i && newY >= 0 && newY < j && terrain[newX][newY] > terrain[x][y]) {
                    int newLength = liftPath[x][y].getLength() + 1;
                    int newSlope = Math.max(liftPath[x][y].getSlope(), Math.abs(terrain[newX][newY] - terrain[x][y]));

                    boolean replace = false;
                    if (liftPath[newX][newY].getLength() == 0) {
                        replace = true;
                    } else if (liftPath[newX][newY].getLength() > newLength) {
                        replace = true;
                    } else if (liftPath[newX][newY].getLength() == newLength
                            && liftPath[newX][newY].getSlope() < newSlope) {
                        replace = true;
                    }

                    if (replace) {
                        liftPath[newX][newY] = new PathInfo(newLength, newSlope, new SimpleEntry<>(x, y));
                        queue.add(new SimpleEntry<>(newX, newY));
                    }
                }
            }
        }
        return liftPath;
    }

    private static PathInfo[][] findPathForPiste(int startCoordinateX, int startCoordinateY) {
        int[][] terrain = hillMap.getStorage();
        int i = hillMap.getY();
        int j = hillMap.getX();
        boolean needToChange = true;
        int maxIterations = i * j * 2;
        int iterations = 0;

        PathInfo[][] pistePath = initializeMatrix(i, j);
        pistePath[startCoordinateX][startCoordinateY].setLength(1);

        while (needToChange && iterations < maxIterations) {
            needToChange = false;
            iterations++;

            for (int row = 0; row < i; row++) {
                for (int col = 0; col < j; col++) {
                    if (pistePath[row][col].getLength() == 0) {
                        continue;
                    }

                    for (int move = 0; move < 4; move++) {
                        int newX = row + xAxisPossibleMoves[move];
                        int newY = col + yAxisPossibleMoves[move];

                        if (newX >= 0 && newX < i && newY >= 0 && newY < j && terrain[newX][newY] > terrain[row][col]) {
                            int newLength = pistePath[row][col].getLength() + 1;
                            int newSlope = Math.max(
                                    pistePath[row][col].getSlope(), Math.abs(terrain[newX][newY] - terrain[row][col])
                            );

                            boolean replace = false;
                            if (pistePath[newX][newY].getLength() == 0) {
                                replace = true;
                            } else if (pistePath[newX][newY].getLength() < newLength) {
                                replace = true;
                            } else if (pistePath[newX][newY].getLength() == newLength
                                    && pistePath[newX][newY].getSlope() > newSlope) {
                                replace = true;
                            }

                            if (replace) {
                                pistePath[newX][newY] = new PathInfo(newLength, newSlope, new SimpleEntry<>(row, col));
                                needToChange = true;
                            }
                        }
                    }
                }
            }
        }
        return pistePath;
    }

    private static List<Integer> findBestPath(PathInfo[][] pathFromTopLeft, PathInfo[][] pathFromBottomRight,
                                              boolean searchType) {
        int[][] terrain = hillMap.getStorage();
        int i = hillMap.getY();
        int j = hillMap.getX();

        int expectedLiftPathLength = i + j - 1;

        SimpleEntry<Integer, Integer> bestMaxPoint = null;
        int bestMaxPointHeight = -1;
        int bestLength = searchType ? Integer.MAX_VALUE : -1;
        int bestSlope = searchType ? -1 : Integer.MAX_VALUE;

        for (int row = 0; row < i; row++) {
            for (int col = 0; col < j; col++) {
                if (pathFromTopLeft[row][col].getLength() > 0 && pathFromBottomRight[row][col].getLength() > 0) {
                    int length = pathFromTopLeft[row][col].getLength() + pathFromBottomRight[row][col].getLength() - 1;
                    int maxHeight = terrain[row][col];
                    int maxSlope = Math.max(pathFromTopLeft[row][col].getSlope(), pathFromBottomRight[row][col].getSlope());

                    if (searchType && length != expectedLiftPathLength) {
                        continue;
                    }
                    boolean isBetter = false;

                    if (searchType) {
                        if (maxHeight > bestMaxPointHeight) {
                            isBetter = true;
                        } else if (maxHeight == bestMaxPointHeight) {
                            if (maxSlope > bestSlope) {
                                isBetter = true;
                            }
                        }
                    } else {
                        if (length > bestLength) {
                            isBetter = true;
                        } else if (length == bestLength) {
                            if (maxHeight > bestMaxPointHeight) {
                                isBetter = true;
                            } else if (maxHeight == bestMaxPointHeight) {
                                if (maxSlope < bestSlope) {
                                    isBetter = true;
                                }
                            }
                        }
                    }

                    if (isBetter || bestMaxPoint == null) {
                        bestMaxPoint = new SimpleEntry<>(row, col);
                        bestMaxPointHeight = maxHeight;
                        bestLength = length;
                        bestSlope = maxSlope;
                    }
                }
            }
        }
        if (bestMaxPoint == null) {
            return null;
        }

        List<Integer> pathHeights = new LinkedList<>();
        Deque<SimpleEntry<Integer, Integer>> upPoints = new LinkedList<>();
        SimpleEntry<Integer, Integer> actual = bestMaxPoint;
        while (actual != null) {
            upPoints.addFirst(actual);
            actual = pathFromTopLeft[actual.getKey()][actual.getValue()].getPreviousPoint();
        }

        Deque<SimpleEntry<Integer, Integer>> downPoints = new LinkedList<>();
        actual = pathFromBottomRight[bestMaxPoint.getKey()][bestMaxPoint.getValue()].getPreviousPoint();
        while (actual != null) {
            downPoints.add(actual);
            actual = pathFromBottomRight[actual.getKey()][actual.getValue()].getPreviousPoint();
        }

        for (SimpleEntry<Integer, Integer> p : upPoints) {
            pathHeights.add(terrain[p.getKey()][p.getValue()]);
        }
        for (SimpleEntry<Integer, Integer> p : downPoints) {
            pathHeights.add(terrain[p.getKey()][p.getValue()]);
        }

        return pathHeights;
    }

    private static void readInput() {
        boolean header = false;
        boolean readingNum = false;
        int num = 0;
        int x = -1;
        int y = 0;
        int pos = 0;

        try {
            byte[] buffer = System.in.readAllBytes();
            while (pos < buffer.length) {
                byte b = buffer[pos];
                if (b >= '0' && b <= '9') {
                    num = num * 10 + (b - '0');
                    readingNum = true;
                } else if (readingNum) {
                    if (! header) {
                        if (y == 0) {
                            y = num;
                        } else {
                            hillMap = new HillMap(num, y);
                            header = true;
                        }
                    } else {
                        hillMap.inputNumber(num, x, y);
                        y++;
                    }
                    num = 0;
                    readingNum = false;
                }

                if ((b == 10 || b == 13) && header) {
                    if (b == 13 && pos + 1 < buffer.length && buffer[pos + 1] == 10) {
                        pos++;
                    }
                    if (x != -1 && y != hillMap.getX()) {
                        System.err.println("Error: Chybny vstup!");
                        System.exit(1);
                    }
                    x++;
                    y = 0;
                }
                pos++;
            }
            if (x != hillMap.getY()) {
                System.err.println("Error: Chybny vstup!");
                System.exit(1);
            }
        }
        catch (Exception e) {
            System.err.println(e.getMessage());
            for (StackTraceElement ste : e.getStackTrace()) {
                System.err.println(ste.toString());
            }
        }
    }

    private static PathInfo[][] initializeMatrix(int row, int col) {
        PathInfo[][] result = new PathInfo[row][col];
        for (int m = 0; m < row; m++) {
            for (int n = 0; n < col; n++) {
                result[m][n] = new PathInfo();
            }
        }
        return result;
    }

    private static void printPath(List<Integer> path) {
        System.out.println(path.size());
        for (int i = 0; i < path.size(); i++) {
            System.out.print(path.get(i));
            if (i < path.size() - 1) {
                System.out.print(" ");
            }
        }
        System.out.println();
    }

    private static class HillMap {
        private final int x;

        private final int y;

        private final int[][] storage;

        public HillMap(int x, int y) {
            this.x = x;
            this.y = y;
            storage = new int[y][x];
        }

        public void inputNumber(int number, int coordinateX, int coordinateY) {
            if (coordinateX < 0 || coordinateX >= this.y || coordinateY < 0 || coordinateY >= this.x) {
                System.err.println("Error: Chybny vstup!");
                System.exit(1);
            }
            storage[coordinateX][coordinateY] = number;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public int[][] getStorage() {
            return storage;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(y).append(' ').append(x).append(' ').append('\n');
            for (int i = 0; i < y; i++) {
                for (int j = 0; j < x; j++) {
                    if (j == 0) {
                        sb.append(storage[i][j]);
                    }
                    else {
                        sb.append(' ').append(storage[i][j]);
                    }
                }
                sb.append('\n');
            }
            return sb.toString();
        }
    }

    private static class PathInfo {
        private final SimpleEntry<Integer, Integer> previousPoint;
        private int length;
        private final int slope;

        public PathInfo() {
            this.length = 0;
            this.slope = 0;
            this.previousPoint = null;
        }

        public PathInfo(int length, int slope, SimpleEntry<Integer, Integer> previousPoint) {
            this.length = length;
            this.slope = slope;
            this.previousPoint = previousPoint;
        }

        public SimpleEntry<Integer, Integer> getPreviousPoint() {
            return previousPoint;
        }

        public int getLength() {
            return length;
        }

        public void setLength(int length) {
            this.length = length;
        }

        public int getSlope() {
            return slope;
        }

    }
}