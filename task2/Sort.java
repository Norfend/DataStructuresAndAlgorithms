package task2;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;

public class Sort {
    private Integer maximumValue;
    private Integer typeOfSequence;
    private Integer virus;

    /**
     * This function read sequence from System input.
     * The First line consists of three elements: maximum value of a sequence,
     * type of sequence (ASC/DES/Random) and virus presence
     * Next lines are numbers of the sequence
     * After sorting function write to System output ASC sequence or write errors to System err
     */
    public void homeWork02() {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        //input data decomposition
        ArrayList<Integer> inputArray = inputHandler(bufferedReader);
        //Start of sorting algorithm
        if (typeOfSequence == 0) {
            //If a sequence not sorted - use radix algorithm
            outputSequence(radix(inputArray, maximumValue));
        }
        if (typeOfSequence == 1 && virus == 0) {
            //If a sequence is ascending sorted - print a sequence
            outputSequence(inputArray);
        }
        if (typeOfSequence == 2 && virus == 0) {
            //If a sequence is descending sorted - reverse a sequence and print it
            Collections.reverse(inputArray);
            outputSequence(inputArray);
        }
        if (typeOfSequence == 1 && virus == 1) {
            //If a sequence is ascending sorted and affected by virus - use insertion sort algorithm
            outputSequence(insertion(inputArray));
        }
        if (typeOfSequence == 2 && virus == 1) {
            //If a sequence is descending sorted and affected by virus -
            // reverse a sequence and use insertion sort algorithm
            Collections.reverse(inputArray);
            outputSequence(insertion(inputArray));
        }
    }

    /**
     *
     * @param bufferedReader take BufferedReader with inputted data from "user" with checking of data.
     *
     * @return ArrayList<Integer> with separated elements from input
     */
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
            throw new RuntimeException(e.getMessage());
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

    /**
     * Function for decomposition of the first inputted line and set value for global variable
     *
     * @param inputLine take String[] for input with [0] is maximum value of a sequence, [1] is type of sequence and [2]
     *                  is for virus parameter
     */
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

    /**
     * Function for checking inputted Integer value if it is inside interval [1;maximumValue]
     * @param inputInteger take Integer with value for checking
     * @return Boolean value true if inputted value is inside interval and false if not
     */
    private Boolean lineValidation(Integer inputInteger) {
        return inputInteger >= 1 && maximumValue >= inputInteger;
    }

    /**
     * Function for printing array into console
     *
     * @param input take ArrayList<Integer> with printable values
     */
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

    /**
     * Function with implementation of Radix sort algorithm
     *
     * @param inputArray take ArrayList<Integer> with values for sorting. All values must be greater than -1
     * @param maximum take Integer value with maximum value of an inputted sequence
     * @return ArrayList<Integer> with an ascending sorted sequence
     */
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

    /**
     * Function with implementation of Insertion sort algorithm
     *
     * @param inputArray take ArrayList<Integer> with values for sorting
     * @return ArrayList<Integer> with an ascending sorted sequence
     */
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
