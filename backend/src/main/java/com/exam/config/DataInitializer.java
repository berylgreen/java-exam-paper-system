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

/**
 * 数据初始化器 — 首次启动时从 JSON 文件加载题库和预置试卷
 *
 * 题库数据存放在 resources/questions_net.json 和 resources/questions_textbook.json
 * 修改题目后重启即可重新初始化。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final QuestionRepository qRepo;
    private final ExamPaperRepository paperRepo;
    private final PaperQuestionRepository pqRepo;
    private final ObjectMapper objectMapper;

    @Override
    public void run(String... args) {
        if (qRepo.count() > 0) {
            log.info("题库已存在，跳过初始化");
            return;
        }
        log.info("开始从 JSON 文件初始化题库...");

        List<Question> netQuestions = loadQuestions("questions_net.json");
        qRepo.saveAll(netQuestions);
        log.info("网络来源题目初始化完成，共 {} 道题", netQuestions.size());

        initPapers(netQuestions);
        log.info("预置试卷初始化完成");

        List<Question> tbQuestions = loadQuestions("questions_textbook.json");
        qRepo.saveAll(tbQuestions);
        log.info("课后习题原题初始化完成，共 {} 道题", tbQuestions.size());
    }

    /**
     * 从 classpath 中的 JSON 文件加载题目列表
     */
    private List<Question> loadQuestions(String filename) {
        try {
            InputStream is = new ClassPathResource(filename).getInputStream();
            List<Map<String, Object>> rawList = objectMapper.readValue(is, new TypeReference<>() {});
            List<Question> questions = new ArrayList<>();
            for (Map<String, Object> raw : rawList) {
                // options 字段：JSON 文件中存储为数组，需转回 JSON 字符串存入数据库
                String optionsStr = null;
                Object opts = raw.get("options");
                if (opts != null) {
                    optionsStr = objectMapper.writeValueAsString(opts);
                }
                Question q = Question.builder()
                        .type(QuestionType.valueOf((String) raw.get("type")))
                        .chapter((String) raw.get("chapter"))
                        .difficulty(Difficulty.valueOf((String) raw.get("difficulty")))
                        .content((String) raw.get("content"))
                        .options(optionsStr)
                        .answer((String) raw.get("answer"))
                        .explanation((String) raw.get("explanation"))
                        .defaultScore((Integer) raw.get("defaultScore"))
                        .source((String) raw.get("source"))
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

        Collections.shuffle(scs); Collections.shuffle(mcs);
        Collections.shuffle(tfs); Collections.shuffle(fbs);
        Collections.shuffle(sas); Collections.shuffle(pgs);

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
            for (int i = 0; i < 10; i++) {
                addPQ(paper, scs.get((p * 10 + i) % scs.size()), ++order, 2);
            }
            for (int i = 0; i < 5; i++) {
                addPQ(paper, mcs.get((p * 5 + i) % mcs.size()), ++order, 4);
            }
            for (int i = 0; i < 5; i++) {
                addPQ(paper, tfs.get((p * 5 + i) % tfs.size()), ++order, 2);
            }
            for (int i = 0; i < 5; i++) {
                addPQ(paper, fbs.get((p * 5 + i) % fbs.size()), ++order, 4);
            }
            for (int i = 0; i < 2; i++) {
                addPQ(paper, sas.get((p * 2 + i) % sas.size()), ++order, 10);
            }
            addPQ(paper, pgs.get(p % pgs.size()), ++order, 10);
        }
    }

    private void addPQ(ExamPaper paper, Question q, int order, int score) {
        pqRepo.save(PaperQuestion.builder()
                .paper(paper).question(q).questionOrder(order).score(score).build());
    }
}
