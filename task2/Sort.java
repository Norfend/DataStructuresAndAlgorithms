package task2;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Sort {
    public void homeWork02() throws IOException {
        BufferedInputStream inputStream = new BufferedInputStream(System.in);
        System.out.println();
        Integer[] inputArray = inputHandler(inputStream);
        for (var _a: inputArray) {
            System.out.println(_a);
        }
    }
    private Integer[] inputHandler(BufferedInputStream bufferedInputStream) throws IOException {
        byte[] array = new byte[bufferedInputStream.available()];
        int readBytes = bufferedInputStream.read(array);
        bufferedInputStream.close();
        String[] inputtedValues = new String(array, StandardCharsets.UTF_8).split("\r\n");
        Integer[] result = new Integer[inputtedValues.length + 2];
        Integer[] firstLine = firstLineDecomposition(inputtedValues[0].split(" "));
            for (int i = 1; i < inputtedValues.length; i++) {
                if (lineValidation(inputtedValues[i], Integer.parseInt(firstLine[0]))) {
                    result[i + 2] = Integer.parseInt(inputtedValues[i]);
                }
                else {
                    System.err.println("Error: Prvek posloupnosti je mimo rozsah!");
                    System.exit(1);
                }
            }
        return result;
    }

    private Integer[] firstLineDecomposition(String[] inputLine) {
        if (Integer.parseInt(inputLine[0]) < 1) {
            System.err.println("Error: Maximum neni kladne!");
            System.exit(1);
        }
        else if (Integer.parseInt(inputLine[1]) < 0 && 2 < Integer.parseInt(inputLine[1])) {
            System.err.println("Error: Neznamy typ razeni posloupnosti!");
            System.exit(1);
        }
        else if (Integer.parseInt(inputLine[2]) < 0 && 1 < Integer.parseInt(inputLine[2])) {
            System.err.println("Error: Nelze urcit, zda posloupnost napadl virus!");
            System.exit(1);
        }
        return new Integer[]{Integer.parseInt(inputLine[0]), Integer.parseInt(inputLine[1]),
                Integer.parseInt(inputLine[2])};
    }

    private Boolean lineValidation (String inputLine, Integer maximumValue) {
        if (Integer.parseInt(inputLine) < 1 && maximumValue > Integer.parseInt(inputLine)) {
            return false;
        }
        return true;
    }
}
