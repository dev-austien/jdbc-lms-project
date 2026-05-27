import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class ServiceCrud {

    private static final String LIST_SQL =
            """
            SELECT service_id, service_type, description, unit_price, pricing_unit
            FROM services
            ORDER BY service_id
            """;

    private ServiceCrud() {
    }

    public static void addService() {
        String type = InputHelper.readLine("Enter service name: ");
        String description = InputHelper.readLine("Enter description: ");
        PricingUnit unit = promptPricingUnit();
        double unitPrice = InputHelper.readDouble("Enter unit price: ");

        String sql =
                "INSERT INTO services (service_type, description, unit_price, pricing_unit) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, type);
            pstmt.setString(2, description);
            pstmt.setDouble(3, unitPrice);
            pstmt.setString(4, unit.name());
            pstmt.executeUpdate();
            System.out.println("\nService added successfully.");
        } catch (SQLException e) {
            System.out.println("Operation failed: " + e.getMessage());
        }
    }

    public static void viewServices() {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(LIST_SQL);
             ResultSet rs = pstmt.executeQuery()) {

            System.out.println("\n===== SERVICES =====");
            boolean found = false;
            while (rs.next()) {
                found = true;
                printServiceCard(mapRow(rs));
            }
            if (!found) {
                System.out.println("No services available.");
            }
        } catch (SQLException e) {
            System.out.println("Could not load services: " + e.getMessage());
            System.out.println("Hint: Update the database — run schema.sql or migrate.sql in src/main/database/");
        }
    }

    public static void updateService() {
        viewServices();

        int id = InputHelper.readInt("\nEnter service ID to update: ");
        String type = InputHelper.readLine("Enter new service name: ");
        String description = InputHelper.readLine("Enter new description: ");
        PricingUnit unit = promptPricingUnit();
        double unitPrice = InputHelper.readDouble("Enter new unit price: ");

        String sql =
                """
                UPDATE services
                SET service_type = ?, description = ?, unit_price = ?, pricing_unit = ?
                WHERE service_id = ?
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, type);
            pstmt.setString(2, description);
            pstmt.setDouble(3, unitPrice);
            pstmt.setString(4, unit.name());
            pstmt.setInt(5, id);

            if (pstmt.executeUpdate() > 0) {
                System.out.println("\nService updated successfully.");
            } else {
                System.out.println("\nNo service found with that ID.");
            }
        } catch (SQLException e) {
            System.out.println("Update failed: " + e.getMessage());
        }
    }

    public static void deleteService() {
        viewServices();

        int id = InputHelper.readInt("\nEnter service ID to delete: ");
        String sql = "DELETE FROM services WHERE service_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            System.out.println("\nService deleted successfully.");
        } catch (SQLException e) {
            System.out.println(
                    "\nDelete failed. A service linked to past orders cannot be removed: " + e.getMessage());
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

            switch (InputHelper.readInt("Enter choice: ")) {
                case 1 -> addService();
                case 2 -> viewServices();
                case 3 -> updateService();
                case 4 -> deleteService();
                case 5 -> {
                    return;
                }
                default -> System.out.println("\nInvalid choice.");
            }
        }
    }

    static ServiceDetails findService(int serviceId) throws SQLException {
        String sql =
                """
                SELECT service_id, service_type, description, unit_price, pricing_unit
                FROM services
                WHERE service_id = ?
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, serviceId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }

        throw new SQLException("Service not found: " + serviceId);
    }

    private static ServiceDetails mapRow(ResultSet rs) throws SQLException {
        return new ServiceDetails(
                rs.getInt("service_id"),
                rs.getString("service_type"),
                rs.getString("description"),
                rs.getDouble("unit_price"),
                PricingUnit.fromDb(rs.getString("pricing_unit"))
        );
    }

    private static void printServiceCard(ServiceDetails service) {
        System.out.println();
        System.out.printf("[%d] %s%n", service.serviceId(), service.serviceType());
        System.out.printf("     Price: %.2f (%s)%n", service.unitPrice(), service.pricingUnit().label());
        System.out.printf("     %s%n", service.description());
    }

    private static PricingUnit promptPricingUnit() {
        System.out.println("\nPricing unit:");
        System.out.println("1. PER_KG (price per 8 kg — wash / express services)");
        System.out.println("2. PER_PIECE (price × pieces — comforters, dry cleaning)");

        return switch (InputHelper.readInt("Enter choice: ")) {
            case 1 -> PricingUnit.PER_KG;
            case 2 -> PricingUnit.PER_PIECE;
            default -> {
                System.out.println("Invalid choice. Defaulting to PER_KG.");
                yield PricingUnit.PER_KG;
            }
        };
    }
}
