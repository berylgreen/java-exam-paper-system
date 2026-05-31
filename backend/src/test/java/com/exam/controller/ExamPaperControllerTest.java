package com.exam.controller;

import com.exam.dto.AutoGenerateRequest;
import com.exam.dto.PaperDTO;
import com.exam.service.ExamPaperService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ExamPaperController.class)
@ActiveProfiles("test")
public class ExamPaperControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ExamPaperService examPaperService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testListPapers() throws Exception {
        PaperDTO paperDTO = new PaperDTO();
        paperDTO.setId(1L);
        paperDTO.setTitle("Test Paper");

        when(examPaperService.findAll()).thenReturn(Collections.singletonList(paperDTO));

        mockMvc.perform(get("/api/papers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("Test Paper"));
    }

    @Test
    public void testPreviewGenerate() throws Exception {
        AutoGenerateRequest request = new AutoGenerateRequest();
        request.setTitle("Auto Generated");

        PaperDTO responseDTO = new PaperDTO();
        responseDTO.setId(2L);
        responseDTO.setTitle("Auto Generated");

        when(examPaperService.previewGenerate(any(AutoGenerateRequest.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/papers/preview-generate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.title").value("Auto Generated"));
    }
}
