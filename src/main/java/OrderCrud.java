import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

public final class OrderCrud {

    private static final String LIST_ORDERS_SQL =
            """
            SELECT o.order_id,
                   u.username,
                   TRIM(CONCAT_WS(' ',
                       up.first_name,
                       NULLIF(up.middle_name, ''),
                       up.last_name,
                       NULLIF(up.suffix, ''))) AS customer_name,
                   s.service_type,
                   s.pricing_unit,
                   oi.quantity,
                   oi.price_at_purchase,
                   o.total_amount,
                   o.status,
                   o.order_date
            FROM orders o
            JOIN users u ON o.customer_id = u.user_id
            LEFT JOIN user_profile up ON u.user_id = up.user_id
            JOIN order_items oi ON o.order_id = oi.order_id
            JOIN services s ON oi.service_id = s.service_id
            """;

    private OrderCrud() {
    }

    public static void viewAllOrders() {
        runOrderQuery(LIST_ORDERS_SQL + " ORDER BY o.order_date DESC", null, "ALL ORDERS");
    }

    public static void viewMyOrders(int customerId) {
        runOrderQuery(
                LIST_ORDERS_SQL + " WHERE o.customer_id = ? ORDER BY o.order_date DESC",
                customerId,
                "MY ORDERS"
        );
    }

    public static void createOrder(int customerId) {
        ServiceCrud.viewServices();

        int serviceId = InputHelper.readInt("\nEnter service ID: ");

        try {
            ServiceDetails service = ServiceCrud.findService(serviceId);
            double quantity = readQuantity(service.pricingUnit());
            double lineTotal = OrderPricing.calculateLineTotal(quantity, service.unitPrice(), service.pricingUnit());

            insertOrderWithItem(customerId, serviceId, quantity, service.unitPrice(), lineTotal);

            System.out.println("\nOrder created successfully.");
            System.out.printf("Service: %s%n", service.serviceType());
            System.out.printf("Quantity: %s%n", formatQuantity(quantity, service.pricingUnit()));
            System.out.printf("Total amount: %.2f%n", lineTotal);
        } catch (SQLException e) {
            System.out.println("Could not create order: " + e.getMessage());
        }
    }

