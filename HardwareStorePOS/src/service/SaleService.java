package service;

import controller.SaleController;
import controller.SaleItemController;
import model.Sale;
import model.SaleItem;

import java.sql.Timestamp;
import java.util.List;

public class SaleService {
    private final SaleController saleController;
    private final SaleItemController saleItemController;

    public SaleService() {
        this.saleController = new SaleController();
        this.saleItemController = new SaleItemController();
    }

    // Method to process a sale and associated items
    public boolean processSale(int userId, double totalAmount, String paymentMethod, List<SaleItem> saleItems) {
        Sale sale = new Sale();
        sale.setUserId(userId);
        sale.setTotalAmount(totalAmount);
        sale.setPaymentMethod(paymentMethod);
        sale.setSaleDate(new Timestamp(System.currentTimeMillis()));

        if (saleController.processSale(sale)) {
            for (SaleItem item : saleItems) {
                item.setSaleId(sale.getSaleId());
            }
            return saleItemController.addSaleItems(saleItems);
        }
        return false;
    }

    // Method to retrieve a sale by ID
    public Sale getSaleById(int saleId) {
        return saleController.getSaleById(saleId);
    }

    // Method to retrieve all sales
    public List<Sale> getAllSales() {
        return saleController.getAllSales();
    }
    
    //Method to get all sasles with detials
    public List<Sale> getAllSalesWithDetails(){
    	return saleController.getAllSalesWithDetails();
    }
}
