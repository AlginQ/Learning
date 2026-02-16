import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordHashGenerator {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        String adminPassword = "admin123";
        String userPassword = "user123";
        
        String adminHash = encoder.encode(adminPassword);
        String userHash = encoder.encode(userPassword);
        
        System.out.println("Admin password hash for 'admin123':");
        System.out.println(adminHash);
        System.out.println();
        System.out.println("User password hash for 'user123':");
        System.out.println(userHash);
        System.out.println();
        System.out.println("Verify admin123:");
        System.out.println("Match result: " + encoder.matches("admin123", adminHash));
        System.out.println();
        System.out.println("Verify user123:");
        System.out.println("Match result: " + encoder.matches("user123", userHash));
    }
}