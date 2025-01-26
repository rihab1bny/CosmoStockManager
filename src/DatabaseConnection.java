import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/gestion_produits_cosmetiques";
    private static final String USER = "root";
    private static final String PASSWORD = "rihab123";

    public static Connection getConnection() throws SQLException {
        System.out.println("attendez quelques secondes svp");
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC driver not found!");
            e.printStackTrace();
            throw new SQLException("JDBC Driver not found", e);
        }
        try {
            Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
            return connection;
        } catch (SQLException e) {
            System.err.println("Error while establishing database connection: " + e.getMessage());
            throw e;
        }
    }
}