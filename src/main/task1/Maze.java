package task1;

import java.awt.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.System.exit;

/**
 * Standard Depth-first search algorithm with preliminary input data checks
 */

public class Maze {
    private static final ArrayList<char[]> labyrinth = new ArrayList<>();

    public static void main(String[] args) {
        int counter = 0;
        Pattern allowedCharacters = Pattern.compile("^[.#]*$");
        Pattern allowedUpperBorder = Pattern.compile("^#\\.#+$");
        Pattern allowedBorder = Pattern.compile("^#.*#$");
        Pattern allowedLowerBorder = Pattern.compile("^#+\\.#$");

        Matcher allowedCharactersMatcher;
        Matcher upperBorderMatcher;
        Matcher lowerBorderMatcher;
        Matcher borderMatcher;

        Scanner scanner = new Scanner(System.in);

        //Check labyrinth width
        int width = -1;

        //Labyrinth loading
        while (scanner.hasNext()) {
            String temporary = scanner.nextLine();
            allowedCharactersMatcher = allowedCharacters.matcher(temporary);

            if (width == -1) {
                width = temporary.length();
            }

            //Check labyrinth symbols
            if (!allowedCharactersMatcher.find()) {
                ErrorChecker.setWrongSymbols();
            }
            
            //Check that labyrinth is rectangle
            if (temporary.length() != width) {
                ErrorChecker.setNotRectangle();
            }

            //Check labyrinth borders
            if (counter == 0) {
                upperBorderMatcher = allowedUpperBorder.matcher(temporary);
                if (! upperBorderMatcher.find()) {
                    ErrorChecker.setWrongBorders();
                }
            } else {
                borderMatcher = allowedBorder.matcher(temporary);
                if (! borderMatcher.find()) {
                    ErrorChecker.setWrongBorders();
                }
            }

            labyrinth.add(temporary.toCharArray());
            counter++;
        }

        //Check labyrinth width
        if (width < 5 || 100 < width) {
            ErrorChecker.setWrongWidth();
        }

        lowerBorderMatcher = allowedLowerBorder.matcher(new String(labyrinth.getLast()));
        if (! lowerBorderMatcher.find()) {
            ErrorChecker.setWrongBorders();
        }

        //Variables for entrance and exit of the maze
        Point entrance = new Point(1, 0);
        Point exit = new Point(width - 2, labyrinth.size() - 1);

        //Check labyrinth length
        if (labyrinth.size() < 5 || 50 < labyrinth.size()) {
            ErrorChecker.setWrongLength();
        }

        //Check labyrinth entrance
        if (labyrinth.get(entrance.y)[entrance.x] != '.') {
            ErrorChecker.setWrongEntrance();
        }

        //Check labyrinth exit
        if (labyrinth.get(exit.y)[exit.x] != '.') {
            ErrorChecker.setWrongExit();
        }

        ErrorChecker.printChecks();

        //Check path
        boolean[][] isVisited = new boolean[width][labyrinth.size()];
        ArrayList<Point> path = new ArrayList<>();
        try {
            recursiveDepthFirstSearch(entrance, exit, isVisited, path);
        }
        //Saving time by forcibly exiting recursion
        catch (Exception ignored) {

        }

        //Checking the existence of a path
        if (path.isEmpty()) {
            System.err.println("Error: Cesta neexistuje!");
            exit(1);
        }

        //Try to "lock" points from the path
        for (Point point: path) {
            boolean[][] visited = new boolean[width][labyrinth.size()];
            labyrinth.get(point.y)[point.x] = '#';
            try {
                recursiveDepthFirstSearch(entrance, exit, visited, new ArrayList<>());
                labyrinth.get(point.y)[point.x] = '!';
            }
            //Saving time by forcibly exiting recursion
            catch (Exception e) {
                labyrinth.get(point.y)[point.x] = '.';
            }
        }
        //Final polishing and printing of labyrinth
        labyrinth.get(entrance.y)[entrance.x] = '!';
        printArray();
    }

    /**
     * Printing of the labyrinth into system.out
     */
    private static void printArray() {
        for (char[] row: Maze.labyrinth) {
            System.out.println(row);
        }
    }

