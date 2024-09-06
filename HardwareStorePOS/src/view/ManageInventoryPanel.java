package view;

import service.InventoryService;
import service.ProductService;
import model.Inventory;
import model.Product;
import util.InputValidator;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class ManageInventoryPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private JTable inventoryTable, productTable;
    private DefaultTableModel inventoryTableModel, productTableModel;
    private JTextField quantityField, productNameField, searchInventoryField, searchProductField;
    private JComboBox<String> changeTypeComboBox;
    private InventoryService inventoryService;
    private ProductService productService;
    private JButton saveButton, searchInventoryButton, searchProductButton, refreshInventoryButton, refreshProductButton;
    private int selectedProductId = -1; // Hidden field for productId
    private int selectedInventoryId = -1; // Hidden field for inventoryId

    public ManageInventoryPanel() {
        inventoryService = new InventoryService();
        productService = new ProductService();
        initializeUIComponents();
        setupEventListeners();
        loadInventoryItems();
        loadProductItems();
    }

    private void initializeUIComponents() {
        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        // Labels
        JLabel searchInventoryLabel = new JLabel("Search Inventory:");
        JLabel searchProductLabel = new JLabel("Search Products:");

        // Inventory Table
        String[] inventoryColumnNames = {"Inventory ID", "Product", "Change Type", "Quantity Change", "Date"};
        inventoryTableModel = new DefaultTableModel(inventoryColumnNames, 0) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        inventoryTable = new JTable(inventoryTableModel);
        setupInventoryTableColumnWidths();
        JScrollPane inventoryScrollPane = new JScrollPane(inventoryTable);

        // Product Table
        String[] productColumnNames = {"PID","SKU", "Name", "Current Quantity"};
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

        // Search Fields and Buttons for Inventory and Product Tables
        searchInventoryField = new JTextField();
        searchInventoryButton = new JButton("Search Inventory");
        refreshInventoryButton = new JButton("Refresh Inventory");

        searchProductField = new JTextField();
        searchProductButton = new JButton("Search Products");
        refreshProductButton = new JButton("Refresh Products");

        // Inventory Form
        JPanel formPanel = createFormPanel();

        saveButton = new JButton("Save");
        JButton clearButton = new JButton("Clear");

        // Set up button actions
        clearButton.addActionListener(e -> clearForm());

        // Horizontal group layout
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addComponent(inventoryScrollPane) // Now on the left
                    .addComponent(productScrollPane))  // Now on the right
                .addGroup(layout.createSequentialGroup()
                    .addComponent(searchInventoryLabel)
                    .addComponent(searchInventoryField)
                    .addComponent(searchInventoryButton)
                    .addComponent(refreshInventoryButton)
                    .addComponent(searchProductLabel)
                    .addComponent(searchProductField)
                    .addComponent(searchProductButton)
                    .addComponent(refreshProductButton))
                .addComponent(formPanel)
                .addGroup(layout.createSequentialGroup()
                    .addComponent(saveButton)
                    .addComponent(clearButton))
        );

        // Vertical group layout
        layout.setVerticalGroup(
            layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(searchInventoryLabel)
                    .addComponent(searchInventoryField)
                    .addComponent(searchInventoryButton)
                    .addComponent(refreshInventoryButton)
                    .addComponent(searchProductLabel)
                    .addComponent(searchProductField)
                    .addComponent(searchProductButton)
                    .addComponent(refreshProductButton))
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(inventoryScrollPane) // Now on the left
                    .addComponent(productScrollPane))  // Now on the right
                .addComponent(formPanel)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(saveButton)
                    .addComponent(clearButton))
        
        );
    }

    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel();
        formPanel.setBorder(BorderFactory.createTitledBorder("Inventory Details"));

        JLabel productNameLabel = new JLabel("Product Name:");
        productNameField = new JTextField();
        productNameField.setEditable(false); // Make it uneditable

        JLabel changeTypeLabel = new JLabel("Change Type:");
        changeTypeComboBox = new JComboBox<>(new String[]{"Restock", "Adjustment"});

        JLabel quantityLabel = new JLabel("Quantity Change:");
        quantityField = new JTextField();

        GroupLayout formLayout = new GroupLayout(formPanel);
        formPanel.setLayout(formLayout);
        formLayout.setAutoCreateGaps(true);
        formLayout.setAutoCreateContainerGaps(true);

        formLayout.setHorizontalGroup(
            formLayout.createSequentialGroup()
                .addGroup(formLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(productNameLabel)
                    .addComponent(changeTypeLabel)
                    .addComponent(quantityLabel))
                .addGroup(formLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(productNameField)
                    .addComponent(changeTypeComboBox)
                    .addComponent(quantityField))
        );

        formLayout.setVerticalGroup(
            formLayout.createSequentialGroup()
                .addGroup(formLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(productNameLabel)
                    .addComponent(productNameField))
                .addGroup(formLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(changeTypeLabel)
                    .addComponent(changeTypeComboBox))
                .addGroup(formLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(quantityLabel)
                    .addComponent(quantityField))
        );

        return formPanel;
    }

    private void setupInventoryTableColumnWidths() {
        TableColumnModel columnModel = inventoryTable.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(50);  // Inventory ID
        columnModel.getColumn(1).setPreferredWidth(150); // Product
        columnModel.getColumn(2).setPreferredWidth(100); // Change Type
        columnModel.getColumn(3).setPreferredWidth(100); // Quantity Change
        columnModel.getColumn(4).setPreferredWidth(150); // Date
    }

    private void setupProductTableColumnWidths() {
        TableColumnModel columnModel = productTable.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(10);  // Product ID
        columnModel.getColumn(1).setPreferredWidth(20);  // SKU
        columnModel.getColumn(2).setPreferredWidth(150); // Name
        columnModel.getColumn(3).setPreferredWidth(100); // Current Quantity
    }

    private void setupEventListeners() {
        saveButton.addActionListener(e -> saveOrUpdateInventoryItem());
        searchInventoryButton.addActionListener(e -> searchInventoryItems());
        searchProductButton.addActionListener(e -> searchProductItems());
        refreshInventoryButton.addActionListener(e -> loadInventoryItems());
        refreshProductButton.addActionListener(e -> loadProductItems());

        productTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int selectedRow = productTable.getSelectedRow();
                if (selectedRow >= 0) {
                    selectedProductId = (int) productTableModel.getValueAt(selectedRow, 0);
                    String productName = (String) productTableModel.getValueAt(selectedRow, 2);
                    productNameField.setText(productName);
                }
            }
        });
    }

    private void loadInventoryItems() {
        inventoryTableModel.setRowCount(0);  // Clear existing rows
        List<Inventory> inventoryItems = inventoryService.getAllInventoryItems();
        for (Inventory inventory : inventoryItems) {
            Product product = productService.getProductById(inventory.getProductId());
            inventoryTableModel.addRow(new Object[]{
                inventory.getInventoryId(),
                product != null ? product.getName() : "Unknown",
                inventory.getChangeType(),
                inventory.getQuantityChange(),
                inventory.getDate()
            });
        }
    }

    private void loadProductItems() {
        productTableModel.setRowCount(

0);  // Clear existing rows
        List<Product> products = productService.getAllProducts();
        for (Product product : products) {
            productTableModel.addRow(new Object[]{
                product.getProductId(),
                product.getSku(),
                product.getName(),
                product.getQuantity()
            });
        }
    }

    private void searchInventoryItems() {
        String query = searchInventoryField.getText().trim();
        if (!query.isEmpty()) {
            inventoryTableModel.setRowCount(0);  // Clear existing rows
            List<Inventory> inventoryItems = inventoryService.searchInventoryItems(query);
            for (Inventory inventory : inventoryItems) {
                Product product = productService.getProductById(inventory.getProductId());
                inventoryTableModel.addRow(new Object[]{
                    inventory.getInventoryId(),
                    product != null ? product.getName() : "Unknown",  // Display product name if found
                    inventory.getChangeType(),
                    inventory.getQuantityChange(),
                    inventory.getDate()
                });
            }
        }
    }


    private void searchProductItems() {
        String query = searchProductField.getText().trim();
        if (!query.isEmpty()) {
            productTableModel.setRowCount(0);  // Clear existing rows
            List<Product> products = productService.getAllProducts();
            for (Product product : products) {
                if (product.getName().toLowerCase().contains(query.toLowerCase()) ||
                    product.getCategory().toLowerCase().contains(query.toLowerCase())) {
                    productTableModel.addRow(new Object[]{
                        product.getProductId(),
                        product.getName(),
                        product.getQuantity()
                    });
                }
            }
        }
    }

    private void saveOrUpdateInventoryItem() {
        String changeType = (String) changeTypeComboBox.getSelectedItem();
        String quantityText = quantityField.getText().trim();

        if (!validateInput(selectedProductId, quantityText)) return;

        int quantityChange = Integer.parseInt(quantityText);

        // Fetch the current product details
        Product product = productService.getProductById(selectedProductId);
        int currentQuantity = product.getQuantity();
        int newQuantity;

        // Determine the new quantity based on the change type
        if (changeType.equalsIgnoreCase("adjustment")) {
            newQuantity = currentQuantity - quantityChange; // Subtract for adjustments
            if (newQuantity < 0) {
                JOptionPane.showMessageDialog(this, "Cannot reduce inventory below zero.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } else if (changeType.equalsIgnoreCase("restock")) {
            newQuantity = currentQuantity + quantityChange; // Add for restocks
        } else {
            JOptionPane.showMessageDialog(this, "Invalid change type.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Inventory inventory = new Inventory();
        inventory.setProductId(selectedProductId);
        inventory.setChangeType(changeType.toLowerCase());
        inventory.setQuantityChange(quantityChange);

        try {
            boolean success;
            if (selectedInventoryId == -1) { // New inventory item
                success = inventoryService.addInventoryItem(inventory);
            } else { // Update existing inventory item
                inventory.setInventoryId(selectedInventoryId);
                success = inventoryService.updateInventoryItem(inventory);
            }

            if (success) {
                JOptionPane.showMessageDialog(this, "Inventory item saved successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadInventoryItems();
                loadProductItems(); // Refresh product quantities
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to save inventory item.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "An error occurred: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }


    private void clearForm() {
        productNameField.setText("");
        changeTypeComboBox.setSelectedIndex(0);
        quantityField.setText("");
        selectedInventoryId = -1;
        saveButton.setText("Save");
    }

    private boolean validateInput(int productId, String quantityText) {
        if (productId == -1) {
            JOptionPane.showMessageDialog(this, "Please select a product.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (!InputValidator.isValidNumber(quantityText)) {
            JOptionPane.showMessageDialog(this, "Quantity must be a valid number.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        int quantityChange = Integer.parseInt(quantityText);
        if ("restock".equalsIgnoreCase((String) changeTypeComboBox.getSelectedItem()) && quantityChange <= 0) {
            JOptionPane.showMessageDialog(this, "Restock quantity must be a positive number.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }
}