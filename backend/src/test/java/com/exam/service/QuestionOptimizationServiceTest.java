package com.exam.service;

import com.exam.config.AiProperties;
import com.exam.dto.QuestionDTO;
import com.exam.dto.QuestionOptimizeRequest;
import com.exam.dto.QuestionOptimizeResponse;
import com.exam.enums.QuestionType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.client.ExpectedCount.once;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@ExtendWith(MockitoExtension.class)
class QuestionOptimizationServiceTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private AiProperties aiProperties;
    private QuestionOptimizationService service;
    private MockRestServiceServer server;

    @BeforeEach
    void setUp() {
        aiProperties = new AiProperties();
        aiProperties.setBaseUrl("http://localhost:1879/v1");
        aiProperties.setApiKey("test-key");
        aiProperties.setModel("gpt-5.4");
        aiProperties.setTimeout(30000);

        RestTemplate restTemplate = new RestTemplate();
        server = MockRestServiceServer.bindTo(restTemplate).build();

        RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder();
        service = new QuestionOptimizationService(objectMapper, aiProperties, restTemplateBuilder);
        org.springframework.test.util.ReflectionTestUtils.setField(service, "restTemplate", restTemplate);
    }

    @Test
    void optimizePreview_shouldMergeOptimizedFieldsForChoiceQuestion() {
        server.expect(once(), requestTo("http://localhost:1879/v1/chat/completions"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header("Authorization", "Bearer test-key"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andRespond(withSuccess("""
                        {
                          "choices": [
                            {
                              "message": {
                                "content": "{\\\"content\\\":\\\"优化后的题干\\\",\\\"answer\\\":\\\"A\\\",\\\"explanation\\\":\\\"更清晰的解析\\\",\\\"options\\\":[{\\\"label\\\":\\\"A\\\",\\\"text\\\":\\\"正确答案\\\"},{\\\"label\\\":\\\"B\\\",\\\"text\\\":\\\"错误答案\\\"}]}"
                              }
                            }
                          ]
                        }
                        """, MediaType.APPLICATION_JSON));

        QuestionOptimizeResponse response = service.optimizePreview(buildRequest(QuestionType.SINGLE_CHOICE));

        assertEquals("优化后的题干", response.getOptimizedQuestion().getContent());
        assertEquals("A", response.getOptimizedQuestion().getAnswer());
        assertEquals("更清晰的解析", response.getOptimizedQuestion().getExplanation());
        assertEquals("[{\"label\":\"A\",\"text\":\"正确答案\"},{\"label\":\"B\",\"text\":\"错误答案\"}]", response.getOptimizedQuestion().getOptions());
        assertEquals("网络2026年1月", response.getOptimizedQuestion().getSource());

        server.verify();
    }

    @Test
    void optimizePreview_shouldKeepOriginalOptionsForNonChoiceQuestion() {
        server.expect(once(), requestTo("http://localhost:1879/v1/chat/completions"))
                .andRespond(withSuccess("""
                        {
                          "choices": [
                            {
                              "message": {
                                "content": "{\\\"content\\\":\\\"优化后的简答题\\\",\\\"answer\\\":\\\"新答案\\\",\\\"explanation\\\":\\\"新解析\\\",\\\"options\\\":[{\\\"label\\\":\\\"A\\\",\\\"text\\\":\\\"不应使用\\\"}]}"
                              }
                            }
                          ]
                        }
                        """, MediaType.APPLICATION_JSON));

        QuestionOptimizeResponse response = service.optimizePreview(buildRequest(QuestionType.SHORT_ANSWER));

        assertEquals("优化后的简答题", response.getOptimizedQuestion().getContent());
        assertEquals("原选项", response.getOptimizedQuestion().getOptions());
        server.verify();
    }

    @Test
    void optimizePreview_shouldRejectBlankPrompt() {
        QuestionOptimizeRequest request = buildRequest(QuestionType.SINGLE_CHOICE);
        request.setPrompt("   ");

        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.optimizePreview(request));
        assertEquals("优化提示词不能为空", ex.getMessage());
    }

    @Test
    void optimizePreview_shouldRejectInvalidOptionsPayload() {
        server.expect(once(), requestTo("http://localhost:1879/v1/chat/completions"))
                .andRespond(withSuccess("""
                        {
                          "choices": [
                            {
                              "message": {
                                "content": "{\\\"content\\\":\\\"优化后的题干\\\",\\\"answer\\\":\\\"A\\\",\\\"explanation\\\":\\\"解析\\\",\\\"options\\\":{\\\"label\\\":\\\"A\\\",\\\"text\\\":\\\"错误结构\\\"}}"
                              }
                            }
                          ]
                        }
                        """, MediaType.APPLICATION_JSON));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.optimizePreview(buildRequest(QuestionType.SINGLE_CHOICE)));
        assertEquals("AI 返回的选项格式无效", ex.getMessage());
        server.verify();
    }

    @Test
    void optimizePreview_shouldParseSseStyleResponse() {
        server.expect(once(), requestTo("http://localhost:1879/v1/chat/completions"))
                .andRespond(withSuccess("""
                        data: {"choices":[{"message":{"content":"{\\\"content\\\":\\\"SSE题干\\\",\\\"answer\\\":\\\"B\\\",\\\"explanation\\\":\\\"SSE解析\\\"}"}}]}

                        data: [DONE]
                        """, MediaType.parseMediaType("text/event-stream;charset=utf-8")));

        QuestionOptimizeResponse response = service.optimizePreview(buildRequest(QuestionType.SHORT_ANSWER));

        assertEquals("SSE题干", response.getOptimizedQuestion().getContent());
        assertEquals("B", response.getOptimizedQuestion().getAnswer());
        assertEquals("SSE解析", response.getOptimizedQuestion().getExplanation());
        server.verify();
    }

    @Test
    void optimizePreview_shouldParseChunkedSseDeltaResponse() {
        server.expect(once(), requestTo("http://localhost:1879/v1/chat/completions"))
                .andRespond(withSuccess("""
                        data: {"choices":[{"delta":{"content":"{\\\"content\\\":\\\"分片题干\\\","}}]}
                        data: {"choices":[{"delta":{"content":"\\\"answer\\\":\\\"C\\\",\\\"explanation\\\":\\\"分片解析\\\"}"}}]}
                        data: [DONE]
                        """, MediaType.parseMediaType("text/event-stream;charset=utf-8")));

        QuestionOptimizeResponse response = service.optimizePreview(buildRequest(QuestionType.SHORT_ANSWER));

        assertEquals("分片题干", response.getOptimizedQuestion().getContent());
        assertEquals("C", response.getOptimizedQuestion().getAnswer());
        assertEquals("分片解析", response.getOptimizedQuestion().getExplanation());
        server.verify();
    }

    private QuestionOptimizeRequest buildRequest(QuestionType type) {
        QuestionDTO question = new QuestionDTO();
        question.setId(1L);
        question.setType(type);
        question.setChapterName("第1章");
        question.setContent("原始题目");
        question.setOptions("原选项");
        question.setAnswer("原答案");
        question.setExplanation("原解析");
        question.setDefaultScore(2);
        question.setSource("网络2026年1月");

        QuestionOptimizeRequest request = new QuestionOptimizeRequest();
        request.setQuestion(question);
        request.setPrompt("请优化题干和答案");
        return request;
    }
}
