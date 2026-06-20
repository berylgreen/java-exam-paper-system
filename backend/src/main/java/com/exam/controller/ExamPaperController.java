package com.exam.controller;

import com.exam.dto.*;
import com.exam.service.ExamPaperService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.List;

/**
 * 试卷管理 API
 */
@Slf4j
@RestController
@RequestMapping("/api/papers")
@RequiredArgsConstructor
public class ExamPaperController {

    private final ExamPaperService paperService;

    /** 获取所有试卷 */
    @GetMapping
    public List<PaperDTO> list() {
        log.info("[API] GET /api/papers 开始");
        List<PaperDTO> result = paperService.findAll();
        log.info("[API] GET /api/papers 完成, 返回 {} 条", result.size());
        return result;
    }

    /** 获取试卷详情 */
    @GetMapping("/{id}")
    public PaperDTO getById(@PathVariable("id") Long id) {
        log.info("[API] GET /api/papers/{} 开始", id);
        PaperDTO result = paperService.findById(id);
        log.info("[API] GET /api/papers/{} 完成", id);
        return result;
    }

    /** 手动创建试卷 */
    @PostMapping
    public PaperDTO create(@RequestBody CreatePaperRequest request) {
        return paperService.create(request);
    }

    /** 自动组卷 */
    @PostMapping("/auto-generate")
    public ResponseEntity<Object> autoGenerate(@RequestBody AutoGenerateRequest request) {
        log.info("[API] POST /api/papers/auto-generate 开始, title={}", request.getTitle());
        try {
            PaperDTO result = paperService.autoGenerate(request);
            log.info("[API] POST /api/papers/auto-generate 完成, paperId={}", result.getId());
            // 手动序列化测试
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            mapper.findAndRegisterModules();
            String json = mapper.writeValueAsString(result);
            log.info("[API] JSON 序列化成功, 长度={}", json.length());
            return ResponseEntity.ok()
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                    .body(json);
        } catch (Exception e) {
            log.error("[API] auto-generate 异常", e);
            return ResponseEntity.status(500).body(java.util.Map.of("error", e.getMessage()));
        }
    }

    /** 自动组卷预览 (不保存到数据库) */
    @PostMapping("/preview-generate")
    public ResponseEntity<Object> previewGenerate(@RequestBody AutoGenerateRequest request) {
        log.info("[API] POST /api/papers/preview-generate 开始, title={}", request.getTitle());
        try {
            PaperDTO result = paperService.previewGenerate(request);
            log.info("[API] POST /api/papers/preview-generate 完成, 共{}题", 
                    result.getQuestions() != null ? result.getQuestions().size() : 0);
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            mapper.findAndRegisterModules();
            String json = mapper.writeValueAsString(result);
            return ResponseEntity.ok()
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                    .body(json);
        } catch (Exception e) {
            log.error("[API] preview-generate 异常", e);
            return ResponseEntity.status(500).body(java.util.Map.of("error", e.getMessage()));
        }
    }

    /** 保存自动组卷结果到数据库 */
    @PostMapping("/save-generated")
    public ResponseEntity<Object> saveGenerated(@RequestBody SaveGeneratedRequest request) {
        log.info("[API] POST /api/papers/save-generated 开始, title={}", request.getTitle());
        try {
            PaperDTO result = paperService.saveGenerated(request);
            log.info("[API] POST /api/papers/save-generated 完成, paperId={}", result.getId());
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            mapper.findAndRegisterModules();
            String json = mapper.writeValueAsString(result);
            return ResponseEntity.ok()
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                    .body(json);
        } catch (Exception e) {
            log.error("[API] save-generated 异常", e);
            return ResponseEntity.status(500).body(java.util.Map.of("error", e.getMessage()));
        }
    }

    /** 删除试卷 */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        paperService.delete(id);
        return ResponseEntity.ok().build();
    }

    /** 批量删除试卷 */
    @DeleteMapping("/batch")
    public ResponseEntity<Void> batchDelete(@RequestBody List<Long> ids) {
        paperService.batchDelete(ids);
        return ResponseEntity.ok().build();
    }

    /** 替换试卷中的题目 */
    @PutMapping("/{id}/replace-question")
    public ResponseEntity<Object> replaceQuestion(@PathVariable("id") Long id, @RequestBody ReplaceQuestionRequest request) {
        log.info("[API] PUT /api/papers/{}/replace-question 开始, oldQ={}, newQ={}", id, request.getOldQuestionId(), request.getNewQuestionId());
        try {
            PaperDTO result = paperService.replaceQuestion(id, request);
            log.info("[API] PUT /api/papers/{}/replace-question 完成", id);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("[API] replace-question 异常", e);
            return ResponseEntity.status(500).body(java.util.Map.of("error", e.getMessage()));
        }
    }

    /** 重新排序试卷中的题目 */
    @PutMapping("/{id}/reorder")
    public ResponseEntity<Object> reorderQuestions(@PathVariable("id") Long id, @RequestBody ReorderRequest request) {
        log.info("[API] PUT /api/papers/{}/reorder 开始", id);
        try {
            PaperDTO result = paperService.reorderQuestions(id, request);
            log.info("[API] PUT /api/papers/{}/reorder 完成", id);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("[API] reorder 异常", e);
            return ResponseEntity.status(500).body(java.util.Map.of("error", e.getMessage()));
        }
    }

    /** 导出试卷为 PDF/DOCX 或 ZIP */
    @GetMapping("/{id}/export")
    public ResponseEntity<byte[]> export(@PathVariable("id") Long id,
                                         @RequestParam(value = "withAnswer", defaultValue = "false") boolean withAnswer,
                                         @RequestParam(value = "types", defaultValue = "docx,pdf") String types) throws IOException {
        List<String> exportTypes = Arrays.asList(types.toLowerCase().split(","));
        ExportResult exportResult = paperService.exportPaper(id, withAnswer, exportTypes);
        String filename = URLEncoder.encode(exportResult.getFilename(), StandardCharsets.UTF_8);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + filename)
                .contentType(MediaType.parseMediaType(exportResult.getContentType()))
                .body(exportResult.getData());
    }
}
