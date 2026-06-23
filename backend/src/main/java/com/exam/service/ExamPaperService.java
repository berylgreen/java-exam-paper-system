package com.exam.service;

import com.exam.dto.*;
import com.exam.entity.*;
import com.exam.enums.Difficulty;
import com.exam.enums.QuestionType;
import com.exam.repository.*;
import lombok.RequiredArgsConstructor;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;
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

        int totalCount = (req.getSingleChoiceCount() != null ? req.getSingleChoiceCount() : 0) +
                (req.getMultipleChoiceCount() != null ? req.getMultipleChoiceCount() : 0) +
                (req.getTrueFalseCount() != null ? req.getTrueFalseCount() : 0) +
                (req.getFillBlankCount() != null ? req.getFillBlankCount() : 0) +
                (req.getShortAnswerCount() != null ? req.getShortAnswerCount() : 0) +
                (req.getProgrammingCount() != null ? req.getProgrammingCount() : 0) +
                (req.getCodeReadingCount() != null ? req.getCodeReadingCount() : 0);

        // 按题型依次抽取
        order = pickQuestions(selectedQuestions, QuestionType.SINGLE_CHOICE,
                req.getSingleChoiceCount(), req.getSingleChoiceScore(), 2, req, order, totalCount);
        order = pickQuestions(selectedQuestions, QuestionType.MULTIPLE_CHOICE,
                req.getMultipleChoiceCount(), req.getMultipleChoiceScore(), 4, req, order, totalCount);
        order = pickQuestions(selectedQuestions, QuestionType.TRUE_FALSE,
                req.getTrueFalseCount(), req.getTrueFalseScore(), 2, req, order, totalCount);
        order = pickQuestions(selectedQuestions, QuestionType.FILL_BLANK,
                req.getFillBlankCount(), req.getFillBlankScore(), 2, req, order, totalCount);
        order = pickQuestions(selectedQuestions, QuestionType.SHORT_ANSWER,
                req.getShortAnswerCount(), req.getShortAnswerScore(), 10, req, order, totalCount);
        // 为了确保项目题在最后抽出，先处理阅读题，再处理程序设计题
        order = pickQuestions(selectedQuestions, QuestionType.CODE_READING,
                req.getCodeReadingCount(), req.getCodeReadingScore(), 5, req, order, totalCount);
        order = pickProgrammingQuestions(selectedQuestions, req.getProgrammingCount(), req, order, totalCount);

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

    /**
     * 自动组卷预览 — 只执行抽题算法，返回预览 DTO，不持久化到数据库
     */
    @Transactional(readOnly = true)
    public PaperDTO previewGenerate(AutoGenerateRequest req) {
        List<PaperQuestion> selectedQuestions = new ArrayList<>();
        int order = 0;

        int totalCount = (req.getSingleChoiceCount() != null ? req.getSingleChoiceCount() : 0) +
                (req.getMultipleChoiceCount() != null ? req.getMultipleChoiceCount() : 0) +
                (req.getTrueFalseCount() != null ? req.getTrueFalseCount() : 0) +
                (req.getFillBlankCount() != null ? req.getFillBlankCount() : 0) +
                (req.getShortAnswerCount() != null ? req.getShortAnswerCount() : 0) +
                (req.getProgrammingCount() != null ? req.getProgrammingCount() : 0) +
                (req.getCodeReadingCount() != null ? req.getCodeReadingCount() : 0);

        // 按题型依次抽取
        order = pickQuestions(selectedQuestions, QuestionType.SINGLE_CHOICE,
                req.getSingleChoiceCount(), req.getSingleChoiceScore(), 2, req, order, totalCount);
        order = pickQuestions(selectedQuestions, QuestionType.MULTIPLE_CHOICE,
                req.getMultipleChoiceCount(), req.getMultipleChoiceScore(), 4, req, order, totalCount);
        order = pickQuestions(selectedQuestions, QuestionType.TRUE_FALSE,
                req.getTrueFalseCount(), req.getTrueFalseScore(), 2, req, order, totalCount);
        order = pickQuestions(selectedQuestions, QuestionType.FILL_BLANK,
                req.getFillBlankCount(), req.getFillBlankScore(), 2, req, order, totalCount);
        order = pickQuestions(selectedQuestions, QuestionType.SHORT_ANSWER,
                req.getShortAnswerCount(), req.getShortAnswerScore(), 10, req, order, totalCount);
        order = pickQuestions(selectedQuestions, QuestionType.CODE_READING,
                req.getCodeReadingCount(), req.getCodeReadingScore(), 5, req, order, totalCount);
        order = pickProgrammingQuestions(selectedQuestions, req.getProgrammingCount(), req, order, totalCount);

        // 计算总分
        int totalScore = selectedQuestions.stream()
                .mapToInt(PaperQuestion::getScore)
                .sum();

        // 组装预览 DTO (不持久化)
        String title = req.getTitle() != null ? req.getTitle()
                : "自动生成试卷 - " + LocalDateTime.now().toString().substring(0, 16);

        PaperDTO dto = new PaperDTO();
        dto.setTitle(title);
        dto.setTotalScore(totalScore);
        dto.setDurationMinutes(req.getDurationMinutes());
        dto.setDescription("自动组卷生成");
        dto.setQuestions(selectedQuestions.stream()
                .map(pq -> {
                    PaperDTO.PaperQuestionDTO pqDto = new PaperDTO.PaperQuestionDTO();
                    pqDto.setQuestionOrder(pq.getQuestionOrder());
                    pqDto.setScore(pq.getScore());
                    pqDto.setQuestion(questionService.toDTO(pq.getQuestion()));
                    return pqDto;
                })
                .collect(Collectors.toList()));

        return dto;
    }

    /**
     * 保存自动组卷结果 — 将前端确认的预览数据持久化
     */
    @Transactional
    public PaperDTO saveGenerated(SaveGeneratedRequest req) {
        // 计算总分
        int totalScore = req.getQuestions().stream()
                .mapToInt(SaveGeneratedRequest.PaperQuestionItem::getScore)
                .sum();

        ExamPaper paper = ExamPaper.builder()
                .title(req.getTitle())
                .totalScore(totalScore)
                .durationMinutes(req.getDurationMinutes())
                .description(req.getDescription() != null ? req.getDescription() : "自动组卷生成")
                .createdAt(LocalDateTime.now())
                .build();
        paper = paperRepository.save(paper);

        // 保存题目关联
        List<PaperQuestion> savedPqs = new ArrayList<>();
        for (SaveGeneratedRequest.PaperQuestionItem item : req.getQuestions()) {
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
        paper.setPaperQuestions(savedPqs);

        return toFullDTO(paper);
    }

    /** 删除试卷 */
    @Transactional
    public void delete(Long id) {
        paperQuestionRepository.deleteByPaperId(id);
        paperRepository.deleteById(id);
    }

    /** 批量删除试卷 */
    @Transactional
    public void batchDelete(List<Long> ids) {
        if (ids != null && !ids.isEmpty()) {
            for (Long id : ids) {
                delete(id);
            }
        }
    }

    /** 替换试卷中的题目 */
    @Transactional
    public PaperDTO replaceQuestion(Long paperId, ReplaceQuestionRequest req) {
        ExamPaper paper = paperRepository.findById(paperId)
                .orElseThrow(() -> new RuntimeException("试卷不存在: " + paperId));

        PaperQuestion pq = paperQuestionRepository.findByPaperIdAndQuestionId(paperId, req.getOldQuestionId())
                .orElseThrow(() -> new RuntimeException("该试卷中不存在指定的题目"));

        Question newQuestion = questionRepository.findById(req.getNewQuestionId())
                .orElseThrow(() -> new RuntimeException("新题目不存在: " + req.getNewQuestionId()));

        pq.setQuestion(newQuestion);
        paperQuestionRepository.save(pq);

        return toFullDTO(paper);
    }

    /** 更新试卷名称 */
    @Transactional
    public PaperDTO updateTitle(Long paperId, String newTitle) {
        ExamPaper paper = paperRepository.findById(paperId)
                .orElseThrow(() -> new RuntimeException("试卷不存在: " + paperId));
        paper.setTitle(newTitle);
        paperRepository.save(paper);
        return toFullDTO(paper);
    }

    /** 重新排序题目 */
    @Transactional
    public PaperDTO reorderQuestions(Long paperId, ReorderRequest req) {
        ExamPaper paper = paperRepository.findById(paperId)
                .orElseThrow(() -> new RuntimeException("试卷不存在: " + paperId));
        
        List<PaperQuestion> pqs = paperQuestionRepository.findByPaperIdOrderByQuestionOrderAsc(paperId);
        if (req.getQuestions() != null) {
            for (ReorderRequest.ReorderItem item : req.getQuestions()) {
                pqs.stream()
                   .filter(pq -> pq.getQuestion().getId().equals(item.getQuestionId()))
                   .findFirst()
                   .ifPresent(pq -> {
                       pq.setQuestionOrder(item.getQuestionOrder());
                       paperQuestionRepository.save(pq);
                   });
            }
        }
        return toFullDTO(paper);
    }

    /** 导出试卷为 PDF/DOCX 文档或 ZIP 包 */
    @Transactional(readOnly = true)
    public ExportResult exportPaper(Long id, boolean withAnswer, List<String> types, String answerSheetType) throws IOException {
        PaperDTO paper = findById(id);
        Set<String> projectPaths = new HashSet<>();
        Set<String> answerProjectPaths = new HashSet<>();
        if (withAnswer) {
            for (PaperDTO.PaperQuestionDTO pq : paper.getQuestions()) {
                QuestionDTO q = pq.getQuestion();
                if (q.getAnswerProjectPath() != null && !q.getAnswerProjectPath().trim().isEmpty()) {
                    answerProjectPaths.add(q.getAnswerProjectPath());
                }
            }
        }

        boolean exportPdf = types != null && types.contains("pdf");
        boolean exportDocx = types != null && types.contains("docx");

        byte[] originalPdfBytes = null;
        if (exportPdf) {
            originalPdfBytes = generatePdfBytes(paper, false, projectPaths);
        }

        byte[] originalWordBytes = null;
        if (exportDocx) {
            originalWordBytes = generateWordBytes(paper, false, projectPaths);
        }

        byte[] answerSheetBytes = null;
        byte[] answerSheetWithAnswersBytes = null;
        String answerSheetFilename = "完整学号_姓名_答题纸.docx";
        
        if ("generate".equals(answerSheetType)) {
            answerSheetBytes = generateAnswerSheetBytes(paper, false);
            if (withAnswer) {
                answerSheetWithAnswersBytes = generateAnswerSheetBytes(paper, true);
            }
        } else if ("template".equals(answerSheetType)) {
            org.springframework.core.io.Resource resource = new org.springframework.core.io.ClassPathResource("学号_姓名_答题纸A卷.docx");
            if (resource.exists()) {
                try (java.io.InputStream is = resource.getInputStream();
                     java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream()) {
                    byte[] buffer = new byte[1024];
                    int len;
                    while ((len = is.read(buffer)) > -1) {
                        baos.write(buffer, 0, len);
                    }
                    answerSheetBytes = baos.toByteArray();
                }
                answerSheetFilename = "完整学号_姓名_空白答题纸模板.docx";
            }
        }
        
        boolean hasAnswerSheet = (answerSheetBytes != null);
        boolean hasAnswerSheetWithAnswers = (answerSheetWithAnswersBytes != null);

        // 如果只选了一种格式，且没有答案版，且没有工程代码，且没有答题纸，则直接返回单文件
        if (!withAnswer && projectPaths.isEmpty() && !hasAnswerSheet) {
            if (exportPdf && !exportDocx) {
                return new ExportResult(paper.getTitle() + ".pdf", "application/pdf", originalPdfBytes);
            }
            if (exportDocx && !exportPdf) {
                return new ExportResult(paper.getTitle() + ".docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document", originalWordBytes);
            }
        }

        // 否则打包成包含选定格式和相关工程的 ZIP
        ByteArrayOutputStream zipBaos = new ByteArrayOutputStream();
        try (java.util.zip.ZipOutputStream zos = new java.util.zip.ZipOutputStream(zipBaos)) {
            if (exportPdf) {
                java.util.zip.ZipEntry pdfEntry = new java.util.zip.ZipEntry(paper.getTitle() + ".pdf");
                zos.putNextEntry(pdfEntry);
                zos.write(originalPdfBytes);
                zos.closeEntry();
                
                if (withAnswer) {
                    Set<String> dummyPaths = new HashSet<>();
                    byte[] answerPdfBytes = generatePdfBytes(paper, true, dummyPaths);
                    java.util.zip.ZipEntry answerPdfEntry = new java.util.zip.ZipEntry(paper.getTitle() + "_答案版.pdf");
                    zos.putNextEntry(answerPdfEntry);
                    zos.write(answerPdfBytes);
                    zos.closeEntry();
                }
            }

            if (exportDocx) {
                java.util.zip.ZipEntry docxEntry = new java.util.zip.ZipEntry(paper.getTitle() + ".docx");
                zos.putNextEntry(docxEntry);
                zos.write(originalWordBytes);
                zos.closeEntry();
                
                if (withAnswer) {
                    Set<String> dummyPaths = new HashSet<>();
                    byte[] answerWordBytes = generateWordBytes(paper, true, dummyPaths);
                    java.util.zip.ZipEntry answerDocxEntry = new java.util.zip.ZipEntry(paper.getTitle() + "_答案版.docx");
                    zos.putNextEntry(answerDocxEntry);
                    zos.write(answerWordBytes);
                    zos.closeEntry();
                }
            }

            // 写入各个工程 ZIP
            for (String projectPath : projectPaths) {
                try {
                    byte[] projectZipBytes = com.exam.util.ZipUtils.zipDirectoryToBytes(projectPath);
                    String projectName = new java.io.File(projectPath).getName();
                    java.util.zip.ZipEntry projEntry = new java.util.zip.ZipEntry("题目工程/" + projectName + ".zip");
                    zos.putNextEntry(projEntry);
                    zos.write(projectZipBytes);
                    zos.closeEntry();
                } catch (Exception e) {
                    System.err.println("打包工程失败: " + projectPath);
                }
            }

            // 写入各个答案工程 ZIP
            for (String answerProjectPath : answerProjectPaths) {
                try {
                    byte[] projectZipBytes = com.exam.util.ZipUtils.zipDirectoryToBytes(answerProjectPath);
                    String projectName = new java.io.File(answerProjectPath).getName();
                    java.util.zip.ZipEntry projEntry = new java.util.zip.ZipEntry("答案工程/" + projectName + ".zip");
                    zos.putNextEntry(projEntry);
                    zos.write(projectZipBytes);
                    zos.closeEntry();
                } catch (Exception e) {
                    System.err.println("打包答案工程失败: " + answerProjectPath);
                }
            }

            // 写入答题纸
            if (hasAnswerSheet) {
                java.util.zip.ZipEntry sheetEntry = new java.util.zip.ZipEntry(answerSheetFilename);
                zos.putNextEntry(sheetEntry);
                zos.write(answerSheetBytes);
                zos.closeEntry();
            }
            if (hasAnswerSheetWithAnswers) {
                java.util.zip.ZipEntry sheetAnsEntry = new java.util.zip.ZipEntry("参考答案_答题纸.docx");
                zos.putNextEntry(sheetAnsEntry);
                zos.write(answerSheetWithAnswersBytes);
                zos.closeEntry();
            }
        }

        String zipFilename = paper.getTitle() + (withAnswer ? "_含答案" : "") + (projectPaths.isEmpty() && answerProjectPaths.isEmpty() ? "" : "_含代码工程") + ".zip";
        return new ExportResult(zipFilename, "application/zip", zipBaos.toByteArray());
    }

    /**
     * 根据试卷内容动态生成答题纸 — 保留原模板的页眉、密封线、考生信息栏，动态替换题目区域
     */
    private byte[] generateAnswerSheetBytes(PaperDTO paper, boolean withAnswer) {
        try {
            org.springframework.core.io.Resource resource = new org.springframework.core.io.ClassPathResource("学号_姓名_答题纸A卷.docx");
            if (!resource.exists()) {
                return null;
            }
            try (java.io.InputStream is = resource.getInputStream();
                 XWPFDocument doc = new XWPFDocument(is)) {

                // 1. 尝试将模板中的标题替换为试卷的标题
                for (XWPFParagraph p : doc.getParagraphs()) {
                    String text = p.getText();
                    if (text != null && text.contains("答题纸")) {
                        for (XWPFRun run : p.getRuns()) {
                            String runText = run.getText(0);
                            if (runText != null && runText.contains("JAVA程序设计机考试卷答题纸（A卷）")) {
                                run.setText(runText.replace("JAVA程序设计机考试卷答题纸（A卷）", paper.getTitle() + "答题纸"), 0);
                            }
                        }
                    }
                }

                // 2. 找到第一个题目大项的起始位置（通常是含有“单项选择题”或“一、”的段落）
                int firstSectionParaIdx = -1;
                List<IBodyElement> elements = doc.getBodyElements();
                for (int i = 0; i < elements.size(); i++) {
                    IBodyElement elem = elements.get(i);
                    if (elem instanceof XWPFParagraph) {
                        String text = ((XWPFParagraph) elem).getText().trim();
                        if (text.startsWith("单项选择题") || text.startsWith("一、") || text.contains("本大题共")) {
                            firstSectionParaIdx = i;
                            break;
                        }
                    }
                }

                // 3. 截断模板：删除原有的全部题目区域，同时保留段落上悬浮的“密封线”和“姓名栏”
                if (firstSectionParaIdx != -1) {
                    int oldTableIdx = -1;
                    // 检查前一个元素是否是模板自带的"得分/评卷人"小表格
                    if (firstSectionParaIdx > 0) {
                        IBodyElement prevElem = elements.get(firstSectionParaIdx - 1);
                        if (prevElem instanceof XWPFTable) {
                            XWPFTable prevTable = (XWPFTable) prevElem;
                            String tblText = prevTable.getText();
                            if (tblText != null && tblText.contains("得分") && tblText.contains("评卷人")) {
                                oldTableIdx = firstSectionParaIdx - 1;
                            }
                        }
                    }

                    // 删除 firstSectionParaIdx 之后的所有元素
                    for (int i = elements.size() - 1; i > firstSectionParaIdx; i--) {
                        doc.removeBodyElement(i);
                    }

                    // 清空 firstSectionParaIdx 这一段落的文本内容，但不要删除它！
                    // 因为原模板中的“密封线”和左侧栏是作为悬浮元素(shape)挂载在这个段落上的
                    XWPFParagraph p = (XWPFParagraph) elements.get(firstSectionParaIdx);
                    for (XWPFRun run : p.getRuns()) {
                        int textCount = run.getCTR().sizeOfTArray();
                        for (int i = textCount - 1; i >= 0; i--) {
                            run.getCTR().removeT(i);
                        }
                    }
                    // 清除该锚点段落的自动编号、大纲级别和段落样式，防止在目录中出现幽灵标题（例如空的"一、"）
                    if (p.getCTP().getPPr() != null) {
                        if (p.getCTP().getPPr().isSetNumPr()) p.getCTP().getPPr().unsetNumPr();
                        if (p.getCTP().getPPr().isSetOutlineLvl()) p.getCTP().getPPr().unsetOutlineLvl();
                        if (p.getCTP().getPPr().isSetPStyle()) p.getCTP().getPPr().unsetPStyle();
                    }

                    // 删除发现的旧"得分/评卷人"小表格
                    if (oldTableIdx != -1) {
                        doc.removeBodyElement(oldTableIdx);
                    }
                }

                // 4. 动态生成新的题目区域
                QuestionType[] typeOrder = {
                    QuestionType.SINGLE_CHOICE, QuestionType.MULTIPLE_CHOICE,
                    QuestionType.TRUE_FALSE, QuestionType.FILL_BLANK,
                    QuestionType.SHORT_ANSWER, QuestionType.CODE_READING, QuestionType.PROGRAMMING
                };
                Map<QuestionType, List<PaperDTO.PaperQuestionDTO>> grouped = paper.getQuestions().stream()
                        .collect(Collectors.groupingBy(
                                pq -> pq.getQuestion().getType(),
                                LinkedHashMap::new,
                                Collectors.toList()));

                List<QuestionType> activeSections = new ArrayList<>();
                for (QuestionType t : typeOrder) {
                    if (grouped.containsKey(t) && !grouped.get(t).isEmpty()) {
                        activeSections.add(t);
                    }
                }
                String[] sectionNums = {"一", "二", "三", "四", "五", "六", "七", "八", "九", "十"};

                int sectionIdx = 0;
                for (QuestionType type : activeSections) {
                    List<PaperDTO.PaperQuestionDTO> questions = grouped.get(type);
                    int sectionScore = questions.stream().mapToInt(PaperDTO.PaperQuestionDTO::getScore).sum();
                    String typeLabel = type == QuestionType.CODE_READING
                            ? type.getLabel() + " （需要写出分析过程）"
                            : type.getLabel();

                    // -- 1. 独立悬浮得分/评卷人小表格 (排在标题之前，通过悬浮属性与标题上沿对齐) --
                    XWPFTable scoreTable = doc.createTable(2, 2);
                    org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblPr tblPr = scoreTable.getCTTbl().getTblPr();
                    if (tblPr == null) tblPr = scoreTable.getCTTbl().addNewTblPr();
                    
                    // 设置宽度
                    org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblWidth tblW = tblPr.isSetTblW() ? tblPr.getTblW() : tblPr.addNewTblW();
                    tblW.setW(java.math.BigInteger.valueOf(2000));
                    tblW.setType(org.openxmlformats.schemas.wordprocessingml.x2006.main.STTblWidth.DXA);

                    // 使用 XmlCursor 动态注入悬浮属性，避免依赖底层 Schema 枚举类编译报错
                    // 使其相对于后面的段落（标题）悬浮靠右，并且垂直对齐于文本上沿
                    org.apache.xmlbeans.XmlCursor cursor = tblPr.newCursor();
                    cursor.toEndToken(); // 移动到 tblPr 结束标签前
                    cursor.insertElement(new javax.xml.namespace.QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "tblpPr"));
                    cursor.toPrevToken(); // 进入新创建的 tblpPr 元素内部
                    cursor.insertAttributeWithValue(new javax.xml.namespace.QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "tblpXSpec"), "right");
                    cursor.insertAttributeWithValue(new javax.xml.namespace.QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "tblpY"), "0");
                    cursor.insertAttributeWithValue(new javax.xml.namespace.QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "vertAnchor"), "text");
                    cursor.insertAttributeWithValue(new javax.xml.namespace.QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "horzAnchor"), "margin");
                    cursor.dispose();

                    scoreTable.getRow(0).getCell(0).setText("得分");
                    scoreTable.getRow(0).getCell(1).setText("评卷人");
                    scoreTable.getRow(1).getCell(0).setText("");
                    scoreTable.getRow(1).getCell(1).setText("");

                    for (int r = 0; r < 2; r++) {
                        for (int c = 0; c < 2; c++) {
                            XWPFTableCell cell = scoreTable.getRow(r).getCell(c);
                            cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
                            if (!cell.getParagraphs().isEmpty()) {
                                XWPFParagraph cp = cell.getParagraphs().get(0);
                                cp.setAlignment(ParagraphAlignment.CENTER);
                                if (!cp.getRuns().isEmpty()) {
                                    cp.getRuns().get(0).setFontFamily("黑体");
                                    cp.getRuns().get(0).setFontSize(12); // 小四
                                }
                            }
                        }
                    }
                    // 将第二行（留给老师手写填写的空白行）的行高设置为两倍（约 600 缇），提供足够的书写空间
                    scoreTable.getRow(1).setHeight(600);

                    // -- 2. 大题标题段落 --
                    XWPFParagraph titlePara = doc.createParagraph();
                    titlePara.setSpacingBefore(10);
                    titlePara.setSpacingAfter(10);
                    
                    // 加上大纲级别0（对应标题1），让它能在左侧目录中显示
                    if (titlePara.getCTP().getPPr() == null) titlePara.getCTP().addNewPPr();
                    titlePara.getCTP().getPPr().addNewOutlineLvl().setVal(java.math.BigInteger.valueOf(0));

                    XWPFRun titleRun = titlePara.createRun();
                    titleRun.setText(String.format("%s、%s（本大题共 %d 小题，共 %d 分）",
                            sectionIdx < sectionNums.length ? sectionNums[sectionIdx] : String.valueOf(sectionIdx + 1),
                            typeLabel, questions.size(), sectionScore));
                    titleRun.setBold(true);
                    titleRun.setFontSize(12);
                    titleRun.setFontFamily("黑体");

                    if (type == QuestionType.PROGRAMMING) {
                        String[] prompts = {
                            "测试时所需的数据可直接定义，无需使用 Scanner 进行键盘输入。",
                            "所有代码统一以文本格式粘贴在答题卡上的源代码框格中，如有需要可自行添加表格行数",
                            "Windows 10 截图快捷键 Win+Shift+S，选矩形截图。"
                        };
                        for (int i = 0; i < prompts.length; i++) {
                            XWPFParagraph p = doc.createParagraph();
                            p.setIndentationLeft(480);
                            p.setIndentationHanging(240);
                            
                            XWPFRun bulletRun = p.createRun();
                            bulletRun.setText("● ");
                            bulletRun.setColor("FF0000"); // Red
                            bulletRun.setFontFamily("宋体");
                            bulletRun.setFontSize(10);
    
                            String prompt = prompts[i];
                            if (i == 2) {
                                XWPFRun run1 = p.createRun();
                                run1.setText("Windows 10 截图快捷键 ");
                                run1.setFontFamily("宋体");
                                run1.setFontSize(10.5);
                                
                                XWPFRun run2 = p.createRun();
                                run2.setText("Win+Shift+S");
                                run2.setColor("FF0000");
                                run2.setFontFamily("宋体");
                                run2.setFontSize(10.5);
                                
                                XWPFRun run3 = p.createRun();
                                run3.setText("，选矩形截图。");
                                run3.setFontFamily("宋体");
                                run3.setFontSize(10.5);
                            } else {
                                XWPFRun textRun = p.createRun();
                                textRun.setText(prompt);
                                textRun.setFontFamily("宋体");
                                textRun.setFontSize(10.5);
                            }
                        }
                    }

                    // -- 根据题型生成不同的答题区 --
                    switch (type) {
                        case SINGLE_CHOICE:
                        case MULTIPLE_CHOICE:
                        case TRUE_FALSE:
                            generateChoiceAnswerArea(doc, questions, withAnswer);
                            break;
                        case FILL_BLANK:
                            generateFillBlankAnswerArea(doc, questions, withAnswer);
                            break;
                        case SHORT_ANSWER:
                        case CODE_READING:
                            generateWrittenAnswerArea(doc, questions, false, withAnswer);
                            break;
                        case PROGRAMMING:
                            generateWrittenAnswerArea(doc, questions, true, withAnswer);
                            break;
                    }

                    sectionIdx++;
                }

                ByteArrayOutputStream out = new ByteArrayOutputStream();
                doc.write(out);
                return out.toByteArray();
            }
        } catch (Exception e) {
            System.err.println("生成答题纸失败: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /** 选择题/判断题答题区：题号 + 格式化下划线的空格，每行5个 */
    private void generateChoiceAnswerArea(XWPFDocument doc, List<PaperDTO.PaperQuestionDTO> questions, boolean withAnswer) {
        int perRow = 5;
        int total = questions.size();
        for (int i = 0; i < total; i += perRow) {
            XWPFParagraph rowPara = doc.createParagraph();
            rowPara.setSpacingBetween(1.5);
            int end = Math.min(i + perRow, total);
            for (int j = i; j < end; j++) {
                // 1. 题号部分
                XWPFRun numRun = rowPara.createRun();
                numRun.setText(String.format("%d、", j + 1));
                numRun.setFontSize(12);
                numRun.setFontFamily("宋体");

                // 2. 下划线空格部分 (提供打字的下划线效果)
                XWPFRun blankRun = rowPara.createRun();
                if (withAnswer) {
                    String ans = questions.get(j).getQuestion().getAnswer();
                    if (ans == null) ans = "";
                    ans = ans.trim();
                    if (ans.length() < 4) {
                        int pad = 4 - ans.length();
                        for (int k = 0; k < pad / 2; k++) ans = " " + ans;
                        while (ans.length() < 4) ans = ans + " ";
                    }
                    blankRun.setText(ans);
                    blankRun.setColor("FF0000");
                } else {
                    blankRun.setText("    "); // 4个空格
                }
                blankRun.setUnderline(UnderlinePatterns.SINGLE);
                blankRun.setFontSize(12);
                blankRun.setFontFamily("宋体");

                // 3. 题与题之间的间隔空格
                XWPFRun spaceRun = rowPara.createRun();
                spaceRun.setText(" "); // 1个间隔空格
                spaceRun.setFontSize(12);
                spaceRun.setFontFamily("宋体");
            }
        }
        doc.createParagraph(); // 留空
    }

    /** 填空题答题区：每题一行编号 + 格式化下划线的空格 */
    private void generateFillBlankAnswerArea(XWPFDocument doc, List<PaperDTO.PaperQuestionDTO> questions, boolean withAnswer) {
        for (int i = 0; i < questions.size(); i++) {
            XWPFParagraph p = doc.createParagraph();
            p.setSpacingBetween(2.0);
            
            // 给填空题小题加上大纲级别1（对应标题2）
            if (p.getCTP().getPPr() == null) p.getCTP().addNewPPr();
            p.getCTP().getPPr().addNewOutlineLvl().setVal(java.math.BigInteger.valueOf(1));
            
            // 1. 题号部分
            XWPFRun numRun = p.createRun();
            numRun.setText(String.format("%d、", i + 1));
            numRun.setFontSize(12);
            numRun.setFontFamily("宋体");

            // 2. 下划线空格部分
            XWPFRun blankRun = p.createRun();
            if (withAnswer) {
                String ans = questions.get(i).getQuestion().getAnswer();
                if (ans == null) ans = "";
                ans = ans.trim();
                if (ans.length() < 35) {
                    int pad = 35 - ans.length();
                    for (int k = 0; k < pad / 2; k++) ans = " " + ans;
                    while (ans.length() < 35) ans = ans + " ";
                }
                blankRun.setText(ans);
                blankRun.setColor("FF0000");
            } else {
                blankRun.setText("                                   "); // 35个空格（半行左右）
            }
            blankRun.setUnderline(UnderlinePatterns.SINGLE);
            blankRun.setFontSize(12);
            blankRun.setFontFamily("宋体");
        }
        doc.createParagraph(); // 留空
    }

    /** 简答/程序分析/编程题答题区 */
    private void generateWrittenAnswerArea(XWPFDocument doc, List<PaperDTO.PaperQuestionDTO> questions, boolean isProgramming, boolean withAnswer) {
        for (int i = 0; i < questions.size(); i++) {
            PaperDTO.PaperQuestionDTO pq = questions.get(i);

            // 题号和分值
            XWPFParagraph qPara = doc.createParagraph();
            
            // 给所有主观题小题（简答、程序分析、编程题）加上大纲级别1（对应标题2）
            if (qPara.getCTP().getPPr() == null) qPara.getCTP().addNewPPr();
            qPara.getCTP().getPPr().addNewOutlineLvl().setVal(java.math.BigInteger.valueOf(1));

            XWPFRun qRun = qPara.createRun();
            qRun.setText(String.format("%d.（%d分）", i + 1, pq.getScore()));
            qRun.setBold(true);
            qRun.setFontSize(12);
            qRun.setFontFamily("宋体");

            if (isProgramming) {
                // 编程题特有：运行结果截图区域
                XWPFParagraph screenshotLabel = doc.createParagraph();
                screenshotLabel.setIndentationLeft(240);
                XWPFRun ssRun = screenshotLabel.createRun();
                ssRun.setText("运行结果截图：");
                ssRun.setFontSize(11);
                ssRun.setFontFamily("宋体");

                // 截图框（用单行单列表格模拟边框）
                XWPFTable ssTable = doc.createTable(1, 1);
                ssTable.setWidth("100%");
                XWPFParagraph ssCell = ssTable.getRow(0).getCell(0).getParagraphArray(0);
                ssCell.setSpacingBetween(1.0);
                for (int k = 0; k < 2; k++) {
                    ssTable.getRow(0).getCell(0).addParagraph();
                }
                
                doc.createParagraph(); // 表格后的留空

                // 相关源代码区域
                XWPFParagraph codeLabel = doc.createParagraph();
                codeLabel.setIndentationLeft(240);
                XWPFRun codeRun = codeLabel.createRun();
                codeRun.setText("相关源代码：");
                codeRun.setFontSize(11);
                codeRun.setFontFamily("宋体");

                // 源代码框（两行框，用表格模拟）
                XWPFTable codeTable = doc.createTable(2, 1);
                codeTable.setWidth("100%");
                for (int r = 0; r < 2; r++) {
                    XWPFParagraph codeCell = codeTable.getRow(r).getCell(0).getParagraphArray(0);
                    codeCell.setSpacingBetween(1.0);
                    for (int k = 0; k < 2; k++) {
                        codeTable.getRow(r).getCell(0).addParagraph();
                    }
                }
                
                doc.createParagraph(); // 题与题之间的留空
                
                if (withAnswer) {
                    String ans = pq.getQuestion().getAnswer();
                    if (ans != null && !ans.isEmpty()) {
                        XWPFParagraph ansPara = doc.createParagraph();
                        XWPFRun ansRun = ansPara.createRun();
                        ansRun.setText("【答案】：");
                        ansRun.setColor("FF0000");
                        ansRun.setBold(true);
                        renderMarkdownBlocksToWord(doc, ansPara, ans, "FF0000");
                    }
                }
            } else {
                if (withAnswer) {
                    String ans = pq.getQuestion().getAnswer();
                    if (ans != null && !ans.isEmpty()) {
                        XWPFParagraph ansPara = doc.createParagraph();
                        XWPFRun ansRun = ansPara.createRun();
                        ansRun.setText("【答案】：");
                        ansRun.setColor("FF0000");
                        ansRun.setBold(true);
                        renderMarkdownBlocksToWord(doc, ansPara, ans, "FF0000");
                    }
                } else {
                    // 简答/程序分析：留8行空白
                    for (int k = 0; k < 8; k++) {
                        doc.createParagraph();
                    }
                }
            }
        }
    }

    private byte[] generatePdfBytes(PaperDTO paper, boolean withAnswer, Set<String> projectPaths) throws IOException {
        byte[] wordBytes = generateWordBytes(paper, withAnswer, projectPaths);
        java.io.File tempDocx = java.io.File.createTempFile("exam-", ".docx");
        try {
            java.nio.file.Files.write(tempDocx.toPath(), wordBytes);
            ProcessBuilder pb = new ProcessBuilder(
                    "libreoffice", "--headless", "--convert-to", "pdf",
                    tempDocx.getAbsolutePath(), "--outdir", tempDocx.getParent()
            );
            Process process = pb.start();
            process.waitFor();
            
            String pdfFileName = tempDocx.getName().replace(".docx", ".pdf");
            java.io.File tempPdf = new java.io.File(tempDocx.getParent(), pdfFileName);
            if (!tempPdf.exists()) {
                throw new IOException("PDF conversion failed: output file not found");
            }
            byte[] pdfBytes = java.nio.file.Files.readAllBytes(tempPdf.toPath());
            tempPdf.delete();
            return pdfBytes;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("PDF conversion interrupted", e);
        } finally {
            tempDocx.delete();
        }
    }

    // ========== 自动组卷辅助方法 ==========

    /**
     * 自动组卷按题型抽取，内部拆分处理项目题逻辑
     */
    private int pickQuestions(List<PaperQuestion> result, QuestionType type,
                              Integer countParam, Integer customScore, int fallbackScore,
                              AutoGenerateRequest req, int currentOrder, int totalCount) {
        int count = countParam != null ? countParam : 0;
        if (count <= 0) return currentOrder;

        boolean mustIncludeProject = Boolean.TRUE.equals(req.getMustIncludeProject());
        boolean isLastBatch = (currentOrder + count == totalCount);

        if (mustIncludeProject && isLastBatch) {
            if (count > 1) {
                currentOrder = doPickQuestions(result, type, count - 1, customScore, fallbackScore, req, currentOrder, false);
            }
            currentOrder = doPickQuestions(result, type, 1, customScore, fallbackScore, req, currentOrder, true);
        } else {
            currentOrder = doPickQuestions(result, type, count, customScore, fallbackScore, req, currentOrder, false);
        }
        return currentOrder;
    }

    /**
     * 按题型和难度/来源比例从题库随机抽取指定数量的题目
     */
    private int doPickQuestions(List<PaperQuestion> result, QuestionType type,
                              int count, Integer customScore, int fallbackScore,
                              AutoGenerateRequest req, int currentOrder, boolean requireProject) {
        if (count <= 0) return currentOrder;

        // 获取候选题目
        List<Question> candidates;
        if (req.getChapters() != null && !req.getChapters().isEmpty()) {
            candidates = new ArrayList<>();
            for (String chapter : req.getChapters()) {
                candidates.addAll(questionRepository.findByTypeAndChapterName(type, chapter));
            }
        } else {
            candidates = questionRepository.findByType(type);
        }

        if (requireProject) {
            candidates = candidates.stream()
                    .filter(q -> q.getProjectPath() != null && !q.getProjectPath().trim().isEmpty())
                    .collect(Collectors.toList());
        } else {
            candidates = candidates.stream()
                    .filter(q -> q.getProjectPath() == null || q.getProjectPath().trim().isEmpty())
                    .collect(Collectors.toList());
        }

        if (candidates.isEmpty()) {
            throw new RuntimeException("题库中 [" + type.getLabel() + "] " + (requireProject ? "项目题" : "非项目题") + "不足，无法组卷");
        }

        // ===== 按来源比例分组抽取 =====
        int textbookPercent = req.getTextbookPercent() != null ? req.getTextbookPercent() : 80;
        int networkPercent = req.getNetworkPercent() != null ? req.getNetworkPercent() : 20;

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

        // 按要求：选择题（单选、多选）与填空题尽量按章节顺序出题
        if (type == QuestionType.SINGLE_CHOICE || type == QuestionType.MULTIPLE_CHOICE || type == QuestionType.FILL_BLANK) {
            picked.sort(java.util.Comparator.comparingInt(q -> 
                    (q.getChapter() != null && q.getChapter().getSortOrder() != null) ? q.getChapter().getSortOrder() : 9999));
        }

        // 构建 PaperQuestion
        for (Question q : picked) {
            currentOrder++;
            int score = customScore != null ? customScore : (q.getDefaultScore() != null ? q.getDefaultScore() : fallbackScore);
            PaperQuestion pq = PaperQuestion.builder()
                    .question(q)
                    .questionOrder(currentOrder)
                    .score(score)
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

    /**
     * 专门用于编程题的按难度抽取逻辑：
     * - 1题：1中等
     * - 2题：1简单，1中等
     * - 3题或以上：1中等，1困难，其余简单
     */
    private int pickProgrammingQuestions(List<PaperQuestion> result, Integer countParam, AutoGenerateRequest req, int currentOrder, int totalCount) {
        int count = countParam != null ? countParam : 0;
        if (count <= 0) return currentOrder;

        boolean mustIncludeProject = Boolean.TRUE.equals(req.getMustIncludeProject());
        boolean isLastBatch = (currentOrder + count == totalCount);

        if (Boolean.TRUE.equals(req.getSpecificProgrammingChapters()) && req.getProgrammingQuestionChapters() != null
                && req.getProgrammingQuestionChapters().size() == count) {
            
            List<Difficulty> requiredDiffs = new ArrayList<>();
            if (count == 1) {
                requiredDiffs.add(Difficulty.MEDIUM);
            } else if (count == 2) {
                requiredDiffs.add(Difficulty.EASY);
                requiredDiffs.add(Difficulty.MEDIUM);
            } else {
                for (int i = 0; i < count - 2; i++) {
                    requiredDiffs.add(Difficulty.EASY);
                }
                requiredDiffs.add(Difficulty.MEDIUM);
                requiredDiffs.add(Difficulty.HARD);
            }
            Collections.shuffle(requiredDiffs);

            List<Question> picked = new ArrayList<>();
            Set<Long> pickedIds = new HashSet<>();

            for (int i = 0; i < count; i++) {
                String chapter = req.getProgrammingQuestionChapters().get(i);
                Difficulty targetDiff = requiredDiffs.get(i);
                boolean requireProject = mustIncludeProject && isLastBatch && (i == count - 1);

                List<Question> chapterCandidates = questionRepository.findByTypeAndChapterName(QuestionType.PROGRAMMING, chapter)
                    .stream().filter(q -> !pickedIds.contains(q.getId())).collect(Collectors.toList());

                if (requireProject) {
                    chapterCandidates = chapterCandidates.stream()
                            .filter(q -> q.getProjectPath() != null && !q.getProjectPath().trim().isEmpty())
                            .collect(Collectors.toList());
                } else {
                    chapterCandidates = chapterCandidates.stream()
                            .filter(q -> q.getProjectPath() == null || q.getProjectPath().trim().isEmpty())
                            .collect(Collectors.toList());
                }

                if (chapterCandidates.isEmpty()) {
                    throw new RuntimeException("题库中 [" + chapter + "] 章节的" + (requireProject ? "项目题" : "非项目题") + "不足，无法满足出题要求");
                }

                Question chosenQ = null;
                List<Question> exactDiff = chapterCandidates.stream().filter(q -> q.getDifficulty() == targetDiff).collect(Collectors.toList());
                if (!exactDiff.isEmpty()) {
                    Collections.shuffle(exactDiff);
                    chosenQ = exactDiff.get(0);
                } else if (!chapterCandidates.isEmpty()) {
                    Collections.shuffle(chapterCandidates);
                    chosenQ = chapterCandidates.get(0);
                } else {
                    throw new RuntimeException("题库中 [编程题] 题目不足，无法满足章节要求");
                }
                picked.add(chosenQ);
                pickedIds.add(chosenQ.getId());
            }

            for (Question q : picked) {
                currentOrder++;
                int score = req.getProgrammingScore() != null ? req.getProgrammingScore() : 
                            (q.getDefaultScore() != null ? q.getDefaultScore() : 
                            (q.getDifficulty() == Difficulty.EASY ? 10 : 20));
                PaperQuestion pq = PaperQuestion.builder()
                        .question(q)
                        .questionOrder(currentOrder)
                        .score(score)
                        .build();
                result.add(pq);
            }
            return currentOrder;
        }

        // 非指定章节逻辑
        if (mustIncludeProject && isLastBatch) {
            if (count > 1) {
                currentOrder = doPickProgrammingQuestions(result, count - 1, req, currentOrder, false);
            }
            currentOrder = doPickProgrammingQuestions(result, 1, req, currentOrder, true);
        } else {
            currentOrder = doPickProgrammingQuestions(result, count, req, currentOrder, false);
        }
        return currentOrder;
    }

    private int doPickProgrammingQuestions(List<PaperQuestion> result, int count, AutoGenerateRequest req, int currentOrder, boolean requireProject) {
        if (count <= 0) return currentOrder;

        List<Question> candidates;
        if (req.getChapters() != null && !req.getChapters().isEmpty()) {
            candidates = new ArrayList<>();
            for (String chapter : req.getChapters()) {
                candidates.addAll(questionRepository.findByTypeAndChapterName(QuestionType.PROGRAMMING, chapter));
            }
        } else {
            candidates = questionRepository.findByType(QuestionType.PROGRAMMING);
        }

        if (requireProject) {
            candidates = candidates.stream()
                    .filter(q -> q.getProjectPath() != null && !q.getProjectPath().trim().isEmpty())
                    .collect(Collectors.toList());
        } else {
            candidates = candidates.stream()
                    .filter(q -> q.getProjectPath() == null || q.getProjectPath().trim().isEmpty())
                    .collect(Collectors.toList());
        }

        if (candidates.isEmpty()) {
            throw new RuntimeException("题库中 [编程题] " + (requireProject ? "项目题" : "非项目题") + "不足，无法组卷");
        }

        Map<Difficulty, List<Question>> byDiff = candidates.stream()
                .collect(Collectors.groupingBy(Question::getDifficulty));
        List<Question> easyPool = byDiff.getOrDefault(Difficulty.EASY, new ArrayList<>());
        List<Question> mediumPool = byDiff.getOrDefault(Difficulty.MEDIUM, new ArrayList<>());
        List<Question> hardPool = byDiff.getOrDefault(Difficulty.HARD, new ArrayList<>());

        int easyNeeded = 0;
        int mediumNeeded = 0;
        int hardNeeded = 0;

        if (count == 1) {
            mediumNeeded = 1;
        } else if (count == 2) {
            easyNeeded = 1;
            mediumNeeded = 1;
        } else {
            mediumNeeded = 1;
            hardNeeded = 1;
            easyNeeded = count - 2;
        }

        List<Question> picked = new ArrayList<>();
        picked.addAll(randomPick(easyPool, easyNeeded));
        picked.addAll(randomPick(mediumPool, mediumNeeded));
        picked.addAll(randomPick(hardPool, hardNeeded));

        // 如果某种难度不足，从其他补充
        if (picked.size() < count) {
            Set<Long> pickedIds = picked.stream().map(Question::getId).collect(Collectors.toSet());
            List<Question> remaining = candidates.stream()
                    .filter(q -> !pickedIds.contains(q.getId()))
                    .collect(Collectors.toList());
            Collections.shuffle(remaining);
            int need = count - picked.size();
            picked.addAll(remaining.subList(0, Math.min(need, remaining.size())));
        }

        for (Question q : picked) {
            currentOrder++;
            // 编程题分值：优先使用自定义分数，否则题库的默认分数，否则根据难度判定(简单10，中等/困难20)
            int score = req.getProgrammingScore() != null ? req.getProgrammingScore() : 
                        (q.getDefaultScore() != null ? q.getDefaultScore() : 
                        (q.getDifficulty() == Difficulty.EASY ? 10 : 20));
            
            PaperQuestion pq = PaperQuestion.builder()
                    .question(q)
                    .questionOrder(currentOrder)
                    .score(score)
                    .build();
            result.add(pq);
        }

        return currentOrder;
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

    private void renderMarkdownToPdfParagraph(Paragraph paragraph, String text, Font normalFont, Font boldFont, Font italicFont, Font codeFont) {
        if (text == null || text.isEmpty()) return;

        String[] lines = text.split("\n");
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            if (i > 0) {
                paragraph.add(Chunk.NEWLINE);
            }

            java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("(\\*\\*(.*?)\\*\\*)|(`(.*?)`)|(\\*([^\\*]+)\\*)");
            java.util.regex.Matcher matcher = pattern.matcher(line);

            int lastEnd = 0;
            while (matcher.find()) {
                if (matcher.start() > lastEnd) {
                    paragraph.add(new Chunk(line.substring(lastEnd, matcher.start()), normalFont));
                }

                if (matcher.group(1) != null) {
                    paragraph.add(new Chunk(matcher.group(2), boldFont));
                } else if (matcher.group(3) != null) {
                    paragraph.add(new Chunk(matcher.group(4), codeFont));
                } else if (matcher.group(5) != null) {
                    paragraph.add(new Chunk(matcher.group(6), italicFont));
                }
                lastEnd = matcher.end();
            }

            if (lastEnd < line.length()) {
                paragraph.add(new Chunk(line.substring(lastEnd), normalFont));
            }
        }
    }

    private byte[] generateWordBytes(PaperDTO paper, boolean withAnswer, Set<String> projectPaths) throws IOException {
        try (XWPFDocument doc = new XWPFDocument()) {
            org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSectPr sectPr = doc.getDocument().getBody().isSetSectPr() 
                ? doc.getDocument().getBody().getSectPr() 
                : doc.getDocument().getBody().addNewSectPr();
            org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPageMar pageMar = sectPr.isSetPgMar() 
                ? sectPr.getPgMar() 
                : sectPr.addNewPgMar();
            pageMar.setTop(java.math.BigInteger.valueOf(1134));
            pageMar.setBottom(java.math.BigInteger.valueOf(1134));
            pageMar.setLeft(java.math.BigInteger.valueOf(1134));
            pageMar.setRight(java.math.BigInteger.valueOf(1134));
            pageMar.setHeader(java.math.BigInteger.valueOf(851));
            pageMar.setFooter(java.math.BigInteger.valueOf(992));
            pageMar.setGutter(java.math.BigInteger.valueOf(0));

            // 标题
            XWPFParagraph titlePara = doc.createParagraph();
            titlePara.setAlignment(ParagraphAlignment.CENTER);
            XWPFRun titleRun = titlePara.createRun();
            titleRun.setText(paper.getTitle() + (withAnswer ? "（答案版）" : ""));
            titleRun.setBold(true);
            titleRun.setFontSize(18);
            titleRun.setFontFamily("黑体");

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
            String[] sectionNums = {"一", "二", "三", "四", "五", "六", "七"};
            int sectionIdx = 0;
            QuestionType[] typeOrder = {
                QuestionType.SINGLE_CHOICE, QuestionType.MULTIPLE_CHOICE,
                QuestionType.TRUE_FALSE, QuestionType.FILL_BLANK,
                QuestionType.SHORT_ANSWER, QuestionType.CODE_READING, QuestionType.PROGRAMMING
            };

            for (QuestionType type : typeOrder) {
                List<PaperDTO.PaperQuestionDTO> questions = grouped.get(type);
                if (questions == null || questions.isEmpty()) continue;

                // 大题标题
                int sectionScore = questions.stream().mapToInt(PaperDTO.PaperQuestionDTO::getScore).sum();
                XWPFParagraph sectionPara = doc.createParagraph();
                XWPFRun sectionRun = sectionPara.createRun();
                String typeLabel = type == QuestionType.CODE_READING ? type.getLabel() + " （需要写出分析过程）" : type.getLabel();
                sectionRun.setText(String.format("%s、%s（共%d题，共%d分）",
                        sectionIdx < sectionNums.length ? sectionNums[sectionIdx] : String.valueOf(sectionIdx + 1),
                        typeLabel, questions.size(), sectionScore));
                sectionRun.setFontSize(12);
                sectionRun.setFontFamily("黑体");

                if (type == QuestionType.PROGRAMMING) {
                    String[] prompts = {
                        "测试时所需的数据可直接定义，无需使用 Scanner 进行键盘输入。",
                        "所有代码统一以文本格式粘贴在答题卡上的源代码框格中，如有需要可自行添加表格行数",
                        "Windows 10 截图快捷键 Win+Shift+S，选矩形截图。"
                    };
                    for (int i = 0; i < prompts.length; i++) {
                        XWPFParagraph p = doc.createParagraph();
                        p.setIndentationLeft(480);
                        p.setIndentationHanging(240);
                        
                        XWPFRun bulletRun = p.createRun();
                        bulletRun.setText("● ");
                        bulletRun.setColor("FF0000"); // Red
                        bulletRun.setFontFamily("宋体");
                        bulletRun.setFontSize(10); // slightly smaller for bullet

                        String prompt = prompts[i];
                        if (i == 2) {
                            XWPFRun run1 = p.createRun();
                            run1.setText("Windows 10 截图快捷键 ");
                            run1.setFontFamily("宋体");
                            run1.setFontSize(10.5);
                            
                            XWPFRun run2 = p.createRun();
                            run2.setText("Win+Shift+S");
                            run2.setColor("FF0000");
                            run2.setFontFamily("宋体");
                            run2.setFontSize(10.5);
                            
                            XWPFRun run3 = p.createRun();
                            run3.setText("，选矩形截图。");
                            run3.setFontFamily("宋体");
                            run3.setFontSize(10.5);
                        } else {
                            XWPFRun textRun = p.createRun();
                            textRun.setText(prompt);
                            textRun.setFontFamily("宋体");
                            textRun.setFontSize(10.5);
                        }
                    }
                }

                // 逐题输出
                int qNum = 1;
                for (PaperDTO.PaperQuestionDTO pq : questions) {
                    QuestionDTO q = pq.getQuestion();
                    if (q.getProjectPath() != null && !q.getProjectPath().trim().isEmpty()) {
                        projectPaths.add(q.getProjectPath());
                    }
                    XWPFParagraph qPara = doc.createParagraph();
                    String content = q.getContent();
                    if (q.getProjectPath() != null && !q.getProjectPath().trim().isEmpty()) {
                        String projectName = new java.io.File(q.getProjectPath()).getName();
                        content += "\n（请在已有工程 " + projectName + " 的基础上修改）";
                    }
                    
                    String fullText = String.format("%d. (%d分) %s", qNum++, pq.getScore(), content);
                    renderMarkdownBlocksToWord(doc, qPara, fullText, null);

                    // 选择题输出选项
                    if (q.getOptions() != null && !q.getOptions().isEmpty()
                            && (type == QuestionType.SINGLE_CHOICE || type == QuestionType.MULTIPLE_CHOICE)) {
                        String opts = q.getOptions();
                        boolean parsedSuccessfully = false;
                        try {
                            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                            if (opts.trim().startsWith("\"")) {
                                opts = mapper.readValue(opts, String.class);
                            }
                            List<Map<String, Object>> optList = mapper.readValue(opts, new com.fasterxml.jackson.core.type.TypeReference<List<Map<String, Object>>>() {});
                            for (Map<String, Object> opt : optList) {
                                String label = String.valueOf(opt.get("label"));
                                String text = String.valueOf(opt.get("text"));
                                XWPFParagraph optPara = doc.createParagraph();
                                optPara.setIndentationLeft(720); // 缩进
                                renderMarkdownBlocksToWord(doc, optPara, label + ". " + text, null);
                            }
                            parsedSuccessfully = true;
                        } catch (Exception e) {
                        }

                        if (!parsedSuccessfully) {
                            String[] labels = {"A", "B", "C", "D", "E", "F"};
                            String[] parts = opts.split("\"text\"\\s*:\\s*\"");
                            for (int i = 1; i < parts.length && i <= labels.length; i++) {
                                String text = parts[i].split("\"")[0];
                                XWPFParagraph optPara = doc.createParagraph();
                                optPara.setIndentationLeft(720); // 缩进
                                renderMarkdownBlocksToWord(doc, optPara, labels[i - 1] + ". " + text, null);
                            }
                        }
                    }

                    if (withAnswer) {
                        XWPFParagraph ansPara = doc.createParagraph();
                        XWPFRun ansRun = ansPara.createRun();
                        ansRun.setColor("FF0000"); // 红色
                        ansRun.setBold(true);
                        ansRun.setFontFamily("宋体");
                        ansRun.setFontSize(10.5);
                        ansRun.setText("【答案】: ");
                        
                        renderMarkdownBlocksToWord(doc, ansPara, q.getAnswer() != null ? q.getAnswer() : "略", "FF0000");
                        
                        if (q.getExplanation() != null && !q.getExplanation().trim().isEmpty()) {
                            XWPFParagraph expPara = doc.createParagraph();
                            XWPFRun expRun = expPara.createRun();
                            expRun.setColor("0000FF"); // 蓝色
                            expRun.setBold(true);
                            expRun.setFontFamily("宋体");
                            expRun.setFontSize(10.5);
                            expRun.setText("【解析】: ");
                            
                            renderMarkdownBlocksToWord(doc, expPara, q.getExplanation(), "0000FF");
                        }
                        // doc.createParagraph().createRun().setText("");
                    } else {
                        if (type == QuestionType.SHORT_ANSWER || type == QuestionType.CODE_READING || type == QuestionType.PROGRAMMING) {
                            for (int i = 0; i < 0; i++) {
                                doc.createParagraph().createRun().setText("");
                            }
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

    private void renderMarkdownBlocksToWord(XWPFDocument doc, XWPFParagraph initialParagraph, String text, String defaultColor) {
        if (text == null || text.isEmpty()) return;

        String[] lines = text.split("\n");
        boolean inCodeBlock = false;
        XWPFParagraph currentPara = initialParagraph;

        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];

            if (line.trim().startsWith("```")) {
                inCodeBlock = !inCodeBlock;
                if (inCodeBlock) {
                    currentPara = doc.createParagraph();
                    org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPPr ppr = currentPara.getCTP().getPPr();
                    if (ppr == null) ppr = currentPara.getCTP().addNewPPr();
                    org.openxmlformats.schemas.wordprocessingml.x2006.main.CTShd shd = ppr.isSetShd() ? ppr.getShd() : ppr.addNewShd();
                    shd.setVal(org.openxmlformats.schemas.wordprocessingml.x2006.main.STShd.CLEAR);
                    shd.setColor("auto");
                    shd.setFill("F4F4F4");
                    currentPara.setSpacingBefore(100);
                    currentPara.setSpacingAfter(100);
                } else {
                    currentPara = doc.createParagraph();
                }
                continue;
            }

            if (inCodeBlock) {
                if (!currentPara.getRuns().isEmpty()) {
                    currentPara.createRun().addBreak();
                }
                XWPFRun run = currentPara.createRun();
                run.setText(line);
                run.setFontFamily("Consolas");
                run.setFontSize(10);
                if (defaultColor != null) {
                    run.setColor(defaultColor);
                }
            } else {
                if (i > 0 && !currentPara.getRuns().isEmpty() && !line.trim().isEmpty()) {
                    currentPara.createRun().addBreak();
                } else if (i > 0 && line.trim().isEmpty()) {
                     currentPara.createRun().addBreak();
                     continue;
                }
                
                boolean isHeading = false;
                String processedLine = line;
                if (processedLine.trim().startsWith("#")) {
                    processedLine = processedLine.replaceFirst("^#+\\s*", "");
                    isHeading = true;
                }
                
                java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("(\\*\\*(.*?)\\*\\*)|(`(.*?)`)|(\\*([^\\*]+)\\*)");
                java.util.regex.Matcher matcher = pattern.matcher(processedLine);

                int lastEnd = 0;
                while (matcher.find()) {
                    if (matcher.start() > lastEnd) {
                        XWPFRun run = currentPara.createRun();
                        run.setText(processedLine.substring(lastEnd, matcher.start()));
                        run.setFontFamily("宋体");
                        run.setFontSize(isHeading ? 12 : 10.5);
                        if (isHeading) run.setBold(true);
                        if (defaultColor != null) run.setColor(defaultColor);
                    }

                    XWPFRun run = currentPara.createRun();
                    if (matcher.group(1) != null) {
                        run.setText(matcher.group(2));
                        run.setBold(true);
                        run.setFontFamily("宋体");
                    } else if (matcher.group(3) != null) {
                        run.setText(matcher.group(4));
                        run.setFontFamily("Consolas");
                        if (isHeading) run.setBold(true);
                    } else if (matcher.group(5) != null) {
                        run.setText(matcher.group(6));
                        run.setItalic(true);
                        run.setFontFamily("宋体");
                        if (isHeading) run.setBold(true);
                    }
                    run.setFontSize(isHeading ? 12 : 10.5);
                    if (defaultColor != null) run.setColor(defaultColor);
                    
                    lastEnd = matcher.end();
                }

                if (lastEnd < processedLine.length()) {
                    XWPFRun run = currentPara.createRun();
                    run.setText(processedLine.substring(lastEnd));
                    run.setFontFamily("宋体");
                    run.setFontSize(isHeading ? 12 : 10.5);
                    if (isHeading) run.setBold(true);
                    if (defaultColor != null) run.setColor(defaultColor);
                }
            }
        }
    }
}
