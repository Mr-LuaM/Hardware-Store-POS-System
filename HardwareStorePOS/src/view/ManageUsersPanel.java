package view;

import service.UserService;
import model.User;
import util.HashUtils;
import util.InputValidator;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class ManageUsersPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private JTable userTable;
    private DefaultTableModel tableModel;
    private JTextField usernameField;
    private JPasswordField passwordField, confirmPasswordField;
    private JComboBox<String> roleComboBox;
    private UserService userService;
    private JButton saveButton;
    private int selectedUserId = -1;

    public ManageUsersPanel() {
        userService = new UserService();
        initializeUIComponents();
        setupEventListeners();
        loadUsers();
    }

    private void initializeUIComponents() {
        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        JLabel searchLabel = new JLabel("Search:");
        JTextField searchField = new JTextField();
        JButton searchButton = new JButton("Search");
        JButton refreshButton = new JButton("Refresh");

        String[] columnNames = {"User ID", "Username", "Password", "Role"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            /**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        userTable = new JTable(tableModel);
        setupTableColumnWidths();

        JScrollPane tableScrollPane = new JScrollPane(userTable);

        JPanel formPanel = createFormPanel();

        saveButton = new JButton("Save");
        JButton clearButton = new JButton("Clear");
        JButton deleteButton = new JButton("Delete");

        // Set up button actions
        searchButton.addActionListener(e -> searchUsers(searchField.getText().trim()));
        refreshButton.addActionListener(e -> loadUsers());
        clearButton.addActionListener(e -> clearForm());
        deleteButton.addActionListener(e -> deleteUser());

        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addComponent(searchLabel)
                    .addComponent(searchField)
                    .addComponent(searchButton)
                    .addComponent(refreshButton))
                .addComponent(tableScrollPane)
                .addComponent(formPanel)
                .addGroup(layout.createSequentialGroup()
                    .addComponent(saveButton)
                    .addComponent(clearButton)
                    .addComponent(deleteButton))
        );

        layout.setVerticalGroup(
            layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(searchLabel)
                    .addComponent(searchField)
                    .addComponent(searchButton)
                    .addComponent(refreshButton))
                .addComponent(tableScrollPane)
                .addComponent(formPanel)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(saveButton)
                    .addComponent(clearButton)
                    .addComponent(deleteButton))
        );
    }

    private void setupTableColumnWidths() {
        TableColumnModel columnModel = userTable.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(50);
        columnModel.getColumn(1).setPreferredWidth(150);
        columnModel.getColumn(2).setPreferredWidth(100);
        columnModel.getColumn(3).setPreferredWidth(100);
    }

    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel();
        formPanel.setBorder(BorderFactory.createTitledBorder("User Details"));

        JLabel usernameLabel = new JLabel("Username:");
        usernameField = new JTextField();
        JLabel passwordLabel = new JLabel("Password:");
        passwordField = new JPasswordField();
        JLabel confirmPasswordLabel = new JLabel("Confirm Password:");
        confirmPasswordField = new JPasswordField();
        JLabel roleLabel = new JLabel("Role:");
        roleComboBox = new JComboBox<>(new String[]{"Admin", "Manager", "Cashier"});

        GroupLayout formLayout = new GroupLayout(formPanel);
        formPanel.setLayout(formLayout);
        formLayout.setAutoCreateGaps(true);
        formLayout.setAutoCreateContainerGaps(true);

        formLayout.setHorizontalGroup(
            formLayout.createSequentialGroup()
                .addGroup(formLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(usernameLabel)
                    .addComponent(passwordLabel)
                    .addComponent(confirmPasswordLabel)
                    .addComponent(roleLabel))
                .addGroup(formLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(usernameField)
                    .addComponent(passwordField)
                    .addComponent(confirmPasswordField)
                    .addComponent(roleComboBox))
        );

        formLayout.setVerticalGroup(
            formLayout.createSequentialGroup()
                .addGroup(formLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(usernameLabel)
                    .addComponent(usernameField))
                .addGroup(formLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(passwordLabel)
                    .addComponent(passwordField))
                .addGroup(formLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(confirmPasswordLabel)
                    .addComponent(confirmPasswordField))
                .addGroup(formLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(roleLabel)
                    .addComponent(roleComboBox))
        );

        return formPanel;
    }

    private void setupEventListeners() {
        saveButton.addActionListener(e -> {
            if (selectedUserId == -1) {
                addUser();
            } else {
                editUser();
            }
        });

        userTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int selectedRow = userTable.getSelectedRow();
                if (selectedRow >= 0) {
                    selectedUserId = (int) tableModel.getValueAt(selectedRow, 0);
                    usernameField.setText(tableModel.getValueAt(selectedRow, 1).toString());
                    roleComboBox.setSelectedItem(tableModel.getValueAt(selectedRow, 3).toString());
                    passwordField.setText("");
                    confirmPasswordField.setText("");
                    saveButton.setText("Update");
                }
            }
        });
    }

    private void loadUsers() {
        tableModel.setRowCount(0);
        List<User> users = userService.getAllUsers();
        for (User user : users) {
            tableModel.addRow(new Object[]{
                user.getUserId(),
                user.getUsername(),
                "********",
                user.getRole()
            });
        }
    }

    private void searchUsers(String username) {
        tableModel.setRowCount(0);
        List<User> users = userService.searchUsersByUsername(username);
        for (User user : users) {
            tableModel.addRow(new Object[]{
                user.getUserId(),
                user.getUsername(),
                "********",
                user.getRole()
            });
        }
    }

    private void addUser() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        String confirmPassword = new String(confirmPasswordField.getPassword()).trim();
        String role = (String) roleComboBox.getSelectedItem();

        if (!validateInput(username, password, confirmPassword, role)) return;

        if (userService.registerUser(username, password, role)) {
            JOptionPane.showMessageDialog(this, "User added successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadUsers();
            clearForm();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to add user. Username might already be taken.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editUser() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        String confirmPassword = new String(confirmPasswordField.getPassword()).trim();
        String role = (String) roleComboBox.getSelectedItem();

        if (!validateInput(username, password, confirmPassword, role)) return;

        String hashedPassword = password.isEmpty() ? 
            userService.getUserById(selectedUserId).getPassword() :
            HashUtils.hashPassword(password);

        User user = new User(selectedUserId, username, hashedPassword, role);

        if (userService.updateUser(user)) {
            JOptionPane.showMessageDialog(this, "User updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadUsers();
            clearForm();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to update user.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteUser() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow >= 0) {
            int userId = (int) tableModel.getValueAt(selectedRow, 0);
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this user?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                if (userService.softDeleteUser(userId)) {
                    JOptionPane.showMessageDialog(this, "User deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadUsers();
                    clearForm();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to delete user.", "Error", JOptionPane.ERROR_MESSAGE);
                }
           

 }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a user to delete.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearForm() {
        usernameField.setText("");
        passwordField.setText("");
        confirmPasswordField.setText("");
        roleComboBox.setSelectedIndex(0);
        selectedUserId = -1;
        saveButton.setText("Save");
    }

    private boolean validateInput(String username, String password, String confirmPassword, String role) {
        if (!InputValidator.isValidString(username)) {
            JOptionPane.showMessageDialog(this, "Username is required.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }
}