package com.example.pay.controller;

import com.example.pay.entity.ZPayCallback;
import com.example.pay.entity.ZPayOrder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Api(tags = "ZPay支付接口")
@RestController
@RequestMapping("/zpay")
public class ZPayController {
    private static final Logger logger = LoggerFactory.getLogger(ZPayController.class);

    @Value("${zpay.pid}")
    private String pid;

    @Value("${zpay.secretKey}")
    private String secretKey;

    @Value("${zpay.cid}")
    private String cid;

    @GetMapping("/callback")
    @ApiOperation("ZPay支付回调")
    public String callback(@ModelAttribute @Valid ZPayCallback zPayCallback) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(zPayCallback);
            logger.info("ZPay回调参数信息: {}", json);
        } catch (JsonProcessingException e) {
            logger.error("ZPay回调参数序列化失败", e);
        }
        return "success";
    }

    @PostMapping("/redirectPay")
    @ApiOperation("跳转链接支付")
    public String redirectPay(@RequestBody ZPayOrder zPayOrder) {
        Map<String, String> params = new TreeMap<>();
        params.put("name", zPayOrder.getName());
        params.put("money", String.valueOf(zPayOrder.getMoney()));
        params.put("type", zPayOrder.getType().getValue());
        params.put("out_trade_no", String.valueOf(System.currentTimeMillis()));
        params.put("notify_url", "notify_url");
        params.put("pid", pid);
        params.put("cid", cid);
        params.put("return_url", "return_url");
        String preParams = concatParams(params) + secretKey;
        String signature = md5(preParams);
        params.put("sign", signature);
        params.put("sign_type", "MD5");
        return "https://z-pay.cn/submit.php?" + concatParams(params);
    }

    private String concatParams(Map<String, String> params) {
        if (params == null || params.isEmpty()) {
            return "";
        }
        return params.entrySet().stream().map(it -> it.getKey() + "=" + it.getValue()).collect(Collectors.joining("&"));
    }

    public static String md5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(input.getBytes());

            // 将 byte[] 转成十六进制字符串
            StringBuilder hexString = new StringBuilder();
            for (byte b : digest) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 algorithm not found", e);
        }
    }
}
