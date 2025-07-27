package com.example.pay.controller;

import com.example.pay.dao.OrderInfoRepository;
import com.example.pay.entity.OrderInfo;
import com.example.pay.entity.ZPayCallback;
import com.example.pay.entity.ZPayOrder;
import com.example.pay.entity.constants.StringConstants;
import com.example.pay.entity.enums.OrderStatus;
import com.example.pay.entity.enums.PayChannel;
import com.example.pay.generator.SerialNumberGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;
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

    @Value("${server.hostname}")
    private String hostname;

    @Autowired
    private SerialNumberGenerator serialNumberGenerator;

    @Autowired
    private OrderInfoRepository orderInfoRepository;

    @GetMapping("/callback")
    @ApiOperation("ZPay支付回调")
    public String callback(@RequestParam Map<String, String> params) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(params);
            logger.info("原始ZPay回调参数信息: {}", json);
            mapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
            ZPayCallback zPayCallback = mapper.convertValue(params, ZPayCallback.class);
            String serialNumber = zPayCallback.getOutTradeNo();
            String tradeStatus = zPayCallback.getTradeStatus();
            if (StringConstants.TRADE_STATUS_SUCCESS.equals(tradeStatus)) {
                OrderInfo daoOrderInfo = orderInfoRepository.findBySerialNumber(serialNumber);
                if (daoOrderInfo == null) {
                    logger.error("ZPay支付回调中序列号：{} 在数据库中不存在", serialNumber);
                } else {
                    daoOrderInfo.setOrderStatus(OrderStatus.PAYED);
                    orderInfoRepository.save(daoOrderInfo);
                }
            } else {
                logger.error("ZPay支付回调中订单状态参数为: {}", tradeStatus);
            }
        } catch (JsonProcessingException e) {
            logger.error("ZPay回调参数序列化失败", e);
        }
        return "success";
    }

    @PostMapping("/geneRedirectPayLink")
    @ApiOperation("生成跳转链接支付")
    public String geneRedirectPayLink(@RequestBody @Valid ZPayOrder zPayOrder) {
        Map<String, String> params = new TreeMap<>();
        params.put("name", zPayOrder.getName());
        params.put("money", String.valueOf(zPayOrder.getMoney()));
        params.put("type", zPayOrder.getType().getValue());
        String serialNumber = serialNumberGenerator.generateSerialNumber();
        params.put("out_trade_no", serialNumber);
        params.put("notify_url", String.format(Locale.US, "https://%s/ftq-pay/zpay/callback", hostname));
        params.put("pid", pid);
        params.put("cid", cid);
        params.put("return_url", zPayOrder.getReturnUrl());
        String preParams = concatParams(params) + secretKey;
        String signature = md5(preParams);
        params.put("sign", signature);
        params.put("sign_type", "MD5");

        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setPayChannel(PayChannel.ZPAY);
        orderInfo.setSerialNumber(serialNumber);
        orderInfo.setProductName(zPayOrder.getName());
        orderInfo.setProductPrice(zPayOrder.getMoney());
        orderInfo.setOrderStatus(OrderStatus.CREATED);
        orderInfoRepository.save(orderInfo);
        return "https://z-pay.cn/submit.php?" + concatParams(params);
    }

    /**
     * 拼接请求参数
     * @param params 请求参数
     * @return
     */
    private String concatParams(Map<String, String> params) {
        if (params == null || params.isEmpty()) {
            return "";
        }
        return params.entrySet().stream().map(it -> it.getKey() + "=" + it.getValue()).collect(Collectors.joining("&"));
    }

    /**
     * md5加密
     * @param input 输入字符串
     * @return
     */
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
