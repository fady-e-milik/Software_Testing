package TestCases;

/**
 * Simple thread-local test context to store generated credentials for a scenario.
 */
public class TestContext {
    private static final ThreadLocal<String> email = new ThreadLocal<>();
    private static final ThreadLocal<String> password = new ThreadLocal<>();

    public static void setEmail(String e) { email.set(e); }
    public static void setPassword(String p) { password.set(p); }
    public static String getEmail() { return email.get(); }
    public static String getPassword() { return password.get(); }
    public static void clear() { email.remove(); password.remove(); }
}
