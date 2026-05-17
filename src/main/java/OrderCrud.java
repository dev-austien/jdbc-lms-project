import java.sql.*;
import java.util.Scanner;

public class OrderCrud {

    static Scanner sc = new Scanner(System.in);

    // ================= VIEW ALL ORDERS =================

    public static void viewAllOrders() {

        try {

            Connection con = DBConnection.getConnection();

            String sql =
                    "SELECT o.order_id, u.username, s.service_type, " +
                            "o.weight, o.total_amount, o.status, o.order_date " +
                            "FROM orders o " +
                            "JOIN users u ON o.user_id = u.user_id " +
                            "JOIN services s ON o.service_id = s.service_id";

            PreparedStatement pst = con.prepareStatement(sql);

            ResultSet rs = pst.executeQuery();

            System.out.println("\n===== ALL ORDERS =====");

            while (rs.next()) {

                System.out.println(
                        rs.getInt("order_id") + " | " +
                                rs.getString("username") + " | " +
                                rs.getString("service_type") + " | " +
                                rs.getDouble("weight") + "kg | " +
                                rs.getDouble("total_amount") + " | " +
                                rs.getString("status") + " | " +
                                rs.getTimestamp("order_date")
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ================= CREATE ORDER =================

    public static void createOrder(int userId) {

        try {

            Connection con = DBConnection.getConnection();

            // SHOW SERVICES
            String show = "SELECT * FROM services";

            PreparedStatement pstShow =
                    con.prepareStatement(show);

            ResultSet rs = pstShow.executeQuery();

            System.out.println("\n===== SERVICES =====");

            while (rs.next()) {

                System.out.println(
                        rs.getInt("service_id") + " | " +
                                rs.getString("service_type") + " | " +
                                rs.getDouble("price")
                );
            }

            System.out.print("\nEnter Service ID: ");
            int serviceId = sc.nextInt();

            System.out.print("Enter Weight (kg): ");
            double weight = sc.nextDouble();

            // GET PRICE
            String priceQuery =
                    "SELECT price FROM services WHERE service_id=?";

            PreparedStatement pstPrice =
                    con.prepareStatement(priceQuery);

            pstPrice.setInt(1, serviceId);

            ResultSet rsPrice = pstPrice.executeQuery();

            double price = 0;

            if (rsPrice.next()) {

                price = rsPrice.getDouble("price");
            }

            // COMPUTE TOTAL
            double total = Math.ceil(weight / 8.0) * price;

            // INSERT ORDER
            String sql =
                    "INSERT INTO orders(user_id, service_id, weight, total_amount) " +
                            "VALUES (?, ?, ?, ?)";

            PreparedStatement pst =
                    con.prepareStatement(sql);

            pst.setInt(1, userId);
            pst.setInt(2, serviceId);
            pst.setDouble(3, weight);
            pst.setDouble(4, total);

            pst.executeUpdate();

            System.out.println("\nOrder Created Successfully!");
            System.out.println("Total Amount: " + total);

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    // ================= VIEW MY ORDERS =================

    public static void viewMyOrders(int userId) {

        try {

            Connection con = DBConnection.getConnection();

            String sql =
                    "SELECT o.order_id, s.service_type, o.weight, " +
                            "o.total_amount, o.status, o.order_date " +
                            "FROM orders o " +
                            "JOIN services s ON o.service_id = s.service_id " +
                            "WHERE o.user_id=?";

            PreparedStatement pst =
                    con.prepareStatement(sql);

            pst.setInt(1, userId);

            ResultSet rs = pst.executeQuery();

            System.out.println("\n===== MY ORDERS =====");

            while (rs.next()) {

                System.out.println(
                        rs.getInt("order_id") + " | " +
                                rs.getString("service_type") + " | " +
                                rs.getDouble("weight") + "kg | " +
                                rs.getDouble("total_amount") + " | " +
                                rs.getString("status") + " | " +
                                rs.getTimestamp("order_date")
                );
            }

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    // ================= UPDATE STATUS =================

    public static void updateStatus() {

        try {

            Connection con = DBConnection.getConnection();

            viewAllOrders();

            System.out.print("\nEnter Order ID: ");
            int orderId = sc.nextInt();

            System.out.println("\n===== STATUS =====");
            System.out.println("1. PENDING");
            System.out.println("2. WASHING");
            System.out.println("3. READY");
            System.out.println("4. CLAIMED");

            System.out.print("Enter Choice: ");
            int choice = sc.nextInt();

            String status = "";

            switch (choice) {

                case 1:
                    status = "PENDING";
                    break;

                case 2:
                    status = "WASHING";
                    break;

                case 3:
                    status = "READY";
                    break;

                case 4:
                    status = "CLAIMED";
                    break;

                default:
                    System.out.println("Invalid Status!");
                    return;
            }

            String sql =
                    "UPDATE orders SET status=? WHERE order_id=?";

            PreparedStatement pst =
                    con.prepareStatement(sql);

            pst.setString(1, status);
            pst.setInt(2, orderId);

            pst.executeUpdate();

            System.out.println("\nOrder Status Updated!");

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    // ================= DELETE ORDER =================

    public static void deleteOrder() {

        try {

            Connection con = DBConnection.getConnection();

            viewAllOrders();

            System.out.print("\nEnter Order ID to Delete: ");
            int orderId = sc.nextInt();

            String sql =
                    "DELETE FROM orders WHERE order_id=?";

            PreparedStatement pst =
                    con.prepareStatement(sql);

            pst.setInt(1, orderId);

            pst.executeUpdate();

            System.out.println("\nOrder Deleted Successfully!");

        } catch (Exception e) {

            e.printStackTrace();
        }
    }
}