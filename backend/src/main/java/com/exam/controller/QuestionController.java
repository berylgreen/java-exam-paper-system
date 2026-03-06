package com.exam.controller;

import com.exam.dto.QuestionDTO;
import com.exam.enums.Difficulty;
import com.exam.enums.QuestionType;
import com.exam.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    /** 分页条件查询题目 */
    @GetMapping
    public Page<QuestionDTO> list(
            @RequestParam(value = "type", required = false) QuestionType type,
            @RequestParam(value = "chapter", required = false) String chapter,
            @RequestParam(value = "difficulty", required = false) Difficulty difficulty,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size) {
        return questionService.findByFilters(type, chapter, difficulty,
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

    /** 题库统计 */
    @GetMapping("/stats")
    public Map<String, Object> stats() {
        return questionService.getStats();
    }
}
