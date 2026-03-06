package com.exam.dto;

import lombok.Data;
import java.util.List;

/**
 * 自动组卷请求 DTO
 */
@Data
public class AutoGenerateRequest {
    /** 试卷标题 */
    private String title;

    /** 考试时长 (分钟) */
    private Integer durationMinutes = 120;

    /** 单选题数量 */
    private Integer singleChoiceCount = 10;

    /** 多选题数量 */
    private Integer multipleChoiceCount = 5;

    /** 判断题数量 */
    private Integer trueFalseCount = 5;

    /** 填空题数量 */
    private Integer fillBlankCount = 5;

    /** 简答题数量 */
    private Integer shortAnswerCount = 2;

    /** 编程题数量 */
    private Integer programmingCount = 1;

    /** 章节范围 (为空则从全部章节抽取) */
    private List<String> chapters;

    /** 难度比例: 简单/中等/困难 (百分比，总和应为100) */
    private Integer easyPercent = 30;
    private Integer mediumPercent = 50;
    private Integer hardPercent = 20;
}
