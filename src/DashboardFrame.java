package ias.dekstop;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class DashboardFrame extends JFrame {

    private JLabel userEmailLabel;
    private JButton logoutButton;

    // Colors matching web design
    private static final Color BG_COLOR = new Color(248, 248, 248); // #F8F8F8
    private static final Color TEXT_COLOR = new Color(51, 51, 51); // #333333
    private static final Color BUTTON_GREEN = new Color(76, 175, 80); // #4CAF50

    public DashboardFrame() {
        setTitle("IAS Dashboard");
        setSize(900, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Set background color
        getContentPane().setBackground(BG_COLOR);
        
        setLayout(new BorderLayout());
        
        // Create header
        JPanel headerPanel = createHeader();
        add(headerPanel, BorderLayout.NORTH);
        
        // Create main content
        JPanel mainPanel = createMainContent();
        add(mainPanel, BorderLayout.CENTER);

        setVisible(true);
        
        // Load user email
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
        
        welcomePanel.add(welcomeTitle);
        welcomePanel.add(welcomeSub);
        
        mainPanel.add(welcomePanel);
        
        return mainPanel;
    }
    
    private void loadUserEmail() {
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
                
                // Extract email from JSON response (simple extraction)
                String responseStr = response.toString();
                if (responseStr.contains("\"email\"")) {
                    String email = responseStr.replaceAll(".*\"email\"\\s*:\\s*\"([^\"]+)\".*", "$1");
                    userEmailLabel.setText(email);
                }
            }
        } catch (Exception e) {
            // If we can't get email, keep the default "—"
            System.out.println("Could not load user email: " + e.getMessage());
        }
    }


    private void logout() {
        LoginFrame.SESSION_COOKIE = null;
        new LoginFrame();
        dispose();
    }
}

