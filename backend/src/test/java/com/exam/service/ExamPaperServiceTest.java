package com.exam.service;

import com.exam.dto.AutoGenerateRequest;
import com.exam.dto.PaperDTO;
import com.exam.entity.Chapter;
import com.exam.entity.Question;
import com.exam.enums.Difficulty;
import com.exam.enums.QuestionType;
import com.exam.repository.ExamPaperRepository;
import com.exam.repository.PaperQuestionRepository;
import com.exam.repository.QuestionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ExamPaperServiceTest {

    @Mock
    private ExamPaperRepository paperRepository;

    @Mock
    private PaperQuestionRepository paperQuestionRepository;

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private QuestionService questionService;

    @InjectMocks
    private ExamPaperService examPaperService;

    private List<Question> mockSingleChoice;
    private List<Question> mockProgramming;

    @BeforeEach
    public void setup() {
        mockSingleChoice = new ArrayList<>();
        Chapter ch1 = Chapter.builder().id(1L).name("第1章").build();
        for (int i = 0; i < 10; i++) {
            Question q = Question.builder()
                    .id((long) i)
                    .type(QuestionType.SINGLE_CHOICE)
                    .chapter(ch1)
                    .difficulty(i < 5 ? Difficulty.EASY : Difficulty.MEDIUM)
                    .content("Question " + i)
                    .defaultScore(2)
                    .source(i % 2 == 0 ? "课后习题原题" : "网络")
                    .build();
            mockSingleChoice.add(q);
        }

        mockProgramming = new ArrayList<>();
        Question pq = Question.builder()
                .id(100L)
                .type(QuestionType.PROGRAMMING)
                .chapter(ch1)
                .difficulty(Difficulty.MEDIUM)
                .content("Programming 1")
                .defaultScore(10)
                .projectPath("path/to/project")
                .build();
        mockProgramming.add(pq);
    }

    @Test
    public void testPreviewGenerate_Success() {
        // Arrange
        AutoGenerateRequest request = new AutoGenerateRequest();
        request.setTitle("Auto Generated Test");
        request.setSingleChoiceCount(5);
        request.setMultipleChoiceCount(0);
        request.setTrueFalseCount(0);
        request.setFillBlankCount(0);
        request.setShortAnswerCount(0);
        request.setCodeReadingCount(0);
        request.setProgrammingCount(1);
        request.setEasyPercent(50);
        request.setMediumPercent(50);
        request.setHardPercent(0);
        request.setTextbookPercent(50);
        request.setNetworkPercent(50);
        request.setMustIncludeProject(true);

        when(questionRepository.findByType(QuestionType.SINGLE_CHOICE)).thenReturn(mockSingleChoice);
        when(questionRepository.findByType(QuestionType.PROGRAMMING)).thenReturn(mockProgramming);

        // Act
        PaperDTO result = examPaperService.previewGenerate(request);

        // Assert
        assertNotNull(result);
        assertEquals("Auto Generated Test", result.getTitle());
        assertEquals(6, result.getQuestions().size()); // 5 SC + 1 Programming
        
        long scCount = result.getQuestions().stream()
                .filter(q -> q.getScore() == 2) // Single Choice default score is passed as 2 to pickQuestions
                .count();
        assertEquals(5, scCount);
    }
    
    @Test
    public void testPreviewGenerate_InsufficientQuestions() {
        // Arrange
        AutoGenerateRequest request = new AutoGenerateRequest();
        request.setSingleChoiceCount(20); // More than available

        when(questionRepository.findByType(QuestionType.SINGLE_CHOICE)).thenReturn(mockSingleChoice);

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> {
            examPaperService.previewGenerate(request);
        });
        
        assertTrue(exception.getMessage().contains("不足"));
    }
}
