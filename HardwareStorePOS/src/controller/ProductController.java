package controller;

import model.Product;
import util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductController {

    public boolean addProduct(Product product) {
        String query = "INSERT INTO Products (sku, name, category, price, quantity, supplier, status) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, product.getSku());
            stmt.setString(2, product.getName());
            stmt.setString(3, product.getCategory());
            stmt.setDouble(4, product.getPrice());
            stmt.setInt(5, product.getQuantity());
            stmt.setString(6, product.getSupplier());
            stmt.setString(7, product.getStatus());  // Added status

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateProduct(Product product) {
        String query = "UPDATE Products SET name = ?, category = ?, price = ?, quantity = ?, supplier = ?, status = ? WHERE sku = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, product.getName());
            stmt.setString(2, product.getCategory());
            stmt.setDouble(3, product.getPrice());
            stmt.setInt(4, product.getQuantity());
            stmt.setString(5, product.getSupplier());
            stmt.setString(6, product.getStatus());  // Added status
            stmt.setString(7, product.getSku());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateProductStatus(String sku, String status) {
        String query = "UPDATE Products SET status = ? WHERE sku = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, status);
            stmt.setString(2, sku);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean updateProductQuantity(int productId, int quantityChange, String changeType) {
        String query = "UPDATE Products SET quantity = quantity + ? WHERE product_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

        	if (changeType.equalsIgnoreCase("Sale")) {
                stmt.setInt(1, -quantityChange); // Subtract quantity for sale
            } else if (changeType.equalsIgnoreCase("Restock")) {
                stmt.setInt(1, quantityChange); // Add quantity for restock
            } else if (changeType.equalsIgnoreCase("Adjustment")) {
                stmt.setInt(1, -quantityChange); // Adjustment is negative in my logic
            } else {
                stmt.setInt(1, quantityChange); // Default case (handle other cases if needed)
            }

            stmt.setInt(2, productId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public Product getProductBySku(String sku) {
        String query = "SELECT * FROM Products WHERE sku = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, sku);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Product product = new Product();
                product.setProductId(rs.getInt("product_id"));
                product.setSku(rs.getString("sku"));
                product.setName(rs.getString("name"));
                product.setCategory(rs.getString("category"));
                product.setPrice(rs.getDouble("price"));
                product.setQuantity(rs.getInt("quantity"));
                product.setSupplier(rs.getString("supplier"));
                product.setStatus(rs.getString("status"));  // Added status
                return product;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        String query = "SELECT * FROM Products";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Product product = new Product();
                product.setProductId(rs.getInt("product_id"));
                product.setSku(rs.getString("sku"));
                product.setName(rs.getString("name"));
                product.setCategory(rs.getString("category"));
                product.setPrice(rs.getDouble("price"));
                product.setQuantity(rs.getInt("quantity"));
                product.setSupplier(rs.getString("supplier"));
                product.setStatus(rs.getString("status"));
                products.add(product);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }
    
    public List<Product> searchProductsByNameOrCategory(String searchQuery) {
        List<Product> products = new ArrayList<>();
        String query = "SELECT * FROM Products WHERE name LIKE ? OR category LIKE ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, "%" + searchQuery + "%");
            stmt.setString(2, "%" + searchQuery + "%");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Product product = new Product();
                product.setProductId(rs.getInt("product_id"));
                product.setSku(rs.getString("sku"));
                product.setName(rs.getString("name"));
                product.setCategory(rs.getString("category"));
                product.setPrice(rs.getDouble("price"));
                product.setQuantity(rs.getInt("quantity"));
                product.setSupplier(rs.getString("supplier"));
                product.setStatus(rs.getString("status"));
                products.add(product);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }
    
    public boolean hasLinkedTransactions(int productId) {
        String saleItemsQuery = "SELECT COUNT(*) FROM sale_items WHERE product_id = ?";
        String inventoryQuery = "SELECT COUNT(*) FROM inventory WHERE product_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement saleItemsStmt = conn.prepareStatement(saleItemsQuery);
             PreparedStatement inventoryStmt = conn.prepareStatement(inventoryQuery)) {
            
            saleItemsStmt.setInt(1, productId);
            ResultSet saleItemsRs = saleItemsStmt.executeQuery();
            if (saleItemsRs.next() && saleItemsRs.getInt(1) > 0) {
                return true;
            }

            inventoryStmt.setInt(1, productId);
            ResultSet inventoryRs = inventoryStmt.executeQuery();
            if (inventoryRs.next() && inventoryRs.getInt(1) > 0) {
                return true;
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteProduct(int productId) {
        if (hasLinkedTransactions(productId)) {
            // Mark the product as unavailable if it is linked to any transactions
            return updateProductStatusById(productId, "Unavailable");
        } else {
            // Delete the product if it has no linked transactions
            String query = "DELETE FROM Products WHERE product_id = ?";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {

                stmt.setInt(1, productId);
                return stmt.executeUpdate() > 0;
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return false;
        }
    }
    private boolean updateProductStatusById(int productId, String status) {
        String query = "UPDATE Products SET status = ? WHERE product_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, status);
            stmt.setInt(2, productId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    public Product getProductById(int productId) {
        String query = "SELECT * FROM Products WHERE product_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, productId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Product product = new Product();
                product.setProductId(rs.getInt("product_id"));
                product.setSku(rs.getString("sku"));
                product.setName(rs.getString("name"));
                product.setCategory(rs.getString("category"));
                product.setPrice(rs.getDouble("price"));
                product.setQuantity(rs.getInt("quantity"));
                product.setSupplier(rs.getString("supplier"));
                product.setStatus(rs.getString("status"));
                return product;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public List<String> getDistinctCategories() {
        List<String> categories = new ArrayList<>();
        String query = "SELECT DISTINCT category FROM Products"; // Adjust table name if needed

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                categories.add(rs.getString("category"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return categories;
    }
    public boolean isSkuDuplicate(String sku) {
        String query = "SELECT COUNT(*) FROM Products WHERE sku = ?"; // Adjust table name if needed

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, sku);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0; // If count > 0, SKU is a duplicate
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false; // If no duplicate found or an error occurs
    }
}
