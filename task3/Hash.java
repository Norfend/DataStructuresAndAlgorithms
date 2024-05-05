package task3;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Hash {
    private final int baseTableLength = 11;
    private final int[][] reporters = {{baseTableLength, 0}, {baseTableLength, 0}, {baseTableLength, 0},
            {baseTableLength, 0}, {baseTableLength, 0}};
    private final String[][] hashTables = new String[5][];
    private Map<Character, Integer> alphabet = new HashMap<>();
    private int currentWriter = -1;

    public void hashing() {
        boolean acceptNews = false;
        boolean acceptedSettings = false;
        boolean deleteNews = false;
        boolean initialization = false;
        Scanner scanner = new Scanner(System.in);
        String[] command;

        while (scanner.hasNextLine()) {
            String nextLine = scanner.nextLine();
            if (!nextLine.isEmpty()) {
                command = lineParser(nextLine);
            }
            else {
                System.err.println("Error: Chybny vstup!");
                continue;
            }
            switch (command[0]) {
                case "Input news": {
                    acceptNews = true;
                    deleteNews = false;
                    currentWriter = -1;
                    if (!initialization) {
                        tablesInitialization();
                        initialization = true;
                    }
                    break;
                }
                case "Table settings": {
                    String[] settings = command[1].split(" ");
                    if (settings.length <= reporters.length && !acceptedSettings) {
                        for (int i = 0; i < settings.length; i++) {
                            try {
                                reporters[i][0] = Integer.parseInt(settings[i]);
                            }
                            catch (Exception e) {
                                System.err.println(e.getMessage());
                                break;
                            }
                        }
                    }
                    else {
                        System.err.println("Error: Chybny vstup!");
                    }
                    break;
                }
                case "Writer settings": {
                    currentWriter = Integer.parseInt(command[1]);
                    acceptNews = false;
                    deleteNews = false;
                    break;
                }
                case "Print": {
                    if (currentWriter != -1) {
                        dataPrinter(currentWriter);
                    }
                    else {
                        System.err.println("Error: Chybny vstup!");
                    }
                    break;
                }
                case "Delete": {
                    if (currentWriter != -1) {
                        acceptNews = false;
                        deleteNews = true;
                    }
                    else {
                        System.err.println("Error: Chybny vstup!");
                    }
                    break;
                }
                case "Error": {
                    System.err.println("Error: Chybny vstup!");
                    acceptNews = false;
                    deleteNews = false;
                    currentWriter = -1;
                    break;
                }
                case "News": {
                    if (acceptNews) {
                        insertNews(command[1]);
                    }
                    if (deleteNews) {
                        System.out.println("delete news");
                    }
                    break;
                }
                default: {
                    System.err.println("Line parser error" + command[0]);
                    System.exit(1);
                }
            }
            if (!acceptedSettings) acceptedSettings = true;
        }
    }

    private String[] lineParser(String inputLine) {
        char[] line = inputLine.toCharArray();
        String[] result = new String[2];
        if (line[0] == '#') {
            if (line.length < 2) {
                result[0] = "Error";
            }
            else if (line[1] == 'a') {
                result[0] = "Input news";
            }
            else if (line[1] == 'i') {
                result[0] = "Table settings";
                result[1] = spaceCutter(inputLine.substring(2));
            }
            else if (49 <= line[1] && line[1] <= 53) {
                result[0] = "Writer settings";
                result[1] = String.valueOf(line[1]);
            }
            else if (line[1] == 'p') {
                result[0] = "Print";
            }
            else if (line[1] == 'd') {
                result[0] = "Delete";
            }
            else {
                result[0] = "Error";
            }
        }
        else {
            result[0] = "News";
            result[1] = spaceCutter(inputLine);
        }
        return result;
    }

    private String spaceCutter(String inputString) {
        char[] stringWithoutSpaces = inputString.toCharArray();
        int startOfNews = 0;
        int endOfNews = stringWithoutSpaces.length;
        for (int i = 0; i < stringWithoutSpaces.length; i++) {
            if (stringWithoutSpaces[i] != ' ') {
                startOfNews = i;
                break;
            }
        }
        for (int i = stringWithoutSpaces.length - 1; i > 0; i--) {
            if (stringWithoutSpaces[i] != ' ') {
                endOfNews = i + 1;
                break;
            }
        }
        return inputString.substring(startOfNews, endOfNews);
    }

    private void dataPrinter(int writerNumber) {
        switch (writerNumber) {
            case 1: {
                System.out.print("Mirek\n\t" + reporters[writerNumber][0] + " ");
                System.out.println(reporters[writerNumber][1]);
            }
            case 2: {
                System.out.print("Jarka\n\t" + reporters[writerNumber][0] + " ");
                System.out.println(reporters[writerNumber][1]);
            }
            case 3: {
                System.out.print("Jindra\n\t" + reporters[writerNumber][0] + " ");
                System.out.println(reporters[writerNumber][1]);
            }
            case 4: {
                System.out.print("Rychlonozka\n\t" + reporters[writerNumber][0] + " ");
                System.out.println(reporters[writerNumber][1]);
            }
            case 5: {
                System.out.print("Cervenacek\n\t" + reporters[writerNumber][0] + " ");
                System.out.println(reporters[writerNumber][1]);
            }
        }
    }

    private void tablesInitialization() {
        for (int i = 0; i < reporters.length; i++) {
            hashTables[i] = new String[reporters[i][0]];
        }
        for (int i = 0; i < 26; i++) {
            alphabet.put((char) (i + 97), i + 1);
        }
        alphabet.put(' ', 31);
    }

    private void insertNews (String inputString) {
        for (int i = 0; i < reporters.length; i++) {
            hashTables[i][Math.toIntExact(hashFunction(inputString.toCharArray(), reporters[i][0]))] = inputString;
            reporters[i][1]++;
        }
    }

    private double power(int inputPower) {
        return Math.pow(32, inputPower);
    }

    private long hashFunction(char[] inputtedCharArray, int tableLength) {
        double hash = 0;
        for (int i = 0; i < inputtedCharArray.length; i++) {
            hash += alphabet.get(inputtedCharArray[i]) * (power(i));
        }
        return (long) (hash % tableLength);
    }
}
