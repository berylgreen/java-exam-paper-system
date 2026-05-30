package com.exam.dto;

import lombok.Data;
import java.util.List;

/**
 * 保存自动组卷结果的请求 DTO
 * 前端在预览确认后，将组卷数据发送至此接口持久化
 */
@Data
public class SaveGeneratedRequest {

    /** 试卷标题 */
    private String title;

    /** 考试时长 (分钟) */
    private Integer durationMinutes;

    /** 描述 */
    private String description;

    /** 题目列表 (含题目ID、题序、分值) */
    private List<PaperQuestionItem> questions;

    @Data
    public static class PaperQuestionItem {
        /** 题目 ID */
        private Long questionId;

        /** 题序 */
        private Integer questionOrder;

        /** 分值 */
        private Integer score;
    }
}
