package view;

import service.UserService;
import model.User;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {
    private static final long serialVersionUID = 1L;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private UserService userService;

    public LoginFrame() {
        setTitle("User Login");
        setMinimumSize(new Dimension(500, 400));  // Set minimum size
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);  // Center on screen

        userService = new UserService();

        // Main panel with GridBagLayout for responsive design and centering
        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);  // Reduced margin between components
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Logo
        ImageIcon originalIcon = new ImageIcon(getClass().getResource("/resources/logo.png"));
        Image originalImage = originalIcon.getImage();
        Image scaledImage = originalImage.getScaledInstance(300, 180, Image.SCALE_SMOOTH);  // Adjust the size of the logo to reduce distortion
        JLabel logoLabel = new JLabel(new ImageIcon(scaledImage));
        logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        mainPanel.add(logoLabel, gbc);

        // Username Label
        JLabel usernameLabel = new JLabel("Username:");
        gbc.gridwidth = 1;
        gbc.gridy = 1;
        gbc.gridx = 0;
        mainPanel.add(usernameLabel, gbc);

        // Username Field
        usernameField = new JTextField(20);
        gbc.gridx = 1;
        mainPanel.add(usernameField, gbc);

        // Password Label
        JLabel passwordLabel = new JLabel("Password:");
        gbc.gridx = 0;
        gbc.gridy = 2;
        mainPanel.add(passwordLabel, gbc);

        // Password Field
        passwordField = new JPasswordField(20);
        gbc.gridx = 1;
        mainPanel.add(passwordField, gbc);

        // Buttons Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton loginButton = new JButton("Login");
        JButton cancelButton = new JButton("Cancel");

        loginButton.addActionListener(e -> login());
        cancelButton.addActionListener(e -> System.exit(0));

        buttonPanel.add(cancelButton);
        buttonPanel.add(loginButton);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(10, 10, 10, 10);  // Normal margin between the buttons and the rest of the form
        mainPanel.add(buttonPanel, gbc);

        // Add the main panel to the frame
        getContentPane().add(mainPanel);

        // Pack and center the frame
        pack();
        setLocationRelativeTo(null);

        // Set background color and button styles
        mainPanel.setBackground(new Color(240, 240, 240));  // Light grey background for the form
        loginButton.setBackground(new Color(0, 70, 137));  // Dark blue color
        loginButton.setForeground(Color.WHITE);
        cancelButton.setBackground(new Color(214, 40, 40));  // ACE red color
        cancelButton.setForeground(Color.WHITE);
    }

    // Method to handle the login process
    private void login() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both username and password.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        User user = userService.loginUser(username, password);

        if (user != null) {
            JOptionPane.showMessageDialog(this, "Login successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
            dispose();
            MainFrame mainFrame = new MainFrame();
            mainFrame.setUserInfo(user.getUsername(), user.getRole());
            mainFrame.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "Invalid username or password.", "Login Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}
