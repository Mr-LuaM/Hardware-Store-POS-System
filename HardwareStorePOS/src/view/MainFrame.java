package view;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private static final long serialVersionUID = 1L;
    private JMenu userMenu;

    // Define buttons as instance variables so they can be accessed in setUserInfo
    private JButton manageProductsButton;
    private JButton processSaleButton;
    private JButton viewReportsButton;
    private JButton manageUsersButton;
    private JButton manageInventoryButton;

    private JButton currentActiveButton;

    public MainFrame() {
        setTitle("Hardware Store POS System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1216, 900);
        setLocationRelativeTo(null);
        getContentPane().setLayout(new BorderLayout());

        // Top Banner
        JPanel bannerPanel = new JPanel();
        bannerPanel.setBackground(new Color(0, 70, 137)); // Dark blue color to complement the ACE red
        bannerPanel.setLayout(new BorderLayout());

        // Load the logo image from resources
        ImageIcon logoIcon = new ImageIcon(getClass().getResource("/resources/logo.png"));
        JLabel logoLabel = new JLabel(logoIcon);
        logoLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Add some padding around the logo
        logoLabel.setHorizontalAlignment(SwingConstants.LEFT);

        // Banner label
        JLabel bannerLabel = new JLabel("Hardware Store POS", SwingConstants.RIGHT);
        bannerLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        bannerLabel.setForeground(Color.WHITE);

        bannerPanel.add(logoLabel, BorderLayout.WEST);
        bannerPanel.add(bannerLabel, BorderLayout.EAST);
        bannerPanel.setPreferredSize(new Dimension(getWidth(), 120));

        getContentPane().add(bannerPanel, BorderLayout.NORTH);

        // Sidebar
        JPanel sidebarPanel = new JPanel();
        sidebarPanel.setBackground(new Color(214, 40, 40)); // ACE red color
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setPreferredSize(new Dimension(200, getHeight())); // Fixed width sidebar

        // Initialize buttons
        manageProductsButton = createSidebarButton("Manage Products");
        processSaleButton = createSidebarButton("Process Sale");
        viewReportsButton = createSidebarButton("View Reports");
        manageUsersButton = createSidebarButton("Manage Users");
        manageInventoryButton = createSidebarButton("Manage Inventory");

        // Add buttons to the sidebar
        sidebarPanel.add(manageProductsButton);
        sidebarPanel.add(processSaleButton);
        sidebarPanel.add(viewReportsButton);
        sidebarPanel.add(manageUsersButton);
        sidebarPanel.add(manageInventoryButton);

        getContentPane().add(sidebarPanel, BorderLayout.WEST);

        // Content Panel
        JPanel contentPanel = new JPanel(new CardLayout());
        getContentPane().add(contentPanel, BorderLayout.CENTER);

        // User Info Section (Menubar)
        JMenuBar menuBar = new JMenuBar();

        JMenu menuFile = new JMenu("File");
        JMenuItem menuItemExit = new JMenuItem("Exit");
        JMenuItem menuItemRestart = new JMenuItem("Restart");

        menuItemExit.addActionListener(e -> System.exit(0));
        menuItemRestart.addActionListener(e -> {
            dispose(); // Close the current frame
            new MainFrame().setVisible(true); // Restart the application by opening a new instance
        });

        menuFile.add(menuItemExit);
        menuFile.add(menuItemRestart);
        menuBar.add(menuFile);

        // User Menu in Menubar
        userMenu = new JMenu("Unknown"); // Placeholder name
        JMenuItem logoutMenuItem = new JMenuItem("Logout");

        // Add action listener for logout
        logoutMenuItem.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Logging out...");
            dispose(); // Close the frame
            new LoginFrame().setVisible(true);
        });

        userMenu.add(logoutMenuItem);
        menuBar.add(Box.createHorizontalGlue()); // Right-align the user menu
        menuBar.add(userMenu);

        setJMenuBar(menuBar);

        // Button Action Listeners
        manageProductsButton.addActionListener(e -> switchToPanel(new ManageProductPanel(), manageProductsButton, contentPanel));
        processSaleButton.addActionListener(e -> switchToPanel(new ManageSalePanel(), processSaleButton, contentPanel));
        viewReportsButton.addActionListener(e -> switchToPanel(new ManageSalesReportPanel(), viewReportsButton, contentPanel));
        manageUsersButton.addActionListener(e -> switchToPanel(new ManageUsersPanel(), manageUsersButton, contentPanel));
        manageInventoryButton.addActionListener(e -> switchToPanel(new ManageInventoryPanel(), manageInventoryButton, contentPanel));
    }

    private JButton createSidebarButton(String text) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        styleButton(button, new Color(0, 70, 137));
        button.setMaximumSize(new Dimension(200, 48));
        return button;
    }

    private void styleButton(JButton button, Color bgColor) {
        button.setFocusPainted(false);
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
    }

    private void switchToPanel(JPanel panel, JButton button, JPanel contentPanel) {
        contentPanel.removeAll();
        contentPanel.add(panel);
        contentPanel.revalidate();
        contentPanel.repaint();
        highlightButton(button);
    }

    private void highlightButton(JButton button) {
        if (currentActiveButton != null) {
            styleButton(currentActiveButton, new Color(0, 70, 137));
        }
        styleButton(button, new Color(0, 100, 200)); // Highlight color
        currentActiveButton = button;
    }

    public void setUserInfo(String userName, String userRole) {
        userMenu.setText(userName + " - " + userRole);

        switch (userRole) {
            case "Admin":
                // Admin can access all routes
                switchToFirstEnabledPanel();
                break;
            case "Manager":
                manageUsersButton.setEnabled(false);
                switchToFirstEnabledPanel();
                break;
            case "Cashier":
                manageProductsButton.setEnabled(false);
                viewReportsButton.setEnabled(false);
                manageUsersButton.setEnabled(false);
                manageInventoryButton.setEnabled(false);
                switchToFirstEnabledPanel(); // Automatically redirect to the first enabled panel
                break;
            default:
                throw new IllegalArgumentException("Unknown role: " + userRole);
        }
    }

    private void switchToFirstEnabledPanel() {
        // Check and activate the first enabled button
        if (manageProductsButton.isEnabled()) {
            manageProductsButton.doClick();
        } else if (processSaleButton.isEnabled()) {
            processSaleButton.doClick();
        } else if (viewReportsButton.isEnabled()) {
            viewReportsButton.doClick();
        } else if (manageUsersButton.isEnabled()) {
            manageUsersButton.doClick();
        } else if (manageInventoryButton.isEnabled()) {
            manageInventoryButton.doClick();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true)); // Start with the LoginFrame
    }
}