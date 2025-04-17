package task2;

import java.io.BufferedInputStream;
import java.util.Arrays;

import static java.lang.System.exit;

public class Sort {

    private static int maximalValue = -1;

    private static int order = -1;

    private static int virus = -1;

    private static int[] inputArray = new int[1000];

    private static int elements = 0;

    public static void main(String[] args) {
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
        }
        catch (Exception e) {
            System.err.println(e.getMessage());
        }
        printArray();
    }

    private static void addNumber(int input) {
        if (input == -1) {
            return;
        }
        if (elements == inputArray.length) {
            inputArray = Arrays.copyOf(inputArray, elements * 2);
        }
        inputArray[elements] = input;
        elements++;
    }

    private static void printArray() {
        for(int i = 0; i < elements; i++) {
            System.out.println(inputArray[i]);
        }
    }

    private static void headerParser(String input) {
        String[] header = input.split(" ");
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
            return result;
        }
    }

    private static class errorChecker {
        private static Boolean wrongHeader = false;
        private static Boolean negativeNumber = false;
        private static Boolean wrongOrder = false;
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
            } else if (wrongOrder) {
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
                errorChecker.negativeNumber = true;
            }
        }

        public static void setWrongOrder() {
            if (!wrongOrder) {
                wrongOrder = true;
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