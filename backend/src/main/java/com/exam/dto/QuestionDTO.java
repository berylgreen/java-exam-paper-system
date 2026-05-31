package com.exam.dto;

import com.exam.enums.Difficulty;
import com.exam.enums.QuestionType;
import lombok.Data;

/**
 * 题目 DTO (请求/响应)
 */
@Data
public class QuestionDTO {
    private Long id;
    private QuestionType type;
    private Long chapterId;
    private String chapterName;
    private Difficulty difficulty;
    private String content;
    private String options;      // JSON 字符串
    private String answer;
    private String explanation;
    private Integer defaultScore;
    private String source;
    private String projectPath;
}
