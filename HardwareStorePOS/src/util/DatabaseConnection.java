package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseConnection {

    private static final String URL = "jdbc:mysql://localhost:3306/hardwareinventorypos"; // Database URL
    private static final String USER = "root"; // Database username
    private static final String PASSWORD = ""; // Database password
    private static final Logger LOGGER = Logger.getLogger(DatabaseConnection.class.getName());

    // Private constructor to prevent instantiation
    private DatabaseConnection() {
    }

    // Method to establish and return a connection to the database
    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to connect to the database", e);
            return null;
        }
    }
}
