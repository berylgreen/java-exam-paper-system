package com.exam.service;

import com.exam.dto.QuestionDTO;
import com.exam.entity.Question;
import com.exam.enums.Difficulty;
import com.exam.enums.QuestionType;
import com.exam.repository.ChapterRepository;
import com.exam.repository.ExamPaperRepository;
import com.exam.repository.FavoriteQuestionRepository;
import com.exam.repository.PaperQuestionRepository;
import com.exam.repository.QuestionRepository;
import com.exam.entity.FavoriteQuestion;
import com.exam.config.MetadataConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileSystemUtils;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
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
    private final FavoriteQuestionRepository favoriteQuestionRepository;
    private final MetadataConfig metadataConfig;

    /** 分页条件查询 */
    public Page<QuestionDTO> findByFilters(QuestionType type, Long chapterId,
                                           Difficulty difficulty, String source, Boolean favorite, String keyword, Pageable pageable) {
        Page<Question> page = questionRepository.findByFilters(type, chapterId, difficulty, source, favorite, keyword, pageable);
        List<Long> qIds = page.getContent().stream().map(Question::getId).toList();
        Set<Long> favIds = new HashSet<>();
        if (!qIds.isEmpty()) {
            favIds.addAll(favoriteQuestionRepository.findByQuestionIdIn(qIds).stream()
                    .map(FavoriteQuestion::getQuestionId).toList());
        }
        return page.map(q -> {
            QuestionDTO dto = toDTO(q);
            dto.setFavorite(favIds.contains(q.getId()));
            return dto;
        });
    }

    /** 获取单题 */
    public QuestionDTO findById(Long id) {
        QuestionDTO dto = questionRepository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new RuntimeException("题目不存在: " + id));
        dto.setFavorite(favoriteQuestionRepository.existsByQuestionId(id));
        return dto;
    }

    /** 新增题目 */
    @Transactional
    public QuestionDTO create(QuestionDTO dto) {
        Question q = toEntity(dto);
        Question saved = questionRepository.save(q);
        syncContentToReadme(saved);
        return toDTO(saved);
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
        Question saved = questionRepository.save(q);
        syncContentToReadme(saved);
        return toDTO(saved);
    }

    /** 同步题干到工程的 README.md */
    private void syncContentToReadme(Question q) {
        if (q.getProjectPath() != null && !q.getProjectPath().trim().isEmpty() &&
            q.getContent() != null && !q.getContent().isEmpty()) {
            try {
                Path p = Paths.get(q.getProjectPath().trim());
                if (!p.isAbsolute()) {
                    p = Paths.get(System.getProperty("user.dir")).getParent().resolve(p).normalize();
                }
                Path readme = p.resolve("README.md");
                String title = "";
                if (java.nio.file.Files.exists(readme)) {
                    String oldContent = java.nio.file.Files.readString(readme);
                    if (oldContent.startsWith("# ")) {
                        int index = oldContent.indexOf('\n');
                        if (index != -1) {
                            title = oldContent.substring(0, index).trim() + "\n\n";
                        } else {
                            title = oldContent.trim() + "\n\n";
                        }
                    }
                } else {
                    java.nio.file.Files.createDirectories(p);
                }
                
                String newContent = q.getContent();
                if (!newContent.startsWith("# ") && !title.isEmpty()) {
                    newContent = title + newContent;
                }
                java.nio.file.Files.writeString(readme, newContent);
            } catch (Exception e) {
                System.err.println("Failed to sync README.md for project: " + q.getProjectPath());
            }
        }
    }

    /** 切换收藏状态 */
    @Transactional
    public void toggleFavorite(Long id, Boolean favorite) {
        if (!questionRepository.existsById(id)) {
            throw new RuntimeException("题目不存在: " + id);
        }
        boolean isFav = favorite != null ? favorite : false;
        if (isFav) {
            if (!favoriteQuestionRepository.existsByQuestionId(id)) {
                FavoriteQuestion fq = FavoriteQuestion.builder()
                        .questionId(id)
                        .createTime(java.time.LocalDateTime.now())
                        .build();
                favoriteQuestionRepository.save(fq);
            }
        } else {
            favoriteQuestionRepository.deleteByQuestionId(id);
        }
    }

    /** 删除题目（同时移除该题在所有试卷中的引用，并删除相关本地工程） */
    @Transactional
    public void delete(Long id) {
        questionRepository.findById(id).ifPresent(q -> {
            deleteProjectFiles(Collections.singletonList(q));
            paperQuestionRepository.deleteByQuestionId(id);
            favoriteQuestionRepository.deleteByQuestionId(id);
            questionRepository.deleteById(id);
        });
    }

    /** 批量删除题目（并删除相关本地工程） */
    @Transactional
    public void deleteBatch(List<Long> ids) {
        if (ids == null || ids.isEmpty()) return;
        List<Question> questions = questionRepository.findAllById(ids);
        deleteProjectFiles(questions);
        paperQuestionRepository.deleteByQuestionIdIn(ids);
        for (Long id : ids) {
            favoriteQuestionRepository.deleteByQuestionId(id);
        }
        questionRepository.deleteAllById(ids);
    }

    /** 删除题目对应的工程文件 */
    private void deleteProjectFiles(List<Question> questions) {
        for (Question q : questions) {
            if (q.getProjectPath() != null && !q.getProjectPath().trim().isEmpty()) {
                try {
                    Path p = Paths.get(q.getProjectPath().trim());
                    if (!p.isAbsolute()) p = Paths.get(System.getProperty("user.dir")).getParent().resolve(p).normalize();
                    FileSystemUtils.deleteRecursively(p.toFile());
                } catch (Exception e) {
                    System.err.println("Failed to delete project path: " + q.getProjectPath());
                }
            }
            if (q.getAnswerProjectPath() != null && !q.getAnswerProjectPath().trim().isEmpty()) {
                try {
                    Path p = Paths.get(q.getAnswerProjectPath().trim());
                    if (!p.isAbsolute()) p = Paths.get(System.getProperty("user.dir")).getParent().resolve(p).normalize();
                    FileSystemUtils.deleteRecursively(p.toFile());
                } catch (Exception e) {
                    System.err.println("Failed to delete answer project path: " + q.getAnswerProjectPath());
                }
            }
        }
    }

    /** 批量修改分值 */
    @Transactional
    public void batchUpdateScore(List<Long> ids, Integer score) {
        if (ids == null || ids.isEmpty() || score == null || score < 0) return;
        List<Question> questions = questionRepository.findAllById(ids);
        for (Question q : questions) {
            q.setDefaultScore(score);
        }
        questionRepository.saveAll(questions);
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
        // 导入新题库前，必须先清除关联的试卷题目、旧试卷和收藏记录
        paperQuestionRepository.deleteAll();
        examPaperRepository.deleteAll();
        favoriteQuestionRepository.deleteAll();
        
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
        
        List<Question> savedQuestions = questionRepository.saveAll(questions);
        for (Question q : savedQuestions) {
            syncContentToReadme(q);
        }
        
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
