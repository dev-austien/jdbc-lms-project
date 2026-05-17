import java.util.*;
import java.sql.*;

public class ServiceCrud {

    // FIX 1: Declare and initialize the Scanner so 'sc' works everywhere in this file
    private static final Scanner sc = new Scanner(System.in);

    public static void addService() {
        // FIX 2: Declare Connection outside to properly close it later
        Connection con = null;
        try {
            con = DBConnection.getConnection();

            System.out.print("Enter Service Type: ");
            String serviceType = sc.nextLine();

            System.out.print("Enter Price: ");
            double price = sc.nextDouble();
            sc.nextLine(); // FIX 3: Clear the scanner buffer newline character!

            String sql = "INSERT INTO services(service_type, price) VALUES (?, ?)";
            PreparedStatement pst = con.prepareStatement(sql);

            pst.setString(1, serviceType);
            pst.setDouble(2, price);

            pst.executeUpdate();
            pst.close(); // Close statement

            System.out.println("\nService Added Successfully!");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // FIX 4: Explicitly close connections to avoid memory leaks
            try { if (con != null) con.close(); } catch (SQLException ex) { ex.printStackTrace(); }
        }
    }

    public static void viewServices() {
        Connection con = null;
        try {
            con = DBConnection.getConnection();

            String sql = "SELECT * FROM services";
            PreparedStatement pst = con.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();

            System.out.println("\n===== SERVICES =====");
            while (rs.next()) {
                System.out.println(
                        rs.getInt("service_id") + " | " +
                                rs.getString("service_type") + " | " +
                                rs.getDouble("price")
                );
            }

            rs.close();
            pst.close();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if (con != null) con.close(); } catch (SQLException ex) { ex.printStackTrace(); }
        }
    }

    public static void updateService() {
        Connection con = null;
        try {
            con = DBConnection.getConnection();

            // Run display utility method
            viewServices();

            System.out.print("\nEnter Service ID to Update: ");
            int id = sc.nextInt();
            sc.nextLine(); // Clear buffer

            System.out.print("Enter New Service Type: ");
            String type = sc.nextLine();

            System.out.print("Enter New Price: ");
            double price = sc.nextDouble();
            sc.nextLine(); // Clear buffer

            String sql = "UPDATE services SET service_type=?, price=? WHERE service_id=?";
            PreparedStatement pst = con.prepareStatement(sql);

            pst.setString(1, type);
            pst.setDouble(2, price);
            pst.setInt(3, id);

            pst.executeUpdate();
            pst.close();

            System.out.println("\nService Updated Successfully!");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if (con != null) con.close(); } catch (SQLException ex) { ex.printStackTrace(); }
        }
    }

    public static void deleteService() {
        Connection con = null;
        try {
            con = DBConnection.getConnection();

            viewServices();

            System.out.print("\nEnter Service ID to Delete: ");
            int id = sc.nextInt();
            sc.nextLine(); // Clear buffer

            String sql = "DELETE FROM services WHERE service_id=?";
            PreparedStatement pst = con.prepareStatement(sql);

            pst.setInt(1, id);

            pst.executeUpdate();
            pst.close();

            System.out.println("\nService Deleted Successfully!");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if (con != null) con.close(); } catch (SQLException ex) { ex.printStackTrace(); }
        }
    }

    public static void serviceMenu() {

        while (true) {

            System.out.println("\n===== SERVICE MENU =====");
            System.out.println("1. Add Service");
            System.out.println("2. View Services");
            System.out.println("3. Update Service");
            System.out.println("4. Delete Service");
            System.out.println("5. Back");

            System.out.print("Enter Choice: ");

            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {

                case 1:
                    addService();
                    break;

                case 2:
                    viewServices();
                    break;

                case 3:
                    updateService();
                    break;

                case 4:
                    deleteService();
                    break;

                case 5:
                    return;

                default:
                    System.out.println("Invalid Choice!");
            }
        }
    }
}