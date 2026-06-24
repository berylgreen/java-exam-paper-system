package com.exam.service;

import com.exam.config.AiProperties;
import com.exam.dto.PaperDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 试卷评分标准 AI 生成服务
 */
@Service
@RequiredArgsConstructor
public class GradingRubricAiService {

    private final ObjectMapper objectMapper;
    private final AiProperties aiProperties;
    private final RestTemplateBuilder restTemplateBuilder;
    private RestTemplate restTemplate;

    public String generateRubric(PaperDTO paper) {
        validateConfig();

        RestTemplate restTemplate = getRestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(aiProperties.getApiKey());

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("model", aiProperties.getModel());
        body.put("stream", false);
        // 不强制使用 json_object，因为我们需要的是一篇排版良好的 Markdown 文档
        body.put("messages", List.of(
                Map.of("role", "system", "content", buildSystemPrompt()),
                Map.of("role", "user", "content", buildUserPrompt(paper))
        ));

        String jsonBody;
        try {
            jsonBody = objectMapper.writeValueAsString(body);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("序列化 AI 请求失败", e);
        }

        try {
            String rawBody = restTemplate.postForObject(
                    normalizeBaseUrl(aiProperties.getBaseUrl()) + "/chat/completions",
                    new HttpEntity<>(jsonBody, headers),
                    String.class
            );
            return parseAiResponse(rawBody);
        } catch (org.springframework.web.client.RestClientResponseException e) {
            int statusCode = e.getStatusCode().value();
            if (statusCode == 401) {
                throw new RuntimeException("AI 服务调用失败：API Key 错误或无效，请检查配置。");
            } else if (statusCode == 429) {
                throw new RuntimeException("AI 服务调用失败：请求过于频繁或额度已用尽。");
            } else {
                throw new RuntimeException("AI 服务响应异常 (状态码 " + statusCode + ")：" + e.getResponseBodyAsString(), e);
            }
        } catch (RestClientException e) {
            throw new RuntimeException("AI 服务网络异常或不可达: " + e.getMessage(), e);
        }
    }

    private void validateConfig() {
        if (aiProperties.getBaseUrl() == null || aiProperties.getBaseUrl().isBlank()) {
            throw new RuntimeException("AI 服务地址未配置");
        }
        if (aiProperties.getApiKey() == null || aiProperties.getApiKey().isBlank()) {
            throw new RuntimeException("AI 服务密钥未配置");
        }
    }

    private RestTemplate getRestTemplate() {
        if (restTemplate == null) {
            long timeoutMillis = aiProperties.getTimeout() > 0 ? aiProperties.getTimeout() : 600000;
            restTemplate = restTemplateBuilder
                    .setConnectTimeout(Duration.ofMillis(timeoutMillis))
                    .setReadTimeout(Duration.ofMillis(timeoutMillis))
                    .build();
        }
        return restTemplate;
    }

    private String buildSystemPrompt() {
        return "你是一名资深的 Java 程序设计基础课程主讲教师。"
                + "现在你需要为一份刚刚组卷完成的 Java 考试试卷制定一份详尽的《评分标准及参考答案细则》。"
                + "请根据用户提供的试卷题目和简要答案，生成一份可以直接打印使用的 Markdown 格式的文档。"
                + "文档应包括每道题的参考答案、得分点分配、扣分细则。对于编程题和程序分析题，需要详细列出考察点、逻辑得分步骤和代码给分点。"
                + "对于选择题和填空题，不需要按小题提供详细解析，只需直接给出标准答案并写明统一的评分方式即可。"
                + "注意：选择题的答案必须横向在一行内显示（例如：1.A  2.B  3.C），不要每个选项单独占一行。"
                + "绝对不要输出“总分汇总”和“阅卷补充说明”等额外总结性内容。"
                + "请使用清晰的标题层次结构，不要包含无关的寒暄语。";
    }

    private String buildUserPrompt(PaperDTO paper) {
        StringBuilder sb = new StringBuilder();
        sb.append("试卷名称：").append(paper.getTitle()).append("\n");
        sb.append("总分：").append(paper.getTotalScore()).append("\n");
        sb.append("考试时长：").append(paper.getDurationMinutes()).append("分钟\n\n");
        sb.append("以下是试卷题目及简要答案信息：\n\n");

        for (int i = 0; i < paper.getQuestions().size(); i++) {
            PaperDTO.PaperQuestionDTO pq = paper.getQuestions().get(i);
            sb.append(i + 1).append(". [").append(pq.getQuestion().getType().getLabel()).append("] ");
            sb.append("（").append(pq.getScore()).append("分）\n");
            sb.append("题干：").append(pq.getQuestion().getContent()).append("\n");
            if (pq.getQuestion().getAnswer() != null) {
                sb.append("简要答案：").append(pq.getQuestion().getAnswer()).append("\n");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    private String parseAiResponse(String rawBody) {
        if (rawBody == null || rawBody.isBlank()) {
            throw new RuntimeException("AI 服务未返回结果");
        }

        try {
            JsonNode raw = objectMapper.readTree(rawBody);
            JsonNode choicesNode = raw.get("choices");
            if (choicesNode == null || !choicesNode.isArray() || choicesNode.isEmpty()) {
                throw new RuntimeException("AI 返回结果缺少 choices");
            }
            JsonNode choiceNode = choicesNode.get(0);
            JsonNode contentNode = choiceNode.path("message").get("content");
            if (contentNode != null && contentNode.isTextual()) {
                return contentNode.asText();
            }
            return rawBody;
        } catch (Exception e) {
            throw new RuntimeException("解析 AI 返回内容失败", e);
        }
    }

    private String normalizeBaseUrl(String baseUrl) {
        return baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
    }
}
