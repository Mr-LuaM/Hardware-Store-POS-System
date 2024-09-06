package view;

import service.ProductService;
import model.Product;
import util.InputValidator;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class ManageProductPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private JTable productTable;
    private DefaultTableModel tableModel;
    private JTextField productIdField, skuField, nameField, newCategoryField, priceField, supplierField;
    private JComboBox<String> categoryComboBox, statusComboBox;
    private JButton saveButton, deleteButton, clearButton;
    private ProductService productService;
    private int selectedProductId = -1; // Hidden field for productId

    public ManageProductPanel() {
        setBorder(null);
        setMinimumSize(new Dimension(1000, 700));  // Set minimum size

        productService = new ProductService();

        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        // Search Panel
        JLabel searchLabel = new JLabel("Search:");
        JTextField searchField = new JTextField();
        JButton searchButton = new JButton("Search");
        JButton refreshButton = new JButton("Refresh");

        // Table Model
        String[] columnNames = {"Product ID", "SKU", "Name", "Category", "Price", "Supplier", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
        };
        productTable = new JTable(tableModel);
        productTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Only one row can be selected at a time
        JScrollPane scrollPane = new JScrollPane(productTable);

        // Product Detail Form
        JPanel formPanel = new JPanel();
        formPanel.setBorder(BorderFactory.createTitledBorder("Product Details"));

        JLabel productIdLabel = new JLabel("Product ID:");
        productIdField = new JTextField();
        productIdField.setEditable(false); // Product ID is not editable, for display only

        JLabel skuLabel = new JLabel("SKU:");
        skuField = new JTextField();

        JLabel nameLabel = new JLabel("Name:");
        nameField = new JTextField();

        JLabel categoryLabel = new JLabel("Category:");
        categoryComboBox = new JComboBox<>();
        newCategoryField = new JTextField(); // This will be used when "Other" is selected

        JLabel priceLabel = new JLabel("Price:");
        priceField = new JTextField();

        JLabel supplierLabel = new JLabel("Supplier:");
        supplierField = new JTextField();

        JLabel statusLabel = new JLabel("Status:");
        statusComboBox = new JComboBox<>(new String[]{"Available", "Unavailable"});

        // Buttons Panel
        saveButton = new JButton("Add Product");
        deleteButton = new JButton("Delete Product");
        clearButton = new JButton("Clear");

        // Horizontal group
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addComponent(searchLabel)
                    .addComponent(searchField)
                    .addComponent(searchButton)
                    .addComponent(refreshButton))
                .addComponent(scrollPane)
                .addComponent(formPanel)
                .addGroup(layout.createSequentialGroup()
                    .addComponent(saveButton)
                    .addComponent(clearButton)
                    .addComponent(deleteButton))
        );

        // Vertical group
        layout.setVerticalGroup(
            layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(searchLabel)
                    .addComponent(searchField)
                    .addComponent(searchButton)
                    .addComponent(refreshButton))
                .addComponent(scrollPane)
                .addComponent(formPanel)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(saveButton)
                    .addComponent(clearButton)
                    .addComponent(deleteButton))
        );

        GroupLayout formLayout = new GroupLayout(formPanel);
        formPanel.setLayout(formLayout);
        formLayout.setAutoCreateGaps(true);
        formLayout.setAutoCreateContainerGaps(true);

        formLayout.setHorizontalGroup(
            formLayout.createSequentialGroup()
                .addGroup(formLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(productIdLabel)
                    .addComponent(skuLabel)
                    .addComponent(nameLabel)
                    .addComponent(categoryLabel)
                    .addComponent(priceLabel)
                    .addComponent(supplierLabel)
                    .addComponent(statusLabel))
                .addGroup(formLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(productIdField)
                    .addComponent(skuField)
                    .addComponent(nameField)
                    .addComponent(categoryComboBox)
                    .addComponent(newCategoryField) // Ensure this is added in the horizontal group
                    .addComponent(priceField)
                    .addComponent(supplierField)
                    .addComponent(statusComboBox))
        );

        formLayout.setVerticalGroup(
            formLayout.createSequentialGroup()
                .addGroup(formLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(productIdLabel)
                    .addComponent(productIdField))
                .addGroup(formLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(skuLabel)
                    .addComponent(skuField))
                .addGroup(formLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(nameLabel)
                    .addComponent(nameField))
                .addGroup(formLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(categoryLabel)
                    .addComponent(categoryComboBox))
                .addGroup(formLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(newCategoryField)) // Ensure this is added in the vertical group
                .addGroup(formLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(priceLabel)
                    .addComponent(priceField))
                .addGroup(formLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(supplierLabel)
                    .addComponent(supplierField))
                .addGroup(formLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(statusLabel)
                    .addComponent(statusComboBox))
        );

        newCategoryField.setVisible(false);
        
        // Load distinct categories into the categoryComboBox
        loadDistinctCategories();

        // Show/hide newCategoryField based on selection
        categoryComboBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if ("Other".equals(e.getItem())) {
                    newCategoryField.setVisible(true);
                } else {
                    newCategoryField.setVisible(false);
                }
                formPanel.revalidate(); // Revalidate the form panel layout when visibility changes
                formPanel.repaint();
            }
        });

        // Button action listeners
        saveButton.addActionListener(e -> saveOrUpdateProduct());
        clearButton.addActionListener(e -> clearForm());
        deleteButton.addActionListener(e -> confirmAndDeleteProduct());
        refreshButton.addActionListener(e -> loadProducts());
        searchButton.addActionListener(e -> searchProducts(searchField.getText().trim()));

        // Table row click listener
        productTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int selectedRow = productTable.getSelectedRow();
                if (selectedRow >= 0) {
                    int productId = Integer.parseInt(tableModel.getValueAt(selectedRow, 0).toString()); // Product ID is likely an int
                    Product product = productService.getProductById(productId);
                    if (product != null) {
                        selectedProductId = product.getProductId();
                        productIdField.setText(String.valueOf(product.getProductId()));
                        skuField.setText(product.getSku());
                        nameField.setText(product.getName());
                        categoryComboBox.setSelectedItem(product.getCategory());
                        priceField.setText(String.valueOf(product.getPrice()));
                        supplierField.setText(product.getSupplier());
                        statusComboBox.setSelectedItem(product.getStatus());
                        saveButton.setText("Update Product"); // Change the button text to "Update Product"
                    }
                }
            }
        });

        loadProducts();
    }

    // Load products into the table
    private void loadProducts() {
        tableModel.setRowCount(0);  // Clear existing rows
        List<Product> products = productService.getAllProducts();
        for (Product product : products) {
            tableModel.addRow(new Object[]{
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

    // Load distinct categories into the categoryComboBox
    private void loadDistinctCategories() {
        categoryComboBox.removeAllItems();
        List<String> categories = productService.getDistinctCategories();
        for (String category : categories) {
            categoryComboBox.addItem(category);
        }
        categoryComboBox.addItem("Other");
        newCategoryField.setVisible(false);
    }

    // Method to save or update a product
    private void saveOrUpdateProduct() {
        String sku = skuField.getText().trim();
        String name = nameField.getText().trim();
        String category = newCategoryField.isVisible() ? newCategoryField.getText().trim() : (String) categoryComboBox.getSelectedItem();
        String priceText = priceField.getText().trim();
        String supplier = supplierField.getText().trim();
        String status = (String) statusComboBox.getSelectedItem();

        if (!validateInput(sku, name, category, priceText, supplier)) return;

        // Check for duplicate SKU
        if (productService.isSkuDuplicate(sku) && (selectedProductId == -1 || !sku.equals(productService.getProductById(selectedProductId).getSku()))) {
            JOptionPane.showMessageDialog(this, "SKU already exists. Please use a different SKU.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        double price = Double.parseDouble(priceText);
        Product product = new Product();
        product.setSku(sku);
        product.setName(name);
        product.setCategory(category);
        product.setPrice(price);
        product.setSupplier(supplier);
        product.setStatus(status);

        if (selectedProductId == -1) {
            if (productService.addProduct(product)) {
                JOptionPane.showMessageDialog(this, "Product added successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add product.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            product.setProductId(selectedProductId);
            if (productService.updateProduct(product)) {
                JOptionPane.showMessageDialog(this, "Product updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update product.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        loadProducts();
        loadDistinctCategories();  // Refresh the categories in case a new one was added
        clearForm();
    }

    // Method to delete a product (mark as unavailable)
    private void confirmAndDeleteProduct() {
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this product?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (productService.deleteProduct(selectedProductId)) {
                JOptionPane.showMessageDialog(this, "Product successfully deleted or marked as unavailable.", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadProducts();
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete or mark product as unavailable.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Method to clear the form
    private void clearForm() {
        productIdField.setText("");
        skuField.setText("");
        nameField.setText("");
        categoryComboBox.setSelectedIndex(0);
        newCategoryField.setText("");
        priceField.setText("");
        supplierField.setText("");
        statusComboBox.setSelectedIndex(0);
        saveButton.setText("Add Product");
        selectedProductId = -1; // Reset selected product
    }

    // Method to search products by name or category
    private void searchProducts(String query) {
        tableModel.setRowCount(0);  // Clear existing rows
        List<Product> products = productService.searchProductsByNameOrCategory(query);
        for (Product product : products) {
            tableModel.addRow(new Object[]{
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

    // Validate input fields
    private boolean validateInput(String sku, String name, String category, String priceText, String supplier) {
        if (!InputValidator.isValidString(sku)) {
            JOptionPane.showMessageDialog(this, "SKU cannot be empty.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (!InputValidator.isValidString(name)) {
            JOptionPane.showMessageDialog(this, "Name cannot be empty.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (!InputValidator.isValidString(category)) {
            JOptionPane.showMessageDialog(this, "Category cannot be empty.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (!InputValidator.isValidNumber(priceText)) {
            JOptionPane.showMessageDialog(this, "Price must be a valid number.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (!InputValidator.isValidString(supplier)) {
            JOptionPane.showMessageDialog(this, "Supplier cannot be empty.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }
}
