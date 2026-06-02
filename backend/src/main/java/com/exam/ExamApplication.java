package com.exam;

import com.exam.config.AiProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * Java 程序设计基础 — 出题组卷系统
 * 启动入口
 */
@SpringBootApplication
@EnableConfigurationProperties(AiProperties.class)
public class ExamApplication {
    public static void main(String[] args) {
        SpringApplication.run(ExamApplication.class, args);
    }
}
