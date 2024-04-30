package task2;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;

public class Sort {
    private Integer maximumValue;
    private Integer typeOfSequence;
    private Integer virus;
    public void homeWork02() {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        ArrayList<Integer> inputArray = inputHandler(bufferedReader);
        if (typeOfSequence == 0) {
            outputSequence(radix(inputArray, maximumValue));
        }
        if (typeOfSequence == 1 && virus == 0) {
            outputSequence(inputArray);
        }
        if (typeOfSequence == 2 && virus == 0) {
            Collections.reverse(inputArray);
            outputSequence(inputArray);
        }
        if (typeOfSequence == 1 && virus == 1) {
            outputSequence(insertion(inputArray));
        }
        if (typeOfSequence == 2 && virus == 1) {
            Collections.reverse(inputArray);
            outputSequence(insertion(inputArray));
        }
    }

    private ArrayList<Integer> inputHandler(BufferedReader bufferedReader) {
        ArrayList<Integer> result = new ArrayList<>();
        String line;
        try {
            firstLineDecomposition(bufferedReader.readLine().split(" "));
            while ((line = bufferedReader.readLine()) != null && !line.isEmpty()) {
                Integer number = Integer.parseInt(line.trim());
                result.add(number);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Integer previousNumber = result.getFirst();
        for (int i = 1; i < result.size(); i++) {
            if (lineValidation(result.get(i))) {
                Integer temp = result.get(i);
                if (typeOfSequence == 1 && virus != 1) {
                    if (previousNumber <= temp) {
                        previousNumber = temp;
                    }
                    else {
                        System.err.println("Error: Posloupnost neni usporadana!");
                        System.exit(1);
                    }
                }
                else if (typeOfSequence == 2 && virus != 1) {
                    if (previousNumber >= temp) {
                        previousNumber = temp;
                    }
                    else {
                        System.err.println("Error: Posloupnost neni usporadana!");
                        System.exit(1);
                    }
                }
            }
            else {
                System.err.println("Error: Prvek posloupnosti je mimo rozsah!");
                System.exit(1);
            }
        }
        if ((result.size()) < 1000) {
            System.err.println("Error: Posloupnost ma mene nez 1000 prvku!");
            System.exit(1);
        }
        if ((result.size()) > 2000000) {
            System.err.println("Error: Posloupnost ma vic nez 2000000 prvku!");
            System.exit(1);
        }
        return result;
    }

    private void firstLineDecomposition(String[] inputLine) {
        maximumValue = Integer.parseInt(inputLine[0]);
        typeOfSequence = Integer.parseInt(inputLine[1]);
        virus = Integer.parseInt(inputLine[2]);
        if (maximumValue < 1) {
            System.err.println("Error: Maximum neni kladne!");
            System.exit(1);
        }
        else if (typeOfSequence < 0 || 2 < typeOfSequence) {
            System.err.println("Error: Neznamy typ razeni posloupnosti!");
            System.exit(1);
        }
        else if (virus < 0 || 1 < virus) {
            System.err.println("Error: Nelze urcit, zda posloupnost napadl virus!");
            System.exit(1);
        }
    }

    private Boolean lineValidation(Integer inputInteger) {
        return inputInteger >= 1 && maximumValue >= inputInteger;
    }

    private void outputSequence(ArrayList<Integer> input) {
        try {
            BufferedWriter outputSequence = new BufferedWriter(new OutputStreamWriter(System.out));
            for (int number : input) {
                outputSequence.write(number);
                outputSequence.newLine();
            }
            outputSequence.flush();
            outputSequence.close();
        }
        catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    private ArrayList<Integer> radix(ArrayList<Integer> inputArray, Integer maximum) {
        Integer[][] radixArray = new Integer[10][inputArray.size()];
        int[] counts = new int[10];
        Integer divider = 1;
        while (maximum / divider > 0) {
            for (Integer value: inputArray) {
                int radixIndex = (value / divider) % 10;
                radixArray[radixIndex][counts[radixIndex]] = value;
                counts[radixIndex]++;
            }
            int position = 0;
            for (int i = 0; i < 10; i++) {
                for (int j = 0; j < counts[i]; j++) {
                    inputArray.set(position, radixArray[i][j]);
                    position++;
                }
                counts[i] = 0;
            }
            divider *= 10;
        }
        return inputArray;
    }

    private ArrayList<Integer> insertion(ArrayList<Integer> inputArray) {
        int arrayLength = inputArray.size();
        for (int i = 1; i < arrayLength; i++) {
            int carriage = inputArray.get(i);
            int j = i - 1;
            while (j >= 0 && inputArray.get(j) > carriage) {
                inputArray.set(j + 1, inputArray.get(j));
                j = j - 1;
            }
            inputArray.set(j + 1, carriage);
        }
        return inputArray;
    }
}
