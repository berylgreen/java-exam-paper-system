package com.exam.config;

import com.exam.entity.*;
import com.exam.enums.*;
import com.exam.repository.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 数据初始化器 — 首次启动时从 JSON 文件加载题库和预置试卷
 *
 * 题库数据存放在 resources/questions.json
 * 可直接使用导出的 JSON 文件替换此文件来更新题库，重启即可重新初始化。
 */
@Slf4j
@Component
@org.springframework.context.annotation.Profile("!test")
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final QuestionRepository qRepo;
    private final ExamPaperRepository paperRepo;
    private final PaperQuestionRepository pqRepo;
    private final ChapterRepository chapterRepository;
    private final ObjectMapper objectMapper;
    private final MetadataConfig metadataConfig;

    @Override
    public void run(String... args) {
        if (qRepo.count() > 0) {
            log.info("题库已存在，跳过题库初始化");
            if (paperRepo.count() == 0) {
                log.info("试卷列表为空，开始初始化预置试卷...");
                List<Question> allQuestions = qRepo.findAll();
                List<Question> paperQuestions = allQuestions.stream()
                        .filter(q -> {
                            if (q.getChapter() == null || q.getChapter().getName() == null) return false;
                            java.util.regex.Matcher m = java.util.regex.Pattern.compile("^第(\\d+)章").matcher(q.getChapter().getName());
                            if (m.find()) {
                                int chap = Integer.parseInt(m.group(1));
                                return chap >= 1 && chap <= 7;
                            }
                            return false;
                        })
                        .collect(Collectors.toList());
                initPapers(paperQuestions);
                log.info("预置试卷初始化完成");
            }
            return;
        }
        
        log.info("从 metadata.json 初始化基础章节...");
        for (MetadataConfig.ChapterPreset cp : metadataConfig.getChapters()) {
            if (chapterRepository.findByName(cp.getName()).isEmpty()) {
                chapterRepository.save(Chapter.builder().name(cp.getName()).sortOrder(cp.getSortOrder()).build());
            }
        }

        log.info("开始从 JSON 文件初始化题库...");

        List<Question> allQuestions = loadQuestions("questions.json");
        qRepo.saveAll(allQuestions);
        log.info("题库初始化完成，共 {} 道题", allQuestions.size());

        // 默认只考从第1章到第7章
        List<Question> paperQuestions = allQuestions.stream()
                .filter(q -> {
                    if (q.getChapter() == null || q.getChapter().getName() == null) return false;
                    java.util.regex.Matcher m = java.util.regex.Pattern.compile("^第(\\d+)章").matcher(q.getChapter().getName());
                    if (m.find()) {
                        int chap = Integer.parseInt(m.group(1));
                        return chap >= 1 && chap <= 7;
                    }
                    return false;
                })
                .collect(Collectors.toList());

        initPapers(paperQuestions);
        log.info("预置试卷初始化完成");
    }

    /**
     * 从 classpath 中的 JSON 文件加载题目列表
     */
    private List<Question> loadQuestions(String filename) {
        try {
            InputStream is = new ClassPathResource(filename).getInputStream();
            List<Map<String, Object>> rawList = objectMapper.readValue(is, new TypeReference<>() {});
            List<Question> questions = new ArrayList<>();
            Map<String, Chapter> chapterCache = new HashMap<>();

            for (Map<String, Object> raw : rawList) {
                // options 字段：JSON 文件中存储为数组，需转回 JSON 字符串存入数据库
                String optionsStr = null;
                Object opts = raw.get("options");
                if (opts != null) {
                    optionsStr = objectMapper.writeValueAsString(opts);
                }

                String chapterName = (String) raw.get("chapter");
                Chapter chapter = null;
                if (chapterName != null && !chapterName.trim().isEmpty()) {
                    chapter = chapterCache.computeIfAbsent(chapterName, name -> {
                        return chapterRepository.findByName(name)
                                .orElseThrow(() -> new IllegalArgumentException("未知的章节名: " + name + "，请先在 metadata.json 中预设"));
                    });
                }

                Question q = Question.builder()
                        .type(QuestionType.valueOf((String) raw.get("type")))
                        .chapter(chapter)
                        .difficulty(Difficulty.valueOf((String) raw.get("difficulty")))
                        .content((String) raw.get("content"))
                        .options(optionsStr)
                        .answer((String) raw.get("answer"))
                        .explanation((String) raw.get("explanation"))
                        .defaultScore((Integer) raw.get("defaultScore"))
                        .source((String) raw.get("source"))
                        .projectPath((String) raw.get("projectPath"))
                        .answerProjectPath((String) raw.get("answerProjectPath"))
                        .build();
                questions.add(q);
            }
            return questions;
        } catch (Exception e) {
            log.error("加载题库文件 {} 失败: {}", filename, e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    // ===== 预置试卷初始化 =====
    private void initPapers(List<Question> all) {
        Map<QuestionType, List<Question>> byType = new HashMap<>();
        for (Question q : all) {
            byType.computeIfAbsent(q.getType(), k -> new ArrayList<>()).add(q);
        }

        List<Question> scs = byType.getOrDefault(QuestionType.SINGLE_CHOICE, new ArrayList<>());
        List<Question> mcs = byType.getOrDefault(QuestionType.MULTIPLE_CHOICE, new ArrayList<>());
        List<Question> tfs = byType.getOrDefault(QuestionType.TRUE_FALSE, new ArrayList<>());
        List<Question> fbs = byType.getOrDefault(QuestionType.FILL_BLANK, new ArrayList<>());
        List<Question> sas = byType.getOrDefault(QuestionType.SHORT_ANSWER, new ArrayList<>());
        List<Question> pgs = byType.getOrDefault(QuestionType.PROGRAMMING, new ArrayList<>());
        List<Question> crs = byType.getOrDefault(QuestionType.CODE_READING, new ArrayList<>());

        Collections.shuffle(scs); Collections.shuffle(mcs);
        Collections.shuffle(tfs); Collections.shuffle(fbs);
        Collections.shuffle(sas); Collections.shuffle(pgs); Collections.shuffle(crs);

        for (int p = 1; p <= 20; p++) {
            ExamPaper paper = ExamPaper.builder()
                    .title("Java程序设计基础 第" + p + "套试卷")
                    .totalScore(100)
                    .durationMinutes(120)
                    .description("涵盖Java基础全部知识点的综合测试")
                    .createdAt(LocalDateTime.now())
                    .build();
            paper = paperRepo.save(paper);

            int order = 0;
            if (!scs.isEmpty()) {
                for (int i = 0; i < 15; i++) {
                    addPQ(paper, scs.get((p * 15 + i) % scs.size()), ++order, 2);
                }
            }
            if (!mcs.isEmpty()) {
                for (int i = 0; i < 5; i++) {
                    addPQ(paper, mcs.get((p * 5 + i) % mcs.size()), ++order, 4);
                }
            }
            if (!tfs.isEmpty()) {
                for (int i = 0; i < 5; i++) {
                    addPQ(paper, tfs.get((p * 5 + i) % tfs.size()), ++order, 2);
                }
            }
            if (!fbs.isEmpty()) {
                for (int i = 0; i < 5; i++) {
                    Question q = fbs.get((p * 5 + i) % fbs.size());
                    int score = q.getDefaultScore() != null ? q.getDefaultScore() : 2;
                    addPQ(paper, q, ++order, score);
                }
            }
            if (!crs.isEmpty()) {
                for (int i = 0; i < 2; i++) {
                    addPQ(paper, crs.get((p * 2 + i) % crs.size()), ++order, 10);
                }
            }
            if (!pgs.isEmpty()) {
                for (int i = 0; i < 3; i++) {
                    Question q = pgs.get((p * 3 + i) % pgs.size());
                    int score = q.getDefaultScore() != null ? q.getDefaultScore() : (i == 0 ? 10 : 20);
                    addPQ(paper, q, ++order, score);
                }
            }
        }
    }

    private void addPQ(ExamPaper paper, Question q, int order, int score) {
        pqRepo.save(PaperQuestion.builder()
                .paper(paper).question(q).questionOrder(order).score(score).build());
    }
}
