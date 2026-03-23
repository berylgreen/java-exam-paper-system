package com.exam.service;

import com.exam.dto.*;
import com.exam.entity.*;
import com.exam.enums.Difficulty;
import com.exam.enums.QuestionType;
import com.exam.repository.*;
import lombok.RequiredArgsConstructor;
import org.apache.poi.xwpf.usermodel.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 试卷管理服务 — 含手动组卷、自动组卷、Word导出
 */
@Service
@RequiredArgsConstructor
public class ExamPaperService {

    private final ExamPaperRepository paperRepository;
    private final PaperQuestionRepository paperQuestionRepository;
    private final QuestionRepository questionRepository;
    private final QuestionService questionService;

    /** 获取所有试卷 (不含题目详情) */
    @Transactional(readOnly = true)
    public List<PaperDTO> findAll() {
        return paperRepository.findAll().stream()
                .map(this::toSimpleDTO)
                .collect(Collectors.toList());
    }

    /** 获取试卷详情 (含所有题目) */
    @Transactional(readOnly = true)
    public PaperDTO findById(Long id) {
        ExamPaper paper = paperRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("试卷不存在: " + id));
        return toFullDTO(paper);
    }

    /** 手动创建试卷 */
    @Transactional
    public PaperDTO create(CreatePaperRequest request) {
        // 计算总分
        int totalScore = request.getQuestions().stream()
                .mapToInt(CreatePaperRequest.PaperQuestionItem::getScore)
                .sum();

        ExamPaper paper = ExamPaper.builder()
                .title(request.getTitle())
                .totalScore(totalScore)
                .durationMinutes(request.getDurationMinutes())
                .description(request.getDescription())
                .createdAt(LocalDateTime.now())
                .build();
        paper = paperRepository.save(paper);

        // 添加题目关联
        List<PaperQuestion> savedPqs = new ArrayList<>();
        for (CreatePaperRequest.PaperQuestionItem item : request.getQuestions()) {
            Question question = questionRepository.findById(item.getQuestionId())
                    .orElseThrow(() -> new RuntimeException("题目不存在: " + item.getQuestionId()));
            PaperQuestion pq = PaperQuestion.builder()
                    .paper(paper)
                    .question(question)
                    .questionOrder(item.getQuestionOrder())
                    .score(item.getScore())
                    .build();
            savedPqs.add(paperQuestionRepository.save(pq));
        }
        // 显式填充集合，避免 toFullDTO 触发延迟加载导致 H2 死锁
        paper.setPaperQuestions(savedPqs);

        return toFullDTO(paper);
    }

    /** 自动组卷核心算法 */
    @Transactional
    public PaperDTO autoGenerate(AutoGenerateRequest req) {
        List<PaperQuestion> selectedQuestions = new ArrayList<>();
        int order = 0;

        // 按题型依次抽取
        order = pickQuestions(selectedQuestions, QuestionType.SINGLE_CHOICE,
                req.getSingleChoiceCount(), 2, req, order);
        order = pickQuestions(selectedQuestions, QuestionType.MULTIPLE_CHOICE,
                req.getMultipleChoiceCount(), 4, req, order);
        order = pickQuestions(selectedQuestions, QuestionType.TRUE_FALSE,
                req.getTrueFalseCount(), 2, req, order);
        order = pickQuestions(selectedQuestions, QuestionType.FILL_BLANK,
                req.getFillBlankCount(), 4, req, order);
        order = pickQuestions(selectedQuestions, QuestionType.SHORT_ANSWER,
                req.getShortAnswerCount(), 10, req, order);
        order = pickQuestions(selectedQuestions, QuestionType.PROGRAMMING,
                req.getProgrammingCount(), 10, req, order);

        // 计算总分
        int totalScore = selectedQuestions.stream()
                .mapToInt(PaperQuestion::getScore)
                .sum();

        // 创建试卷
        String title = req.getTitle() != null ? req.getTitle()
                : "自动生成试卷 - " + LocalDateTime.now().toString().substring(0, 16);

        ExamPaper paper = ExamPaper.builder()
                .title(title)
                .totalScore(totalScore)
                .durationMinutes(req.getDurationMinutes())
                .description("自动组卷生成")
                .createdAt(LocalDateTime.now())
                .build();
        paper = paperRepository.save(paper);

        // 保存关联
        List<PaperQuestion> savedPqs = new ArrayList<>();
        for (PaperQuestion pq : selectedQuestions) {
            pq.setPaper(paper);
            savedPqs.add(paperQuestionRepository.save(pq));
        }
        // 显式填充集合，避免 toFullDTO 触发延迟加载导致 H2 死锁
        paper.setPaperQuestions(savedPqs);

        return toFullDTO(paper);
    }

    /** 删除试卷 */
    @Transactional
    public void delete(Long id) {
        paperQuestionRepository.deleteByPaperId(id);
        paperRepository.deleteById(id);
    }

    /** 导出试卷为 Word 文档 */
    @Transactional(readOnly = true)
    public byte[] exportToWord(Long id) throws IOException {
        PaperDTO paper = findById(id);

        try (XWPFDocument doc = new XWPFDocument()) {
            // 标题
            XWPFParagraph titlePara = doc.createParagraph();
            titlePara.setAlignment(ParagraphAlignment.CENTER);
            XWPFRun titleRun = titlePara.createRun();
            titleRun.setText(paper.getTitle());
            titleRun.setBold(true);
            titleRun.setFontSize(18);
            titleRun.setFontFamily("宋体");

            // 试卷信息
            XWPFParagraph infoPara = doc.createParagraph();
            infoPara.setAlignment(ParagraphAlignment.CENTER);
            XWPFRun infoRun = infoPara.createRun();
            infoRun.setText(String.format("总分: %d分  时间: %d分钟",
                    paper.getTotalScore(), paper.getDurationMinutes()));
            infoRun.setFontSize(12);
            infoRun.setFontFamily("宋体");

            // 分隔线
            doc.createParagraph().createRun().addBreak();

            // 按题型分组输出
            Map<QuestionType, List<PaperDTO.PaperQuestionDTO>> grouped = paper.getQuestions().stream()
                    .collect(Collectors.groupingBy(
                            pq -> pq.getQuestion().getType(),
                            LinkedHashMap::new,
                            Collectors.toList()));

            // 大题序号
            String[] sectionNums = {"一", "二", "三", "四", "五", "六"};
            int sectionIdx = 0;
            // 定义题型显示顺序
            QuestionType[] typeOrder = {
                QuestionType.SINGLE_CHOICE, QuestionType.MULTIPLE_CHOICE,
                QuestionType.TRUE_FALSE, QuestionType.FILL_BLANK,
                QuestionType.SHORT_ANSWER, QuestionType.PROGRAMMING
            };

            for (QuestionType type : typeOrder) {
                List<PaperDTO.PaperQuestionDTO> questions = grouped.get(type);
                if (questions == null || questions.isEmpty()) continue;

                // 大题标题
                int sectionScore = questions.stream().mapToInt(PaperDTO.PaperQuestionDTO::getScore).sum();
                XWPFParagraph sectionPara = doc.createParagraph();
                XWPFRun sectionRun = sectionPara.createRun();
                sectionRun.setText(String.format("%s、%s (共%d题，共%d分)",
                        sectionIdx < sectionNums.length ? sectionNums[sectionIdx] : String.valueOf(sectionIdx + 1),
                        type.getLabel(), questions.size(), sectionScore));
                sectionRun.setBold(true);
                sectionRun.setFontSize(14);
                sectionRun.setFontFamily("宋体");

                // 逐题输出
                int qNum = 1;
                for (PaperDTO.PaperQuestionDTO pq : questions) {
                    QuestionDTO q = pq.getQuestion();
                    XWPFParagraph qPara = doc.createParagraph();
                    XWPFRun qRun = qPara.createRun();
                    qRun.setText(String.format("%d. (%d分) %s", qNum++, pq.getScore(), q.getContent()));
                    qRun.setFontSize(12);
                    qRun.setFontFamily("宋体");

                    // 选择题输出选项
                    if (q.getOptions() != null && !q.getOptions().isEmpty()
                            && (type == QuestionType.SINGLE_CHOICE || type == QuestionType.MULTIPLE_CHOICE)) {
                        // 简单解析 JSON 选项
                        String opts = q.getOptions();
                        // 格式: [{"label":"A","text":"xxx"}, ...]
                        // 简单用正则提取
                        String[] labels = {"A", "B", "C", "D", "E", "F"};
                        String[] parts = opts.split("\"text\"\\s*:\\s*\"");
                        for (int i = 1; i < parts.length && i <= labels.length; i++) {
                            String text = parts[i].split("\"")[0];
                            XWPFParagraph optPara = doc.createParagraph();
                            optPara.setIndentationLeft(720); // 缩进
                            XWPFRun optRun = optPara.createRun();
                            optRun.setText(labels[i - 1] + ". " + text);
                            optRun.setFontSize(12);
                            optRun.setFontFamily("宋体");
                        }
                    }

                    // 编程题/简答题留空行
                    if (type == QuestionType.SHORT_ANSWER || type == QuestionType.PROGRAMMING) {
                        for (int i = 0; i < 5; i++) {
                            doc.createParagraph().createRun().setText("");
                        }
                    }
                }

                sectionIdx++;
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            doc.write(out);
            return out.toByteArray();
        }
    }

    // ========== 自动组卷辅助方法 ==========

    /**
     * 按题型和难度/来源比例从题库随机抽取指定数量的题目
     */
    private int pickQuestions(List<PaperQuestion> result, QuestionType type,
                              int count, int scorePerQuestion,
                              AutoGenerateRequest req, int currentOrder) {
        if (count <= 0) return currentOrder;

        // 获取候选题目
        List<Question> candidates;
        if (req.getChapters() != null && !req.getChapters().isEmpty()) {
            candidates = new ArrayList<>();
            for (String chapter : req.getChapters()) {
                candidates.addAll(questionRepository.findByTypeAndChapter(type, chapter));
            }
        } else {
            candidates = questionRepository.findByType(type);
        }

        if (candidates.isEmpty()) {
            throw new RuntimeException("题库中 [" + type.getLabel() + "] 题目不足，无法组卷");
        }

        // ===== 按来源比例分组抽取 =====
        int textbookPercent = req.getTextbookPercent() != null ? req.getTextbookPercent() : 0;
        int networkPercent = req.getNetworkPercent() != null ? req.getNetworkPercent() : 100;

        // 按来源分组
        Map<String, List<Question>> bySource = candidates.stream()
                .collect(Collectors.groupingBy(q -> q.getSource() != null ? q.getSource() : "网络2026年1月"));

        List<Question> textbookPool = bySource.getOrDefault("课后习题原题", new ArrayList<>());
        // 非课后习题的都算网络来源
        List<Question> networkPool = new ArrayList<>();
        for (Map.Entry<String, List<Question>> entry : bySource.entrySet()) {
            if (!"课后习题原题".equals(entry.getKey())) {
                networkPool.addAll(entry.getValue());
            }
        }

        // 按比例计算各来源数量
        int textbookCount = Math.round(count * textbookPercent / 100f);
        int networkCount = count - textbookCount;

        // 从各来源池中按难度比例抽取
        List<Question> picked = new ArrayList<>();

        // 抽取课后习题
        if (textbookCount > 0) {
            List<Question> tbPicked = pickByDifficulty(textbookPool, textbookCount, req);
            picked.addAll(tbPicked);
        }

        // 抽取网络来源
        if (networkCount > 0) {
            List<Question> nwPicked = pickByDifficulty(networkPool, networkCount, req);
            picked.addAll(nwPicked);
        }

        // 如果某来源不足，从所有候选中补充
        if (picked.size() < count) {
            Set<Long> pickedIds = picked.stream().map(Question::getId).collect(Collectors.toSet());
            List<Question> remaining = candidates.stream()
                    .filter(q -> !pickedIds.contains(q.getId()))
                    .collect(Collectors.toList());
            Collections.shuffle(remaining);
            int need = count - picked.size();
            picked.addAll(remaining.subList(0, Math.min(need, remaining.size())));
        }

        // 构建 PaperQuestion
        for (Question q : picked) {
            currentOrder++;
            PaperQuestion pq = PaperQuestion.builder()
                    .question(q)
                    .questionOrder(currentOrder)
                    .score(scorePerQuestion)
                    .build();
            result.add(pq);
        }

        return currentOrder;
    }

    /**
     * 从题目池中按难度比例随机抽取
     */
    private List<Question> pickByDifficulty(List<Question> pool, int count, AutoGenerateRequest req) {
        if (pool.isEmpty() || count <= 0) return new ArrayList<>();

        Map<Difficulty, List<Question>> byDifficulty = pool.stream()
                .collect(Collectors.groupingBy(Question::getDifficulty));

        int easyCount = Math.round(count * req.getEasyPercent() / 100f);
        int hardCount = Math.round(count * req.getHardPercent() / 100f);
        int mediumCount = count - easyCount - hardCount;

        List<Question> picked = new ArrayList<>();
        picked.addAll(randomPick(byDifficulty.getOrDefault(Difficulty.EASY, new ArrayList<>()), easyCount));
        picked.addAll(randomPick(byDifficulty.getOrDefault(Difficulty.MEDIUM, new ArrayList<>()), mediumCount));
        picked.addAll(randomPick(byDifficulty.getOrDefault(Difficulty.HARD, new ArrayList<>()), hardCount));

        // 如果某难度不足，从池中其他题补充
        if (picked.size() < count) {
            Set<Long> pickedIds = picked.stream().map(Question::getId).collect(Collectors.toSet());
            List<Question> remaining = pool.stream()
                    .filter(q -> !pickedIds.contains(q.getId()))
                    .collect(Collectors.toList());
            Collections.shuffle(remaining);
            int need = count - picked.size();
            picked.addAll(remaining.subList(0, Math.min(need, remaining.size())));
        }

        return picked;
    }

    /** 从列表中随机抽取 n 个元素 */
    private <T> List<T> randomPick(List<T> list, int n) {
        if (n <= 0 || list.isEmpty()) return new ArrayList<>();
        List<T> copy = new ArrayList<>(list);
        Collections.shuffle(copy);
        return copy.subList(0, Math.min(n, copy.size()));
    }

    // ========== DTO 转换 ==========

    private PaperDTO toSimpleDTO(ExamPaper paper) {
        PaperDTO dto = new PaperDTO();
        dto.setId(paper.getId());
        dto.setTitle(paper.getTitle());
        dto.setTotalScore(paper.getTotalScore());
        dto.setDurationMinutes(paper.getDurationMinutes());
        dto.setDescription(paper.getDescription());
        dto.setCreatedAt(paper.getCreatedAt());
        // 计算题目数量
        dto.setQuestions(paper.getPaperQuestions().stream()
                .map(pq -> {
                    PaperDTO.PaperQuestionDTO pqDto = new PaperDTO.PaperQuestionDTO();
                    pqDto.setId(pq.getId());
                    pqDto.setQuestionOrder(pq.getQuestionOrder());
                    pqDto.setScore(pq.getScore());
                    return pqDto;
                })
                .collect(Collectors.toList()));
        return dto;
    }

    private PaperDTO toFullDTO(ExamPaper paper) {
        PaperDTO dto = new PaperDTO();
        dto.setId(paper.getId());
        dto.setTitle(paper.getTitle());
        dto.setTotalScore(paper.getTotalScore());
        dto.setDurationMinutes(paper.getDurationMinutes());
        dto.setDescription(paper.getDescription());
        dto.setCreatedAt(paper.getCreatedAt());
        dto.setQuestions(paper.getPaperQuestions().stream()
                .map(pq -> {
                    PaperDTO.PaperQuestionDTO pqDto = new PaperDTO.PaperQuestionDTO();
                    pqDto.setId(pq.getId());
                    pqDto.setQuestionOrder(pq.getQuestionOrder());
                    pqDto.setScore(pq.getScore());
                    pqDto.setQuestion(questionService.toDTO(pq.getQuestion()));
                    return pqDto;
                })
                .collect(Collectors.toList()));
        return dto;
    }
}
