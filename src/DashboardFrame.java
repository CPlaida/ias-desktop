package ias.dekstop;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class DashboardFrame extends JFrame {

    private JLabel userEmailLabel;
    private JButton logoutButton;
    private JLabel enable2FALink;
    /** Email used at login - used for 2FA even if /api/user fails */
    private final String loginEmail;

    // Colors matching web design (like localhost:8080/dashboard)
    private static final Color BG_COLOR = new Color(248, 248, 248); // #F8F8F8
    private static final Color TEXT_COLOR = new Color(51, 51, 51); // #333333
    private static final Color BUTTON_GREEN = new Color(76, 175, 80); // #4CAF50
    private static final Color LINK_BLUE = new Color(0, 102, 204);   // blue link like web

    /** @param loginEmail email used to log in (so 2FA can be keyed correctly even without backend) */
    public DashboardFrame(String loginEmail) {
        this.loginEmail = loginEmail != null ? loginEmail.trim() : "";
        setTitle("IAS Dashboard");
        setSize(900, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        getContentPane().setBackground(BG_COLOR);
        setLayout(new BorderLayout());
        
        JPanel headerPanel = createHeader();
        add(headerPanel, BorderLayout.NORTH);
        JPanel mainPanel = createMainContent();
        add(mainPanel, BorderLayout.CENTER);

        // Show login email immediately so 2FA setup always has the right email
        if (!this.loginEmail.isEmpty()) {
            userEmailLabel.setText(this.loginEmail);
        }
        setVisible(true);
        loadUserEmail();
    }
    
    private JPanel createHeader() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(BG_COLOR);
        headerPanel.setBorder(new EmptyBorder(20, 30, 20, 30));
        
        // Left side - Brand
        JLabel titleLabel = new JLabel("IAS Dashboard");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(TEXT_COLOR);
        
        // Right side - User email and Sign out button
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        rightPanel.setBackground(BG_COLOR);
        
        userEmailLabel = new JLabel("—");
        userEmailLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        userEmailLabel.setForeground(TEXT_COLOR);
        
        logoutButton = new JButton("Sign out");
        logoutButton.setFont(new Font("Arial", Font.PLAIN, 14));
        logoutButton.setBackground(BUTTON_GREEN);
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFocusPainted(false);
        logoutButton.setBorderPainted(false);
        logoutButton.setPreferredSize(new Dimension(100, 35));
        logoutButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutButton.addActionListener(e -> logout());
        
        rightPanel.add(userEmailLabel);
        rightPanel.add(logoutButton);
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(rightPanel, BorderLayout.EAST);
        
        return headerPanel;
    }
    
    private JPanel createMainContent() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(BG_COLOR);
        mainPanel.setBorder(new EmptyBorder(40, 30, 40, 30));
        
        // Welcome section
        JPanel welcomePanel = new JPanel();
        welcomePanel.setLayout(new BoxLayout(welcomePanel, BoxLayout.Y_AXIS));
        welcomePanel.setBackground(BG_COLOR);
        welcomePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel welcomeTitle = new JLabel("Welcome to Dashboard!");
        welcomeTitle.setFont(new Font("Arial", Font.BOLD, 28));
        welcomeTitle.setForeground(TEXT_COLOR);
        welcomeTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        welcomeTitle.setBorder(new EmptyBorder(0, 0, 10, 0));
        
        JLabel welcomeSub = new JLabel("You're signed in. Here's your overview.");
        welcomeSub.setFont(new Font("Arial", Font.PLAIN, 16));
        welcomeSub.setForeground(TEXT_COLOR);
        welcomeSub.setAlignmentX(Component.LEFT_ALIGNMENT);
        welcomeSub.setBorder(new EmptyBorder(0, 0, 20, 0));

        enable2FALink = new JLabel("Enable Two-Factor Authentication (Google Authenticator)");
        enable2FALink.setFont(new Font("Arial", Font.PLAIN, 14));
        enable2FALink.setForeground(LINK_BLUE);
        enable2FALink.setCursor(new Cursor(Cursor.HAND_CURSOR));
        enable2FALink.setAlignmentX(Component.LEFT_ALIGNMENT);
        enable2FALink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                String email = userEmailLabel.getText();
                if (email == null || email.trim().isEmpty() || "—".equals(email.trim())) {
                    email = loginEmail;
                }
                TotpSetupDialog d = new TotpSetupDialog(DashboardFrame.this, email, DashboardFrame.this);
                d.setVisible(true);
            }
            @Override
            public void mouseEntered(MouseEvent e) {
                enable2FALink.setText("<html><u>Enable Two-Factor Authentication (Google Authenticator)</u></html>");
            }
            @Override
            public void mouseExited(MouseEvent e) {
                enable2FALink.setText("Enable Two-Factor Authentication (Google Authenticator)");
            }
        });
        
        welcomePanel.add(welcomeTitle);
        welcomePanel.add(welcomeSub);
        welcomePanel.add(enable2FALink);
        
        mainPanel.add(welcomePanel);
        
        return mainPanel;
    }

    /** Called after 2FA has been enabled for this user. */
    public void hideTwoFactorLink() {
        if (enable2FALink != null) {
            enable2FALink.setVisible(false);
        }
    }
    
    public void loadUserEmail() {
        // Try to get user email from backend
        try {
            URL url = new URL("http://localhost:8080/api/user");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            
            if (LoginFrame.SESSION_COOKIE != null) {
                conn.setRequestProperty("Cookie", LoginFrame.SESSION_COOKIE);
            }
            
            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line);
                }
                br.close();
                String responseStr = response.toString();
                if (responseStr.contains("\"email\"")) {
                    String email = responseStr.replaceAll(".*\"email\"\\s*:\\s*\"([^\"]+)\".*", "$1");
                    userEmailLabel.setText(email);
                }
            }
        } catch (Exception e) {
            System.out.println("Could not load user email: " + e.getMessage());
        }
        // Hide 2FA link if already enabled for this user (use label or login email)
        String emailToCheck = userEmailLabel.getText();
        if (emailToCheck == null || emailToCheck.trim().isEmpty() || "—".equals(emailToCheck.trim())) {
            emailToCheck = loginEmail;
        }
        if (emailToCheck != null && !emailToCheck.trim().isEmpty() && TwoFactorStore.isEnabledForEmail(emailToCheck) && enable2FALink != null) {
            enable2FALink.setVisible(false);
        }
    }

    private void logout() {
        LoginFrame.SESSION_COOKIE = null;
        new LoginFrame();
        dispose();
    }
}