    public static void updateStatus() {
        viewAllOrders();

        int orderId = InputHelper.readInt("\nEnter order ID: ");
        String status = promptStatus();
        if (status == null) {
            return;
        }

        String sql = "UPDATE orders SET status = ? WHERE order_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, status);
            pstmt.setInt(2, orderId);

            if (pstmt.executeUpdate() > 0) {
                System.out.println("\nOrder status updated.");
            } else {
                System.out.println("\nNo order found with that ID.");
            }
        } catch (SQLException e) {
            System.out.println("Update failed: " + e.getMessage());
        }
    }

    public static void deleteOrder() {
        viewAllOrders();

        int orderId = InputHelper.readInt("\nEnter order ID to delete: ");
        String sql = "DELETE FROM orders WHERE order_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, orderId);

            if (pstmt.executeUpdate() > 0) {
                System.out.println("\nOrder deleted successfully.");
            } else {
                System.out.println("\nNo order found with that ID.");
            }
        } catch (SQLException e) {
            System.out.println("Delete failed: " + e.getMessage());
        }
    }

    private static double readQuantity(PricingUnit pricingUnit) {
        if (pricingUnit == PricingUnit.PER_PIECE) {
            int pieces = InputHelper.readInt(pricingUnit.quantityPrompt());
            return Math.max(pieces, 1);
        }
        double kg = InputHelper.readDouble(pricingUnit.quantityPrompt());
        return kg > 0 ? kg : 1;
    }

    private static void insertOrderWithItem(
            int customerId,
            int serviceId,
            double quantity,
            double priceAtPurchase,
            double totalAmount
    ) throws SQLException {
        String orderSql = "INSERT INTO orders (customer_id, total_amount) VALUES (?, ?)";
        String itemSql =
                "INSERT INTO order_items (order_id, service_id, quantity, price_at_purchase) VALUES (?, ?, ?, ?)";

        Connection conn = DBConnection.getConnection();
        if (conn == null) {
            throw new SQLException("Database connection unavailable.");
        }

        try {
            conn.setAutoCommit(false);

            int orderId;
            try (PreparedStatement orderStmt = conn.prepareStatement(orderSql, Statement.RETURN_GENERATED_KEYS)) {
                orderStmt.setInt(1, customerId);
                orderStmt.setDouble(2, totalAmount);
                orderStmt.executeUpdate();

                try (ResultSet keys = orderStmt.getGeneratedKeys()) {
                    if (!keys.next()) {
                        throw new SQLException("Could not create order record.");
                    }
                    orderId = keys.getInt(1);
                }
            }

            try (PreparedStatement itemStmt = conn.prepareStatement(itemSql)) {
                itemStmt.setInt(1, orderId);
                itemStmt.setInt(2, serviceId);
                itemStmt.setDouble(3, quantity);
                itemStmt.setDouble(4, priceAtPurchase);
                itemStmt.executeUpdate();
            }

            conn.commit();
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
            conn.close();
        }
    }

    private static void runOrderQuery(String sql, Integer customerId, String title) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            if (customerId != null) {
                pstmt.setInt(1, customerId);
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                System.out.println("\n===== " + title + " =====");
                printOrderHeader();

                boolean found = false;
                while (rs.next()) {
                    printOrderRow(rs);
                    found = true;
                }

                if (!found) {
                    System.out.println("No orders found.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Could not load orders: " + e.getMessage());
            System.out.println("Hint: Update the database — run schema.sql or migrate.sql in src/main/database/");
        }
    }

    private static void printOrderHeader() {
        System.out.printf(
                "%-6s | %-12s | %-18s | %-22s | %-10s | %8s | %10s | %-8s | %s%n",
                "ID", "Username", "Customer", "Service", "Qty", "Rate", "Total", "Status", "Date"
        );
    }

    private static void printOrderRow(ResultSet rs) throws SQLException {
        PricingUnit unit = PricingUnit.fromDb(rs.getString("pricing_unit"));
        double quantity = rs.getDouble("quantity");
        String customerName = rs.getString("customer_name");
        if (customerName == null || customerName.isBlank()) {
            customerName = "(no profile)";
        }

        System.out.printf(
                "%-6d | %-12s | %-18s | %-22s | %-10s | %8.2f | %10.2f | %-8s | %s%n",
                rs.getInt("order_id"),
                rs.getString("username"),
                truncate(customerName, 18),
                truncate(rs.getString("service_type"), 22),
                formatQuantity(quantity, unit),
                rs.getDouble("price_at_purchase"),
                rs.getDouble("total_amount"),
                rs.getString("status"),
                rs.getTimestamp("order_date")
        );
    }

    private static String formatQuantity(double quantity, PricingUnit unit) {
        return switch (unit) {
            case PER_KG -> String.format("%.2f kg", quantity);
            case PER_PIECE -> String.format("%d pc", (int) quantity);
        };
    }

    private static String truncate(String value, int maxLength) {
        if (value == null) {
            return "";
        }
        return value.length() <= maxLength ? value : value.substring(0, maxLength - 3) + "...";
    }

    private static String promptStatus() {
        System.out.println("\n===== STATUS =====");
        System.out.println("1. PENDING");
        System.out.println("2. WASHING");
        System.out.println("3. READY");
        System.out.println("4. CLAIMED");

        return switch (InputHelper.readInt("Enter choice: ")) {
            case 1 -> "PENDING";
            case 2 -> "WASHING";
            case 3 -> "READY";
            case 4 -> "CLAIMED";
            default -> {
                System.out.println("\nInvalid status.");
                yield null;
            }
        };
    }
}
