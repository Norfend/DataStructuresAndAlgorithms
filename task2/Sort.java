package task2;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Sort {
    public void homeWork02() throws IOException {
        BufferedInputStream inputStream = new BufferedInputStream(System.in);
        byte[] array = new byte[inputStream.available()];
        inputStream.read(array);
        inputStream.close();
        String[] inputtedValues = new String(array, StandardCharsets.UTF_8).split("\n");
        System.out.println();
    }
}
