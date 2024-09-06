package view;

import controller.UserController;
import model.User;
import util.HashUtils;
import util.InputValidator;

import javax.swing.*;
import java.awt.*;

public class AddUserFrame extends JFrame {
    private static final long serialVersionUID = 1L;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JComboBox<String> roleComboBox;
    private UserController userController;

    public AddUserFrame() {
        setTitle("Add New User");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        userController = new UserController();
        getContentPane().setLayout(null);

        // Create the input panel for user data
        JPanel inputPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        inputPanel.setBounds(0, 0, 384, 208);
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Username field
        inputPanel.add(new JLabel("Username:"));
        usernameField = new JTextField();
        inputPanel.add(usernameField);

        // Password field
        inputPanel.add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        inputPanel.add(passwordField);

        // Confirm Password field
        inputPanel.add(new JLabel("Confirm Password:"));
        confirmPasswordField = new JPasswordField();
        inputPanel.add(confirmPasswordField);

        // Role selection
        inputPanel.add(new JLabel("Role:"));
        roleComboBox = new JComboBox<>(new String[]{"Cashier", "Manager"});
        inputPanel.add(roleComboBox);

        getContentPane().add(inputPanel);

        // Create the button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBounds(0, 218, 384, 43);
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");

        // Add action listeners to the buttons
        saveButton.addActionListener(e -> saveUser());
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);

        getContentPane().add(buttonPanel);
    }

    // Method to save the user
    private void saveUser() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        String confirmPassword = new String(confirmPasswordField.getPassword()).trim();
        String role = (String) roleComboBox.getSelectedItem();

        // Validate inputs using ValidationUtils
        if (!InputValidator.isValidString(username)) {
            JOptionPane.showMessageDialog(this, "Username is required.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!InputValidator.isValidString(password) || !InputValidator.isValidString(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "Password fields cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Hash the password using HashUtils
        String hashedPassword = HashUtils.hashPassword(password);
        User user = new User(username, hashedPassword, role);

        // Attempt to save the user using the controller
        if (userController.addUser(user)) {
            JOptionPane.showMessageDialog(this, "User added successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to add user. Username might already be taken.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AddUserFrame().setVisible(true));
    }
}
