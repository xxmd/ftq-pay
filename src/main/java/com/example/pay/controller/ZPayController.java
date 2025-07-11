package com.example.pay.controller;

import com.example.pay.entity.ZPayCallback;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Api(tags = "ZPay支付接口")
@RestController
@RequestMapping("/zpay")
public class ZPayController {
    private static final Logger logger = LoggerFactory.getLogger(ZPayController.class);

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
}
