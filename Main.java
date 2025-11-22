import java.util.*;
class User {
    private String username;
    private String role;

    public User(String username, String role) {
        this.username = username;
        this.role = role;
    }

    public String getUsername() { return username; }
    public String getRole() { return role; }
}

class PermissionManager {
    public String getPermissions(User user) {
        switch (user.getRole()) {
            case "student": return "VIEW ONLY";
            case "teacher": return "VIEW + EDIT";
            case "admin": return "FULL ACCESS";
            default: return "NO PERMISSION";
        }
    }
}

class SecurityLayer {

    // Password check
    public boolean passwordCheck(String inputPassword, String realPassword) {
        return inputPassword.equals(realPassword);
    }

    // OTP generation
    public int generate2FACode() {
        Random r = new Random();
        return 100000 + r.nextInt(900000);
    }

    // Check OTP
    public boolean twoFactorCheck(int realCode, int userCode) {
        return realCode == userCode;
    }

    // Intrusion detection (STRONGER)
    public boolean intrusionDetection(boolean otpMatch, int failedAttempts) {
        return otpMatch && failedAttempts == 0;
    }
}

public class Main {

    // Validate input so that sir cannot break it
    public static String safeInput(Scanner input, String message) {
        String value;
        do {
            System.out.print(message);
            value = input.nextLine().trim();
            if (value.isEmpty()) {
                System.out.println("Input cannot be blank. Try again.");
            }
        } while (value.isEmpty());
        return value;
    }

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);

        Map<String, String> passwords = new HashMap<>();
        Map<String, String> roles = new HashMap<>();

        passwords.put("Paula", "pass123");
        roles.put("Paula", "student");

        passwords.put("Mark James", "mypassword");
        roles.put("Mark James", "teacher");

        passwords.put("Ambot", "admin2025");
        roles.put("Ambot", "admin");

        // Input username
        String username = safeInput(input, "Enter username: ");

        if (!passwords.containsKey(username)) {
            System.out.println("ACCESS DENIED: Unknown user.");
            return;
        }

        SecurityLayer sl = new SecurityLayer();

        // Password attempts
        int passwordAttempts = 0;
        boolean passwordSuccess = false;

        while (passwordAttempts < 3) {
            String pw = safeInput(input, "Enter password: ");

            if (sl.passwordCheck(pw, passwords.get(username))) {
                passwordSuccess = true;
                break;
            } else {
                passwordAttempts++;
                System.out.println("Incorrect password (" + passwordAttempts + "/3).");
            }
        }

        if (!passwordSuccess) {
            System.out.println("ACCOUNT LOCKED due to multiple failed password attempts.");
            return;
        }

        // Auto-role assignment
        String role = roles.get(username);
        User user = new User(username, role);
        PermissionManager pm = new PermissionManager();

        // Generate OTP
        int otp = sl.generate2FACode();
        System.out.println("Your 2FA Code: " + otp);

        // OTP attempts
        int otpAttempts = 0;
        boolean otpSuccess = false;

        while (otpAttempts < 3) {

            System.out.print("Enter 2FA code: ");
            String otpInput = input.nextLine().trim();

            // Trap non-number input
            if (!otpInput.matches("\\d+")) {
                System.out.println("Invalid input. Numbers only.");
                otpAttempts++;
                continue;
            }

            int typedOtp = Integer.parseInt(otpInput);

            if (sl.twoFactorCheck(otp, typedOtp)) {
                otpSuccess = true;
                break;
            } else {
                otpAttempts++;
                System.out.println("Incorrect 2FA code (" + otpAttempts + "/3).");
            }
        }

        if (!otpSuccess) {
            System.out.println("ACCESS BLOCKED due to multiple incorrect 2FA attempts.");
            return;
        }

        // Intrusion detection
        if (!sl.intrusionDetection(otpSuccess, otpAttempts)) {
            System.out.println("INTRUSION DETECTED! System locked.");
            return;
        }

        // SUCCESS
        System.out.println("\nLogin Successful!");
        System.out.println("Assigned Role: " + user.getRole().toUpperCase());
        System.out.println("Permission Level: " + pm.getPermissions(user));
    }
}
