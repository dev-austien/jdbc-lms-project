import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UserCrud {

    // Procedural static method - direct database insert operation
    public static boolean registerUser(String username, String password, String role) {

        // 1. Grab the raw connection from your DBConnection class
        Connection conn = DBConnection.getConnection();

        if (conn == null) {
            System.out.println("Error: Database connection is unavailable.");
            return false;
        }

        // 2. Prepare the insert statement using placeholders (?)
        String sql = "INSERT INTO users (username, password, role) VALUES (?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Map the plain variables directly to the parameters
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.setString(3, role.toUpperCase()); // Forces uppercase to match your DB ENUM

            // 3. Run the update query
            int rowsInserted = pstmt.executeUpdate();

            // Manually close the connection since it wasn't opened in a try-with-resources here
            conn.close();

            return rowsInserted > 0;

        } catch (SQLException e) {
            System.out.println("Registration Failed: " + e.getMessage());

            // Ensure connection is cleaned up if a duplicate entry or SQL error happens
            try {
                if (conn != null) conn.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }

            return false;
        }
    }
}