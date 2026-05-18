package ias.dekstop;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

public class LoginFrame extends JFrame {

    private JTextField emailField;
    private JPasswordField passwordField;
    private JLabel registerLink;

    private static final String FIREBASE_API_KEY = "AIzaSyDFQpn2ADkTg0F5t4uo4cwkyRDg51fkUn4";
    public static String SESSION_COOKIE = null;
    
    // Colors matching web design
    private static final Color BG_COLOR = new Color(245, 245, 245); // #F5F5F5
    private static final Color TEXT_COLOR = new Color(51, 51, 51); // #333333
    private static final Color BUTTON_GREEN = new Color(109, 179, 63); // #6DB33F
    private static final Color LINK_BLUE = new Color(0, 0, 255); // #0000FF
    private static final Color FIELD_BORDER = new Color(204, 204, 204); // #CCCCCC
    private static final String[] TOTP_REQUIRED_KEYS = {
            "requiresTotp",
            "requiresTOTP",
            "requireTotp",
            "totpRequired",
            "totp_enabled",
            "totpEnabled",
            "twoFactorEnabled",
            "two_factor_enabled",
            "mfaEnabled",
            "mfa_enabled",
            "twoFactorAuthEnabled",
            "two_factor_auth_enabled",
            "isTwoFactorEnabled",
            "isMfaEnabled",
            "2faEnabled"
    };

    public LoginFrame() {
        setTitle("IAS Firebase Login");
        setSize(450, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Set background color
        getContentPane().setBackground(BG_COLOR);
        
        // Create main container panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(BG_COLOR);
        mainPanel.setBorder(new EmptyBorder(60, 80, 60, 80));
        
        // Title
        JLabel titleLabel = new JLabel("Login");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        titleLabel.setBorder(new EmptyBorder(0, 0, 30, 0));
        
        // Email field container
        JPanel emailPanel = new JPanel();
        emailPanel.setLayout(new BoxLayout(emailPanel, BoxLayout.Y_AXIS));
        emailPanel.setBackground(BG_COLOR);
        emailPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        emailPanel.setBorder(new EmptyBorder(0, 0, 20, 0));
        
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        emailLabel.setForeground(TEXT_COLOR);
        emailLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        emailLabel.setBorder(new EmptyBorder(0, 0, 8, 0));
        
        emailField = new JTextField(20);
        emailField.setFont(new Font("Arial", Font.PLAIN, 14));
        emailField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(FIELD_BORDER, 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        emailField.setPreferredSize(new Dimension(250, 35));
        emailField.setMaximumSize(new Dimension(250, 35));
        emailField.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        emailPanel.add(emailLabel);
        emailPanel.add(emailField);
        
        // Password field container
        JPanel passwordPanel = new JPanel();
        passwordPanel.setLayout(new BoxLayout(passwordPanel, BoxLayout.Y_AXIS));
        passwordPanel.setBackground(BG_COLOR);
        passwordPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        passwordPanel.setBorder(new EmptyBorder(0, 0, 25, 0));
        
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        passwordLabel.setForeground(TEXT_COLOR);
        passwordLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        passwordLabel.setBorder(new EmptyBorder(0, 0, 8, 0));
        
        passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(FIELD_BORDER, 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        passwordField.setPreferredSize(new Dimension(250, 35));
        passwordField.setMaximumSize(new Dimension(250, 35));
        passwordField.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        passwordPanel.add(passwordLabel);
        passwordPanel.add(passwordField);
        
        // Login button
        JButton loginButton = new JButton("Login");
        loginButton.setFont(new Font("Arial", Font.PLAIN, 14));
        loginButton.setBackground(BUTTON_GREEN);
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setBorderPainted(false);
        loginButton.setPreferredSize(new Dimension(250, 40));
        loginButton.setMaximumSize(new Dimension(250, 40));
        loginButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        loginButton.setBorder(new EmptyBorder(0, 0, 0, 0));
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginButton.addActionListener(e -> login());
        
        // Register link
        JPanel linkPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        linkPanel.setBackground(BG_COLOR);
        linkPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        linkPanel.setBorder(new EmptyBorder(20, 0, 0, 0));
        
        JLabel noAccountLabel = new JLabel("No account? ");
        noAccountLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        noAccountLabel.setForeground(TEXT_COLOR);
        
        registerLink = new JLabel("Register here");
        registerLink.setFont(new Font("Arial", Font.PLAIN, 13));
        registerLink.setForeground(LINK_BLUE);
        registerLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
        registerLink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                new RegisterFrame();
                dispose();
            }
            
            @Override
            public void mouseEntered(MouseEvent e) {
                registerLink.setText("<html><u>Register here</u></html>");
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                registerLink.setText("Register here");
            }
        });
        
        linkPanel.add(noAccountLabel);
        linkPanel.add(registerLink);
        
        // Result label (initially hidden)
        JLabel resultLabel = new JLabel("");
        resultLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        resultLabel.setForeground(Color.RED);
        resultLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        resultLabel.setBorder(new EmptyBorder(15, 0, 0, 0));
        
        // Add components to main panel
        mainPanel.add(titleLabel);
        mainPanel.add(emailPanel);
        mainPanel.add(passwordPanel);
        mainPanel.add(loginButton);
        mainPanel.add(linkPanel);
        mainPanel.add(resultLabel);
        
        // Center the main panel
        setLayout(new BorderLayout());
        JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        centerPanel.setBackground(BG_COLOR);
        centerPanel.add(mainPanel);
        add(centerPanel, BorderLayout.CENTER);

        setVisible(true);
    }

