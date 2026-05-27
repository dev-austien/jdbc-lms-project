import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.Optional;

public final class UserCrud {

    private UserCrud() {
    }

    public record LoginResult(int userId, String role) {
    }

    public record ProfileInput(
            String firstName,
            String middleName,
            String lastName,
            String suffix,
            String phoneNumber
    ) {
    }

    public static Optional<LoginResult> authenticate(String username, String password) {
        String sql = "SELECT user_id, role FROM users WHERE username = ? AND password = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new LoginResult(rs.getInt("user_id"), rs.getString("role")));
                }
            }
        } catch (SQLException e) {
            System.out.println("Login failed: " + e.getMessage());
        }

        return Optional.empty();
    }

    public static boolean registerUser(
            String username,
            String password,
            String role,
            ProfileInput profile
    ) {
        String userSql = "INSERT INTO users (username, password, role) VALUES (?, ?, ?)";
        String profileSql =
                "INSERT INTO user_profile (user_id, first_name, middle_name, last_name, suffix, phone_number) "
                        + "VALUES (?, ?, ?, ?, ?, ?)";

        Connection conn = DBConnection.getConnection();
        if (conn == null) {
            return false;
        }

        try {
            conn.setAutoCommit(false);

            int userId;
            try (PreparedStatement userStmt = conn.prepareStatement(userSql, Statement.RETURN_GENERATED_KEYS)) {
                userStmt.setString(1, username);
                userStmt.setString(2, password);
                userStmt.setString(3, role.toUpperCase());

                if (userStmt.executeUpdate() == 0) {
                    conn.rollback();
                    return false;
                }

                try (ResultSet keys = userStmt.getGeneratedKeys()) {
                    if (!keys.next()) {
                        conn.rollback();
                        return false;
                    }
                    userId = keys.getInt(1);
                }
            }

            try (PreparedStatement profileStmt = conn.prepareStatement(profileSql)) {
                profileStmt.setInt(1, userId);
                profileStmt.setString(2, profile.firstName());
                setNullableString(profileStmt, 3, profile.middleName());
                profileStmt.setString(4, profile.lastName());
                setNullableString(profileStmt, 5, profile.suffix());
                setNullableString(profileStmt, 6, profile.phoneNumber());

                if (profileStmt.executeUpdate() == 0) {
                    conn.rollback();
                    return false;
                }
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            rollbackQuietly(conn);
            System.out.println("Registration failed: " + e.getMessage());
            return false;
        } finally {
            closeQuietly(conn);
        }
    }

    private static void setNullableString(PreparedStatement pstmt, int index, String value)
            throws SQLException {
        if (value == null || value.isBlank()) {
            pstmt.setNull(index, Types.VARCHAR);
        } else {
            pstmt.setString(index, value.trim());
        }
    }

    private static void rollbackQuietly(Connection conn) {
        if (conn == null) {
            return;
        }
        try {
            conn.rollback();
        } catch (SQLException ignored) {
        }
    }

    private static void closeQuietly(Connection conn) {
        if (conn == null) {
            return;
        }
        try {
            conn.close();
        } catch (SQLException ignored) {
        }
    }
}
