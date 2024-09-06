package util;

public class InputValidator {

    // Method to validate that a string is not empty or null
    public static boolean isValidString(String input) {
        if (input == null || input.trim().isEmpty()) {
            logValidationError("String Validation", "String cannot be null or empty.");
            return false;
        }
        return true;
    }

    // Method to validate if a string is a valid number (e.g., for price, quantity)
    public static boolean isValidNumber(String input) {
        if (input == null || input.trim().isEmpty()) {
            logValidationError("Number Validation", "Number cannot be null or empty.");
            return false;
        }
        try {
            Double.parseDouble(input);
            return true;
        } catch (NumberFormatException e) {
            logValidationError("Number Validation", "Invalid number format.");
            return false;
        }
    }

    // Method to validate if a string is a valid integer
    public static boolean isValidInteger(String input) {
        if (input == null || input.trim().isEmpty()) {
            logValidationError("Integer Validation", "Integer cannot be null or empty.");
            return false;
        }
        try {
            Integer.parseInt(input);
            return true;
        } catch (NumberFormatException e) {
            logValidationError("Integer Validation", "Invalid integer format.");
            return false;
        }
    }

    // Method to validate an email address (simple validation)
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            logValidationError("Email Validation", "Email cannot be null or empty.");
            return false;
        }
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        if (email.matches(emailRegex)) {
            return true;
        } else {
            logValidationError("Email Validation", "Invalid email format.");
            return false;
        }
    }

    // Method to log validation errors with a message
    private static void logValidationError(String fieldName, String message) {
        String logMessage = "Validation Error - " + fieldName + ": " + message;
        LoggerUtils.log(logMessage);
    }
}
