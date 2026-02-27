package ias.dekstop;

import io.nayuki.qrcodegen.QrCode;

import java.awt.image.BufferedImage;

/**
 * Generates QR code image from a string (e.g. otpauth URI).
 * Uses Nayuki's pure-Java QR library (no external JARs).
 */
public final class QRCodeHelper {

    private static final int BORDER = 4;
    private static final int TARGET_SIZE_PX = 260;

    private QRCodeHelper() {}

    /**
     * Generate a QR code image for the given content (e.g. otpauth://...).
     */
    public static BufferedImage generateQR(String content) {
        if (content == null || content.isEmpty()) return null;
        try {
            QrCode qr = QrCode.encodeText(content, QrCode.Ecc.MEDIUM);
            int modules = qr.size + BORDER * 2;
            int scale = Math.max(1, TARGET_SIZE_PX / modules);
            int size = modules * scale;
            BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
            for (int y = 0; y < img.getHeight(); y++) {
                for (int x = 0; x < img.getWidth(); x++) {
                    boolean dark = qr.getModule(x / scale - BORDER, y / scale - BORDER);
                    img.setRGB(x, y, dark ? 0x000000 : 0xFFFFFF);
                }
            }
            return img;
        } catch (Exception e) {
            return null;
        }
    }
}
