package client;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:C:/Users/dobri/OneDrive/Desktop/senior_project/src/main/java/client/database.db";



    public static Connection conn = null;

    public static Connection getConnection() {
        if (conn == null) {
            try {
                conn = DriverManager.getConnection(DB_URL);
                System.out.println("Connected to the database");
            } catch (SQLException e) {
                System.err.println("Error connecting to the database: " + e.getMessage());
                e.printStackTrace(); // Print the full stack trace for debugging
            }
        }
        return conn;
    }
}

