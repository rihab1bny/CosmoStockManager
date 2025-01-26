import java.sql.Connection;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        try (Connection connection = DatabaseConnection.getConnection()) {
      
            Utilisateur user = new Utilisateur(1, "Admin", "admin@example.com", "password");

            Menu.menuPrincipal(user);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
