package com.ljb.socket.android.utils;

import android.text.TextUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

/**
 * 运用md5 加密
 *
 * @version 2015-4-25 下午3:42:06 类说明
 */
public class EncodeMD5 {
    private static MessageDigest digest = null;

    public synchronized static String hash(String data) {
        if (digest == null) {
            try {
                digest = MessageDigest.getInstance("MD5");
            } catch (NoSuchAlgorithmException nsae) {
                System.err.println("Failed to load the MD5 MessageDigest. "
                        + "Jive will be unable to function normally.");
                nsae.printStackTrace();
            }
        }
        // Now, compute hash.
        digest.update(data.getBytes());
        return encodeHex(digest.digest());
    }

    public static String encodeHex(byte[] bytes) {
        StringBuffer buf = new StringBuffer(bytes.length * 2);
        int i;

        for (i = 0; i < bytes.length; i++) {
            if ((bytes[i] & 0xff) < 0x10) {
                buf.append("0");
            }
            buf.append(Long.toString(bytes[i] & 0xff, 16));
        }
        return buf.toString();
    }

    /**
     * @param md5Byte
     * @return md5加密byte
     */
    public static String hashByte(byte[] md5Byte) {
        if (digest == null) {
            try {
                digest = MessageDigest.getInstance("MD5");
            } catch (NoSuchAlgorithmException nsae) {
                System.err.println("Failed to load the MD5 MessageDigest. "
                        + "Jive will be unable to function normally.");
                nsae.printStackTrace();
            }
        }
        digest.update(md5Byte);
        return encodeHex(digest.digest());
    }

    public static String bigFileMD5(String inputFile) {
        // 缓冲区大小（这个可以抽出一个参数）
        int bufferSize = 256 * 1024;
        FileInputStream fileInputStream = null;
        DigestInputStream digestInputStream = null;
        try {
            // 拿到一个MD5转换器（同样，这里可以换成SHA1）
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            // 使用DigestInputStream
            fileInputStream = new FileInputStream(inputFile);
            digestInputStream = new DigestInputStream(fileInputStream, messageDigest);
            // read的过程中进行MD5处理，直到读完文件
            byte[] buffer = new byte[bufferSize];
            while (digestInputStream.read(buffer) > 0) ;
            // 获取最终的MessageDigest
            messageDigest = digestInputStream.getMessageDigest();
            // 拿到结果，也是字节数组，包含16个元素
            byte[] resultByteArray = messageDigest.digest();
            // 同样，把字节数组转换成字符串
            return byteArrayToHex(resultByteArray).toUpperCase(Locale.getDefault());
        } catch (IOException | NoSuchAlgorithmException e) {
            return null;
        } finally {
            try {
                if (null != digestInputStream) {
                    digestInputStream.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (null != fileInputStream) {
                    fileInputStream.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //下面这个函数用于将字节数组换成成16进制的字符串

    private static String byteArrayToHex(byte[] byteArray) {
        String hs = "";
        String stmp;

        int size = byteArray.length;

        for (int n = 0; n < size; n++) {
            stmp = (Integer.toHexString(byteArray[n] & 0XFF));
            if (stmp.length() == 1) {
                hs = hs + "0" + stmp;
            } else {
                hs = hs + stmp;
            }
            if (n < size - 1) {
                hs = hs + "";
            }
        }
        return hs;

    }


    private static String bytes2Hex(byte[] src) {
        char[] res = new char[src.length * 2];
        final char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        for (int i = 0, j = 0; i < src.length; i++) {
            res[j++] = hexDigits[src[i] >>> 4 & 0x0f];
            res[j++] = hexDigits[src[i] & 0x0f];
        }

        return new String(res);
    }

    private static String getMd5ByFile(File file) {
        String value = null;
        FileInputStream in = null;
        try {
            in = new FileInputStream(file);

            MessageDigest digester = MessageDigest.getInstance("MD5");
            byte[] bytes = new byte[8192];
            int byteCount;
            while ((byteCount = in.read(bytes)) > 0) {
                digester.update(bytes, 0, byteCount);
            }
            value = bytes2Hex(digester.digest());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != in) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return value;
    }

    /**
     * 判断文件的MD5是否为指定值
     *
     * @param file
     * @param md5
     * @return
     */
    public static boolean checkMd5(File file, String md5) {
        if (TextUtils.isEmpty(md5)) {
            throw new RuntimeException("md5 cannot be empty");
        }
        String fileMd5 = getMd5ByFile(file);

        return md5.equalsIgnoreCase(fileMd5);
    }

    /**
     * 判断文件的MD5是否为指定值
     *
     * @param filePath
     * @param md5
     * @return
     */
    public static boolean checkMd5(String filePath, String md5) {
        return checkMd5(new File(filePath), md5);
    }


    public final static String getMessageDigest(byte[] buffer) {
        char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
        try {
            MessageDigest mdTemp = MessageDigest.getInstance("MD5");
            mdTemp.update(buffer);
            byte[] md = mdTemp.digest();
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            return null;
        }
    }


}
