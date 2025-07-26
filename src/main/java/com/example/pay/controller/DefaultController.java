package com.example.pay.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
public class DefaultController {

    // 通过 application.properties 读取项目名称和版本
    @Value("${spring.application.name:unknown}")
    private String projectName;

    @Value("${project.version:unknown}")
    private String projectVersion;

    @GetMapping("/")
    public ProjectInfo hello() {
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        return new ProjectInfo(projectName, projectVersion, date);
    }

    // 返回 JSON 的 POJO 类
    static class ProjectInfo {
        private String name;
        private String version;
        private String date;

        public ProjectInfo(String name, String version, String date) {
            this.name = name;
            this.version = version;
            this.date = date;
        }

        public String getName() {
            return name;
        }

        public String getVersion() {
            return version;
        }

        public String getDate() {
            return date;
        }
    }
}
