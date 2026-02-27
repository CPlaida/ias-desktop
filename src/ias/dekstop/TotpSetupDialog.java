package ias.dekstop;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Setup 2FA dialog matching the web app: QR code to scan, manual secret, 6-digit confirm.
 */
public class TotpSetupDialog extends JDialog {

    private JTextField codeField;
    private String secret;
    private boolean confirmed;
    private final String userEmail;
    private final DashboardFrame dashboardFrame;

    // Match web app colors (light grey content, green buttons)
    private static final Color BG_COLOR = new Color(248, 248, 248);
    private static final Color TEXT_COLOR = new Color(51, 51, 51);
    private static final Color BUTTON_GREEN = new Color(76, 175, 80);
    private static final Color FIELD_BORDER = new Color(204, 204, 204);

    public TotpSetupDialog(Frame parent, String userEmail, DashboardFrame dashboardFrame) {
        super(parent, "Setup Two-Factor Authentication", true);
        this.userEmail = userEmail;
        this.dashboardFrame = dashboardFrame;
        setSize(520, 620);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(BG_COLOR);

        secret = fetchSetupFromBackend();
        if (secret == null) {
            secret = TOTP.generateSecret();
        }

        String otpauth = TOTP.getOtpAuthUri(secret, userEmail != null && !userEmail.isEmpty() && !userEmail.equals("—") ? userEmail : "user", "IAS");

        setLayout(new BorderLayout(0, 0));

        // ----- Header: title left, "Back to Dashboard" right -----
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BG_COLOR);
        header.setBorder(new EmptyBorder(20, 24, 16, 24));

        JLabel titleLabel = new JLabel("Setup Two-Factor Authentication");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setForeground(TEXT_COLOR);

        JButton backBtn = new JButton("Back to Dashboard");
        backBtn.setFont(new Font("Arial", Font.PLAIN, 14));
        backBtn.setBackground(BUTTON_GREEN);
        backBtn.setForeground(Color.WHITE);
        backBtn.setFocusPainted(false);
        backBtn.setBorderPainted(false);
        backBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backBtn.addActionListener(e -> dispose());

