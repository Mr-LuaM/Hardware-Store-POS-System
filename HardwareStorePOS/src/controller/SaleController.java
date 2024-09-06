package controller;

import model.Sale;
import model.SaleItem;
import util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SaleController {

    public boolean processSale(Sale sale) {
        String query = "INSERT INTO Sales (user_id, total_amount, payment_method, sale_date) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, sale.getUserId());
            stmt.setDouble(2, sale.getTotalAmount());
            stmt.setString(3, sale.getPaymentMethod());
            stmt.setTimestamp(4, sale.getSaleDate());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        sale.setSaleId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Sale getSaleById(int saleId) {
        String query = "SELECT * FROM Sales WHERE sale_id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, saleId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Sale sale = new Sale();
                    sale.setSaleId(rs.getInt("sale_id"));
                    sale.setUserId(rs.getInt("user_id"));
                    sale.setTotalAmount(rs.getDouble("total_amount"));
                    sale.setPaymentMethod(rs.getString("payment_method"));
                    sale.setSaleDate(rs.getTimestamp("sale_date"));
                    return sale;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Sale> getAllSales() {
        List<Sale> sales = new ArrayList<>();
        String query = "SELECT * FROM Sales";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Sale sale = new Sale();
                sale.setSaleId(rs.getInt("sale_id"));
                sale.setUserId(rs.getInt("user_id"));
                sale.setTotalAmount(rs.getDouble("total_amount"));
                sale.setPaymentMethod(rs.getString("payment_method"));
                sale.setSaleDate(rs.getTimestamp("sale_date"));
                sales.add(sale);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return sales;
    }
    
    public List<Sale> getAllSalesWithDetails() {
        List<Sale> sales = new ArrayList<>();
        Map<Integer, Sale> saleMap = new HashMap<>();  // Use a map to track existing sales by sale_id

        String query = "SELECT s.sale_id, s.total_amount, s.payment_method, s.sale_date, u.username, p.name AS product_name, si.quantity, si.price " +
                       "FROM Sales s " +
                       "JOIN Users u ON s.user_id = u.user_id " +
                       "JOIN sale_items si ON s.sale_id = si.sale_id " +
                       "JOIN Products p ON si.product_id = p.product_id";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int saleId = rs.getInt("sale_id");

                // Check if the sale already exists in the map
                Sale sale = saleMap.get(saleId);
                if (sale == null) {
                    sale = new Sale();
                    sale.setSaleId(saleId);
                    sale.setTotalAmount(rs.getDouble("total_amount"));
                    sale.setPaymentMethod(rs.getString("payment_method"));
                    sale.setSaleDate(rs.getTimestamp("sale_date"));
                    sale.setUsername(rs.getString("username"));  // Add the username

                    saleMap.put(saleId, sale);  // Add sale to map
                    sales.add(sale);  // Add sale to the final list
                }

                // Add SaleItem details (accumulating product names and quantities)
                SaleItem saleItem = new SaleItem();
                saleItem.setProductName(rs.getString("product_name"));
                saleItem.setQuantity(rs.getInt("quantity"));
                saleItem.setPrice(rs.getDouble("price"));

                String itemDescription = saleItem.getProductName() + " (" + saleItem.getQuantity() + ")";
                sale.addItemDescription(itemDescription);  // Store the concatenated description in the sale
            }

            // After all items are processed, join them into a single string per sale
            for (Sale sale : sales) {
                sale.setAllItemsDescription(String.join(", ", sale.getItemDescriptions()));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return sales;
    }


}
