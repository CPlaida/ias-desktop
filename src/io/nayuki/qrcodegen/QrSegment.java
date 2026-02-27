/*
 * QR Code generator library (Java)
 *
 * Copyright (c) Project Nayuki. (MIT License)
 * https://www.nayuki.io/page/qr-code-generator-library
 */

package io.nayuki.qrcodegen;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

public final class QrSegment {

    public static QrSegment makeBytes(byte[] data) {
        Objects.requireNonNull(data);
        BitBuffer bb = new BitBuffer();
        for (byte b : data)
            bb.appendBits(b & 0xFF, 8);
        return new QrSegment(Mode.BYTE, data.length, bb);
    }

    public static QrSegment makeNumeric(CharSequence digits) {
        Objects.requireNonNull(digits);
        if (!NUMERIC_REGEX.matcher(digits).matches())
            throw new IllegalArgumentException("String contains non-numeric characters");
        BitBuffer bb = new BitBuffer();
        for (int i = 0; i < digits.length(); ) {
            int n = Math.min(digits.length() - i, 3);
            bb.appendBits(Integer.parseInt(digits.subSequence(i, i + n).toString()), n * 3 + 1);
            i += n;
        }
        return new QrSegment(Mode.NUMERIC, digits.length(), bb);
    }

    public static QrSegment makeAlphanumeric(CharSequence text) {
        Objects.requireNonNull(text);
        if (!ALPHANUMERIC_REGEX.matcher(text).matches())
            throw new IllegalArgumentException("String contains unencodable characters");
        BitBuffer bb = new BitBuffer();
        int i;
        for (i = 0; i <= text.length() - 2; i += 2) {
            int temp = ALPHANUMERIC_CHARSET.indexOf(text.charAt(i)) * 45;
            temp += ALPHANUMERIC_CHARSET.indexOf(text.charAt(i + 1));
            bb.appendBits(temp, 11);
        }
        if (i < text.length())
            bb.appendBits(ALPHANUMERIC_CHARSET.indexOf(text.charAt(i)), 6);
        return new QrSegment(Mode.ALPHANUMERIC, text.length(), bb);
    }

    public static List<QrSegment> makeSegments(CharSequence text) {
        Objects.requireNonNull(text);
        List<QrSegment> result = new ArrayList<>();
        if (text.equals(""));
        else if (NUMERIC_REGEX.matcher(text).matches())
            result.add(makeNumeric(text));
        else if (ALPHANUMERIC_REGEX.matcher(text).matches())
            result.add(makeAlphanumeric(text));
        else
            result.add(makeBytes(text.toString().getBytes(StandardCharsets.UTF_8)));
        return result;
    }

    public final Mode mode;
    public final int numChars;
    final BitBuffer data;

    public QrSegment(Mode md, int numCh, BitBuffer data) {
        mode = Objects.requireNonNull(md);
        this.data = data.clone();
        if (numCh < 0) throw new IllegalArgumentException("Invalid value");
        numChars = numCh;
    }

    public BitBuffer getData() {
        return data.clone();
    }

    static int getTotalBits(List<QrSegment> segs, int version) {
        Objects.requireNonNull(segs);
        long result = 0;
        for (QrSegment seg : segs) {
            Objects.requireNonNull(seg);
            int ccbits = seg.mode.numCharCountBits(version);
            if (seg.numChars >= (1 << ccbits)) return -1;
            result += 4L + ccbits + seg.data.bitLength();
            if (result > Integer.MAX_VALUE) return -1;
        }
        return (int) result;
    }

    private static final Pattern NUMERIC_REGEX = Pattern.compile("[0-9]*");
    private static final Pattern ALPHANUMERIC_REGEX = Pattern.compile("[A-Z0-9 $%*+./:-]*");
    static final String ALPHANUMERIC_CHARSET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ $%*+-./:";

    public enum Mode {
        NUMERIC(0x1, 10, 12, 14),
        ALPHANUMERIC(0x2, 9, 11, 13),
        BYTE(0x4, 8, 16, 16),
        KANJI(0x8, 8, 10, 12),
        ECI(0x7, 0, 0, 0);

        final int modeBits;
        private final int[] numBitsCharCount;

        Mode(int mode, int... ccbits) {
            modeBits = mode;
            numBitsCharCount = ccbits;
        }

        int numCharCountBits(int ver) {
            return numBitsCharCount[(ver + 7) / 17];
        }
    }
}
