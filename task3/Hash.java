package task3;

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
        boolean printing = false;
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
                    printing = false;
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
                    printing = true;
                    break;
                }
                case "Print": {
                    if (currentWriter != -1) {
                        dataPrinter(currentWriter - 1);
                        acceptNews = false;
                        deleteNews = false;
                    }
                    else {
                        System.err.println("Error: Chybny vstup!");
                    }
                    break;
                }
                case "Delete": {
                    if (currentWriter != -1) {
                        acceptNews = false;
                        //printing = false;
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
                        for (int i = 0; i < reporters.length; i++) {
                            insertNews(command[1], i);
                        }
                    }
                    else if (deleteNews) {
                        deleteNews(command[1], currentWriter - 1);
                    }
                    else if (printing) {
                        newsPrinter(command[1], currentWriter - 1);
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

    private void dataPrinter(int reporter) {
        switch (reporter) {
            case 0: {
                System.out.print("Mirek\n\t" + reporters[reporter][0] + " ");
                System.out.println(reporters[reporter][1]);
                break;
            }
            case 1: {
                System.out.print("Jarka\n\t" + reporters[reporter][0] + " ");
                System.out.println(reporters[reporter][1]);
                break;
            }
            case 2: {
                System.out.print("Jindra\n\t" + reporters[reporter][0] + " ");
                System.out.println(reporters[reporter][1]);
                break;
            }
            case 3: {
                System.out.print("Rychlonozka\n\t" + reporters[reporter][0] + " ");
                System.out.println(reporters[reporter][1]);
                break;
            }
            case 4: {
                System.out.print("Cervenacek\n\t" + reporters[reporter][0] + " ");
                System.out.println(reporters[reporter][1]);
                break;
            }
            default: {
                System.err.println("Error in dataPrinter with writeNumber = " + reporter);
            }
        }
    }

    private void newsPrinter(String inputString, int reporter) {
        int newsIndex = Math.toIntExact(hashFunction(inputString.toCharArray(), reporters[reporter][0]));
        if (hashTables[reporter][newsIndex] != null && hashTables[reporter][newsIndex].equals(inputString)) {
            System.out.print("\t" + inputString + " " + newsIndex + " " + newsTable[reporter][newsIndex]);
            System.out.println();
        }
        else {
            boolean flag = true;
            int newIndex = newsIndex;
            while (flag) {
                newIndex++;
                if (newIndex >= hashTables[reporter].length) {
                    newIndex = 0;
                }
                if (newIndex == newsIndex) {
                    System.out.print("\t" + inputString + " " + -1 + " " + newsTable[reporter][newIndex]);
                    System.out.println();
                    break;
                }
                if (hashTables[reporter][newIndex] != null && hashTables[reporter][newIndex].equals(inputString)) {
                    System.out.print("\t" + inputString + " " + newIndex + " " + newsTable[reporter][newIndex]);
                    System.out.println();
                    flag = false;
                }
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

    private void insertNews(String inputString, int reporter) {
        int index = Math.toIntExact(hashFunction(inputString.toCharArray(), reporters[reporter][0]));
        if (hashTables[reporter][index] == null) {
            hashTables[reporter][index] = inputString;
            newsTable[reporter][index]++;
            reporters[reporter][1]++;
        }
        else {
            if (!hashTables[reporter][index].equals(inputString)) {
                int newIndex = linearProbing(index, inputString, reporter);
                if (hashTables[reporter][newIndex] == null) {
                    hashTables[reporter][newIndex] = inputString;
                    newsTable[reporter][newIndex]++;
                    reporters[reporter][1]++;
                }
                else if (!hashTables[reporter][newIndex].equals(inputString)) {
                    hashTables[reporter][newIndex] = inputString;
                    newsTable[reporter][newIndex]++;
                    reporters[reporter][1]++;
                }
                else {
                    newsTable[reporter][newIndex]++;
                }
            }
            else {
                newsTable[reporter][index]++;
            }
        }
        reHash(reporter, "Inserting");
    }

    private void deleteNews(String inputString, int reporter) {
        int index = Math.toIntExact(hashFunction(inputString.toCharArray(), reporters[reporter][0]));
        if (hashTables[reporter][index] != null && hashTables[reporter][index].equals(inputString)) {
            if (newsTable[reporter][index] > 1) {
                newsTable[reporter][index]--;
            }
            else if (newsTable[reporter][index] == 1){
                hashTables[reporter][index] = "-1";
                reporters[reporter][1]--;
                newsTable[reporter][index]--;
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
                    break;
                }
                if (hashTables[reporter][newIndex] != null && hashTables[reporter][newIndex].equals(inputString)) {
                    if (newsTable[reporter][newIndex] > 1) {
                        newsTable[reporter][newIndex]--;
                        flag = false;
                    }
                    else if (newsTable[reporter][newIndex] == 1){
                        hashTables[reporter][newIndex] = "-1";
                        newsTable[reporter][newIndex]--;
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
        reHash(reporter, "Deleting");
    }

    private int linearProbing(int index, String inputString, int reporter) {
        int startIndex = index;
        int potentialIndex = -1;
        while (true) {
            startIndex++;
            if (startIndex >= hashTables[reporter].length) {
                startIndex = 0;
            }
            if (startIndex == index) {
                return potentialIndex;
            }
            if (hashTables[reporter][startIndex] == null || hashTables[reporter][startIndex].equals(inputString)) {
                return startIndex;
            }
            if (hashTables[reporter][startIndex].equals("-1")) {
                potentialIndex = startIndex;
            }
        }
    }

    private long power(int inputPower, int tableLength) {
        long result = 1L;
        for (long i = 0; i < inputPower; i++) {
            result = (result * 32) % tableLength;
        }
        return result;
    }

    private long hashFunction(char[] inputtedCharArray, int tableLength) {
        long hash = 0;
        for (int i = 0; i < inputtedCharArray.length; i++) {
            int letter = alphabet.get(inputtedCharArray[i]);
            long power = power(i, tableLength);
            long temp = letter * power;
            hash += temp;
        }
        return hash % tableLength;
    }

    private void reHash(int reporter, String operationType) {
        boolean reHashing = false;
        if (operationType.equals("Inserting") && reporters[reporter][1] >= reporters[reporter][0] * 0.7) {
            reporters[reporter][0] = reporters[reporter][0] * 2;
            reHashing = true;
        }
        else if (operationType.equals("Deleting") && reporters[reporter][1] <= reporters[reporter][0] * 0.3) {
            reporters[reporter][0] = Math.max((reporters[reporter][1] / 2), reporters[reporter][2]);
            reHashing = true;
        }
        if (reHashing) {
            String[] newHashTable = new String[reporters[reporter][0]];
            int[] newNewsTable = new int[reporters[reporter][0]];
            for (int i = 0; i < hashTables[reporter].length; i++) {
                if (hashTables[reporter][i] != null) {
                    int newIndex = Math.toIntExact(hashFunction(hashTables[reporter][i].toCharArray(),
                            reporters[reporter][0]));
                    if (newHashTable[newIndex] == null) {
                        newHashTable[newIndex] = hashTables[reporter][i];
                        newNewsTable[newIndex] = newsTable[reporter][i];
                    }
                    else {
                        boolean flag = true;
                        int startIndex = newIndex;
                        while (flag) {
                            startIndex++;
                            if (startIndex >= newHashTable.length) {
                                startIndex = 0;
                            }
                            if (startIndex == newIndex) {
                                System.err.println("Error in reHash");
                                System.exit(1);
                            }
                            if (newHashTable[startIndex] == null) {
                                newHashTable[startIndex] = hashTables[reporter][i];
                                newNewsTable[startIndex] = newsTable[reporter][i];
                                flag = false;
                            }
                        }
                    }
                }
            }
            hashTables[reporter] = newHashTable;
            newsTable[reporter] = newNewsTable;
        }
    }
}
