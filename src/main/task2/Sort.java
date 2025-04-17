package task2;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.util.Arrays;

import static java.lang.System.exit;

public class Sort {

    private static int maximalValue = -1;

    private static int typeOfSequence = -1;

    private static int virus = -1;

    private static int[] inputArray = new int[1000];

    private static int elements = 0;

    public static void main(String[] args) {
        readInput();
        ErrorChecker.printChecks();
        if (typeOfSequence == 0) {
            radix();
            printArray(false);
        }

        if (virus == 0 && typeOfSequence == 1) {
            printArray(false);
        }

        if (virus == 0 && typeOfSequence == 2) {
            printArray(true);
        }

        if (virus == 1 && typeOfSequence == 1) {
            insertion();
            printArray(false);
        }

        if (virus == 1 && typeOfSequence == 2) {
            insertion();
            printArray(true);
        }
    }

    private static void addNumber(int input) {
        if (elements == inputArray.length) {
            inputArray = Arrays.copyOf(inputArray, elements * 2);
        }

        if (input < 1 || input > maximalValue) {
            ErrorChecker.setWrongNumber();
        }

        inputArray[elements] = input;

        if (typeOfSequence == 1 && virus != 1 && elements != 0 && inputArray[elements - 1] > inputArray[elements]) {
            ErrorChecker.setNotOrdered();
        }

        if (typeOfSequence == 2 && virus != 1 && elements != 0 && inputArray[elements - 1] < inputArray[elements]) {
            ErrorChecker.setNotOrdered();
        }
        elements++;
    }

    private static void printArray(boolean reverse) {
        try (BufferedOutputStream output = new BufferedOutputStream(System.out)){
            if (! reverse) {
                for (int i = 0; i < elements; i++) {
                    String numStr = inputArray[i] + "\n";
                    byte[] bytes = numStr.getBytes();

                    output.write(bytes);
                }
            } else {
                for (int i = elements - 1; i >= 0; i--) {
                    String numStr = inputArray[i] + "\n";
                    byte[] bytes = numStr.getBytes();

                    output.write(bytes);
                }
            }
            output.flush();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    private static void readInput() {
        boolean nextLine = false;
        boolean header = false;

        try(BufferedInputStream input = new BufferedInputStream(System.in)) {
            int symbol = input.read();
            while (symbol != -1) {
                Num.addSymbol(symbol);
                if ((symbol == 10 || symbol == 13) && ! nextLine) {
                    nextLine = true;
                    if (header) {
                        addNumber(Integer.parseInt(Num.getNumber()));
                    } else {
                        headerParser(Num.getNumber());
                        header = true;
                    }
                } else {
                    nextLine = false;
                }
                symbol = input.read();
            }

            inputArray = Arrays.copyOfRange(inputArray, 0, elements);

            if (inputArray.length < 1000) {
                ErrorChecker.setShortInput();
            }

            if (inputArray.length > 2000000) {
                ErrorChecker.setLongInput();
            }
        }
        catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    private static void headerParser(String input) {
        String[] header = input.split(" ");
        if (header.length == 3) {
            maximalValue = Integer.parseInt(header[0]);
            if (maximalValue < 1) {
                ErrorChecker.setNegativeNumber();
            }

            typeOfSequence = Integer.parseInt(header[1]);
            if (typeOfSequence < 0 || 2 < typeOfSequence) {
                ErrorChecker.setWrongSequence();
            }

            virus = Integer.parseInt(header[2]);
            if (virus != 0 && virus != 1) {
                ErrorChecker.setWrongVirus();
            }
        }
        else {
            ErrorChecker.setWrongHeader();
        }
    }

    private static void insertion() {
        int arrayLength = inputArray.length;
        for (int i = 1; i < arrayLength; i++) {
            int carriage = inputArray[i];
            int j = i - 1;
            while (j >= 0 && inputArray[j] > carriage) {
                inputArray[j + 1] = inputArray[j];
                j--;
            }
            inputArray[j + 1] = carriage;
        }
    }

    private static void radix() {
        int[][] radixArray = new int[10][inputArray.length];
        int[] counts = new int[10];
        int divider = 1;

        while (maximalValue / divider > 0) {
            for (int value : inputArray) {
                int radixIndex = (value / divider) % 10;
                radixArray[radixIndex][counts[radixIndex]] = value;
                counts[radixIndex]++;
            }

            int position = 0;
            for (int i = 0; i < 10; i++) {
                for (int j = 0; j < counts[i]; j++) {
                    inputArray[position] = radixArray[i][j];
                    position++;
                }
                counts[i] = 0;
            }
            divider *= 10;
        }
    }

    private static class Num {

        private static char[] inputSymbol = new char[1];

        private static int numberOfElements = 0;

        public static void addSymbol(int symbol) {
            if (numberOfElements == inputSymbol.length) {
                inputSymbol = Arrays.copyOf(inputSymbol, numberOfElements * 2);
            }
            inputSymbol[numberOfElements] = (char) symbol;
            numberOfElements++;
        }

        public static String getNumber() {
            String result = new String(inputSymbol, 0, numberOfElements - 1);
            inputSymbol = new char[1];
            numberOfElements = 0;
            return result.trim();
        }
    }

    private static class ErrorChecker {
        private static Boolean wrongHeader = false;
        private static Boolean negativeNumber = false;
        private static Boolean wrongSequence = false;
        private static Boolean wrongVirus = false;
        private static Boolean wrongNumber = false;
        private static Boolean notOrdered = false;
        private static Boolean shortInput = false;
        private static Boolean longInput = false;

        public static void printChecks() {
            if (wrongHeader) {
                System.err.println("Error: Chybna hlavicka souboru!");
                exit(1);
            } else if (negativeNumber) {
                System.err.println("Error: Maximum neni kladne!");
                exit(1);
            } else if (wrongSequence) {
                System.err.println("Error: Neznamy typ razeni posloupnosti!");
                exit(1);
            } else if (wrongVirus) {
                System.err.println("Error: Nelze urcit, zda posloupnost napadl virus!");
                exit(1);
            } else if (wrongNumber) {
                System.err.println("Error: Prvek posloupnosti je mimo rozsah!");
                exit(1);
            } else if (notOrdered) {
                System.err.println("Error: Posloupnost neni usporadana!");
                exit(1);
            } else if (shortInput) {
                System.err.println("Error: Posloupnost ma mene nez 1000 prvku!");
                exit(1);
            } else if (longInput) {
                System.err.println("Error: Posloupnost ma vic nez 2000000 prvku!");
                exit(1);
            }
        }

        public static void setWrongHeader() {
            if (!wrongHeader) {
                wrongHeader = true;
            }
        }

        public static void setNegativeNumber() {
            if (!negativeNumber) {
                ErrorChecker.negativeNumber = true;
            }
        }

        public static void setWrongSequence() {
            if (!wrongSequence) {
                wrongSequence = true;
            }
        }

        public static void setWrongVirus() {
            if (!wrongVirus) {
                wrongVirus = true;
            }
        }

        public static void setWrongNumber() {
            if (!wrongNumber) {
                wrongNumber = true;
            }
        }

        public static void setNotOrdered() {
            if (!notOrdered) {
                notOrdered = true;
            }
        }

        public static void setShortInput() {
            if (!shortInput) {
                shortInput = true;
            }
        }

        public static void setLongInput() {
            if (!longInput) {
                longInput = true;
            }
        }
    }
}