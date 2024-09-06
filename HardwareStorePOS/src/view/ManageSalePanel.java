package view;

import model.Inventory;
import model.Product;
import model.SaleItem;
import service.InventoryService;
import service.ProductService;
import service.SaleService;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;



public class ManageSalePanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private JTable productTable, cartTable;
    private DefaultTableModel productTableModel, cartTableModel;
    private JTextField searchProductField, quickAddField, moneyGivenField, changeField, totalField;
    private JComboBox<String> paymentMethodComboBox;
    private ProductService productService;
    private SaleService saleService;
    private JButton removeFromCartButton, clearCartButton, processSaleButton, quickAddButton;
    private int selectedProductId = -1; // Hidden field for productId
    private List<SaleItem> cartItems = new ArrayList<>();

    public ManageSalePanel() {
        productService = new ProductService();
        saleService = new SaleService();

        initializeUIComponents();
        setupEventListeners();
        loadProductItems();
    }

    private void initializeUIComponents() {
        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        // Split the layout into two sections: Product Selection and Cart Management
        JPanel productSelectionPanel = createProductSelectionPanel();
        JPanel cartManagementPanel = createCartManagementPanel();
        JPanel paymentPanel = createPaymentPanel();
        JPanel quickAddPanel = createQuickAddPanel();

        // Add panels to the layout
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addComponent(productSelectionPanel)
                    .addComponent(cartManagementPanel))
                .addComponent(quickAddPanel)
                .addComponent(paymentPanel)
        );

        layout.setVerticalGroup(
            layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(productSelectionPanel)
                    .addComponent(cartManagementPanel))
                .addComponent(quickAddPanel)
                .addComponent(paymentPanel)
        );
    }

    private JPanel createProductSelectionPanel() {
        JPanel panel = new JPanel();
        GroupLayout layout = new GroupLayout(panel);
        panel.setLayout(layout);

        // Labels
        JLabel searchProductLabel = new JLabel("Search Products:");

        // Product Table
        String[] productColumnNames = {"Product ID", "SKU", "Name", "Price", "Quantity"};
        productTableModel = new DefaultTableModel(productColumnNames, 0) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        productTable = new JTable(productTableModel);
        setupProductTableColumnWidths();
        JScrollPane productScrollPane = new JScrollPane(productTable);

        // Search Field and Buttons
        searchProductField = new JTextField();
        JButton searchProductButton = new JButton("Search");
        searchProductButton.addActionListener(e -> searchProducts(searchProductField.getText().trim()));
        JButton refreshProductButton = new JButton("Refresh");
        refreshProductButton.addActionListener(e -> loadProductItems());
   

        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addComponent(searchProductLabel)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(searchProductField)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(searchProductButton)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(refreshProductButton))
                .addComponent(productScrollPane)
        );

        layout.setVerticalGroup(
            layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(searchProductLabel)
                    .addComponent(searchProductField)
                    .addComponent(searchProductButton)
                    .addComponent(refreshProductButton))
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(productScrollPane)
        );

        return panel;
    }

    private JPanel createCartManagementPanel() {
        JPanel panel = new JPanel();
        GroupLayout layout = new GroupLayout(panel);
        panel.setLayout(layout);

        // Cart Table
        String[] cartColumnNames = {"Product ID", "SKU", "Name", "Price", "Quantity", "Total"};
        cartTableModel = new DefaultTableModel(cartColumnNames, 0) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        cartTable = new JTable(cartTableModel);
        setupCartTableColumnWidths();
        JScrollPane cartScrollPane = new JScrollPane(cartTable);

        // Buttons
        removeFromCartButton = new JButton("Remove from Cart");
        clearCartButton = new JButton("Clear Cart");

        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(cartScrollPane)
                .addGroup(layout.createSequentialGroup()
                    .addComponent(removeFromCartButton)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(clearCartButton))
        );

        layout.setVerticalGroup(
            layout.createSequentialGroup()
                .addComponent(cartScrollPane)
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(removeFromCartButton)
                    .addComponent(clearCartButton))
        );

        return panel;
    }

    private JPanel createQuickAddPanel() {
        JPanel panel = new JPanel();
        GroupLayout layout = new GroupLayout(panel);
        panel.setLayout(layout);

        JLabel quickAddLabel = new JLabel("Quick Add (Format: SKU@Quantity):");
        quickAddField = new JTextField();
        quickAddButton = new JButton("Quick Add");

        layout.setHorizontalGroup(
            layout.createSequentialGroup()
                .addComponent(quickAddLabel)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(quickAddField)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(quickAddButton)
        );

        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(quickAddLabel)
                .addComponent(quickAddField)
                .addComponent(quickAddButton)
        );

        return panel;
    }

    private JPanel createPaymentPanel() {
        JPanel panel = new JPanel();
        GroupLayout layout = new GroupLayout(panel);
        panel.setLayout(layout);

        JLabel paymentMethodLabel = new JLabel("Payment Method:");
        paymentMethodComboBox = new JComboBox<>(new String[]{"Cash"});
        
        JLabel moneyGivenLabel = new JLabel("Money Given:");
        moneyGivenField = new JTextField();

        JLabel changeLabel = new JLabel("Change:");
        changeField = new JTextField();
        changeField.setEditable(false);

        JLabel totalLabel = new JLabel("Total:");
        totalField = new JTextField();
        totalField.setEditable(false);

        processSaleButton = new JButton("Process Sale");
        processSaleButton.setEnabled(false);

        layout.setHorizontalGroup(
            layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(paymentMethodLabel)
                    .addComponent(moneyGivenLabel)
                    .addComponent(changeLabel)
                    .addComponent(totalLabel))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(paymentMethodComboBox)
                    .addComponent(moneyGivenField)
                    .addComponent(changeField)
                    .addComponent(totalField)
                    .addComponent(processSaleButton))
        );

        layout.setVerticalGroup(
            layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(paymentMethodLabel)
                    .addComponent(paymentMethodComboBox))
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(moneyGivenLabel)
                    .addComponent(moneyGivenField))
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(changeLabel)
                    .addComponent(changeField))
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(totalLabel)
                    .addComponent(totalField))
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(processSaleButton)
        );

        return panel;
    }

    private void setupProductTableColumnWidths() {
        TableColumnModel columnModel = productTable.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(50);  // Product ID
        columnModel.getColumn(1).setPreferredWidth(100); // SKU
        columnModel.getColumn(2).setPreferredWidth(150); // Name
        columnModel.getColumn(3).setPreferredWidth(100); // Price
        columnModel.getColumn(4).setPreferredWidth(100); // Quantity
    }

    private void setupCartTableColumnWidths() {
        TableColumnModel columnModel = cartTable.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(50);  // Product ID
        columnModel.getColumn(1).setPreferredWidth(100); // SKU
        columnModel.getColumn(2).setPreferredWidth(150); // Name
        columnModel.getColumn(3).setPreferredWidth(100); // Price
        columnModel.getColumn(4).setPreferredWidth(100); // Quantity
        columnModel.getColumn(5).setPreferredWidth(100); // Total
    }

    private void setupEventListeners() {
        quickAddButton.addActionListener(e -> handleQuickAdd());
        removeFromCartButton.addActionListener(e -> removeFromCart());
        clearCartButton.addActionListener(e -> clearCart());

        moneyGivenField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateChangeAndButtonState();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateChangeAndButtonState();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateChangeAndButtonState();
            }
        });

        productTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int selectedRow = productTable.getSelectedRow();
                if (selectedRow >= 0) {
                    selectedProductId = (int) productTableModel.getValueAt(selectedRow, 0);
                    showAddToCartModal(); // Show modal on product click
                }
            }
        });

        processSaleButton.addActionListener(e -> processSale());
    }

    private void loadProductItems() {
        productTableModel.setRowCount(0);  // Clear existing rows
        List<Product> products = productService.getAllProducts();
        for (Product product : products) {
            productTableModel.addRow(new Object[]{
                product.getProductId(),
                product.getSku(),
                product.getName(),
                product.getPrice(),
                product.getQuantity()
            });
        }
    }

    private void showAddToCartModal() {
        if (selectedProductId == -1) {
            JOptionPane.showMessageDialog(this, "Please select a product first.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Product product = productService.getProductById(selectedProductId);

        JPanel panel = new JPanel(new GridLayout(2, 2, 10, 10)); // Added padding between components
        JLabel productNameLabel = new JLabel("Product: ");
        JLabel productNameValue = new JLabel(product.getName());
        JLabel quantityLabel = new JLabel("Quantity: ");
        JTextField quantityInput = new JTextField();

        panel.add(productNameLabel);
        panel.add(productNameValue);
        panel.add(quantityLabel);
        panel.add(quantityInput);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add to Cart", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                int quantityToAdd = Integer.parseInt(quantityInput.getText().trim());
                addToCart(product, quantityToAdd);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Please enter a valid quantity.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleQuickAdd() {
        String input = quickAddField.getText().trim();
        if (input.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a SKU and quantity in the format SKU@Quantity.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String[] parts = input.split("@");
        if (parts.length != 2) {
            JOptionPane.showMessageDialog(this, "Invalid format. Please use SKU@Quantity.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String sku = parts[0].trim();
        int quantity;
        try {
            quantity = Integer.parseInt(parts[1].trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Quantity must be a valid number.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Product product = productService.getProductBySku(sku);
        if (product == null) {
            JOptionPane.showMessageDialog(this, "Product with SKU " + sku + " not found.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        addToCart(product, quantity);
    }

    private void addToCart(Product product, int quantityToAdd) {
        if (quantityToAdd <= 0) {
            JOptionPane.showMessageDialog(this, "Quantity must be greater than zero.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Calculate total quantity if the product is already in the cart
        int existingQuantityInCart = getCartQuantity(product.getProductId());
        int totalRequestedQuantity = existingQuantityInCart + quantityToAdd;

        // Validate stock
        if (totalRequestedQuantity > product.getQuantity()) {
            JOptionPane.showMessageDialog(this, "Quantity exceeds available stock.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Check if the product is already in the cart
        boolean productExistsInCart = false;
        for (int i = 0; i < cartTableModel.getRowCount(); i++) {
            int productIdInCart = (int) cartTableModel.getValueAt(i, 0);
            if (productIdInCart == product.getProductId()) {
                int currentQuantity = (int) cartTableModel.getValueAt(i, 4);
                int newQuantity = currentQuantity + quantityToAdd;
                double newTotalPrice = product.getPrice() * newQuantity;

                // Update the existing row
                cartTableModel.setValueAt(newQuantity, i, 4);
                cartTableModel.setValueAt(newTotalPrice, i, 5);

                // Update the cart item
                cartItems.get(i).setQuantity(newQuantity);
                cartItems.get(i).setPrice(newTotalPrice);

                productExistsInCart = true;
                break;
            }
        }

        if (!productExistsInCart) {
            // Add the product to the cart if it doesn't already exist
            double totalPrice = product.getPrice() * quantityToAdd;

            SaleItem saleItem = new SaleItem();
            saleItem.setProductId(product.getProductId());
            saleItem.setQuantity(quantityToAdd);
            saleItem.setPrice(totalPrice);

            cartItems.add(saleItem);

            cartTableModel.addRow(new Object[]{
                product.getProductId(),
                product.getSku(),  // Display SKU in the cart table
                product.getName(),
                product.getPrice(),
                quantityToAdd,
                totalPrice
            });
        }

        updateTotalField(); // Update total field whenever the cart is updated
        clearProductSelection();
    }

    // Helper method to get the quantity of a product already in the cart
    private int getCartQuantity(int productId) {
        return cartItems.stream()
            .filter(item -> item.getProductId() == productId)
            .mapToInt(SaleItem::getQuantity)
            .sum();
    }

    private void removeFromCart() {
        int selectedRow = cartTable.getSelectedRow();
        if (selectedRow >= 0) {
            cartItems.remove(selectedRow);
            cartTableModel.removeRow(selectedRow);
            updateTotalField(); // Update total field whenever an item is removed
        }
    }

    private void clearCart() {
        cartTableModel.setRowCount(0);
        cartItems.clear();
        updateTotalField(); // Update total field whenever the cart is cleared
    }

    private void updateChangeAndButtonState() {
        try {
            double totalAmount = Double.parseDouble(totalField.getText());
            double moneyGiven = Double.parseDouble(moneyGivenField.getText());

            if (moneyGiven >= totalAmount) {
                double change = moneyGiven - totalAmount;
                changeField.setText(String.valueOf(change));
                processSaleButton.setEnabled(true); // Enable the button only if money given is sufficient
            } else {
                changeField.setText("");
                processSaleButton.setEnabled(false); // Disable the button if money given is insufficient
            }
        } catch (NumberFormatException e) {
            changeField.setText("");
            processSaleButton.setEnabled(false); // Disable the button in case of invalid input
        }
    }

    private void updateTotalField() {
        double totalAmount = cartItems.stream().mapToDouble(SaleItem::getPrice).sum();
        totalField.setText(String.valueOf(totalAmount));
        updateChangeAndButtonState(); // Update change and button state when total changes
    }

    private void processSale() {
        if (cartItems.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Cart is empty. Add items before processing sale.", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double totalAmount = cartItems.stream().mapToDouble(SaleItem::getPrice).sum();
        String paymentMethod = (String) paymentMethodComboBox.getSelectedItem();
       // double moneyGiven = Double.parseDouble(moneyGivenField.getText());
        //double change = moneyGiven - totalAmount; //di kona nagamit

        boolean success = saleService.processSale(1, totalAmount, paymentMethod, cartItems); // Assuming userId is 1 for simplicity
        if (success) {
            // Update inventory and product quantities after a successful sale
            for (SaleItem item : cartItems) {
                int productId = item.getProductId();
                int quantitySold = item.getQuantity();

                // Update the inventory
                Inventory inventory = new Inventory();
                inventory.setProductId(productId);
                inventory.setChangeType("Sale");
                inventory.setQuantityChange(quantitySold); // Negative because items are being sold
                inventory.setDate(new Timestamp(System.currentTimeMillis()));

                InventoryService inventoryService = new InventoryService();
                inventoryService.addInventoryItem(inventory); // This already handles updating the product quantity
            }

            // Generate and print the receipt
            printReceipt();

            // After printing the receipt, clear the cart and reload products
            clearCart();
            loadProductItems();
            clearForm();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to process sale.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }



    private void clearProductSelection() {
        selectedProductId = -1;
        quickAddField.setText(""); // Clear quick add field after adding to cart
        moneyGivenField.setText(""); // Clear the money given field
        changeField.setText(""); // Clear the change field
    }
    private void printReceipt() {
        String receiptText = generateReceipt();
        JTextArea textArea = new JTextArea(receiptText);
        try {
            textArea.print();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to print receipt.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String generateReceipt() {
        StringBuilder receipt = new StringBuilder();
        receipt.append("ACE HARDWARE\n");
        receipt.append("Address: Masipit, Calapan\n");
        receipt.append("Contact: 09*6700**02\n");
        receipt.append("Date: ").append(new Timestamp(System.currentTimeMillis())).append("\n\n");
        
        receipt.append("Items Purchased:\n");
        receipt.append("---------------------------------------------\n");
        receipt.append(String.format("%-20s %-10s %-10s\n", "Product", "Qty", "Total"));
        receipt.append("---------------------------------------------\n");

        for (SaleItem item : cartItems) {
            Product product = productService.getProductById(item.getProductId());
            receipt.append(String.format("%-20s %-10d %-10.2f\n", product.getName(), item.getQuantity(), item.getPrice()));
        }

        receipt.append("---------------------------------------------\n");
        receipt.append(String.format("%-20s %-10s %-10.2f\n", "", "Total:", cartItems.stream().mapToDouble(SaleItem::getPrice).sum()));
        receipt.append(String.format("%-20s %-10s %-10.2f\n", "", "Money Given:", Double.parseDouble(moneyGivenField.getText())));
        receipt.append(String.format("%-20s %-10s %-10.2f\n", "", "Change:", Double.parseDouble(changeField.getText())));
        receipt.append("---------------------------------------------\n");
        receipt.append("Thank you for your purchase!\n");
        receipt.append("---------------------------------------------\n");
//      rewqvv
        return receipt.toString();
    }
    private void clearForm() {
        // Clear input fields
        moneyGivenField.setText("");
        changeField.setText("");
        paymentMethodComboBox.setSelectedIndex(0); // Reset to the first option, e.g., "Cash"
    }
    private void searchProducts(String query) {
    	productTableModel.setRowCount(0);  // Clear existing rows
        List<Product> products = productService.searchProductsByNameOrCategory(query);
        for (Product product : products) {
        	productTableModel.addRow(new Object[]{
                product.getProductId(), 
                product.getSku(), 
                product.getName(), 
                product.getCategory(), 
                product.getPrice(), 
                product.getSupplier(), 
                product.getStatus()
            });
        }
    }
}
