package ias.dekstop;

import java.io.*;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Simple local storage for per-email TOTP secrets and enabled flag.
 * This is client-side only and does not affect the backend.
 */
public final class TwoFactorStore {

    private static final String FILE_NAME =
            System.getProperty("user.home") + File.separator + ".ias-desktop-2fa";

    private static final Map<String, String> emailToSecret = new HashMap<>();
    private static boolean loaded = false;

    private TwoFactorStore() {}

    private static String normalizeEmail(String email) {
        if (email == null) return "";
        return email.trim().toLowerCase(Locale.ROOT);
    }

    private static synchronized void ensureLoaded() {
        if (loaded) return;
        loaded = true;
        File f = new File(FILE_NAME);
        if (!f.exists()) return;
        try (BufferedReader reader = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|", 2);
                if (parts.length == 2) {
                    String email = parts[0].trim();
                    String secret = parts[1].trim();
                    if (!email.isEmpty() && !secret.isEmpty()) {
                        emailToSecret.put(email, secret);
                    }
                }
            }
        } catch (IOException ignored) {
        }
    }

    private static synchronized void persist() {
        File f = new File(FILE_NAME);
        try (PrintWriter out = new PrintWriter(new FileWriter(f))) {
            for (Map.Entry<String, String> e : emailToSecret.entrySet()) {
                out.println(e.getKey() + "|" + e.getValue());
            }
        } catch (IOException ignored) {
        }
    }

    public static synchronized boolean isEnabledForEmail(String email) {
        ensureLoaded();
        return emailToSecret.containsKey(normalizeEmail(email));
    }

    public static synchronized String getSecretForEmail(String email) {
        ensureLoaded();
        return emailToSecret.get(normalizeEmail(email));
    }

    public static synchronized void saveSecretForEmail(String email, String secret) {
        if (email == null || email.trim().isEmpty() || secret == null || secret.trim().isEmpty()) {
            return;
        }
        ensureLoaded();
        emailToSecret.put(normalizeEmail(email), secret.trim());
        persist();
    }
}

