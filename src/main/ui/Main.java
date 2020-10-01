package ui;

import java.time.LocalDate;

public class Main {
    public static void main(String[] args) {
        LocalDate d1 = LocalDate.now().minusWeeks(1);
        System.out.println(LocalDate.now().compareTo(d1));
    }
}
