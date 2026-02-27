package ias.dekstop;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

/**
 * TOTP (Time-based One-Time Password) - RFC 6238.
 * Pure Java, no external libraries. Uses 30-second step, 6-digit code.
 */
public final class TOTP {

    private static final int TIME_STEP_SECONDS = 30;
    private static final int CODE_DIGITS = 6;
    private static final int CLOCK_SKEW_STEPS = 1; // allow ±1 step (30 sec) for clock skew

    private TOTP() {}

    /**
     * Generate current 6-digit TOTP code for the given Base32 secret.
     *
     * @param secretBase32 Base32-encoded secret (e.g. from backend or QR)
     * @return 6-digit string, or null if invalid secret
     */
    public static String generateCode(String secretBase32) {
        byte[] secret = decodeBase32(secretBase32);
        if (secret == null || secret.length == 0) return null;
        long counter = System.currentTimeMillis() / 1000 / TIME_STEP_SECONDS;
        return generateCodeForCounter(secret, counter);
    }

    /**
     * Verify a 6-digit code against the secret. Allows ±1 time step for clock skew.
     *
     * @param secretBase32 Base32-encoded secret
     * @param code         6-digit code from user
     * @return true if code is valid
     */
    public static boolean verify(String secretBase32, String code) {
        if (secretBase32 == null || code == null || code.length() != CODE_DIGITS) return false;
        code = code.replaceAll("\\s", "");
        if (code.length() != CODE_DIGITS) return false;
        byte[] secret = decodeBase32(secretBase32);
        if (secret == null || secret.length == 0) return false;
        long counter = System.currentTimeMillis() / 1000 / TIME_STEP_SECONDS;
        for (long c = counter - CLOCK_SKEW_STEPS; c <= counter + CLOCK_SKEW_STEPS; c++) {
            String expected = generateCodeForCounter(secret, c);
            if (code.equals(expected)) return true;
        }
        return false;
    }

    private static String generateCodeForCounter(byte[] secret, long counter) {
        try {
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(new SecretKeySpec(secret, "HmacSHA1"));
            byte[] counterBytes = ByteBuffer.allocate(8).putLong(counter).array();
            byte[] hash = mac.doFinal(counterBytes);
            int offset = hash[hash.length - 1] & 0x0F;
            int binary = ((hash[offset] & 0x7F) << 24)
                    | ((hash[offset + 1] & 0xFF) << 16)
                    | ((hash[offset + 2] & 0xFF) << 8)
                    | (hash[offset + 3] & 0xFF);
            int otp = binary % (int) Math.pow(10, CODE_DIGITS);
            return String.format(Locale.US, "%0" + CODE_DIGITS + "d", otp);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            return null;
        }
    }

    /**
     * Decode Base32 (RFC 4648) - A-Z, 2-7. Case-insensitive, padding optional.
     */
    static byte[] decodeBase32(String base32) {
        if (base32 == null) return null;
        base32 = base32.replaceAll("\\s|-", "").toUpperCase(Locale.US);
        if (base32.isEmpty()) return null;
        base32 = base32.replace("=", "");
        int outLen = (base32.length() * 5 + 7) / 8;
        byte[] out = new byte[outLen];
        int buf = 0, bits = 0, outIdx = 0;
        for (int i = 0; i < base32.length(); i++) {
            int v = base32CharToValue(base32.charAt(i));
            if (v < 0) return null;
            buf = (buf << 5) | v;
            bits += 5;
            if (bits >= 8) {
                bits -= 8;
                out[outIdx++] = (byte) (buf >> bits);
            }
        }
        return out;
    }

    private static int base32CharToValue(char c) {
        if (c >= 'A' && c <= 'Z') return c - 'A';
        if (c >= '2' && c <= '7') return c - '2' + 26;
        return -1;
    }

    /**
     * Generate a random Base32 secret (e.g. for new 2FA setup). 20 chars = 100 bits.
     */
    public static String generateSecret() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567";
        StringBuilder sb = new StringBuilder(20);
        java.security.SecureRandom r = new java.security.SecureRandom();
        for (int i = 0; i < 20; i++) sb.append(chars.charAt(r.nextInt(chars.length())));
        return sb.toString();
    }

    /**
     * Build otpauth URI for QR code / manual entry (e.g. Google Authenticator).
     */
    public static String getOtpAuthUri(String secret, String accountName, String issuer) {
        return String.format("otpauth://totp/%s:%s?secret=%s&issuer=%s&algorithm=SHA1&digits=6&period=30",
                issuer != null ? issuer : "IAS",
                accountName != null ? accountName : "user",
                secret,
                issuer != null ? issuer : "IAS");
    }
}
