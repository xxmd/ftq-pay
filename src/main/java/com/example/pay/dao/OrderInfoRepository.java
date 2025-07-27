package com.example.pay.dao;

import com.example.pay.entity.OrderInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderInfoRepository extends JpaRepository<OrderInfo, Long> {
    OrderInfo findBySerialNumber(String serialNumber);
}
