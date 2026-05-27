import java.util.Optional;

public class Main {

    private static int loggedInUserId;
    private static String loggedInRole;

    public static void main(String[] args) {
        while (true) {
            System.out.println("\n===== LAUNDRY SERVICE MANAGEMENT SYSTEM =====");
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.println("3. Exit");

            switch (InputHelper.readInt("Enter choice: ")) {
                case 1 -> handleLogin();
                case 2 -> handleRegister();
                case 3 -> {
                    System.out.println("\nSystem exit...");
                    InputHelper.close();
                    return;
                }
                default -> System.out.println("\nInvalid choice.");
            }
        }
    }

    private static void handleLogin() {
        String username = InputHelper.readLine("Enter username: ");
        String password = InputHelper.readLine("Enter password: ");

        Optional<UserCrud.LoginResult> result = UserCrud.authenticate(username, password);
        if (result.isEmpty()) {
            System.out.println("\nInvalid username or password.");
            return;
        }

        loggedInUserId = result.get().userId();
        loggedInRole = result.get().role();
        System.out.println("\nLogin successful.");
        openRoleMenu(loggedInRole);
    }

    private static void handleRegister() {
        String username = InputHelper.readLine("Enter username: ");
        String password = InputHelper.readLine("Enter password: ");
        String role = InputHelper.readLine("Enter role (STAFF/CUSTOMER): ").toUpperCase();

        if (role.equalsIgnoreCase("admin")) {
            System.out.println("Registration Failed");
            main(null);
            return;
        }

        System.out.println("\n--- Profile ---");
        String firstName = InputHelper.readLine("First name: ");
        String middleName = InputHelper.readLine("Middle name (optional): ");
        String lastName = InputHelper.readLine("Last name: ");
        String suffix = InputHelper.readLine("Suffix (optional): ");
        String phone = InputHelper.readLine("Phone number (optional): ");

        UserCrud.ProfileInput profile = new UserCrud.ProfileInput(
                firstName, middleName, lastName, suffix, phone
        );

        if (UserCrud.registerUser(username, password, role, profile)) {
            System.out.println("\nRegistration successful.");
        }
    }

    private static void openRoleMenu(String role) {
        if ("ADMIN".equalsIgnoreCase(role)) {
            adminMenu();
        } else if ("STAFF".equalsIgnoreCase(role)) {
            staffMenu();
        } else if ("CUSTOMER".equalsIgnoreCase(role)) {
            customerMenu();
        } else {
            System.out.println("\nUnknown role. Logging out.");
        }
    }

    private static void adminMenu() {
        while (true) {
            System.out.println("\n===== ADMIN MENU =====");
            System.out.println("1. Manage Services");
            System.out.println("2. View Orders");
            System.out.println("3. Delete Orders");
            System.out.println("4. Logout");

            switch (InputHelper.readInt("Enter choice: ")) {
                case 1 -> ServiceCrud.serviceMenu();
                case 2 -> OrderCrud.viewAllOrders();
                case 3 -> OrderCrud.deleteOrder();
                case 4 -> {
                    System.out.println("\nLogging out...");
                    return;
                }
                default -> System.out.println("\nInvalid choice.");
            }
        }
    }

    private static void staffMenu() {
        while (true) {
            System.out.println("\n===== STAFF MENU =====");
            System.out.println("1. View Orders");
            System.out.println("2. Update Order Status");
            System.out.println("3. Logout");

            switch (InputHelper.readInt("Enter choice: ")) {
                case 1 -> OrderCrud.viewAllOrders();
                case 2 -> OrderCrud.updateStatus();
                case 3 -> {
                    System.out.println("\nLogging out...");
                    return;
                }
                default -> System.out.println("\nInvalid choice.");
            }
        }
    }

    private static void customerMenu() {
        while (true) {
            System.out.println("\n===== CUSTOMER MENU =====");
            System.out.println("1. Create Order");
            System.out.println("2. View My Orders");
            System.out.println("3. Logout");

            switch (InputHelper.readInt("Enter choice: ")) {
                case 1 -> OrderCrud.createOrder(loggedInUserId);
                case 2 -> OrderCrud.viewMyOrders(loggedInUserId);
                case 3 -> {
                    System.out.println("\nLogging out...");
                    return;
                }
                default -> System.out.println("\nInvalid choice.");
            }
        }
    }
}