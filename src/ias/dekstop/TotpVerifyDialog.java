package ias.dekstop;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Dialog for user to enter 6-digit TOTP code during login.
 */
public class TotpVerifyDialog extends JDialog {

    private JTextField codeField;
    private JLabel messageLabel;
    private String enteredCode;
    private boolean verified;

    private static final Color BG_COLOR = new Color(245, 245, 245);
    private static final Color TEXT_COLOR = new Color(51, 51, 51);
    private static final Color BUTTON_GREEN = new Color(109, 179, 63);
    private static final Color FIELD_BORDER = new Color(204, 204, 204);

    public TotpVerifyDialog(Frame parent) {
        super(parent, "Two-Factor Authentication", true);
        setSize(380, 220);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(BG_COLOR);

        JPanel main = new JPanel();
        main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));
        main.setBackground(BG_COLOR);
        main.setBorder(new EmptyBorder(24, 32, 24, 32));

        messageLabel = new JLabel("Enter the 6-digit code from your authenticator app.");
        messageLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        messageLabel.setForeground(TEXT_COLOR);
        messageLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        messageLabel.setBorder(new EmptyBorder(0, 0, 16, 0));

        JLabel codeLabel = new JLabel("Code:");
        codeLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        codeLabel.setForeground(TEXT_COLOR);
        codeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        codeLabel.setBorder(new EmptyBorder(0, 0, 6, 0));

        codeField = new JTextField(8);
        codeField.setFont(new Font("Arial", Font.PLAIN, 18));
        codeField.setHorizontalAlignment(JTextField.CENTER);
        codeField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(FIELD_BORDER, 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        codeField.setPreferredSize(new Dimension(160, 40));
        codeField.setMaximumSize(new Dimension(160, 40));
        codeField.setAlignmentX(Component.LEFT_ALIGNMENT);
        codeField.addActionListener(e -> doVerify());

        JButton verifyBtn = new JButton("Verify");
        verifyBtn.setFont(new Font("Arial", Font.PLAIN, 14));
        verifyBtn.setBackground(BUTTON_GREEN);
        verifyBtn.setForeground(Color.WHITE);
        verifyBtn.setFocusPainted(false);
        verifyBtn.setBorderPainted(false);
        verifyBtn.setPreferredSize(new Dimension(120, 36));
        verifyBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        verifyBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        verifyBtn.addActionListener(e -> doVerify());

        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.setFont(new Font("Arial", Font.PLAIN, 13));
        cancelBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        cancelBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        cancelBtn.addActionListener(e -> {
            enteredCode = null;
            verified = false;
            dispose();
        });

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        btnPanel.setBackground(BG_COLOR);
        btnPanel.add(verifyBtn);
        btnPanel.add(cancelBtn);

        main.add(messageLabel);
        main.add(codeLabel);
        main.add(codeField);
        main.add(Box.createVerticalStrut(12));
        main.add(btnPanel);

        setLayout(new BorderLayout());
        add(main, BorderLayout.CENTER);
    }

    private void doVerify() {
        String code = codeField.getText();
        if (code == null) code = "";
        code = code.replaceAll("\\s", "");
        if (code.length() != 6) {
            messageLabel.setText("Please enter exactly 6 digits.");
            messageLabel.setForeground(Color.RED);
            return;
        }
        if (!code.matches("\\d{6}")) {
            messageLabel.setText("Code must contain only numbers.");
            messageLabel.setForeground(Color.RED);
            return;
        }
        enteredCode = code;
        verified = true;
        dispose();
    }

    /** Call after dialog is closed. Returns the entered 6-digit code, or null if cancelled. */
    public String getEnteredCode() {
        return enteredCode;
    }

    public boolean isVerified() {
        return verified;
    }
}
