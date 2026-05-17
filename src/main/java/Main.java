import java.util.Scanner;
import java.sql.*;

public class Main {

    static Scanner sc = new Scanner(System.in);
    static int loggedInUserId;
    static String loggedInRole;

    public static void main(String[] args) {

        while (true) {
            System.out.println("\n===== LAUNDRY SERVICE MANAGEMENT SYSTEM =====");
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.println("3. Exit");
            System.out.print("Enter Choice: ");

            // Secure against non-integer inputs causing infinite loops
            if (!sc.hasNextInt()) {
                System.out.println("\nInvalid Choice! Please enter a number.");
                sc.next(); // Clear the bad input
                continue;
            }
            int choice = sc.nextInt();
            sc.nextLine(); // Consume newline left over from nextInt()

            switch (choice) {

                // ================= LOGIN =================
                case 1:
                    System.out.print("Enter Username: ");
                    String username = sc.nextLine();

                    System.out.print("Enter Password: ");
                    String password = sc.nextLine();

                    String sql = "SELECT * FROM users WHERE username=? AND password=?";

                    // Try-with-resources automatically closes con, pst, and rs
                    try (Connection con = DBConnection.getConnection();
                         PreparedStatement pst = con.prepareStatement(sql)) {
                        
                        pst.setString(1, username);
                        pst.setString(2, password);

                        try (ResultSet rs = pst.executeQuery()) {
                            if (rs.next()) {
                                loggedInUserId = rs.getInt("user_id");
                                loggedInRole = rs.getString("role");

                                System.out.println("\nLogin Successful!");

                                if ("ADMIN".equalsIgnoreCase(loggedInRole)) {
                                    adminMenu();
                                } else if ("STAFF".equalsIgnoreCase(loggedInRole)) {
                                    staffMenu();
                                } else if ("CUSTOMER".equalsIgnoreCase(loggedInRole)) {
                                    customerMenu();
                                }
                            } else {
                                System.out.println("\nInvalid Username or Password!");
                            }
                        }
                    } catch (Exception e) {
                        System.out.println("\nDatabase connection error occurred during login.");
                        e.printStackTrace();
                    }
                    break;

                // ================= REGISTER =================
                case 2:
                    System.out.print("Enter Username: ");
                    String newUsername = sc.nextLine();

                    System.out.print("Enter Password: ");
                    String newPassword = sc.nextLine();

                    System.out.print("Enter Role (ADMIN/STAFF/CUSTOMER): ");
                    String role = sc.nextLine().toUpperCase();

                    String registerSql = "INSERT INTO users (username, password, role) VALUES (?, ?, ?)";

                    try (Connection con = DBConnection.getConnection();
                         PreparedStatement pst = con.prepareStatement(registerSql)) {
                        
                        pst.setString(1, newUsername);
                        pst.setString(2, newPassword);
                        pst.setString(3, role);

                        int rowsInserted = pst.executeUpdate();
                        if (rowsInserted > 0) {
                            System.out.println("\nRegistration Successful!");
                        }
                    } catch (Exception e) {
                        System.out.println("\nError: Registration failed.");
                        e.printStackTrace();
                    }
                    break;

                // ================= EXIT =================
                case 3:
                    System.out.println("\nSystem Exit...");
                    sc.close(); // Clean up scanner resource
                    System.exit(0);
                    break;

                default:
                    System.out.println("\nInvalid Choice!");
            }
        }
    }

    // ================= ADMIN MENU =================
    public static void adminMenu() {
        while (true) {
            System.out.println("\n===== ADMIN MENU =====");
            System.out.println("1. Manage Services");
            System.out.println("2. View Orders");
            System.out.println("3. Delete Orders");
            System.out.println("4. Logout");
            System.out.print("Enter Choice: ");

            if (!sc.hasNextInt()) {
                System.out.println("\nInvalid Choice!");
                sc.next();
                continue;
            }
            int choice = sc.nextInt();
            sc.nextLine(); 

            switch (choice) {
                case 1:
                    ServiceCrud.serviceMenu();
                    break;
                case 2:
                    OrderCrud.viewAllOrders();
                    break;
                case 3:
                    OrderCrud.deleteOrder();
                    break;
                case 4:
                    System.out.println("\nLogging out...");
                    return;
                default:
                    System.out.println("\nInvalid Choice!");
            }
        }
    }

    // ================= STAFF MENU =================
    public static void staffMenu() {
        while (true) {
            System.out.println("\n===== STAFF MENU =====");
            System.out.println("1. View Orders");
            System.out.println("2. Update Order Status");
            System.out.println("3. Logout");
            System.out.print("Enter Choice: ");

            if (!sc.hasNextInt()) {
                System.out.println("\nInvalid Choice!");
                sc.next();
                continue;
            }
            int choice = sc.nextInt();
            sc.nextLine(); 

            switch (choice) {
                case 1:
                    OrderCrud.viewAllOrders();
                    break;
                case 2:
                    OrderCrud.updateStatus();
                    break;
                case 3:
                    System.out.println("\nLogging out...");
                    return;
                default:
                    System.out.println("\nInvalid Choice!");
            }
        }
    }

    // ================= CUSTOMER MENU =================
    public static void customerMenu() {
        while (true) {
            System.out.println("\n===== CUSTOMER MENU =====");
            System.out.println("1. Create Order");
            System.out.println("2. View My Orders");
            System.out.println("3. Logout");
            System.out.print("Enter Choice: ");

            if (!sc.hasNextInt()) {
                System.out.println("\nInvalid Choice!");
                sc.next();
                continue;
            }
            int choice = sc.nextInt();
            sc.nextLine(); 

            switch (choice) {
                case 1:
                    OrderCrud.createOrder(loggedInUserId);
                    break;
                case 2:
                    OrderCrud.viewMyOrders(loggedInUserId);
                    break;
                case 3:
                    System.out.println("\nLogging out...");
                    return;
                default:
                    System.out.println("\nInvalid Choice!");
            }
        }
    }
}