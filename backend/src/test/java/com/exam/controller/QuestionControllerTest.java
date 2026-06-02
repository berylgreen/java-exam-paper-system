package com.exam.controller;

import com.exam.dto.QuestionDTO;
import com.exam.dto.QuestionOptimizeRequest;
import com.exam.dto.QuestionOptimizeResponse;
import com.exam.enums.QuestionType;
import com.exam.service.QuestionOptimizationService;
import com.exam.service.QuestionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(QuestionController.class)
@ActiveProfiles("test")
class QuestionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private QuestionService questionService;

    @MockBean
    private QuestionOptimizationService questionOptimizationService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void optimizePreview_shouldReturnOptimizedQuestion() throws Exception {
        QuestionOptimizeRequest request = buildRequest("请优化题目");

        QuestionDTO optimizedQuestion = new QuestionDTO();
        optimizedQuestion.setType(QuestionType.SINGLE_CHOICE);
        optimizedQuestion.setContent("优化后的题干");
        optimizedQuestion.setAnswer("A");
        optimizedQuestion.setExplanation("优化后的解析");
        optimizedQuestion.setOptions("[{\"label\":\"A\",\"text\":\"选项A\"}]");

        QuestionOptimizeResponse response = new QuestionOptimizeResponse();
        response.setOptimizedQuestion(optimizedQuestion);

        when(questionOptimizationService.optimizePreview(any(QuestionOptimizeRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/questions/optimize-preview")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.optimizedQuestion.content").value("优化后的题干"))
                .andExpect(jsonPath("$.optimizedQuestion.answer").value("A"))
                .andExpect(jsonPath("$.optimizedQuestion.explanation").value("优化后的解析"));
    }

    @Test
    void optimizePreview_shouldReturnBadRequestWhenServiceThrows() throws Exception {
        QuestionOptimizeRequest request = buildRequest("   ");
        when(questionOptimizationService.optimizePreview(any(QuestionOptimizeRequest.class)))
                .thenThrow(new RuntimeException("优化提示词不能为空"));

        mockMvc.perform(post("/api/questions/optimize-preview")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("优化提示词不能为空"));
    }

    private QuestionOptimizeRequest buildRequest(String prompt) {
        QuestionDTO question = new QuestionDTO();
        question.setType(QuestionType.SINGLE_CHOICE);
        question.setContent("原始题干");
        question.setAnswer("原答案");
        question.setExplanation("原解析");

        QuestionOptimizeRequest request = new QuestionOptimizeRequest();
        request.setQuestion(question);
        request.setPrompt(prompt);
        return request;
    }
}
