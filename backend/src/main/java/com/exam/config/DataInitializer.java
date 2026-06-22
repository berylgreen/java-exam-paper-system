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
    private final ChapterRepository chapterRepository;
    private final ObjectMapper objectMapper;
    private final MetadataConfig metadataConfig;

    @Override
    public void run(String... args) {
        if (qRepo.count() > 0) {
            log.info("题库已存在，跳过题库初始化");
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

}
