package task1;

import java.awt.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.System.exit;
import static java.lang.System.in;

/**
 * Standard Depth-first search algorithm with preliminary input data checks
 *
 * @return Outputs errors to the System.err or a maze with the required points into system.out
 */

public class Maze {
    //Static variable for the maze
    private static final ArrayList<char[]> labyrinth = new ArrayList<>();

    public void maze() {
        Scanner scanner = new Scanner(System.in);
        //Labyrinth width
        int width = -1;
        //Labyrinth loading
        while (scanner.hasNext()) {
            //Check labyrinth width
            if (labyrinth.size() > 50) {
                System.err.println("Error: Delka bludiste je mimo rozsah!");
                exit(1);
            }
            String temporary = scanner.next();
            //Check labyrinth length
            if (width == -1) {
                width = temporary.length();
                if (width < 5 || 100 < width) {
                    System.err.println("Error: Sirka bludiste je mimo rozsah!");
                    exit(1);
                }
            } else if (width != temporary.length()) {
                System.err.println("Error: Bludiste neni obdelnikove!");
                exit(1);
            }
            labyrinth.add(temporary.toCharArray());
        }
        //Variables for entrance and exit of the maze
        Point entrance = new Point(1, 0);
        Point exit = new Point(width - 2, labyrinth.size() - 1);

        //Check labyrinth width
        if (labyrinth.size() < 5) {
            System.err.println("Error: Delka bludiste je mimo rozsah!");
            exit(1);
        }
        //Check labyrinth entrance
        else if (labyrinth.get(entrance.y)[entrance.x] != '.') {
            System.err.println("Error: Vstup neni vlevo nahore!");
            exit(1);
        }
        //Check labyrinth exit
        else if (labyrinth.get(exit.y)[exit.x] != '.') {
            System.err.println("Error: Vystup neni vpravo dole!");
            exit(1);
        }

        //Check labyrinth symbols
        for (char[] chars : labyrinth) {
            Pattern pattern = Pattern.compile("^[.#]*$");
            Matcher matcher = pattern.matcher(new String(chars));
            if (!matcher.find()) {
                System.err.println("Error: Bludiste obsahuje nezname znaky!");
                exit(1);
            }
        }
        //Check labyrinth symbols (to complete task requirements)
        for (int i = 0; i < labyrinth.size(); i++) {
            if (i == 0) {
                Pattern patternBorder = Pattern.compile("^[#]\\.[#]{" + (labyrinth.get(i).length - 2) + "}$");
                Matcher matcher = patternBorder.matcher(new String(labyrinth.get(i)));
                if (!matcher.find()) {
                    System.err.println("Error: Bludiste neni oplocene!");
                    exit(1);
                }
            }
            else if (i == labyrinth.size() - 1) {
                Pattern patternBorder = Pattern.compile("^[#]{" + (labyrinth.get(i).length - 2) +"}\\.[#]$");
                Matcher matcher = patternBorder.matcher(new String(labyrinth.get(i)));
                if (!matcher.find()) {
                    System.err.println("Error: Bludiste neni oplocene!");
                    exit(1);
                }
            }
            else {
                Pattern patternBorder = Pattern.compile("^[#].*[#]$");
                Matcher matcher = patternBorder.matcher(new String(labyrinth.get(i)));
                if (!matcher.find()) {
                    System.err.println("Error: Bludiste neni oplocene!");
                    exit(1);
                }
            }
        }

        //Check path
        boolean[][] isVisited = new boolean[width][labyrinth.size()];
        ArrayList<Point> path = new ArrayList<>();
        try {
            recursiveDepthFirstSearch(entrance, exit, isVisited, path);
        }
        //Saving time by forcibly exiting recursion
        catch (Exception e) {
        }
        //Checking the existence of a path
        if (path.size() == 0) {
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
            catch (Exception e) {
                labyrinth.get(point.y)[point.x] = '.';
            }
        }
        //Final polishing and printing of labyrinth
        labyrinth.get(entrance.y)[entrance.x] = '!';
        printArray(labyrinth);
    }

    /**
     * Printing of the labyrinth into system.out
     *
     * @param inputArray Printable array variable
     */
    private static void printArray(ArrayList<char[]> inputArray) {
        for (char[] row: inputArray) {
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
    private void recursiveDepthFirstSearch(Point current, Point end, boolean[][] inputVisited,
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
                inputPath.remove(inputPath.get(inputPath.size() - 1));
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
    private ArrayList<Point> checkRoutes(Point inputPoint, boolean[][] inputVisited) {
        ArrayList<Point> result = new ArrayList<>();
        //Check right
        if ((inputPoint.x + 1) < (labyrinth.get(labyrinth.size() - 1).length - 1) &&
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
}
