package com.example.iotLinkWandemo.signature;

/**
 * @author ipodao@qq.com
 * @date 2019/8/28 19:46
 */

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Map;
import java.util.TreeMap;

/**
 * API 签名工具。
 *
 * @author Alibaba Cloud
 * @date 2019/01/20
 */
public class SignatureUtils {
    private final static String CHARSET_UTF8 = "utf8";
    private final static String ALGORITHM = "UTF-8";
    private final static String SEPARATOR = "&";
    private final static String METHOD_NAME_POST = "POST";
    /**
     * 分解请求串中的参数。
     * @param url 原始 URL。
     * @return 分解后的参数名、参数值对的映射
     */
    public static Map<String, String> splitQueryString(String url)
            throws URISyntaxException,
            UnsupportedEncodingException {
        URI uri = new URI(url);
        String query = uri.getQuery();
        final String[] pairs = query.split("&");
        TreeMap<String, String> queryMap = new TreeMap<String, String>();
        for (String pair : pairs) {
            final int idx = pair.indexOf("=");
            final String key = idx > 0 ? pair.substring(0, idx) : pair;
            if (!queryMap.containsKey(key)) {
                queryMap.put(key, URLDecoder.decode(pair.substring(idx + 1), CHARSET_UTF8));
            }
        }
        return queryMap;
    }
    /**
     * 计算签名名转译成合适的编码。
     * @param httpMethod HTTP 请求的 Method。
     * @param parameter 原始请求串中参数名、参数值对的映射。
     * @param accessKeySecret 阿里云账号的 Access Key Secret。
     * @return 转译成合适编码的签名。
     */
    public static String generate(String httpMethod, Map<String, Object> parameter, String accessKeySecret)
            throws Exception {
        String stringToSign = generateStringToSign(httpMethod, parameter);
        System.out.println("signString---" + stringToSign);
        byte[] signBytes = hmacSHA1Signature(accessKeySecret + "&", stringToSign);
        String signature = newStringByBase64(signBytes);
        if (signature == null) {
            return "";
        }
        System.out.println("signature----" + signature);
        if (METHOD_NAME_POST.equals(httpMethod)) {
            return signature;
        }
        return URLEncoder.encode(signature, ALGORITHM);
    }
    /**
     * 计算签名中间产物 StringToSign。
     * @param httpMethod HTTP 请求的 Method。
     * @param parameter 原始请求串中参数名、参数值对的映射。
     * @return 签名中间产物 StringToSign。
     */
    public static String generateStringToSign(String httpMethod, Map<String, Object> parameter)
            throws IOException {
        TreeMap<String, Object> sortParameter = new TreeMap<>(parameter);
        String canonicalizedQueryString = UrlUtil.canonicalizeQueryString(sortParameter, true);
        if (httpMethod == null || httpMethod.isEmpty()) {
            throw new RuntimeException("httpMethod can not be empty");
        }
        StringBuilder stringToSign = new StringBuilder();
        stringToSign.append(httpMethod).append(SEPARATOR);
        stringToSign.append(percentEncode("/")).append(SEPARATOR);
        stringToSign.append(percentEncode(canonicalizedQueryString));
        return stringToSign.toString();
    }
    /**
     * 对原文进行百分号编码处理。
     * @param text 要处理的原文。
     * @return 处理后的百分号编码。
     */
    public static String percentEncode(String text) {
        try {
            return text == null ? null
                    : URLEncoder.encode(text, CHARSET_UTF8)
                    .replace("+", "%20")
                    .replace("*", "%2A")
                    .replace("%7E", "~");
        } catch (Exception e) {
            System.out.println("Percentage encoding error:" + e.getMessage());
        }
        return "";
    }
    /**
     * HMAC-SHA1 键控散列。
     * @param secret HMAC-SHA1 使用的 Secret。
     * @param baseString 原文。
     * @return 散列值。
     */
    public static byte[] hmacSHA1Signature(String secret, String baseString)
            throws Exception {
        if (secret == null || secret.isEmpty()) {
            throw new IOException("secret can not be empty");
        }
        if (baseString == null || baseString.isEmpty()) {
            return null;
        }
        Mac mac = Mac.getInstance("HmacSHA1");
        SecretKeySpec keySpec = new SecretKeySpec(secret.getBytes(CHARSET_UTF8), ALGORITHM);
        mac.init(keySpec);
        return mac.doFinal(baseString.getBytes(CHARSET_UTF8));
    }
    /**
     * Base 64 编码。
     * @param bytes 原文。
     * @return Base 64 编码。
     */
    public static String newStringByBase64(byte[] bytes)
            throws UnsupportedEncodingException {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        return new String(Base64.encodeBase64(bytes, false), CHARSET_UTF8);
    }
}