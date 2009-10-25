package org.mifos.framework.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PushbackInputStream;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

/**
 * Original code from <a href=
 * "http://tripoverit.blogspot.com/2007/04/javas-utf-8-and-unicode-writing-is.html"
 * >Trip over IT</a>. No license specified.
 * <p>
 * This implementation is much less than ideal (see WARNING in
 * {@link UnicodeInputStream#UnicodeInputStream(InputStream, String)}), but it's
 * the best we've got for now.
 */
public class UnicodeUtil {

    /**
     * Convert data to given character set.
     * 
     * @return decoded bytes. Includes BOM unless "ASCII" is used for
     *         desiredOutputEncoding.
     */
    public static byte[] convert(final byte[] bytes, final String desiredOutputEncoding) throws IOException {
        // Workaround for bug that will not be fixed by SUN
        // http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4508058
        UnicodeInputStream uis = new UnicodeInputStream(new ByteArrayInputStream(bytes), "ASCII");
        boolean unicodeOutputReqd = (getBOM(desiredOutputEncoding).equals("")) ? false : true;
        String enc = uis.getEncoding();
        String BOM = getBOM(enc); // get the BOM of the inputstream

        if ("".equals(BOM)) {
            // inputstream looks like ascii...
            // create a BOM based on the outputstream
            BOM = getBOM(desiredOutputEncoding);
        }
        uis.close();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(bytes,
                uis.getBomOffset(), bytes.length), enc));
        Writer w = new BufferedWriter(new OutputStreamWriter(out, desiredOutputEncoding));

        // dont write a BOM for ascii(out) as the OutputStreamWriter
        // will not process it correctly.
        if (!"".equals(BOM) && unicodeOutputReqd) {
            w.write(BOM);
        }

        char[] buffer = new char[4096];
        int len;
        while (true) {
            len = br.read(buffer);
            if (len == -1) {
                break;
            }
            w.write(buffer, 0, len);
        }

        br.close(); // Close the input.
        w.close(); // Flush and close output.
        return out.toByteArray();
    }

    public static String getBOM(final String enc) throws UnsupportedEncodingException {
        String result;
        if ("US-ASCII".equals(enc)) {
            // no bom required for ASCII
            result = "";
        } else if ("UTF-8".equals(enc)) {
            byte[] bom = new byte[3];
            bom[0] = (byte) 0xEF;
            bom[1] = (byte) 0xBB;
            bom[2] = (byte) 0xBF;
            result = new String(bom, enc);
        } else if ("UTF-16BE".equals(enc)) {
            byte[] bom = new byte[2];
            bom[0] = (byte) 0xFE;
            bom[1] = (byte) 0xFF;
            result = new String(bom, enc);
        } else if ("UTF-16LE".equals(enc)) {
            byte[] bom = new byte[2];
            bom[0] = (byte) 0xFF;
            bom[1] = (byte) 0xFE;
            result = new String(bom, enc);
        } else if ("UTF-32BE".equals(enc)) {
            byte[] bom = new byte[4];
            bom[0] = (byte) 0x00;
            bom[1] = (byte) 0x00;
            bom[2] = (byte) 0xFE;
            bom[3] = (byte) 0xFF;
            result = new String(bom, enc);
        } else if ("UTF-32LE".equals(enc)) {
            byte[] bom = new byte[4];
            bom[0] = (byte) 0x00;
            bom[1] = (byte) 0x00;
            bom[2] = (byte) 0xFF;
            bom[3] = (byte) 0xFE;
            result = new String(bom, enc);
        } else {
            throw new UnsupportedEncodingException("unknown encoding: " + enc);
        }
        return result;
    }

    /* justification: lazy. No good reason, just don't want to rewrite this. */
    @SuppressWarnings("PMD.CyclomaticComplexity")
    public static class UnicodeInputStream extends InputStream {
        private final PushbackInputStream internalIn;

        private boolean isInited = false;

        private int bomOffset = -1;

        private final String fallbackEncoding;

        private String encoding;

        public static final int BOM_SIZE = 4;

        /**
         * WARNING: {@link #read()} behaves differently after {@link #init()} is
         * called!
         * 
         * @param fallbackEncoding
         *            This encoding will be used if encoding cannot be detected
         *            from byte-order mark.
         */
        public UnicodeInputStream(final InputStream in, final String fallbackEncoding) {
            super();
            internalIn = new PushbackInputStream(in, BOM_SIZE);
            this.fallbackEncoding = fallbackEncoding;
        }

        public String getFallbackEncoding() {
            return fallbackEncoding;
        }

        public String getEncoding() {
            if (!isInited) {
                try {
                    init();
                } catch (IOException ex) {
                    throw new IllegalStateException("Init method failed.", ex);
                }
            }
            return encoding;
        }

        /**
         * Read-ahead four bytes and check for BOM marks. Extra bytes are unread
         * back to the stream, only BOM bytes are skipped.
         */
        protected void init() throws IOException {
            if (isInited) {
                return;
            }

            byte bom[] = new byte[BOM_SIZE];
            int n, unread;
            n = internalIn.read(bom, 0, bom.length);

            if ((bom[0] == (byte) 0x00) && (bom[1] == (byte) 0x00) && (bom[2] == (byte) 0xFE)
                    && (bom[3] == (byte) 0xFF)) {
                encoding = "UTF-32BE";
                unread = n - 4;
            } else if ((bom[0] == (byte) 0xFF) && (bom[1] == (byte) 0xFE) && (bom[2] == (byte) 0x00)
                    && (bom[3] == (byte) 0x00)) {
                encoding = "UTF-32LE";
                unread = n - 4;
            } else if ((bom[0] == (byte) 0xEF) && (bom[1] == (byte) 0xBB) && (bom[2] == (byte) 0xBF)) {
                encoding = "UTF-8";
                unread = n - 3;
            } else if ((bom[0] == (byte) 0xFE) && (bom[1] == (byte) 0xFF)) {
                encoding = "UTF-16BE";
                unread = n - 2;
            } else if ((bom[0] == (byte) 0xFF) && (bom[1] == (byte) 0xFE)) {
                encoding = "UTF-16LE";
                unread = n - 2;
            } else {
                // Unicode BOM mark not found, unread all bytes
                encoding = fallbackEncoding;
                unread = n;
            }
            bomOffset = BOM_SIZE - unread;
            if (unread > 0) {
                internalIn.unread(bom, (n - unread), unread);
            }

            isInited = true;
        }

        @Override
        public void close() throws IOException {
            init();
            isInited = true;
            internalIn.close();
        }

        @Override
        public int read() throws IOException {
            init();
            isInited = true;
            return internalIn.read();
        }

        public int getBomOffset() {
            return bomOffset;
        }
    }

    public static BufferedReader getUnicodeAwareBufferedReader(String file) throws IOException {
        return getUnicodeAwareBufferedReader(new FileInputStream(file));
    }

    public static BufferedReader getUnicodeAwareBufferedReader(InputStream stream) throws IOException {
        UnicodeInputStream in = new UnicodeInputStream(stream, System.getProperty("file.encoding"));
        return new BufferedReader(new InputStreamReader(in, in.getEncoding()));
    }
}
