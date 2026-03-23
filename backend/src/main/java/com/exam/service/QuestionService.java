package com.exam.service;

import com.exam.dto.QuestionDTO;
import com.exam.entity.Question;
import com.exam.enums.Difficulty;
import com.exam.enums.QuestionType;
import com.exam.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 题目管理服务
 */
@Service
@RequiredArgsConstructor
public class QuestionService {

    private final QuestionRepository questionRepository;

    /** 分页条件查询 */
    public Page<QuestionDTO> findByFilters(QuestionType type, String chapter,
                                           Difficulty difficulty, String source, Pageable pageable) {
        return questionRepository.findByFilters(type, chapter, difficulty, source, pageable)
                .map(this::toDTO);
    }

    /** 获取单题 */
    public QuestionDTO findById(Long id) {
        return questionRepository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new RuntimeException("题目不存在: " + id));
    }

    /** 新增题目 */
    @Transactional
    public QuestionDTO create(QuestionDTO dto) {
        Question q = toEntity(dto);
        return toDTO(questionRepository.save(q));
    }

    /** 修改题目 */
    @Transactional
    public QuestionDTO update(Long id, QuestionDTO dto) {
        Question q = questionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("题目不存在: " + id));
        q.setType(dto.getType());
        q.setChapter(dto.getChapter());
        q.setDifficulty(dto.getDifficulty());
        q.setContent(dto.getContent());
        q.setOptions(dto.getOptions());
        q.setAnswer(dto.getAnswer());
        q.setExplanation(dto.getExplanation());
        q.setDefaultScore(dto.getDefaultScore());
        q.setSource(dto.getSource());
        return toDTO(questionRepository.save(q));
    }

    /** 删除题目 */
    @Transactional
    public void delete(Long id) {
        questionRepository.deleteById(id);
    }

    /** 获取所有章节 */
    public List<String> getAllChapters() {
        return questionRepository.findAllChapters();
    }

    /** 获取所有来源 */
    public List<String> getAllSources() {
        return questionRepository.findAllSources();
    }

    /** 题库统计 */
    public Map<String, Object> getStats() {
        Map<String, Object> stats = new LinkedHashMap<>();

        // 总题数
        stats.put("total", questionRepository.count());

        // 按题型统计
        Map<String, Long> byType = new LinkedHashMap<>();
        for (Object[] row : questionRepository.countByType()) {
            byType.put(((QuestionType) row[0]).getLabel(), (Long) row[1]);
        }
        stats.put("byType", byType);

        // 按章节统计
        Map<String, Long> byChapter = new LinkedHashMap<>();
        for (Object[] row : questionRepository.countByChapter()) {
            byChapter.put((String) row[0], (Long) row[1]);
        }
        stats.put("byChapter", byChapter);

        // 按难度统计
        Map<String, Long> byDifficulty = new LinkedHashMap<>();
        for (Object[] row : questionRepository.countByDifficulty()) {
            byDifficulty.put(((Difficulty) row[0]).getLabel(), (Long) row[1]);
        }
        stats.put("byDifficulty", byDifficulty);

        // 按来源统计
        Map<String, Long> bySource = new LinkedHashMap<>();
        for (Object[] row : questionRepository.countBySource()) {
            String src = row[0] != null ? (String) row[0] : "未知";
            bySource.put(src, (Long) row[1]);
        }
        stats.put("bySource", bySource);

        return stats;
    }

    // ========== 转换方法 ==========

    public QuestionDTO toDTO(Question q) {
        QuestionDTO dto = new QuestionDTO();
        dto.setId(q.getId());
        dto.setType(q.getType());
        dto.setChapter(q.getChapter());
        dto.setDifficulty(q.getDifficulty());
        dto.setContent(q.getContent());
        dto.setOptions(q.getOptions());
        dto.setAnswer(q.getAnswer());
        dto.setExplanation(q.getExplanation());
        dto.setDefaultScore(q.getDefaultScore());
        dto.setSource(q.getSource());
        return dto;
    }

    private Question toEntity(QuestionDTO dto) {
        return Question.builder()
                .type(dto.getType())
                .chapter(dto.getChapter())
                .difficulty(dto.getDifficulty())
                .content(dto.getContent())
                .options(dto.getOptions())
                .answer(dto.getAnswer())
                .explanation(dto.getExplanation())
                .defaultScore(dto.getDefaultScore())
                .source(dto.getSource())
                .build();
    }
}