    /**
     * Recursive Depth-first search algorithm
     *
     * @param current Initial point
     *
     * @param end Exit point
     *
     * @param inputVisited Array of visited "vertices"
     *
     * @param inputPath Array for visited "vertices"
     */
    private static void recursiveDepthFirstSearch(Point current, Point end, boolean[][] inputVisited,
                                                  ArrayList<Point> inputPath) {
        inputVisited[current.x][current.y] = true;
        if (current.x == end.x && current.y == end.y) {
            inputPath.add(current);
            throw new RuntimeException("Path found");
        }
        else {
            ArrayList<Point> routes = checkRoutes(current, inputVisited);
            for (Point route : routes) {
                inputPath.add(current);
                recursiveDepthFirstSearch(route, end, inputVisited, inputPath);
                inputPath.remove(inputPath.getLast());
            }
        }
    }

    /**
     * Search for possible shifts from the input point
     *
     * @param inputPoint Initial point
     *
     * @param inputVisited Array of visited "vertices"
     *
     * @return ArrayList of possible shifts from an input point
     */
    private static ArrayList<Point> checkRoutes(Point inputPoint, boolean[][] inputVisited) {
        ArrayList<Point> result = new ArrayList<>();
        //Check right
        if ((inputPoint.x + 1) < (labyrinth.getLast().length - 1) &&
                labyrinth.get(inputPoint.y)[inputPoint.x + 1] != '#' && !inputVisited[inputPoint.x + 1][inputPoint.y])
            result.add(new Point((inputPoint.x + 1), inputPoint.y));
        //Check down
        if ((inputPoint.y + 1) <= (labyrinth.size() - 1) && labyrinth.get(inputPoint.y + 1)[inputPoint.x] != '#' &&
                !inputVisited[inputPoint.x][inputPoint.y + 1])
            result.add(new Point(inputPoint.x, (inputPoint.y + 1)));
        //Check left
        if (0 < (inputPoint.x - 1) && labyrinth.get(inputPoint.y)[inputPoint.x - 1] != '#' &&
                !inputVisited[inputPoint.x - 1][inputPoint.y])
            result.add(new Point((inputPoint.x - 1), inputPoint.y));
        //Check up
        if (0 < (inputPoint.y - 1) && labyrinth.get(inputPoint.y - 1)[inputPoint.x] != '#' &&
                !inputVisited[inputPoint.x][inputPoint.y - 1])
            result.add(new Point(inputPoint.x, (inputPoint.y - 1)));
        return result;
    }

    private static class ErrorChecker {
        private static Boolean notRectangle = false;
        private static Boolean wrongEntrance = false;
        private static Boolean wrongExit = false;
        private static Boolean wrongWidth = false;
        private static Boolean wrongLength = false;
        private static Boolean wrongSymbols = false;
        private static Boolean wrongBorders = false;

        public static void printChecks() {
            if (notRectangle) {
                System.err.println("Error: Bludiste neni obdelnikove!");
                exit(1);
            } else if (wrongEntrance) {
                System.err.println("Error: Vstup neni vlevo nahore!");
                exit(1);
            } else if (wrongExit) {
                System.err.println("Error: Vystup neni vpravo dole!");
                exit(1);
            } else if (wrongWidth) {
                System.err.println("Error: Sirka bludiste je mimo rozsah!");
                exit(1);
            } else if (wrongLength) {
                System.err.println("Error: Delka bludiste je mimo rozsah!");
                exit(1);
            } else if (wrongSymbols) {
                System.err.println("Error: Bludiste obsahuje nezname znaky!");
                exit(1);
            } else if (wrongBorders) {
                System.err.println("Error: Bludiste neni oplocene!");
                exit(1);
            }
        }

        public static void setNotRectangle() {
            if (! notRectangle) {
                notRectangle = true;
            }
        }

        public static void setWrongEntrance() {
            if (!wrongEntrance) {
                ErrorChecker.wrongEntrance = true;
            }
        }

        public static void setWrongExit() {
            if (!wrongExit) {
                wrongExit = true;
            }
        }

        public static void setWrongWidth() {
            if (!wrongWidth) {
                wrongWidth = true;
            }
        }

        public static void setWrongLength() {
            if (!wrongLength) {
                wrongLength = true;
            }
        }

        public static void setWrongSymbols() {
            if (! wrongSymbols) {
                wrongSymbols = true;
            }
        }

        public static void setWrongBorders() {
            if (! wrongBorders) {
                wrongBorders = true;
            }
        }
    }
}