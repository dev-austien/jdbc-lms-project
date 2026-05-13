import java.sql.*;
import java.util.*;

public class CRUDOperation {

    // --- Authentication --- //

    public static boolean registerUser(String username, String password, String role ) {
        String sql = "INSERT INTO Users (usernamme, password_hash, role) VALUES (?, ? ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.setString(3, role);

            int rowAffected = pstmt.executeUpdate();
            return rowAffected > 0;
        } catch (SQLException e) {
            System.out.println("Error registering user: " + e.getMessage());
            return false;
        }
    }

    public static boolean login(String username, String password) {
        String sql = "SELECT * FROM Users WHERE username = ? AND password_hash = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);

            ResultSet rs = pstmt.executeQuery();
            return rs.next(); // Returns true if a match is found

        } catch (SQLException e) {
            System.out.println("Database error during login: " + e.getMessage());
            return false;
        }
    }




}
