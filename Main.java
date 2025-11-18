import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

class User {
    private String username;
    private String role;

    public User(String username, String role) {
        this.username = username;
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public String getRole() {
        return role;
    }
}

class PermissionManager {
    public String getPermissions(User user) {
        switch (user.getRole()) {
            case "student":
                return "VIEW ONLY";
            case "teacher":
                return "VIEW + EDIT";
            case "admin":
                return "FULL ACCESS";
            default:
                return "NO PERMISSION";
        }
    }
}

class SecurityLayer {
    public boolean passwordCheck(String inputPassword, String realPassword) {
        return inputPassword.equals(realPassword);
    }

    public int generate2FACode() {
        Random rand = new Random();
        return 100000 + rand.nextInt(900000);
    }

    public boolean twoFactorCheck(int realCode, int userCode) {
        return realCode == userCode;
    }

    public boolean intrusionDetection(boolean otpMatch) {
        return otpMatch;
    }
}

public class Main {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);

        // Users and passwords map
        Map<String, String> users = new HashMap<>();
        users.put("Paula", "pass123");
        users.put("Mark James", "mypassword");
        users.put("Ambot", "admin2025");

        System.out.print("Enter username: ");
        String username = input.nextLine().trim();

        if (!users.containsKey(username)) {
            System.out.println("Access denied. Unknown user.");
            return;
        }

        System.out.print("Enter password: ");
        String password = input.nextLine().trim();

        SecurityLayer sl = new SecurityLayer();
        if (!sl.passwordCheck(password, users.get(username))) {
            System.out.println("Password failed.");
            return;
        }

        System.out.print("Enter role (student, teacher, admin): ");
        String role = input.nextLine().trim();

        User user = new User(username, role);
        PermissionManager pm = new PermissionManager();

        int otp = sl.generate2FACode();
        System.out.println("Your 2FA code: " + otp);

        System.out.print("Enter the 2FA code: ");
        int typedOtp = input.nextInt();

        boolean otpMatch = sl.twoFactorCheck(otp, typedOtp);

        if (!sl.intrusionDetection(otpMatch)) {
            System.out.println("Suspicious activity detected! Access blocked.");
            return;
        }

        System.out.println("Login successful!");
        System.out.println("Permission granted: " + pm.getPermissions(user));
    }
}
