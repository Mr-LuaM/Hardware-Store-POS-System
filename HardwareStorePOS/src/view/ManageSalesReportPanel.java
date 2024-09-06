package view;

import service.SaleService;
import model.Sale;


import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;

public class ManageSalesReportPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private JTable salesTable;
    private DefaultTableModel salesTableModel;
    private JTextField searchField;
    private SaleService saleService;

    public ManageSalesReportPanel() {
        saleService = new SaleService();
        initializeUIComponents();
        loadSalesData();
    }

    private void initializeUIComponents() {

        // Top Panel with Search and Refresh Buttons
        FlowLayout fl_topPanel = new FlowLayout(FlowLayout.LEFT);
        fl_topPanel.setAlignOnBaseline(true);
        fl_topPanel.setVgap(0);
        JPanel topPanel = new JPanel(fl_topPanel);
        JLabel searchLabel = new JLabel("Search Sales:");
        searchField = new JTextField(20);
        JButton searchButton = new JButton("Search");
        JButton refreshButton = new JButton("Refresh");

        searchButton.addActionListener(e -> searchSales());
        refreshButton.addActionListener(e -> loadSalesData());

        topPanel.add(searchLabel);
        topPanel.add(searchField);
        topPanel.add(searchButton);
        topPanel.add(refreshButton);

        // Sales Table
        String[] columnNames = {"Sale ID", "Username", "Total Amount", "Payment Method", "Date", "Items"};
        salesTableModel = new DefaultTableModel(columnNames, 0);
        salesTable = new JTable(salesTableModel);
        JScrollPane scrollPane = new JScrollPane(salesTable);

        // Use GroupLayout for responsiveness
        GroupLayout groupLayout = new GroupLayout(this);
        groupLayout.setHorizontalGroup(
        	groupLayout.createParallelGroup(Alignment.LEADING)
        		.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 414, Short.MAX_VALUE)
        		.addGroup(groupLayout.createSequentialGroup()
        			.addComponent(topPanel, GroupLayout.DEFAULT_SIZE, 404, Short.MAX_VALUE)
        			.addGap(20))
        );
        groupLayout.setVerticalGroup(
        	groupLayout.createParallelGroup(Alignment.LEADING)
        		.addGroup(groupLayout.createSequentialGroup()
        			.addContainerGap()
        			.addComponent(topPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        			.addPreferredGap(ComponentPlacement.RELATED)
        			.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 240, Short.MAX_VALUE))
        );
        groupLayout.setAutoCreateContainerGaps(true);

        setLayout(groupLayout);
    }

    // Load sales with details (user and items)
    private void loadSalesData() {
        salesTableModel.setRowCount(0);  // Clear table
        List<Sale> sales = saleService.getAllSalesWithDetails();  // Fetch sales with all items

        for (Sale sale : sales) {
            salesTableModel.addRow(new Object[]{
                sale.getSaleId(),
                sale.getUsername(),
                sale.getTotalAmount(),
                sale.getPaymentMethod(),
                sale.getSaleDate(),
                sale.getAllItemsDescription()  // Display concatenated items description
            });
        }
    }

    // Search sales by ID, username, or payment method
    private void searchSales() {
        String query = searchField.getText().trim();
        salesTableModel.setRowCount(0);  // Clear the table

        // Fetch all sales with details
        List<Sale> sales = saleService.getAllSalesWithDetails();

        // Filter sales based on the search query
        for (Sale sale : sales) {
            // Search by Sale ID, Username, or Payment Method
            if (String.valueOf(sale.getSaleId()).contains(query) ||  
                sale.getUsername().toLowerCase().contains(query.toLowerCase()) ||  
                sale.getPaymentMethod().toLowerCase().contains(query.toLowerCase())) {  
                
                // Add the filtered sale row to the table model
                salesTableModel.addRow(new Object[]{
                    sale.getSaleId(),
                    sale.getUsername(),
                    sale.getTotalAmount(),
                    sale.getPaymentMethod(),
                    sale.getSaleDate(),
                    sale.getAllItemsDescription()  // Concatenated items
                });
            }
        }
    }


}