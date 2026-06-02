package com.exam.dto;

import lombok.Data;

/**
 * AI 优化题目请求
 */
@Data
public class QuestionOptimizeRequest {
    private QuestionDTO question;
    private String prompt;
}
