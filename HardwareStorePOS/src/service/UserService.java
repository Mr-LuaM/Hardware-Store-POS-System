package service;

import java.util.List;

import controller.UserController;
import model.User;
import util.HashUtils;
import util.InputValidator;

public class UserService {
    private final UserController userController;

    public UserService() {
        this.userController = new UserController();
    }

    public boolean registerUser(String username, String password, String role) {
        if (!InputValidator.isValidString(username) || !InputValidator.isValidString(password) || !InputValidator.isValidString(role)) {
            return false; // Invalid input
        }

        String hashedPassword = HashUtils.hashPassword(password);

        User user = new User();
        user.setUsername(username);
        user.setPassword(hashedPassword);
        user.setRole(role);

        return userController.addUser(user);
    }

    public User loginUser(String username, String password) {
        if (!InputValidator.isValidString(username) || !InputValidator.isValidString(password)) {
            return null; // Invalid input
        }

        String hashedPassword = HashUtils.hashPassword(password);

        return userController.loginUser(username, hashedPassword);
    }

    public boolean updateUser(User user) {
        return userController.updateUser(user);
    }

    public boolean softDeleteUser(int userId) {
        return userController.softDeleteUser(userId);
    }

    public User getUserById(int userId) {
        return userController.getUserById(userId);
    }

    public List<User> getAllUsers() {
        return userController.getAllUsers();
    }

    public List<User> searchUsersByUsername(String username) {
        return userController.searchUsersByUsername(username);
    }
}