    private void login() {
        try {
            String email = emailField.getText();
            String password = new String(passwordField.getPassword());

            SESSION_COOKIE = null;
            String idToken = firebaseLogin(email, password);

            SessionLoginResult result = sendTokenToBackend(idToken);

            if (!result.success) {
                JOptionPane.showMessageDialog(this, "Backend Login Failed");
                clearForm();
                return;
            }

            boolean local2faEnabled = TwoFactorStore.isEnabledForEmail(email);
            boolean needTotp = result.requiresTotp || local2faEnabled;

            if (needTotp) {
                TotpVerifyDialog totpDialog = new TotpVerifyDialog(this);
                totpDialog.setVisible(true);
                String code = totpDialog.getEnteredCode();
                if (code == null) {
                    clearForm();
                    return;
                }
                boolean ok;
                if (result.requiresTotp) {
                    ok = verifyTotpWithBackend(result, email, code);
                } else {
                    String secret = TwoFactorStore.getSecretForEmail(email);
                    ok = secret != null && TOTP.verify(secret, code);
                }
                if (!ok) {
                    JOptionPane.showMessageDialog(this, "Invalid or expired code. Please try again.");
                    clearForm();
                    return;
                }
            }

            new DashboardFrame(email, needTotp);
            dispose();

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Login Failed: " + e.getMessage());
            clearForm();
        }
    }

    /** Result of session login: success and whether TOTP verification is required. */
    private static class SessionLoginResult {
        final boolean success;
        final boolean requiresTotp;
        final String tempToken;
        SessionLoginResult(boolean success, boolean requiresTotp, String tempToken) {
            this.success = success;
            this.requiresTotp = requiresTotp;
            this.tempToken = tempToken;
        }
    }

