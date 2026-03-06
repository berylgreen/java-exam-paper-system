package com.exam.dto;

import lombok.Data;
import java.util.List;

/**
 * 创建试卷请求 DTO
 */
@Data
public class CreatePaperRequest {
    private String title;
    private Integer durationMinutes;
    private String description;
    private List<PaperQuestionItem> questions;

    @Data
    public static class PaperQuestionItem {
        private Long questionId;
        private Integer questionOrder;
        private Integer score;
    }
}
