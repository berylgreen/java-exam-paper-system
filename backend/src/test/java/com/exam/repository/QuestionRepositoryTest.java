package com.exam.repository;

import com.exam.entity.Chapter;
import com.exam.entity.Question;
import com.exam.enums.Difficulty;
import com.exam.enums.QuestionType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
public class QuestionRepositoryTest {

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private ChapterRepository chapterRepository;

    @Test
    public void testSaveAndFindByTypeAndDifficulty() {
        // Arrange
        Chapter chapter = Chapter.builder().name("第1章").sortOrder(1).build();
        chapter = chapterRepository.save(chapter);

        Question q1 = Question.builder()
                .type(QuestionType.SINGLE_CHOICE)
                .chapter(chapter)
                .difficulty(Difficulty.EASY)
                .content("Test Q1")
                .answer("A")
                .defaultScore(2)
                .source("Source A")
                .build();
        
        Question q2 = Question.builder()
                .type(QuestionType.SINGLE_CHOICE)
                .chapter(chapter)
                .difficulty(Difficulty.HARD)
                .content("Test Q2")
                .answer("B")
                .defaultScore(2)
                .source("Source B")
                .build();

        questionRepository.save(q1);
        questionRepository.save(q2);

        // Act
        List<Question> result = questionRepository.findByTypeAndDifficulty(QuestionType.SINGLE_CHOICE, Difficulty.EASY);

        // Assert
        assertEquals(1, result.size());
        assertEquals("Test Q1", result.get(0).getContent());
    }

    @Test
    public void testFindAllSources() {
        // Arrange
        Chapter chapter = Chapter.builder().name("第1章").sortOrder(1).build();
        chapter = chapterRepository.save(chapter);

        Question q1 = Question.builder()
                .type(QuestionType.SINGLE_CHOICE)
                .chapter(chapter)
                .difficulty(Difficulty.EASY)
                .content("Test Q1")
                .answer("A")
                .defaultScore(2)
                .source("Source X")
                .build();

        Question q2 = Question.builder()
                .type(QuestionType.SINGLE_CHOICE)
                .chapter(chapter)
                .difficulty(Difficulty.EASY)
                .content("Test Q2")
                .answer("B")
                .defaultScore(2)
                .source("Source X")
                .build();
                
        Question q3 = Question.builder()
                .type(QuestionType.SINGLE_CHOICE)
                .chapter(chapter)
                .difficulty(Difficulty.EASY)
                .content("Test Q3")
                .answer("C")
                .defaultScore(2)
                .source("Source Y")
                .build();

        questionRepository.saveAll(List.of(q1, q2, q3));

        // Act
        List<String> sources = questionRepository.findAllSources();

        // Assert
        assertEquals(2, sources.size());
        assertTrue(sources.contains("Source X"));
        assertTrue(sources.contains("Source Y"));
    }
}