    /** POST TOTP code to backend; returns true if verification succeeded. */
    private boolean verifyTotpWithBackend(SessionLoginResult result, String email, String code) {
        String safeEmail = jsonEscape(email != null ? email.trim() : "");
        String safeCode = jsonEscape(code != null ? code.trim() : "");
        String safeTempToken = jsonEscape(result.tempToken);
        if (!safeTempToken.isEmpty()) {
            int responseCode = postJson(
                    "http://localhost:8080/api/verifyTotp",
                    "{\"tempToken\":\"" + safeTempToken + "\",\"code\":\"" + safeCode + "\"}"
            );
            return responseCode == 200;
        }

        String[] urls = {
                "http://localhost:8080/api/verifyTotp",
                "http://localhost:8080/api/totp/verify",
                "http://localhost:8080/api/2fa/verify",
                "http://localhost:8080/api/mfa/verify",
                "http://localhost:8080/api/two-factor/verify"
        };
        String[] payloads = {
                "{\"code\":\"" + safeCode + "\"}",
                "{\"totp\":\"" + safeCode + "\"}",
                "{\"totpCode\":\"" + safeCode + "\"}",
                "{\"token\":\"" + safeCode + "\"}",
                "{\"otp\":\"" + safeCode + "\"}",
                "{\"email\":\"" + safeEmail + "\",\"code\":\"" + safeCode + "\"}",
                "{\"email\":\"" + safeEmail + "\",\"totp\":\"" + safeCode + "\"}",
                "{\"email\":\"" + safeEmail + "\",\"totpCode\":\"" + safeCode + "\"}",
                "{\"email\":\"" + safeEmail + "\",\"token\":\"" + safeCode + "\"}",
                "{\"email\":\"" + safeEmail + "\",\"otp\":\"" + safeCode + "\"}"
        };

        for (String verifyUrl : urls) {
            for (String payload : payloads) {
                int responseCode = postJson(verifyUrl, payload);
                if (responseCode == 200) {
                    return true;
                }
                if (responseCode == 404 || responseCode == 405) {
                    break;
                }
            }
        }
        return false;
    }

