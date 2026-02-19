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
import java.util.Map;

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

            String idToken = firebaseLogin(email, password);

            boolean success = sendTokenToBackend(idToken);

            if (success) {
                new DashboardFrame();
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Backend Login Failed");
                clearForm();
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Login Failed: " + e.getMessage());
            clearForm();
        }
    }

    /** Clears email and password fields. */
    private void clearForm() {
        emailField.setText("");
        passwordField.setText("");
    }

    private String firebaseLogin(String email, String password) throws Exception {

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

        BufferedReader br = new BufferedReader(
                new InputStreamReader(conn.getInputStream()));

        StringBuilder response = new StringBuilder();
        String line;

        while ((line = br.readLine()) != null) {
            response.append(line);
        }

        br.close();

        String result = response.toString();
        System.out.println("Firebase Response: " + result);

        // Safe extraction
        String idToken = result.replaceAll(".*\"idToken\"\\s*:\\s*\"([^\"]+)\".*", "$1");

        return idToken;
    }

    private boolean sendTokenToBackend(String idToken) throws Exception {

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

        // 🔥 Extract ONLY JSESSIONID
        Map<String, List<String>> headers = conn.getHeaderFields();
        List<String> cookies = headers.get("Set-Cookie");

        if (cookies != null) {
            String rawCookie = cookies.get(0);
            SESSION_COOKIE = rawCookie.split(";")[0]; // IMPORTANT
            System.out.println("Saved Session Cookie: " + SESSION_COOKIE);
        }

        return responseCode == 200;
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

