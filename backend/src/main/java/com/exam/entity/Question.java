package com.exam.entity;

import com.exam.enums.Difficulty;
import com.exam.enums.QuestionType;
import jakarta.persistence.*;
import lombok.*;

/**
 * 题目实体
 */
@Entity
@Table(name = "question")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 题型 */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private QuestionType type;

    /** 所属章节 */
    @Column(nullable = false, length = 50)
    private String chapter;

    /** 难度 */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Difficulty difficulty;

    /** 题目内容 (支持 Markdown) */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    /** 选项 JSON (选择题用，其他题型为 null) */
    @Column(columnDefinition = "TEXT")
    private String options;

    /** 正确答案 */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String answer;

    /** 答案解析 */
    @Column(columnDefinition = "TEXT")
    private String explanation;

    /** 默认分值 */
    @Column(nullable = false)
    private Integer defaultScore;

    /** 题目来源 */
    @Column(length = 50)
    private String source;
}
