package com.example.iotLinkWandemo;

import cn.hutool.http.HttpUtil;
import com.aliyun.openservices.shade.com.alibaba.fastjson.JSONObject;
import com.example.iotLinkWandemo.signature.SignatureUtils;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author ipodao@qq.com
 * @date 2019/8/29 13:58
 */
public class Test2 {
    private static final String GETURL = "https://linkwan.cn-shanghai.aliyuncs.com/";
    private static final String ENCODING = "UTF-8";
    /**
     * 阿里云账号的 Access Key Id。
     */
    public static final String ACCESS_KEY_ID = "";
    /**
     * 阿里云账号的 Access Key Secret。
     */
    public static final String ACCESS_KEY_SECRET = "";

    @Test
    public void test() throws Exception {
        String action = "GetNode";//获取节点信息。
        Map<String, Object> param = new HashMap<>();
        param.put("Format", "JSON");
        param.put("Version", "2018-12-30");
        param.put("SignatureNonce", getUUID());
        param.put("SignatureVersion", "1.0");
        param.put("SignatureMethod", "HMAC-SHA1");
        param.put("AccessKeyId", ACCESS_KEY_ID);
        param.put("Timestamp", gettimeStamp());
        param.put("RegionId", "cn-shanghai");
        param.put("Action", action);
        param.put("GwEui", "d896e0fff0106192");

        String signature = SignatureUtils.generate("GET", param, ACCESS_KEY_SECRET);
        System.out.println("最终signature：" + signature);
        param.put("Signature", URLDecoder.decode(signature, ENCODING));

        String result3 = HttpUtil.get(GETURL, param);
        System.out.println("最终 result3：" + JSONObject.toJSON(result3));

    }

    private static String percentEncode(String value) throws UnsupportedEncodingException {
        return value != null ? URLEncoder.encode(value, ENCODING).replace("+", "%20").replace("*", "%2A").replace("%7E", "~") : null;
    }

    public String gettimeStamp() {
//        DateFormat sdf = new SimpleDateFormat("YYYY-MM-dd'T'hh:mm:ssZ");
//        DateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ss.SSSZ");
//        DateFormat sdf = new SimpleDateFormat( "YYYY-MM-DD'T'hh:mm:ssZ");
//        DateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ss.SSSZ");
//        DateFormat sdf = new SimpleDateFormat("YYYY-MM-DDThh:mm:ssZ");
//        DateFormat sdf = new SimpleDateFormat("YYYY-MM-DD'T'hh:mm:ss'Z'");
        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:SS'Z'");


        String val = sdf.format(new Date());
        System.out.println("获取的时间戳：" + val);

//        String pattern = "YYYY-MM-DD'T'hh:mm:ss'Z'";
//        String val = DateFormatUtils.format(new Date(), pattern);
        System.out.println("获取的时间戳：" + val);


        return val;
    }

    public String getUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }
}
