package task3;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Hash {
    private final int[][] reporters = {{11, 0, 11}, {11, 0, 11}, {11, 0, 11}, {11, 0, 11}, {11, 0, 11}};
    private final String[][] hashTables = new String[5][];
    private final int[][] newsTable = new int[5][];
    private final Map<Character, Integer> alphabet = new HashMap<>();
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
                                reporters[i][2] = reporters[i][0];
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
                        dataPrinter(currentWriter - 1);
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
                        deleteNews(currentWriter, command[1]);
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
            case 0: {
                System.out.print("Mirek\n\t" + reporters[writerNumber][0] + " ");
                System.out.println(reporters[writerNumber][1]);
                break;
            }
            case 1: {
                System.out.print("Jarka\n\t" + reporters[writerNumber][0] + " ");
                System.out.println(reporters[writerNumber][1]);
                break;
            }
            case 2: {
                System.out.print("Jindra\n\t" + reporters[writerNumber][0] + " ");
                System.out.println(reporters[writerNumber][1]);
                break;
            }
            case 3: {
                System.out.print("Rychlonozka\n\t" + reporters[writerNumber][0] + " ");
                System.out.println(reporters[writerNumber][1]);
                break;
            }
            case 4: {
                System.out.print("Cervenacek\n\t" + reporters[writerNumber][0] + " ");
                System.out.println(reporters[writerNumber][1]);
                break;
            }
            default: {
                System.err.println("Error in dataPrinter with writeNumber = " + writerNumber);
            }
        }
    }

    private void tablesInitialization() {
        for (int i = 0; i < reporters.length; i++) {
            hashTables[i] = new String[reporters[i][0]];
            newsTable[i] = new int[reporters[i][0]];
        }
        for (int i = 0; i < 26; i++) {
            alphabet.put((char) (i + 97), i + 1);
        }
        alphabet.put(' ', 31);
    }

    private void insertNews(String inputString) {
        for (int i = 0; i < reporters.length; i++) {
            int index = Math.toIntExact(hashFunction(inputString.toCharArray(), reporters[i][0]));
            if (hashTables[i][index] == null) {
                hashTables[i][index] = inputString;
                newsTable[i][index]++;
                reporters[i][1]++;
            }
            else {
                if (!hashTables[i][index].equals(inputString)) {
                    int newIndex = linearProbing(index, i, inputString);
                    if (newIndex != -2 && newIndex != -1) {
                        hashTables[i][newIndex] = inputString;
                        newsTable[i][newIndex]++;
                        reporters[i][1]++;
                    }
                    else if (newIndex == -1) {
                        newsTable[i][index]++;
                    }
                    else {
                        System.err.println("Error in insertNews");
                        System.exit(1);
                    }
                }
                else {
                    newsTable[i][index]++;
                }
            }
            increaseTable(i);
        }
    }

    private void deleteNews(int reporter, String inputString) {
        int index = Math.toIntExact(hashFunction(inputString.toCharArray(), reporters[reporter][0]));
        if (hashTables[reporter][index].equals(inputString)) {
            if (newsTable[reporter][index] > 1) {
                newsTable[reporter][index]--;
            }
            else if (newsTable[reporter][index] == 1){
                hashTables[reporter][index] = null;
                reporters[reporter][1]--;
            }
            else {
                System.err.println("Error in deleteNews");
                System.exit(1);
            }
        }
        else {
            boolean flag = true;
            int newIndex = index;
            while (flag) {
                newIndex++;
                if (newIndex >= hashTables[reporter].length) {
                    newIndex = 0;
                }
                if (newIndex == index) {
                    System.err.println("Error in deleteNews");
                    System.exit(1);
                }
                if (hashTables[reporter][newIndex].equals(inputString)) {
                    if (newsTable[reporter][newIndex] > 1) {
                        newsTable[reporter][newIndex]--;
                        flag = false;
                    }
                    else if (newsTable[reporter][newIndex] == 1){
                        hashTables[reporter][newIndex] = null;
                        reporters[reporter][1]--;
                        flag = false;
                    }
                    else {
                        System.err.println("Error in deleteNews");
                        System.exit(1);
                    }
                }
            }
        }
        decreaseTable(reporter);
    }

    private int linearProbing(int index, int reporter, String inputString) {
        int startIndex = index;
        while (true) {
            index++;
            if (index >= hashTables[reporter].length) {
                index = 0;
            }
            if (index == startIndex) {
                System.err.println("Error in linearProbing");
                System.exit(1);
            }
            if (hashTables[reporter][index] == null) {
                return index;
            }
            else if (hashTables[reporter][index].equals(inputString)) {
                return -1;
            }
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

    private void increaseTable(int reporterNumber) {
        if (reporters[reporterNumber][1] >= reporters[reporterNumber][0] * 0.7) {
            hashTables[reporterNumber] = Arrays.copyOf(hashTables[reporterNumber],
                    hashTables[reporterNumber].length * 2);
            newsTable[reporterNumber] = Arrays.copyOf(newsTable[reporterNumber],
                    newsTable[reporterNumber].length * 2);
        }
    }

    private void decreaseTable(int reporterNumber) {
        if (reporters[reporterNumber][1] >= reporters[reporterNumber][0] * 0.3) {
            hashTables[reporterNumber] = Arrays.copyOf(hashTables[reporterNumber],
                    hashTables[reporterNumber].length * 2);
            newsTable[reporterNumber] = Arrays.copyOf(newsTable[reporterNumber],
                    newsTable[reporterNumber].length * 2);
        }
    }
}
