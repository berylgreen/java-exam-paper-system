package com.exam.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 试卷 DTO (响应)
 */
@Data
public class PaperDTO {
    private Long id;
    private String title;
    private Integer totalScore;
    private Integer durationMinutes;
    private String description;
    private LocalDateTime createdAt;
    private List<PaperQuestionDTO> questions;

    /**
     * 试卷中的题目 DTO
     */
    @Data
    public static class PaperQuestionDTO {
        private Long id;
        private Integer questionOrder;
        private Integer score;
        private QuestionDTO question;
    }
}
