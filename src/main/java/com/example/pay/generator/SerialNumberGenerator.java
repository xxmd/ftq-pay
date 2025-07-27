package com.example.pay.generator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * 流水号生成器
 */
@Component
public class SerialNumberGenerator {

    private static final String KEY_PREFIX = "order:seq:";

    @Autowired
    private StringRedisTemplate redisTemplate;

    public String generateSerialNumber() {
        // 生成当前日期字符串
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String redisKey = KEY_PREFIX + date;

        // Redis原子自增
        Long seq = redisTemplate.opsForValue().increment(redisKey);

        // 第一次生成时设置过期时间为24小时
        if (seq != null && seq == 1L) {
            redisTemplate.expire(redisKey, Duration.ofDays(1));
        }

        // 补零，流水号长度固定为4位（例如 0001, 0023）
        String serial = String.format("%04d", seq);
        return date + serial;
    }
}
