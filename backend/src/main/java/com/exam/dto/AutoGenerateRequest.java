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
    private Integer multipleChoiceCount = 0;

    /** 判断题数量 */
    private Integer trueFalseCount = 0;

    /** 填空题数量 */
    private Integer fillBlankCount = 5;

    /** 简答题数量 */
    private Integer shortAnswerCount = 0;

    /** 编程题数量 */
    private Integer programmingCount = 3;

    /** 程序分析题数量 */
    private Integer codeReadingCount = 1;

    /** 章节范围 (为空则从全部章节抽取) */
    private List<String> chapters;

    /** 难度比例: 简单/中等/困难 (百分比，总和应为100) */
    private Integer easyPercent = 30;
    private Integer mediumPercent = 50;
    private Integer hardPercent = 20;

    /** 来源比例 (百分比，总和应为100) */
    private Integer textbookPercent = 80;
    private Integer networkPercent = 20;

    /** 是否必须有一题带项目题 */
    private Boolean mustIncludeProject = true;
}
