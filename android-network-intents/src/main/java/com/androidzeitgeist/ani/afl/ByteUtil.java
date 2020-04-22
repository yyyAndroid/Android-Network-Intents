package com.androidzeitgeist.ani.afl;

import android.text.TextUtils;

/**
 * 字节数据帮助类.
 *
 * @author RobinYang.
 * @since 2018-09-18 11:56.
 */
public class ByteUtil {

    /**
     * @param value integer type.
     * @return 从索引0开始，顺序存储从高位到低位的数据.
     */
    public static byte[] intToBytes(int value) {
        byte[] src = new byte[4];
        src[0] = (byte) ((value >> 24) & 0xFF);
        src[1] = (byte) ((value >> 16) & 0xFF);
        src[2] = (byte) ((value >> 8) & 0xFF);
        src[3] = (byte) (value & 0xFF);
        return src;
    }

    /**
     * @param value short type. value小于0未测试.
     * @return 从索引0开始，顺序存储从高位到低位的数据.
     */
    public static byte[] shortToBytes(short value) {
        byte[] src = new byte[2];
        src[0] = (byte) (value >> 8 & 0xFF);
        src[1] = (byte) (value & 0xFF);
        return src;
    }

    public static short bytesToShort(byte low, byte high) {
        return (short) (low & 0xff | (high & 0xff) << 8);
    }

    public static int bytesToInt(byte low, byte b2, byte b3, byte high) {
        return (low & 0xff | (b2 & 0xff) << 8 | (b3 & 0xff) << 16 | (high & 0xff) << 24);
    }

    public static String byte2hex(byte[] buffer) {
        String h = "";

        if (buffer != null) {
            for (byte aBuffer : buffer) {
                String temp = Integer.toHexString(aBuffer & 0xFF);
                if (temp.length() == 1) {
                    temp = "0" + temp;
                }

                if (TextUtils.isEmpty(h)) {
                    h = temp;
                } else {
                    h += " " + temp;
                }
            }
        }

        return h;
    }

    public static String bytes2HexString(byte[] buffer) {
        StringBuilder result = new StringBuilder();

        if (buffer != null) {
            for (byte aBuffer : buffer) {
                String temp = Integer.toHexString(aBuffer & 0xFF);
                if (temp.length() == 1) {
                    temp = "0" + temp;
                }
                result.append(temp);
            }
        }

        return result.toString();
    }

    public static String byte2ASCIIString(byte[] buffer) {
        return byte2ASCIIString(buffer, false);
    }

    public static String byte2ASCIIString(byte[] buffer, boolean reverse) {
        StringBuilder h = new StringBuilder();

        if (buffer != null) {
            if (reverse) {
                for (int i = buffer.length - 1; i >= 0; i--) {
                    h.append((char)buffer[i]);
                }
            } else {
                for (byte aBuffer : buffer) {
                    h.append((char)aBuffer);
                }
            }
        }

        return h.toString();
    }

    public static boolean startWith(byte[] src, byte[] startBytes) {
        if (src == null || startBytes == null
                || src.length == 0 || startBytes.length == 0
                || src.length < startBytes.length) {
            return false;
        }

        for (int i = 0; i < startBytes.length; i++) {
            if (startBytes[i] != src[i]) {
                return false;
            }
        }

        return true;
    }

    /**
     * 获取除去索引0的剩余字节数组.
     * @param src
     * @return
     */
    public static byte[] getBytesAfterStart(byte[] src) {
        byte[] skipStartBytes = new byte[src.length - 1];
        System.arraycopy(src, 1, skipStartBytes, 0, src.length - 1);
        return skipStartBytes;
    }

    private static String trimSpace(String str) {
        return str.replace(" ", "");
    }

    public static int getHeight4(byte data) {//获取高四位
        int height;
        height = ((data & 0xf0) >> 4);
        return height;
    }

    public static int getLow4(byte data) {//获取低四位
        int low;
        low = (data & 0x0f);
        return low;
    }
}
