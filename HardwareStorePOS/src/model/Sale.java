package model;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class Sale {
    private int saleId;
    private int userId;
    private double totalAmount;
    private String paymentMethod;
    private double amountPaid; // New field
    private Timestamp saleDate;
    private String username; // 
    private List<SaleItem> saleItems = new ArrayList<>();
    private List<String> itemDescriptions = new ArrayList<>();
    private String allItemsDescription; 
    // Getters and Setters
    public int getSaleId() {
        return saleId;
    }

    public void setSaleId(int saleId) {
        this.saleId = saleId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public double getAmountPaid() {
        return amountPaid;
    }

    public void setAmountPaid(double amountPaid) {
        this.amountPaid = amountPaid;
    }

    public Timestamp getSaleDate() {
        return saleDate;
    }

    public void setSaleDate(Timestamp saleDate) {
        this.saleDate = saleDate;
    }
    public List<SaleItem> getSaleItems() {
        return saleItems;
    }

    public void addSaleItem(SaleItem saleItem) {
        this.saleItems.add(saleItem);
    }

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	

	  public void addItemDescription(String itemDescription) {
	        itemDescriptions.add(itemDescription);
	    }

	    public List<String> getItemDescriptions() {
	        return itemDescriptions;
	    }

	    public String getAllItemsDescription() {
	        return allItemsDescription;
	    }

	    public void setAllItemsDescription(String allItemsDescription) {
	        this.allItemsDescription = allItemsDescription;
	    }
}
