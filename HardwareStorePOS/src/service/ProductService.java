package service;

import controller.ProductController;
import model.Product;

import java.util.List;

public class ProductService {

    private ProductController productController;

    public ProductService() {
        this.productController = new ProductController();
    }

    public boolean addProduct(Product product) {
        return productController.addProduct(product);
    }

    public boolean updateProduct(Product product) {
        return productController.updateProduct(product);
    }

    public boolean updateProductStatus(String sku, String status) {
        return productController.updateProductStatus(sku, status);
    }

    public boolean deleteProduct(int productId) { // Changed from String to int
        return productController.deleteProduct(productId);
    }

    public Product getProductBySku(String sku) {
        return productController.getProductBySku(sku);
    }

    public Product getProductById(int productId) {
        return productController.getProductById(productId);
    }
    
    public List<Product> getAllProducts() {
        return productController.getAllProducts();
    }

    public List<Product> searchProductsByNameOrCategory(String searchQuery) {
        return productController.searchProductsByNameOrCategory(searchQuery);
    }
    
    public List<String> getDistinctCategories() {
        return productController.getDistinctCategories();
    }
    public boolean isSkuDuplicate(String sku) {
        return productController.isSkuDuplicate(sku);
    }
}
