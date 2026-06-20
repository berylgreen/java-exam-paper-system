package com.exam.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * 题目收藏夹实体
 */
@Entity
@Table(name = "favorite_question")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class FavoriteQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 收藏的题目ID */
    @Column(name = "question_id", nullable = false, unique = true)
    private Long questionId;

    /** 收藏时间 */
    @Column(name = "create_time")
    private java.time.LocalDateTime createTime;
}
