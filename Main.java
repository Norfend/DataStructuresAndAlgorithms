import task2.Sort;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        Sort sort = new Sort();
        try {
            sort.homeWork02();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
