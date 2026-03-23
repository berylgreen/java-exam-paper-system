package com.exam.repository;

import com.exam.entity.Question;
import com.exam.enums.Difficulty;
import com.exam.enums.QuestionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

    /** 多条件分页查询 */
    @Query("SELECT q FROM Question q WHERE " +
           "(:type IS NULL OR q.type = :type) AND " +
           "(:chapter IS NULL OR q.chapter = :chapter) AND " +
           "(:difficulty IS NULL OR q.difficulty = :difficulty) AND " +
           "(:source IS NULL OR q.source = :source)")
    Page<Question> findByFilters(
            @Param("type") QuestionType type,
            @Param("chapter") String chapter,
            @Param("difficulty") Difficulty difficulty,
            @Param("source") String source,
            Pageable pageable);

    /** 按题型+章节+难度查询 (自动组卷用) */
    List<Question> findByTypeAndDifficulty(QuestionType type, Difficulty difficulty);

    List<Question> findByTypeAndChapterAndDifficulty(QuestionType type, String chapter, Difficulty difficulty);

    List<Question> findByType(QuestionType type);

    List<Question> findByTypeAndChapter(QuestionType type, String chapter);

    /** 按题型+来源查询 (自动组卷用) */
    List<Question> findByTypeAndSource(QuestionType type, String source);

    List<Question> findByTypeAndChapterAndSource(QuestionType type, String chapter, String source);

    /** 获取所有章节 (去重) */
    @Query("SELECT DISTINCT q.chapter FROM Question q ORDER BY q.chapter")
    List<String> findAllChapters();

    /** 获取所有来源 (去重) */
    @Query("SELECT DISTINCT q.source FROM Question q WHERE q.source IS NOT NULL ORDER BY q.source")
    List<String> findAllSources();

    /** 按题型统计数量 */
    @Query("SELECT q.type, COUNT(q) FROM Question q GROUP BY q.type")
    List<Object[]> countByType();

    /** 按章节统计数量 */
    @Query("SELECT q.chapter, COUNT(q) FROM Question q GROUP BY q.chapter")
    List<Object[]> countByChapter();

    /** 按难度统计数量 */
    @Query("SELECT q.difficulty, COUNT(q) FROM Question q GROUP BY q.difficulty")
    List<Object[]> countByDifficulty();

    /** 按来源统计数量 */
    @Query("SELECT q.source, COUNT(q) FROM Question q GROUP BY q.source")
    List<Object[]> countBySource();
}
