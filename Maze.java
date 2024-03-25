import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.System.exit;

public class Maze {
    public static void main(String[] args) {
        ArrayList<char[]> labyrinth = new ArrayList<>();
        ArrayList<char[]> result = new ArrayList<>();
        Scanner scanner = new Scanner(System.in);
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
            result.add(temporary.toCharArray());
        }

        //Check labyrinth width
        if (labyrinth.size() < 5) {
            System.err.println("Error: Delka bludiste je mimo rozsah!");
            exit(1);
        }
        //Check labyrinth entrance
        else if (labyrinth.get(0)[1] != '.') {
            System.err.println("Error: Vstup neni vlevo nahore!");
            exit(1);
        }
        //Check labyrinth exit
        else if (labyrinth.get(labyrinth.size() - 1)[labyrinth.get(labyrinth.size() - 1).length - 2] != '.') {
            System.err.println("Error: Vystup neni vpravo dole!");
            exit(1);
        }

        //Check labyrinth symbols
        for (int i = 0; i < labyrinth.size(); i++) {
            Pattern pattern = Pattern.compile("^[.#]*$");
            Matcher matcher = pattern.matcher(new String(labyrinth.get(i)));
            if (!matcher.find()) {
                System.err.println("Error: Bludiste obsahuje nezname znaky!");
                exit(1);
            }
        }

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
        int[][] isVisited = new int[labyrinth.size()][width];
        if (path(labyrinth, isVisited)) {
            for (int y = isVisited.length - 1; y > 0; y--) {
                for (int x = isVisited[y].length - 1; x >= 0; x--) {
                    if (isVisited[y][x] == 1) {
                        int[][] isVisitedCopy = new int[labyrinth.size()][width];
                        labyrinth.get(y)[x] = '#';
                        if (!path(labyrinth, isVisitedCopy)) {
                            result.get(y)[x] = '!';
                            labyrinth.get(y)[x] = '.';
                        }
                        else {
                            labyrinth.get(y)[x] = '.';
                        }
                    }
                }
            }
            result.get(0)[1] = '!';
        }
        else {
            System.err.println("Error: Cesta neexistuje!");
            exit(1);
        }
        printArray(result);
    }

    private static void printArray(ArrayList<char[]> inputArray) {
        for (char[] row: inputArray) {
            System.out.println(row);
        }
    }

    private static void printArray(int[][] inputArray) {
        System.out.println();
        for (int[] row: inputArray) {
            for(var var: row) {
                System.out.print(var + " ");
            }
            System.out.println();
        }
    }

    private static boolean path(ArrayList<char[]> inputArray, int[][] inputVisited) {
        int x = 1;
        int y = 0;
        try {
            while (true) {
                if (x == inputArray.get(inputArray.size() - 1).length - 2 && y == inputArray.size() - 1) {
                    inputVisited[y][x] = 1;
                    return true;
                }
                if (x < inputArray.get(0).length && y < inputArray.size()) {
                    //Move forward
                    if (inputArray.get(y + 1)[x] == '.' && inputVisited[y + 1][x] == 0) {
                        inputVisited[y][x] = 1;
                        y++;
                    } else if (inputArray.get(y)[x + 1] == '.' && inputVisited[y][x + 1] == 0) {
                        inputVisited[y][x] = 1;
                        x++;
                    } else if (inputArray.get(y)[x - 1] == '.' && inputVisited[y][x - 1] == 0) {
                        inputVisited[y][x] = 1;
                        x--;
                    } else if (y - 1 > -1 && inputArray.get(y - 1)[x] == '.' && inputVisited[y - 1][x] == 0) {
                        inputVisited[y][x] = 1;
                        y--;
                    }
                    //Move rearward
                    else if (y - 1 > -1 && inputVisited[y - 1][x] == 1) {
                        inputVisited[y][x] = 2;
                        y--;
                    } else if (inputVisited[y][x - 1] == 1) {
                        inputVisited[y][x] = 2;
                        x--;
                    } else if (inputVisited[y][x + 1] == 1) {
                        inputVisited[y][x] = 2;
                        x++;
                    } else if (inputVisited[y + 1][x] == 1) {
                        inputVisited[y][x] = 2;
                        y++;
                    } else {
                        return false;
                    }
                } else {
                    System.err.println("Array out of bound");
                }
            }
        }
        catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return false;
    }
}