    private int postJson(String requestUrl, String json) {
        try {
            URL url = new URL(requestUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            if (SESSION_COOKIE != null) {
                conn.setRequestProperty("Cookie", SESSION_COOKIE);
            }
            conn.setDoOutput(true);
            try (OutputStream os = conn.getOutputStream()) {
                os.write(json.getBytes());
            }
            int responseCode = conn.getResponseCode();
            saveSessionCookie(conn);
            System.out.println("TOTP verify " + requestUrl + " -> HTTP " + responseCode);
            String responseBody = readResponseBody(conn, responseCode);
            if (!responseBody.isEmpty()) {
                System.out.println("TOTP verify response: " + responseBody);
            }
            return responseCode;
        } catch (Exception e) {
            System.out.println("TOTP verify failed for " + requestUrl + ": " + e.getMessage());
            return -1;
        }
    }

    /** Clears email and password fields. */
    private void clearForm() {
        emailField.setText("");
        passwordField.setText("");
    }

    private String firebaseLogin(String email, String password) throws Exception {

        if (email == null || email.trim().isEmpty() || password == null || password.isEmpty()) {
            throw new Exception("Email and password are required.");
        }

        String firebaseUrl =
                "https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key="
                        + FIREBASE_API_KEY;

        String jsonInput = "{"
                + "\"email\":\"" + email + "\","
                + "\"password\":\"" + password + "\","
                + "\"returnSecureToken\":true"
                + "}";

        URL url = new URL(firebaseUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        try (OutputStream os = conn.getOutputStream()) {
            os.write(jsonInput.getBytes());
        }

        int responseCode = conn.getResponseCode();

        InputStream stream = (responseCode == 200)
                ? conn.getInputStream()
                : conn.getErrorStream();

        StringBuilder response = new StringBuilder();
        if (stream != null) {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(stream))) {
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line);
                }
            }
        }

        String result = response.toString();
        System.out.println("Firebase Response (" + responseCode + "): " + result);

        if (responseCode != 200) {
            // Try to extract a friendly Firebase error message
            String message = "Login Failed.";
            if (result.contains("\"message\"")) {
                String extracted = result.replaceAll(".*\"message\"\\s*:\\s*\"([^\"]+)\".*", "$1");
                if (!extracted.isEmpty() && !extracted.equals(result)) {
                    message = extracted;
                }
            }
            throw new Exception(message + " (HTTP " + responseCode + ")");
        }

        // Safe extraction of idToken on success
        String idToken = result.replaceAll(".*\"idToken\"\\s*:\\s*\"([^\"]+)\".*", "$1");
        if (idToken == null || idToken.isEmpty() || idToken.equals(result)) {
            throw new Exception("Could not parse idToken from Firebase response.");
        }

        return idToken;
    }

    private SessionLoginResult sendTokenToBackend(String idToken) throws Exception {

        URL url = new URL("http://localhost:8080/api/sessionLogin");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        String jsonInput = "{ \"idToken\": \"" + idToken + "\" }";

        try (OutputStream os = conn.getOutputStream()) {
            os.write(jsonInput.getBytes());
        }

        int responseCode = conn.getResponseCode();
        System.out.println("Backend Response Code: " + responseCode);

        // Read response body (for requiresTotp flag)
        String responseBody = readResponseBody(conn, responseCode);
        System.out.println("Backend Response Body: " + responseBody);
        // Extract session cookie
        saveSessionCookie(conn);

        boolean requiresTotp = hasTrueBoolean(responseBody, TOTP_REQUIRED_KEYS);
        String tempToken = extractJsonString(responseBody, "tempToken");
        if (!requiresTotp && responseCode == 200 && !tempToken.isEmpty()) {
            requiresTotp = true;
        }
        if (!requiresTotp && responseCode == 200 && SESSION_COOKIE != null) {
            requiresTotp = fetchTotpStatusFromBackend();
        }

        return new SessionLoginResult(responseCode == 200, requiresTotp, tempToken);
    }

    private boolean fetchTotpStatusFromBackend() {
        String[] statusUrls = {
                "http://localhost:8080/api/totp/status",
                "http://localhost:8080/api/user"
        };
        for (String statusUrl : statusUrls) {
            try {
                URL url = new URL(statusUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                if (SESSION_COOKIE != null) {
                    conn.setRequestProperty("Cookie", SESSION_COOKIE);
                }
                int responseCode = conn.getResponseCode();
                if (responseCode != 200) {
                    continue;
                }
                String body = readResponseBody(conn, responseCode);
                System.out.println("TOTP status " + statusUrl + " -> " + body);
                if (hasTrueBoolean(body, TOTP_REQUIRED_KEYS)) {
                    return true;
                }
            } catch (Exception ignored) {
            }
        }
        return false;
    }

    private static boolean hasTrueBoolean(String json, String... keys) {
        if (json == null || json.trim().isEmpty()) {
            return false;
        }
        for (String key : keys) {
            String quotedKey = Pattern.quote(key);
            Pattern trueBoolean = Pattern.compile(
                    "\"(?i:" + quotedKey + ")\"\\s*:\\s*(?:true|1|\"true\"|\"1\"|\"enabled\")",
                    Pattern.CASE_INSENSITIVE
            );
            if (trueBoolean.matcher(json).find()) {
                return true;
            }
        }
        String lower = json.toLowerCase(Locale.ROOT);
        return lower.contains("multi-factor-auth-required")
                || lower.contains("mfa_required")
                || lower.contains("totp_required")
                || lower.contains("two_factor_required");
    }

    private static String readResponseBody(HttpURLConnection conn, int responseCode) throws IOException {
        InputStream in = responseCode >= 200 && responseCode < 400
                ? conn.getInputStream()
                : conn.getErrorStream();
        if (in == null) {
            return "";
        }
        try (InputStream stream = in) {
            return new String(stream.readAllBytes());
        }
    }

    private static void saveSessionCookie(HttpURLConnection conn) {
        Map<String, List<String>> headers = conn.getHeaderFields();
        List<String> cookies = headers.get("Set-Cookie");
        if (cookies != null && !cookies.isEmpty()) {
            String rawCookie = cookies.get(0);
            SESSION_COOKIE = rawCookie.split(";")[0];
            System.out.println("Saved Session Cookie: " + SESSION_COOKIE);
        }
    }

    private static String extractJsonString(String json, String key) {
        if (json == null || json.isEmpty() || key == null || key.isEmpty()) {
            return "";
        }
        Pattern pattern = Pattern.compile(
                "\"" + Pattern.quote(key) + "\"\\s*:\\s*\"([^\"]*)\"",
                Pattern.CASE_INSENSITIVE
        );
        java.util.regex.Matcher matcher = pattern.matcher(json);
        return matcher.find() ? matcher.group(1) : "";
    }

    private static String jsonEscape(String value) {
        if (value == null) {
            return "";
        }
        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    

  

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
     public static void main(String[] args) {
        new LoginFrame();
    }
}

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables

