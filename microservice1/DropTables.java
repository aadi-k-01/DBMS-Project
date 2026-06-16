import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class DropTables {
    public static void main(String[] args) {
        try (Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5433/MyDb", "postgres", "Adarsh@123");
             Statement stmt = conn.createStatement()) {
            stmt.execute("DROP TABLE IF EXISTS time_slots CASCADE;");
            stmt.execute("DROP TABLE IF EXISTS appointments CASCADE;");
            System.out.println("Tables dropped successfully. Spring Boot will now recreate them with correct UUID string types!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
