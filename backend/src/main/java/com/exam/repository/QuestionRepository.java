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
           "(:chapterId IS NULL OR q.chapter.id = :chapterId) AND " +
           "(:difficulty IS NULL OR q.difficulty = :difficulty) AND " +
           "(:source IS NULL OR q.source = :source) AND " +
           "(:keyword IS NULL OR :keyword = '' OR q.content LIKE CONCAT('%', :keyword, '%'))")
    Page<Question> findByFilters(
            @Param("type") QuestionType type,
            @Param("chapterId") Long chapterId,
            @Param("difficulty") Difficulty difficulty,
            @Param("source") String source,
            @Param("keyword") String keyword,
            Pageable pageable);

    /** 按题型+章节+难度查询 (自动组卷用) */
    List<Question> findByTypeAndDifficulty(QuestionType type, Difficulty difficulty);

    List<Question> findByTypeAndChapterIdAndDifficulty(QuestionType type, Long chapterId, Difficulty difficulty);

    List<Question> findByType(QuestionType type);

    List<Question> findByTypeAndChapterId(QuestionType type, Long chapterId);

    @Query("SELECT q FROM Question q WHERE q.type = :type AND q.chapter.name = :chapterName")
    List<Question> findByTypeAndChapterName(@Param("type") QuestionType type, @Param("chapterName") String chapterName);

    /** 按题型+来源查询 (自动组卷用) */
    List<Question> findByTypeAndSource(QuestionType type, String source);

    List<Question> findByTypeAndChapterIdAndSource(QuestionType type, Long chapterId, String source);

    /** 废弃，请使用 ChapterRepository.findAll() */

    /** 获取所有来源 (去重) */
    @Query("SELECT DISTINCT q.source FROM Question q WHERE q.source IS NOT NULL ORDER BY q.source")
    List<String> findAllSources();

    /** 按题型统计数量 */
    @Query("SELECT q.type, COUNT(q) FROM Question q GROUP BY q.type")
    List<Object[]> countByType();

    /** 按章节统计数量 */
    @Query("SELECT q.chapter.name, COUNT(q) FROM Question q GROUP BY q.chapter.name")
    List<Object[]> countByChapter();

    /** 按难度统计数量 */
    @Query("SELECT q.difficulty, COUNT(q) FROM Question q GROUP BY q.difficulty")
    List<Object[]> countByDifficulty();

    /** 按来源统计数量 */
    @Query("SELECT q.source, COUNT(q) FROM Question q GROUP BY q.source")
    List<Object[]> countBySource();
}
