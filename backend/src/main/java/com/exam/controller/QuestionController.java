package com.exam.controller;

import com.exam.dto.QuestionDTO;
import com.exam.dto.QuestionOptimizeRequest;
import com.exam.dto.QuestionOptimizeResponse;
import com.exam.dto.SyncAnswerRequest;
import com.exam.dto.SyncAnswerResponse;
import com.exam.enums.Difficulty;
import com.exam.enums.QuestionType;
import com.exam.service.QuestionOptimizationService;
import com.exam.service.QuestionService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * 题目管理 API
 */
@Slf4j
@RestController
@RequestMapping("/api/questions")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;
    private final QuestionOptimizationService questionOptimizationService;
    private final ObjectMapper objectMapper;

    /** 分页条件查询题目 */
    @GetMapping
    public Page<QuestionDTO> list(
            @RequestParam(value = "type", required = false) QuestionType type,
            @RequestParam(value = "chapterId", required = false) Long chapterId,
            @RequestParam(value = "difficulty", required = false) Difficulty difficulty,
            @RequestParam(value = "source", required = false) String source,
            @RequestParam(value = "favorite", required = false) Boolean favorite,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size) {
        return questionService.findByFilters(type, chapterId, difficulty, source, favorite, keyword,
                PageRequest.of(page, size, Sort.by("id")));
    }

    /** 获取单题 */
    @GetMapping("/{id}")
    public QuestionDTO getById(@PathVariable("id") Long id) {
        return questionService.findById(id);
    }

    /** 新增题目 */
    @PostMapping
    public QuestionDTO create(@RequestBody QuestionDTO dto) {
        return questionService.create(dto);
    }

    /** 修改题目 */
    @PutMapping("/{id}")
    public QuestionDTO update(@PathVariable("id") Long id, @RequestBody QuestionDTO dto) {
        return questionService.update(id, dto);
    }

    /** AI 优化题目预览 */
    @PostMapping("/optimize-preview")
    public QuestionOptimizeResponse optimizePreview(@RequestBody QuestionOptimizeRequest request) {
        return questionOptimizationService.optimizePreview(request);
    }

    /** 直接对题目进行 AI 优化并保存 */
    @PostMapping("/{id}/optimize")
    public QuestionDTO optimizeAndSave(@PathVariable("id") Long id, @RequestBody Map<String, String> payload) {
        QuestionDTO original = questionService.findById(id);
        QuestionOptimizeRequest req = new QuestionOptimizeRequest();
        req.setQuestion(original);
        req.setPrompt(payload.getOrDefault("prompt", "请将题干表述更清晰，并补充更严谨的答案与解析"));
        QuestionOptimizeResponse res = questionOptimizationService.optimizePreview(req);
        QuestionDTO optimized = res.getOptimizedQuestion();
        QuestionDTO updated = questionService.update(id, optimized);
        log.info("单题 AI 优化成功，题目ID: {}", id);
        return updated;
    }


    /** 删除题目 */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        questionService.delete(id);
        return ResponseEntity.ok().build();
    }

    /** 切换收藏状态 */
    @PutMapping("/{id}/favorite")
    public ResponseEntity<Void> toggleFavorite(@PathVariable("id") Long id, @RequestParam("favorite") Boolean favorite) {
        questionService.toggleFavorite(id, favorite);
        return ResponseEntity.ok().build();
    }

    /** 批量删除题目 */
    @DeleteMapping("/batch")
    public ResponseEntity<Void> deleteBatch(@RequestBody List<Long> ids) {
        questionService.deleteBatch(ids);
        return ResponseEntity.ok().build();
    }

    public static class BatchScoreRequest {
        private List<Long> ids;
        private Integer score;
        public List<Long> getIds() { return ids; }
        public void setIds(List<Long> ids) { this.ids = ids; }
        public Integer getScore() { return score; }
        public void setScore(Integer score) { this.score = score; }
    }

    public static class BatchOptimizeRequest {
        private List<Long> ids;
        private String prompt;
        public List<Long> getIds() { return ids; }
        public void setIds(List<Long> ids) { this.ids = ids; }
        public String getPrompt() { return prompt; }
        public void setPrompt(String prompt) { this.prompt = prompt; }
    }

    /** 批量修改分值 */
    @PutMapping("/batch/score")
    public ResponseEntity<Void> batchUpdateScore(@RequestBody BatchScoreRequest request) {
        questionService.batchUpdateScore(request.getIds(), request.getScore());
        return ResponseEntity.ok().build();
    }

    /** 批量 AI 优化 */
    @PostMapping("/batch/optimize")
    public ResponseEntity<Void> batchOptimize(@RequestBody BatchOptimizeRequest request) {
        String prompt = request.getPrompt();
        if (prompt == null || prompt.trim().isEmpty()) {
            prompt = "请将题干表述更清晰，并补充更严谨的答案与解析";
        }
        for (Long id : request.getIds()) {
            try {
                QuestionDTO original = questionService.findById(id);
                QuestionOptimizeRequest req = new QuestionOptimizeRequest();
                req.setQuestion(original);
                req.setPrompt(prompt);
                QuestionOptimizeResponse res = questionOptimizationService.optimizePreview(req);
                QuestionDTO optimized = res.getOptimizedQuestion();
                questionService.update(id, optimized);
                log.info("批量优化单题成功，题目ID: {}", id);
            } catch (Exception e) {
                // 忽略单题的优化失败，继续下一个
                log.error("批量优化单题失败，题目ID: {}", id, e);
            }
            try {
                // 增加延时，防止触发第三方 AI 接口的速率限制 (Rate Limit) 或并发限制
                Thread.sleep(1500);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            }
        }
        return ResponseEntity.ok().build();
    }


    /** 下载题目的关联工程或答案工程 ZIP */
    @GetMapping("/{id}/download-project")
    public ResponseEntity<byte[]> downloadProject(@PathVariable("id") Long id, @RequestParam(value = "type", defaultValue = "project") String type) throws Exception {
        QuestionDTO q = questionService.findById(id);
        String path = "answer".equals(type) ? q.getAnswerProjectPath() : q.getProjectPath();
        
        if (path == null || path.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(null);
        }
        
        byte[] zipBytes = com.exam.util.ZipUtils.zipDirectoryToBytes(path);
        String projectName = new java.io.File(path).getName();
        String filename = java.net.URLEncoder.encode(projectName + ".zip", java.nio.charset.StandardCharsets.UTF_8);
        
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + filename)
                .contentType(MediaType.parseMediaType("application/zip"))
                .body(zipBytes);
    }

    /** 获取题目工程中的所有代码 */
    @GetMapping("/{id}/project-code")
    public ResponseEntity<Map<String, String>> getProjectCode(@PathVariable("id") Long id, @RequestParam(value = "type", defaultValue = "project") String type) {
        QuestionDTO q = questionService.findById(id);
        String path = "answer".equals(type) ? q.getAnswerProjectPath() : q.getProjectPath();
        if (path == null || path.trim().isEmpty()) {
            return ResponseEntity.ok(java.util.Collections.emptyMap());
        }
        
        try {
            Map<String, String> codes = new java.util.LinkedHashMap<>();
            java.nio.file.Path tempPath = java.nio.file.Paths.get(path);
            if (!tempPath.isAbsolute()) {
                tempPath = java.nio.file.Paths.get(System.getProperty("user.dir")).getParent().resolve(tempPath).normalize();
            }
            final java.nio.file.Path projectPath = tempPath;
            java.io.File dir = projectPath.toFile();
            if (dir.exists() && dir.isDirectory()) {
                java.nio.file.Files.walk(projectPath)
                    .filter(java.nio.file.Files::isRegularFile)
                    .forEach(file -> {
                        String name = file.getFileName().toString();
                        if (name.endsWith(".java") || name.endsWith(".xml") || name.endsWith(".txt") || name.endsWith(".md") || name.endsWith(".sql") || name.endsWith(".properties") || name.endsWith(".yml") || name.endsWith(".yaml") || name.endsWith(".html") || name.endsWith(".js") || name.endsWith(".css") || name.endsWith(".vue")) {
                            try {
                                String content = java.nio.file.Files.readString(file);
                                codes.put(projectPath.relativize(file).toString().replace("\\", "/"), content);
                            } catch (Exception e) {
                                // ignore
                            }
                        }
                    });
            }
            return ResponseEntity.ok(codes);
        } catch (Exception e) {
            log.error("读取题目工程代码失败", e);
            return ResponseEntity.status(500).build();
        }
    }

    /** 获取所有章节 */
    @GetMapping("/chapters")
    public List<com.exam.entity.Chapter> chapters() {
        return questionService.getAllChapters();
    }

    /** 获取所有来源 */
    @GetMapping("/sources")
    public List<String> sources() {
        return questionService.getAllSources();
    }

    /** 题库统计 */
    @GetMapping("/stats")
    public Map<String, Object> stats() {
        return questionService.getStats();
    }

    /** 导出全部题目为 JSON 文件下载 */
    @GetMapping("/export")
    public ResponseEntity<byte[]> exportQuestions() throws Exception {
        List<QuestionDTO> all = questionService.exportAll();
        // 将 options 字符串转为真实 JSON 数组，方便阅读和编辑
        List<Map<String, Object>> exportData = all.stream().map(q -> {
            Map<String, Object> map = new java.util.LinkedHashMap<>();
            map.put("type", q.getType());
            map.put("chapter", q.getChapterName());
            map.put("difficulty", q.getDifficulty());
            map.put("content", q.getContent());
            if (q.getOptions() != null) {
                try { map.put("options", objectMapper.readTree(q.getOptions())); }
                catch (Exception e) { map.put("options", q.getOptions()); }
            }
            map.put("answer", q.getAnswer());
            map.put("explanation", q.getExplanation());
            map.put("defaultScore", q.getDefaultScore());
            map.put("source", q.getSource());
            map.put("projectPath", q.getProjectPath());
            map.put("answerProjectPath", q.getAnswerProjectPath());
            return map;
        }).toList();

        com.fasterxml.jackson.core.util.DefaultPrettyPrinter printer = new com.fasterxml.jackson.core.util.DefaultPrettyPrinter();
        printer.indentArraysWith(com.fasterxml.jackson.core.util.DefaultIndenter.SYSTEM_LINEFEED_INSTANCE);
        byte[] json = objectMapper.writer(printer).writeValueAsBytes(exportData);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=questions.json")
                .contentType(MediaType.APPLICATION_JSON)
                .body(json);
    }

    /** 导入题目 JSON 文件 (替换全部题库) */
    @PostMapping("/import")
    public Map<String, Object> importQuestions(@RequestParam("file") MultipartFile file) throws Exception {
        List<Map<String, Object>> rawList = objectMapper.readValue(
                file.getInputStream(), new TypeReference<>() {});

        List<QuestionDTO> dtos = rawList.stream().map(raw -> {
            QuestionDTO dto = new QuestionDTO();
            dto.setType(QuestionType.valueOf((String) raw.get("type")));
            dto.setChapterName((String) raw.get("chapter"));
            dto.setDifficulty(Difficulty.valueOf((String) raw.get("difficulty")));
            dto.setContent((String) raw.get("content"));
            Object opts = raw.get("options");
            if (opts != null) {
                try { dto.setOptions(objectMapper.writeValueAsString(opts)); }
                catch (Exception e) { dto.setOptions(String.valueOf(opts)); }
            }
            dto.setAnswer((String) raw.get("answer"));
            dto.setExplanation((String) raw.get("explanation"));
            dto.setDefaultScore((Integer) raw.get("defaultScore"));
            dto.setSource((String) raw.get("source"));
            dto.setProjectPath((String) raw.get("projectPath"));
            dto.setAnswerProjectPath((String) raw.get("answerProjectPath"));
            return dto;
        }).toList();

        Map<String, Object> result = questionService.importAll(dtos);
        int count = (int) result.get("successCount");
        @SuppressWarnings("unchecked")
        List<String> errors = (List<String>) result.get("errors");
        
        String message = "成功导入 " + count + " 道题目";
        if (!errors.isEmpty()) {
            message += "，失败 " + errors.size() + " 道题目";
        }
        
        return Map.of(
            "success", true,
            "count", count,
            "message", message,
            "errors", errors
        );
    }
}
