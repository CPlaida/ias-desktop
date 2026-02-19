package ias.dekstop;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class RegisterFrame extends JFrame {

    private JTextField emailField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JLabel loginLink;
    
    // Placeholder text
    private String emailPlaceholder = "Email";
    private String passwordPlaceholder = "Password";
    private String confirmPasswordPlaceholder = "Confirm Password";

    private static final String FIREBASE_API_KEY = "AIzaSyDFQpn2ADkTg0F5t4uo4cwkyRDg51fkUn4";
    
    
    private static final Color BG_COLOR = new Color(245, 245, 245); // light gray background
    private static final Color TEXT_COLOR = new Color(33, 33, 33); // bold black title
    private static final Color BUTTON_GREEN = new Color(92, 184, 92); // #5cb85c vibrant green
    private static final Color LINK_COLOR = new Color(102, 51, 153); // purple link like web
    private static final Color FIELD_BORDER = new Color(206, 212, 218); // subtle gray border
    private static final Color FIELD_BG = Color.WHITE;
    private static final Color PLACEHOLDER_COLOR = new Color(158, 158, 158); // gray placeholder
    
    private static final int FIELD_WIDTH = 280;
    private static final int FIELD_HEIGHT = 40;
    
    public RegisterFrame() {
        setTitle("IAS Firebase Register");
        setSize(420, 520);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(BG_COLOR);
        
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(BG_COLOR);
        mainPanel.setBorder(new EmptyBorder(48, 48, 48, 48));
        
        // Title - bold black "Register"
        JLabel titleLabel = new JLabel("Register");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 26));
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        titleLabel.setBorder(new EmptyBorder(0, 0, 32, 0));
        
        // Email - placeholder only, no label
        emailField = createPlaceholderTextField(emailPlaceholder);
        emailField.setPreferredSize(new Dimension(FIELD_WIDTH, FIELD_HEIGHT));
        emailField.setMaximumSize(new Dimension(FIELD_WIDTH, FIELD_HEIGHT));
        emailField.setAlignmentX(Component.LEFT_ALIGNMENT);
        emailField.setBackground(FIELD_BG);
        
        // Password - placeholder only
        passwordField = createPlaceholderPasswordField(passwordPlaceholder);
        passwordField.setPreferredSize(new Dimension(FIELD_WIDTH, FIELD_HEIGHT));
        passwordField.setMaximumSize(new Dimension(FIELD_WIDTH, FIELD_HEIGHT));
        passwordField.setAlignmentX(Component.LEFT_ALIGNMENT);
        passwordField.setBackground(FIELD_BG);
        
        // Confirm Password - placeholder only
        confirmPasswordField = createPlaceholderPasswordField(confirmPasswordPlaceholder);
        confirmPasswordField.setPreferredSize(new Dimension(FIELD_WIDTH, FIELD_HEIGHT));
        confirmPasswordField.setMaximumSize(new Dimension(FIELD_WIDTH, FIELD_HEIGHT));
        confirmPasswordField.setAlignmentX(Component.LEFT_ALIGNMENT);
        confirmPasswordField.setBackground(FIELD_BG);
        
        // Register button - full width, green
        JButton registerButton = new JButton("Register");
        registerButton.setFont(new Font("Arial", Font.PLAIN, 14));
        registerButton.setBackground(BUTTON_GREEN);
        registerButton.setForeground(Color.WHITE);
        registerButton.setFocusPainted(false);
        registerButton.setBorderPainted(false);
        registerButton.setPreferredSize(new Dimension(FIELD_WIDTH, FIELD_HEIGHT));
        registerButton.setMaximumSize(new Dimension(FIELD_WIDTH, FIELD_HEIGHT));
        registerButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        registerButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        registerButton.setOpaque(true);
        registerButton.addActionListener(e -> registerUser());
        mainPanel.add(registerButton);
        
        // "Already have an account? Login here" - link in purple
        JPanel linkPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        linkPanel.setBackground(BG_COLOR);
        linkPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        linkPanel.setBorder(new EmptyBorder(24, 0, 0, 0));
        
        JLabel haveAccountLabel = new JLabel("Already have an account? ");
        haveAccountLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        haveAccountLabel.setForeground(TEXT_COLOR);
        
        loginLink = new JLabel("Login here");
        loginLink.setFont(new Font("Arial", Font.PLAIN, 14));
        loginLink.setForeground(LINK_COLOR);
        loginLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginLink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                goBackToLogin();
            }
            @Override
            public void mouseEntered(MouseEvent e) {
                loginLink.setText("<html><u>Login here</u></html>");
            }
            @Override
            public void mouseExited(MouseEvent e) {
                loginLink.setText("Login here");
            }
        });
        
        linkPanel.add(haveAccountLabel);
        linkPanel.add(loginLink);
        
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createVerticalStrut(8));
        mainPanel.add(emailField);
        mainPanel.add(Box.createVerticalStrut(16));
        mainPanel.add(passwordField);
        mainPanel.add(Box.createVerticalStrut(16));
        mainPanel.add(confirmPasswordField);
        mainPanel.add(Box.createVerticalStrut(24));
        mainPanel.add(registerButton);
        mainPanel.add(linkPanel);
        
        setLayout(new BorderLayout());
        JPanel centerWrapper = new JPanel();
        centerWrapper.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
        centerWrapper.setBackground(BG_COLOR);
        centerWrapper.add(mainPanel);
        add(centerWrapper, BorderLayout.CENTER);

        setVisible(true);
    }
    
    private JTextField createPlaceholderTextField(String placeholder) {
        JTextField field = new JTextField(placeholder);
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setForeground(PLACEHOLDER_COLOR);
        field.setBackground(FIELD_BG);
        field.setOpaque(true);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(FIELD_BORDER, 1),
            BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));
        
        field.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(TEXT_COLOR);
                }
            }
            
            @Override
            public void focusLost(FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setText(placeholder);
                    field.setForeground(PLACEHOLDER_COLOR);
                }
            }
        });
        
        return field;
    }
    
    private JPasswordField createPlaceholderPasswordField(String placeholder) {
        JPasswordField field = new JPasswordField();
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setForeground(PLACEHOLDER_COLOR);
        field.setBackground(FIELD_BG);
        field.setOpaque(true);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(FIELD_BORDER, 1),
            BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));
        
        // Set initial placeholder text
        field.setEchoChar((char) 0);
        field.setText(placeholder);
        
        field.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (new String(field.getPassword()).equals(placeholder)) {
                    field.setText("");
                    field.setForeground(TEXT_COLOR);
                    field.setEchoChar('•');
                }
            }
            
            @Override
            public void focusLost(FocusEvent e) {
                if (field.getPassword().length == 0) {
                    field.setEchoChar((char) 0);
                    field.setText(placeholder);
                    field.setForeground(PLACEHOLDER_COLOR);
                }
            }
        });
        
        return field;
    }
     
     private void registerUser() {

        try {
            String email = emailField.getText().trim();
            String password = new String(passwordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());
            
            // Handle placeholder text
            if (email.equals(emailPlaceholder)) email = "";
            if (password.equals(passwordPlaceholder)) password = "";
            if (confirmPassword.equals(confirmPasswordPlaceholder)) confirmPassword = "";

            if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields are required!");
                clearForm();
                return;
            }

            if (!password.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(this, "Passwords do not match!");
                clearForm();
                return;
            }

            String firebaseUrl =
                    "https://identitytoolkit.googleapis.com/v1/accounts:signUp?key="
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

            BufferedReader br = new BufferedReader(new InputStreamReader(stream));
            StringBuilder response = new StringBuilder();
            String line;

            while ((line = br.readLine()) != null) {
                response.append(line);
            }

            br.close();

            if (responseCode == 200) {
                JOptionPane.showMessageDialog(this, "Registration Successful!");
                clearForm();
                new LoginFrame();
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Registration Failed:\n" + response.toString());
                clearForm();
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            clearForm();
        }
    }

    /** Clears all fields and resets placeholders (same behavior as login form). */
    private void clearForm() {
        emailField.setText(emailPlaceholder);
        emailField.setForeground(PLACEHOLDER_COLOR);
        passwordField.setEchoChar((char) 0);
        passwordField.setText(passwordPlaceholder);
        passwordField.setForeground(PLACEHOLDER_COLOR);
        confirmPasswordField.setEchoChar((char) 0);
        confirmPasswordField.setText(confirmPasswordPlaceholder);
        confirmPasswordField.setForeground(PLACEHOLDER_COLOR);
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

    private void goBackToLogin() {
        new LoginFrame();
        dispose();
    }

    /**
     * @param args the command line arguments
     */
   
     
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
