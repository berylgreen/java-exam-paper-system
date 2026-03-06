package com.exam.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 试卷实体
 */
@Entity
@Table(name = "exam_paper")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ExamPaper {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 试卷标题 */
    @Column(nullable = false, length = 200)
    private String title;

    /** 总分 */
    @Column(nullable = false)
    private Integer totalScore;

    /** 考试时长 (分钟) */
    @Column(nullable = false)
    private Integer durationMinutes;

    /** 描述 */
    @Column(length = 500)
    private String description;

    /** 创建时间 */
    @Column(nullable = false)
    private LocalDateTime createdAt;

    /** 试卷包含的题目 (关联关系) */
    @OneToMany(mappedBy = "paper", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("questionOrder ASC")
    @Builder.Default
    private List<PaperQuestion> paperQuestions = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
