package com.exam.service;

import com.exam.dto.QuestionDTO;
import com.exam.entity.Question;
import com.exam.enums.Difficulty;
import com.exam.enums.QuestionType;
import com.exam.repository.ChapterRepository;
import com.exam.repository.ExamPaperRepository;
import com.exam.repository.PaperQuestionRepository;
import com.exam.repository.QuestionRepository;
import com.exam.config.MetadataConfig;
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
    private final PaperQuestionRepository paperQuestionRepository;
    private final ExamPaperRepository examPaperRepository;
    private final ChapterRepository chapterRepository;
    private final MetadataConfig metadataConfig;

    /** 分页条件查询 */
    public Page<QuestionDTO> findByFilters(QuestionType type, Long chapterId,
                                           Difficulty difficulty, String source, Pageable pageable) {
        return questionRepository.findByFilters(type, chapterId, difficulty, source, pageable)
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
        if (dto.getChapterId() != null) {
            q.setChapter(chapterRepository.findById(dto.getChapterId()).orElse(null));
        }
        q.setDifficulty(dto.getDifficulty());
        q.setContent(dto.getContent());
        q.setOptions(dto.getOptions());
        q.setAnswer(dto.getAnswer());
        q.setExplanation(dto.getExplanation());
        q.setDefaultScore(dto.getDefaultScore());
        q.setSource(dto.getSource());
        q.setProjectPath(dto.getProjectPath());
        q.setAnswerProjectPath(dto.getAnswerProjectPath());
        return toDTO(questionRepository.save(q));
    }

    /** 删除题目（同时移除该题在所有试卷中的引用） */
    @Transactional
    public void delete(Long id) {
        paperQuestionRepository.deleteByQuestionId(id);
        questionRepository.deleteById(id);
    }

    /** 批量删除题目 */
    @Transactional
    public void deleteBatch(List<Long> ids) {
        if (ids == null || ids.isEmpty()) return;
        paperQuestionRepository.deleteByQuestionIdIn(ids);
        questionRepository.deleteAllById(ids);
    }

    /** 获取所有章节 */
    public List<com.exam.entity.Chapter> getAllChapters() {
        return chapterRepository.findAll(org.springframework.data.domain.Sort.by("sortOrder"));
    }

    /** 获取所有已使用的题库来源 */
    public List<String> getAllSources() {
        Set<String> sources = new LinkedHashSet<>(metadataConfig.getSources());
        questionRepository.findAllSources().forEach(src -> {
            if (src != null && !src.trim().isEmpty()) {
                sources.add(src);
            }
        });
        return new ArrayList<>(sources);
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

    /** 导出全部题目 */
    public List<QuestionDTO> exportAll() {
        return questionRepository.findAll().stream().map(this::toDTO).toList();
    }

    /** 导入题目 (清空后批量导入) */
    @Transactional
    public Map<String, Object> importAll(List<QuestionDTO> dtos) {
        // 导入新题库前，必须先清除关联的试卷题目和旧试卷，否则会违反外键约束
        paperQuestionRepository.deleteAll();
        examPaperRepository.deleteAll();
        
        questionRepository.deleteAll();
        
        List<Question> questions = new ArrayList<>();
        List<String> errors = new ArrayList<>();
        int index = 1;
        for (QuestionDTO dto : dtos) {
            try {
                questions.add(toEntity(dto));
            } catch (Exception e) {
                errors.add("第" + index + "题导入失败: " + e.getMessage());
            }
            index++;
        }
        
        questionRepository.saveAll(questions);
        return Map.of(
            "successCount", questions.size(),
            "errors", errors
        );
    }

    // ========== 转换方法 ==========

    public QuestionDTO toDTO(Question q) {
        QuestionDTO dto = new QuestionDTO();
        dto.setId(q.getId());
        dto.setType(q.getType());
        if (q.getChapter() != null) {
            dto.setChapterId(q.getChapter().getId());
            dto.setChapterName(q.getChapter().getName());
        }
        dto.setDifficulty(q.getDifficulty());
        dto.setContent(q.getContent());
        dto.setOptions(q.getOptions());
        dto.setAnswer(q.getAnswer());
        dto.setExplanation(q.getExplanation());
        dto.setDefaultScore(q.getDefaultScore());
        dto.setSource(q.getSource());
        dto.setProjectPath(q.getProjectPath());
        dto.setAnswerProjectPath(q.getAnswerProjectPath());
        return dto;
    }

    private Question toEntity(QuestionDTO dto) {
        com.exam.entity.Chapter chap = null;
        if (dto.getChapterId() != null) {
            chap = chapterRepository.findById(dto.getChapterId())
                    .orElseThrow(() -> new IllegalArgumentException("指定的章节ID不存在"));
        } else if (dto.getChapterName() != null) {
            String name = dto.getChapterName();
            chap = chapterRepository.findByName(name)
                    .orElseThrow(() -> new IllegalArgumentException("章节不存在: " + name + "，请先在代码或数据库中添加该章节"));
        }

        if (chap == null) {
            throw new IllegalArgumentException("必须指定题目的所属章节");
        }
        return Question.builder()
                .type(dto.getType())
                .chapter(chap)
                .difficulty(dto.getDifficulty())
                .content(dto.getContent())
                .options(dto.getOptions())
                .answer(dto.getAnswer())
                .explanation(dto.getExplanation())
                .defaultScore(dto.getDefaultScore())
                .source(dto.getSource())
                .projectPath(dto.getProjectPath())
                .answerProjectPath(dto.getAnswerProjectPath())
                .build();
    }
}
