package service;

import controller.InventoryController;
import controller.ProductController;
import model.Inventory;

import java.util.List;

public class InventoryService {

    private final InventoryController inventoryController;
    private final ProductController productController;

    public InventoryService() {
        this.inventoryController = new InventoryController();
        this.productController = new ProductController();
    }

    public boolean addInventoryItem(Inventory inventory) {
        if (inventoryController.addInventoryItem(inventory)) {
            return productController.updateProductQuantity(inventory.getProductId(), inventory.getQuantityChange(), inventory.getChangeType());
        }
        return false;
    }

    public boolean updateInventoryItem(Inventory inventory) {
        if (inventoryController.updateInventoryItem(inventory)) {
            return productController.updateProductQuantity(inventory.getProductId(), inventory.getQuantityChange(), inventory.getChangeType());
        }
        return false;
    }

    public boolean deleteInventoryItem(int inventoryId) {
        return inventoryController.deleteInventoryItem(inventoryId);
    }

    public List<Inventory> getAllInventoryItems() {
        return inventoryController.getAllInventoryItems();
    }

    public List<Inventory> searchInventoryItems(String query) {
        return inventoryController.searchInventoryItems(query);
    }

}
