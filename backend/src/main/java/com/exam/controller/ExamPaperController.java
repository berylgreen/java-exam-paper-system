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

    /** 删除试卷 */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        paperService.delete(id);
        return ResponseEntity.ok().build();
    }

    /** 导出试卷为 Word */
    @GetMapping("/{id}/export")
    public ResponseEntity<byte[]> export(@PathVariable("id") Long id) throws IOException {
        byte[] content = paperService.exportToWord(id);
        String filename = URLEncoder.encode("试卷_" + id + ".docx", StandardCharsets.UTF_8);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + filename)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(content);
    }
}
