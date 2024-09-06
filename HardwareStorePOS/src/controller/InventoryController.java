package controller;

import model.Inventory;
import util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InventoryController {

    public boolean addInventoryItem(Inventory inventory) {
        String query = "INSERT INTO Inventory (product_id, change_type, quantity_change, date) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, inventory.getProductId());
            stmt.setString(2, inventory.getChangeType());
            stmt.setInt(3, inventory.getQuantityChange());
            stmt.setTimestamp(4, new Timestamp(System.currentTimeMillis()));

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateInventoryItem(Inventory inventory) {
        String query = "UPDATE Inventory SET product_id = ?, change_type = ?, quantity_change = ? WHERE inventory_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, inventory.getProductId());
            stmt.setString(2, inventory.getChangeType());
            stmt.setInt(3, inventory.getQuantityChange());
            stmt.setInt(4, inventory.getInventoryId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteInventoryItem(int inventoryId) {
        String query = "DELETE FROM Inventory WHERE inventory_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, inventoryId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Inventory> getAllInventoryItems() {
        List<Inventory> inventoryItems = new ArrayList<>();
        String query = "SELECT * FROM Inventory ORDER BY date DESC";  // Ensure it orders by created_at
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Inventory inventory = new Inventory();
                inventory.setInventoryId(rs.getInt("inventory_id"));
                inventory.setProductId(rs.getInt("product_id"));
                inventory.setChangeType(rs.getString("change_type"));
                inventory.setQuantityChange(rs.getInt("quantity_change"));
                inventory.setDate(rs.getTimestamp("date"));  // Handle date field correctly
                inventoryItems.add(inventory);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return inventoryItems;
    }

    public List<Inventory> searchInventoryItems(String query) {
        List<Inventory> inventoryItems = new ArrayList<>();
        
        // Using wildcard search for the provided query on multiple columns.
        String sqlQuery = "SELECT Inventory.*, Products.name AS product_name " +
                          "FROM Inventory " +
                          "JOIN Products ON Inventory.product_id = Products.product_id " +
                          "WHERE Inventory.inventory_id LIKE ? " +
                          "OR Inventory.product_id LIKE ? " +
                          "OR Inventory.change_type LIKE ? " +
                          "OR Inventory.quantity_change LIKE ? " +
                          "OR Inventory.date LIKE ? " +
                          "OR Products.name LIKE ? " +
                          "ORDER BY Inventory.date DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sqlQuery)) {

            // Set the query parameter for each column being searched
            String searchPattern = "%" + query + "%";
            for (int i = 1; i <= 6; i++) {
                stmt.setString(i, searchPattern);
            }
            
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Inventory inventory = new Inventory();
                inventory.setInventoryId(rs.getInt("inventory_id"));
                inventory.setProductId(rs.getInt("product_id"));
                inventory.setChangeType(rs.getString("change_type"));
                inventory.setQuantityChange(rs.getInt("quantity_change"));
                inventory.setDate(rs.getTimestamp("date"));
                
                inventoryItems.add(inventory);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return inventoryItems;
    }
}
