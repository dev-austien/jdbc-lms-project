import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {

    public static Connection getConnection() {

        try {

            // LOAD MYSQL JDBC DRIVER
            Class.forName("com.mysql.cj.jdbc.Driver");

            Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/lsms_db",
                "root",
                ""
            );

            System.out.println("Database Connected Successfully!");

            return conn;

        } catch (Exception e) {

            System.out.println(
                "Connection Failed! Check if XAMPP is running and 'lsms_db' exists."
            );

            e.printStackTrace();

            return null;
        }
    }
}
