package controller;

import model.SaleItem;
import util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class SaleItemController {

    public boolean addSaleItems(List<SaleItem> saleItems) {
        String query = "INSERT INTO sale_items (sale_id, product_id, quantity, price) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            for (SaleItem saleItem : saleItems) {
                stmt.setInt(1, saleItem.getSaleId());
                stmt.setInt(2, saleItem.getProductId());
                stmt.setInt(3, saleItem.getQuantity());
                stmt.setDouble(4, saleItem.getPrice());
                stmt.addBatch();
            }

            int[] affectedRows = stmt.executeBatch();
            for (int i : affectedRows) {
                if (i == PreparedStatement.EXECUTE_FAILED) {
                    return false;
                }
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
