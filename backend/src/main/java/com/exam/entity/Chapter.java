package com.exam.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * 章节实体
 */
@Entity
@Table(name = "chapter")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Chapter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 章节名称 */
    @Column(nullable = false, unique = true, length = 50)
    private String name;

    /** 排序权重 */
    @Column(nullable = false)
    private Integer sortOrder;
}
