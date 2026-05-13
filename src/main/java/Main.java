import java.sql.Connection;
import java.util.Scanner;

public class Main {
    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {

        Connection myConn = DBConnection.getConnection();

        if (myConn != null) {
            System.out.println("Status: You are ready to code your LSMS CRUD operations!");
        } else {
            System.out.println("Status: Database connection failed.");
        }

        int login_attemp = 1;
        String username;
        String password;
        if (login_attemp < 3) {
            System.out.print("username: ");
            username = sc.nextLine();
            System.out.print("password: ");
            password =  sc.nextLine();
        }
    }
}
