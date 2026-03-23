package com.exam.controller;

import com.exam.dto.QuestionDTO;
import com.exam.enums.Difficulty;
import com.exam.enums.QuestionType;
import com.exam.service.QuestionService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
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
@RestController
@RequestMapping("/api/questions")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;
    private final ObjectMapper objectMapper;

    /** 分页条件查询题目 */
    @GetMapping
    public Page<QuestionDTO> list(
            @RequestParam(value = "type", required = false) QuestionType type,
            @RequestParam(value = "chapter", required = false) String chapter,
            @RequestParam(value = "difficulty", required = false) Difficulty difficulty,
            @RequestParam(value = "source", required = false) String source,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size) {
        return questionService.findByFilters(type, chapter, difficulty, source,
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

    /** 删除题目 */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        questionService.delete(id);
        return ResponseEntity.ok().build();
    }

    /** 获取所有章节 */
    @GetMapping("/chapters")
    public List<String> chapters() {
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
            map.put("chapter", q.getChapter());
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
            return map;
        }).toList();

        byte[] json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsBytes(exportData);
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
            dto.setChapter((String) raw.get("chapter"));
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
            return dto;
        }).toList();

        int count = questionService.importAll(dtos);
        return Map.of("success", true, "count", count, "message", "成功导入 " + count + " 道题目");
    }
}
