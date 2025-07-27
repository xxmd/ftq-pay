package com.example.pay.entity;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;

@MappedSuperclass
public class BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // 主键 id
    public Long id;

    @CreationTimestamp
    @Column
    // 创建日期
    public Date createTime;

    @UpdateTimestamp
    // 更新日期
    public Date updateTime;
}
