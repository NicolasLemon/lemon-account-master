package com.ruoyi.common.utils;


import com.ruoyi.common.utils.sign.Base64;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

/**
 * AES256加解密 工具类
 *
 * @author https://www.cnblogs.com/xxoome/p/13927481.html
 * @author Nicolas·Lemon
 * @since 2023/04/11
 */
@Slf4j
@SuppressWarnings("unused")
public class KeyUtils {

    private static final String AES = "AES";

    /**
     * 默认密钥（32个字节）
     */
    private static final String DEFAULT_KEY = "TRBuX0XCDpcdCYaGnzjY8M#4NA!O2e7r";

    /**
     * 默认偏移量（16个字节）
     */
    private static final String DEFAULT_KEY_IV = "DpcdCYaGnz!jYO#e";

    /**
     * 加密解密算法/加密模式/填充方式
     */
    private static final String CIPHER_ALGORITHM = "AES/CBC/PKCS5Padding";

    static {
        java.security.Security.setProperty("crypto.policy", "unlimited");
    }


    /**
     * AES256加密（默认密钥和偏移量）
     *
     * @param content 加密内容
     */
    public static String aes256Encode(String content) {
        return aes256Encode(null, null, content);
    }

    /**
     * AES256解密（默认密钥和偏移量）
     *
     * @param content 解密内容
     */
    public static String aes256Decode(String content) {
        return aes256Decode(null, null, content);
    }

    /**
     * AES256加密（自定义密钥和偏移量）
     *
     * @param key     密钥
     * @param keyIv   偏移量
     * @param content 加密内容
     */
    public static String aes256Encode(String key, String keyIv, String content) {
        // 不处理空字符串
        if (StringUtils.isEmpty(content)) {
            return content;
        }
        // 若key和keyIv是空的话，就使用默认的key和keyIv
        key = StringUtils.isEmpty(key) ? DEFAULT_KEY : key;
        keyIv = StringUtils.isEmpty(keyIv) ? DEFAULT_KEY_IV : keyIv;

        try {
            SecretKey secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), AES);
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(keyIv.getBytes(StandardCharsets.UTF_8)));
            // 获取加密内容的字节数组(这里要设置为utf-8)不然内容中如果有中文和英文混合中文就会解密为乱码
            byte[] byteEncode = content.getBytes(StandardCharsets.UTF_8);
            // 根据密码器的初始化方式加密
            byte[] byteAes = cipher.doFinal(byteEncode);
            // 将密文转换为Base64字符串
            return Base64.encode(byteAes);
        } catch (Exception e) {
            log.error("AES加密出错啦，返回原明文");
            e.printStackTrace();
        }

        return content;
    }

    /**
     * AES256解密（自定义密钥和偏移量）
     *
     * @param key     密钥
     * @param keyIv   偏移量
     * @param content 解密内容
     */
    public static String aes256Decode(String key, String keyIv, String content) {
        // 不处理空字符串
        if (StringUtils.isEmpty(content)) {
            return content;
        }
        // 若key和keyIv是空的话，就使用默认的key和keyIv
        key = StringUtils.isEmpty(key) ? DEFAULT_KEY : key;
        keyIv = StringUtils.isEmpty(keyIv) ? DEFAULT_KEY_IV : keyIv;

        try {
            SecretKey secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), AES);
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(keyIv.getBytes(StandardCharsets.UTF_8)));
            // 将编码成Base64后的密文解码成字节数组
            byte[] byteContent = Base64.decode(content);
            // 解密
            byte[] byteDecode = cipher.doFinal(byteContent);
            return new String(byteDecode, StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("AES解密出错啦，返回原密文");
            e.printStackTrace();
        }

        return content;
    }

    /**
     * 生成一个keyLength长度的随机key值
     *
     * @param keyLength key长度
     * @return 长度为keyLength的随机key值
     */
    public static String generateKey(int keyLength) {
        // 定义key库由小写字母、大写字母、数字、标点符号组成
        final String lowerCase = "abcdefghijklmnopqrstuvwxyz";
        final String upperCase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        final String numberCase = "0123456789";
        final String punctuationCase = "!@#$%^&*";
        final String allCodeString = lowerCase + upperCase + numberCase + punctuationCase;
        // 生成随机key值
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < keyLength; i++) {
            int randomIndex = (int) (Math.random() * allCodeString.length());
            code.append(allCodeString.charAt(randomIndex));
        }
        return code.toString();
    }

}
