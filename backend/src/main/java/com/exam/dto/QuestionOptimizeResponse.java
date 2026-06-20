package com.exam.dto;

import lombok.Data;

/**
 * AI 优化题目响应
 */
@Data
public class QuestionOptimizeResponse {
    private QuestionDTO optimizedQuestion;
    private java.util.List<String> updatedProjectFiles;
}