        header.add(titleLabel, BorderLayout.WEST);
        header.add(backBtn, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // ----- Main content: instructions, QR, secret, code, confirm -----
        JPanel main = new JPanel();
        main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));
        main.setBackground(BG_COLOR);
        main.setBorder(new EmptyBorder(0, 24, 24, 24));

        JLabel instructions = new JLabel("<html><center>"
                + "1) Scan the QR code using Google Authenticator (or any TOTP app).<br/>"
                + "2) Your authenticator will then SHOW a 6-digit code (you don't type it there).<br/>"
                + "3) Type that 6-digit code below to confirm."
                + "</center></html>");
        instructions.setFont(new Font("Arial", Font.PLAIN, 14));
        instructions.setForeground(TEXT_COLOR);
        instructions.setAlignmentX(Component.CENTER_ALIGNMENT);
        instructions.setBorder(new EmptyBorder(0, 0, 20, 0));
        main.add(instructions);

        // QR code (pure Java, no external JARs)
        BufferedImage qrImage = QRCodeHelper.generateQR(otpauth);
        JLabel qrLabel = new JLabel(qrImage != null ? new ImageIcon(qrImage) : null);
        qrLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        qrLabel.setBorder(new EmptyBorder(0, 0, 16, 0));
        main.add(qrLabel);

        JLabel manualLabel = new JLabel("Or enter this secret manually: " + secret);
        manualLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        manualLabel.setForeground(TEXT_COLOR);
        manualLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        manualLabel.setBorder(new EmptyBorder(0, 0, 20, 0));
        main.add(manualLabel);

        JLabel codePrompt = new JLabel("Enter 6-digit code from app:");
        codePrompt.setFont(new Font("Arial", Font.PLAIN, 13));
        codePrompt.setForeground(TEXT_COLOR);
        codePrompt.setAlignmentX(Component.CENTER_ALIGNMENT);
        codePrompt.setBorder(new EmptyBorder(0, 0, 8, 0));
        main.add(codePrompt);

        codeField = new JTextField(10);
        codeField.setFont(new Font("Arial", Font.PLAIN, 20));
        codeField.setHorizontalAlignment(JTextField.CENTER);
        codeField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(FIELD_BORDER, 1),
                BorderFactory.createEmptyBorder(10, 14, 10, 14)
        ));
        codeField.setPreferredSize(new Dimension(180, 44));
        codeField.setMaximumSize(new Dimension(180, 44));
        codeField.setAlignmentX(Component.CENTER_ALIGNMENT);
        codeField.addActionListener(e -> doConfirm());
        main.add(codeField);

        main.add(Box.createVerticalStrut(24));

        JButton confirmBtn = new JButton("Confirm & Enable 2FA");
        confirmBtn.setFont(new Font("Arial", Font.PLAIN, 14));
        confirmBtn.setBackground(BUTTON_GREEN);
        confirmBtn.setForeground(Color.WHITE);
        confirmBtn.setFocusPainted(false);
        confirmBtn.setBorderPainted(false);
        confirmBtn.setPreferredSize(new Dimension(220, 40));
        confirmBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        confirmBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        confirmBtn.addActionListener(e -> doConfirm());

        JPanel btnWrap = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnWrap.setBackground(BG_COLOR);
        btnWrap.add(confirmBtn);
        main.add(btnWrap);

        add(main, BorderLayout.CENTER);

        // Make sure the user immediately sees where to type.
        SwingUtilities.invokeLater(() -> codeField.requestFocusInWindow());
    }

    private String fetchSetupFromBackend() {
        try {
            URL url = new URL("http://localhost:8080/api/totp/setup");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            if (LoginFrame.SESSION_COOKIE != null) {
                conn.setRequestProperty("Cookie", LoginFrame.SESSION_COOKIE);
            }
            int code = conn.getResponseCode();
            if (code != 200) return null;
            String body;
            try (InputStream in = conn.getInputStream()) {
                body = new String(in.readAllBytes());
            }
            if (body.contains("\"secret\"")) {
                String s = body.replaceAll(".*\"secret\"\\s*:\\s*\"([^\"]+)\".*", "$1");
                if (!s.equals(body)) return s;
            }
        } catch (Exception e) {
            // ignore
        }
        return null;
    }

    private void doConfirm() {
        String code = codeField.getText();
        if (code == null) code = "";
        code = code.replaceAll("\\s", "");
        if (code.length() != 6 || !code.matches("\\d{6}")) {
            JOptionPane.showMessageDialog(this, "Please enter a valid 6-digit code.");
            return;
        }
        boolean ok = confirmTotpWithBackend(code);
        if (!ok) {
            ok = TOTP.verify(secret, code);
        }
        if (ok) {
            // Mark 2FA as enabled locally for this email
            if (userEmail != null && !userEmail.trim().isEmpty() && !userEmail.equals("—")) {
                TwoFactorStore.saveSecretForEmail(userEmail, secret);
                if (dashboardFrame != null) {
                    SwingUtilities.invokeLater(dashboardFrame::hideTwoFactorLink);
                }
            }
            confirmed = true;
            JOptionPane.showMessageDialog(this, "Two-Factor Authentication has been enabled.");
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Invalid code. Please try again.");
        }
    }

    private boolean confirmTotpWithBackend(String code) {
        try {
            URL url = new URL("http://localhost:8080/api/totp/confirm");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            if (LoginFrame.SESSION_COOKIE != null) {
                conn.setRequestProperty("Cookie", LoginFrame.SESSION_COOKIE);
            }
            conn.setDoOutput(true);
            String json = "{\"code\":\"" + code + "\"}";
            try (OutputStream os = conn.getOutputStream()) {
                os.write(json.getBytes());
            }
            return conn.getResponseCode() == 200;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isConfirmed() {
        return confirmed;
    }
}
