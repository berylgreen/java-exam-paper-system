package com.exam.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * 试卷-题目关联实体 (中间表)
 */
@Entity
@Table(name = "paper_question")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class PaperQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 所属试卷 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paper_id", nullable = false)
    private ExamPaper paper;

    /** 关联题目 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    /** 题序 */
    @Column(nullable = false)
    private Integer questionOrder;

    /** 本题在此试卷中的分值 */
    @Column(nullable = false)
    private Integer score;
}
