import java.util.Scanner;

public class InputHelper {

    private static final Scanner scanner = new Scanner(System.in);

    private InputHelper() {
    }

    public static String readLine(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }

    public static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            if (scanner.hasNextInt()) {
                int value = scanner.nextInt();
                consumeNewline();
                return value;
            }
            System.out.println("\nInvalid input! Please enter a number.");
            scanner.next();
        }
    }

    public static double readDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            if (scanner.hasNextDouble()) {
                double value = scanner.nextDouble();
                consumeNewline();
                return value;
            }
            System.out.println("\nInvalid input! Please enter a number.");
            scanner.next();
        }
    }

    private static void consumeNewline() {
        if (scanner.hasNextLine()) {
            scanner.nextLine();
        }
    }

    public static void close() {
        scanner.close();
    }
}