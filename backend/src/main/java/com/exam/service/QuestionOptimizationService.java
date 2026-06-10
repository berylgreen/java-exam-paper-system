package com.exam.service;

import com.exam.config.AiProperties;
import com.exam.dto.QuestionDTO;
import com.exam.dto.QuestionOptimizeRequest;
import com.exam.dto.QuestionOptimizeResponse;
import com.exam.enums.QuestionType;
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
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;
import com.exam.dto.SyncAnswerRequest;
import com.exam.dto.SyncAnswerResponse;

/**
 * 题目 AI 优化服务
 */
@Service
@RequiredArgsConstructor
public class QuestionOptimizationService {

    private final ObjectMapper objectMapper;
    private final AiProperties aiProperties;
    private final RestTemplateBuilder restTemplateBuilder;
    private RestTemplate restTemplate;

    public QuestionOptimizeResponse optimizePreview(QuestionOptimizeRequest request) {
        validateRequest(request);

        QuestionDTO original = request.getQuestion();
        AiResponse aiResponse = callAi(original, request.getPrompt().trim());

        QuestionDTO optimized = copyQuestion(original);
        optimized.setContent(readText(aiResponse.content(), original.getContent()));
        optimized.setAnswer(readText(aiResponse.answer(), original.getAnswer()));
        optimized.setExplanation(readText(aiResponse.explanation(), original.getExplanation()));

        if (isChoiceQuestion(original.getType()) && aiResponse.options() != null) {
            optimized.setOptions(serializeOptions(aiResponse.options()));
        }

        QuestionOptimizeResponse response = new QuestionOptimizeResponse();
        response.setOptimizedQuestion(optimized);
        return response;
    }

    public SyncAnswerResponse syncAnswerToProject(SyncAnswerRequest request) {
        if (request == null || request.getAnswerText() == null || request.getAnswerProjectPath() == null) {
            throw new RuntimeException("参数不能为空");
        }
        
        String projectPathStr = request.getAnswerProjectPath().trim();
        Path projectPath = Paths.get(projectPathStr);
        if (!Files.exists(projectPath)) {
            // Option to create dir if it doesn't exist, but maybe we just proceed or create.
            try {
                Files.createDirectories(projectPath);
            } catch (Exception e) {
                throw new RuntimeException("无法创建目标工程目录: " + projectPathStr);
            }
        }
        
        // Read file structure
        StringBuilder structure = new StringBuilder();
        try (Stream<Path> paths = Files.walk(projectPath, 3)) {
            paths.filter(Files::isRegularFile)
                 .filter(p -> p.toString().endsWith(".java"))
                 .forEach(p -> structure.append(projectPath.relativize(p)).append("\n"));
        } catch (Exception e) {
            structure.append("(无现有Java文件或读取失败)");
        }

        String prompt = "你是一个代码同步助手。以下是一道题目标准答案的文本，其中可能包含Java代码（如markdown标记）。\n"
                + "目标工程路径: " + projectPathStr + "\n"
                + "该目录下已有的Java文件结构:\n" + structure.toString() + "\n\n"
                + "请剥离说明性文字，提取纯正的Java代码。并结合目标工程路径，推理出代码应该存放的完整绝对路径。\n"
                + "返回严格的JSON数组格式，最外层不要```json标记。\n"
                + "结构要求：\n[\n  {\"filePath\": \"绝对路径\", \"content\": \"纯Java代码\"}\n]";

        AiResponse aiResponse = callAiForSync(request.getAnswerText(), prompt);
        
        // Parse the raw content returned by AI, which should be the JSON array
        List<String> updatedFiles = new ArrayList<>();
        try {
            String jsonContent = aiResponse.content();
            if (jsonContent == null || jsonContent.isBlank()) {
                 throw new RuntimeException("AI 未返回内容");
            }
            JsonNode arrayNode = objectMapper.readTree(normalizeStructuredJson(jsonContent));
            if (!arrayNode.isArray()) {
                throw new RuntimeException("AI 未返回 JSON 数组");
            }
            
            for (JsonNode node : arrayNode) {
                String filePath = node.path("filePath").asText();
                String content = node.path("content").asText();
                
                if (filePath != null && !filePath.isEmpty() && content != null && !content.isEmpty()) {
                    Path targetPath = Paths.get(filePath);
                    if (!targetPath.isAbsolute()) {
                        targetPath = projectPath.resolve(targetPath);
                    }
                    Files.createDirectories(targetPath.getParent());
                    Files.writeString(targetPath, content);
                    updatedFiles.add(targetPath.toString());
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("同步写入文件失败: " + e.getMessage(), e);
        }
        
        SyncAnswerResponse response = new SyncAnswerResponse();
        response.setUpdatedFiles(updatedFiles);
        return response;
    }
    
    private AiResponse callAiForSync(String answerText, String systemPrompt) {
        RestTemplate restTemplate = getRestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(aiProperties.getApiKey());

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("model", aiProperties.getModel());
        body.put("stream", false);
        // Do not force json_object if we want an array directly, or we can just ask for string.
        body.put("messages", List.of(
                Map.of("role", "system", "content", systemPrompt),
                Map.of("role", "user", "content", "需要提取的代码源文本如下：\n" + answerText)
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
            return parseAiResponseForSync(rawBody);
        } catch (RestClientException e) {
            throw new RuntimeException("AI 服务调用失败，请稍后重试: " + e.getMessage(), e);
        }
    }

    private AiResponse parseAiResponseForSync(String rawBody) {
        if (rawBody == null || rawBody.isBlank()) {
            throw new RuntimeException("AI 服务未返回结果");
        }
        try {
            String content = extractStructuredContent(rawBody);
            if (content == null || content.isBlank()) {
                throw new RuntimeException("AI 返回内容为空");
            }
            return new AiResponse(content, null, null, null);
        } catch (Exception e) {
            throw new RuntimeException("解析AI响应失败", e);
        }
    }

    private void validateRequest(QuestionOptimizeRequest request) {
        if (request == null || request.getQuestion() == null) {
            throw new RuntimeException("题目信息不能为空");
        }
        if (request.getPrompt() == null || request.getPrompt().trim().isEmpty()) {
            throw new RuntimeException("优化提示词不能为空");
        }
        if (request.getQuestion().getContent() == null || request.getQuestion().getContent().trim().isEmpty()) {
            throw new RuntimeException("题目内容不能为空");
        }
        if (aiProperties.getBaseUrl() == null || aiProperties.getBaseUrl().isBlank()) {
            throw new RuntimeException("AI 服务地址未配置");
        }
        if (aiProperties.getApiKey() == null || aiProperties.getApiKey().isBlank()) {
            throw new RuntimeException("AI 服务密钥未配置");
        }
    }

    private AiResponse callAi(QuestionDTO question, String prompt) {
        RestTemplate restTemplate = getRestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(aiProperties.getApiKey());

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("model", aiProperties.getModel());
        body.put("stream", false);
        body.put("response_format", Map.of("type", "json_object"));
        body.put("messages", List.of(
                Map.of("role", "system", "content", buildSystemPrompt()),
                Map.of("role", "user", "content", buildUserPrompt(question, prompt))
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
        } catch (RestClientException e) {
            throw new RuntimeException("AI 服务调用失败，请稍后重试: " + e.getMessage(), e);
        }
    }

    private RestTemplate getRestTemplate() {
        if (restTemplate == null) {
            restTemplate = restTemplateBuilder
                    .setConnectTimeout(Duration.ofMillis(aiProperties.getTimeout()))
                    .setReadTimeout(Duration.ofMillis(aiProperties.getTimeout()))
                    .build();
        }
        return restTemplate;
    }

    private String buildSystemPrompt() {
        return "你是一名 Java 程序设计基础课程命题优化助手。"
                + "请根据用户给定的题目与优化要求，只优化题干、答案、解析，以及选择题选项。"
                + "不要修改题型、章节、难度、来源、分值、工程路径等元数据。"
                + "必须只返回 JSON，最外层不要使用 ```json 这样的 Markdown 标记，也不要包含任何额外的说明文字。"
                + "注意：在 JSON 内部的 answer 和 explanation 字段中，如果涉及代码展示，请务必保留或使用 Markdown 格式（如 ```java 等代码块标记）进行排版。"
                + "JSON 结构必须为 {\"content\":string,\"answer\":string,\"explanation\":string,\"options\":[{\"label\":string,\"text\":string}]}。"
                + "如果不是单选题或多选题，可省略 options 字段或返回 null。";
    }

    private String buildUserPrompt(QuestionDTO question, String prompt) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("prompt", prompt);
        payload.put("question", question);
        try {
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("构造 AI 请求失败", e);
        }
    }

    private AiResponse parseAiResponse(String rawBody) {
        if (rawBody == null || rawBody.isBlank()) {
            throw new RuntimeException("AI 服务未返回结果");
        }

        try {
            String content = extractStructuredContent(rawBody);
            if (content == null || content.isBlank()) {
                throw new RuntimeException("AI 返回内容为空");
            }

            JsonNode node = objectMapper.readTree(normalizeStructuredJson(content));
            List<OptionItem> options = null;
            JsonNode optionsNode = node.get("options");
            if (optionsNode != null && !optionsNode.isNull()) {
                options = parseOptions(optionsNode);
            }
            return new AiResponse(
                    node.path("content").asText(null),
                    node.path("answer").asText(null),
                    node.path("explanation").asText(null),
                    options
            );
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("AI 返回的内容不是有效 JSON", e);
        }
    }

    private String extractStructuredContent(String rawBody) throws JsonProcessingException {
        String trimmed = rawBody.trim();
        if (trimmed.startsWith("data:")) {
            return extractContentFromSse(trimmed);
        }

        try {
            JsonNode raw = objectMapper.readTree(trimmed);
            return extractContentFromEnvelope(raw);
        } catch (JsonProcessingException e) {
            return trimmed;
        }
    }

    private String extractContentFromSse(String rawBody) throws JsonProcessingException {
        StringBuilder builder = new StringBuilder();
        String[] lines = rawBody.split("\\r?\\n");
        for (String line : lines) {
            String current = line.trim();
            if (!current.startsWith("data:")) {
                continue;
            }
            String payload = current.substring(5).trim();
            if (payload.isEmpty() || "[DONE]".equals(payload)) {
                continue;
            }
            JsonNode envelope = objectMapper.readTree(payload);
            String content = extractContentFromEnvelope(envelope);
            if (content != null && !content.isBlank()) {
                builder.append(content);
            }
        }
        if (builder.isEmpty()) {
            throw new RuntimeException("AI 服务未返回有效数据");
        }
        return builder.toString();
    }

    private String extractContentFromEnvelope(JsonNode raw) {
        JsonNode choicesNode = raw.get("choices");
        if (choicesNode == null || !choicesNode.isArray() || choicesNode.isEmpty()) {
            throw new RuntimeException("AI 返回结果缺少 choices");
        }

        JsonNode choiceNode = choicesNode.get(0);
        String content = extractContent(choiceNode.path("message").get("content"));
        if (content != null && !content.isBlank()) {
            return content;
        }

        return extractContent(choiceNode.path("delta").get("content"));
    }

    private String extractContent(JsonNode contentNode) {
        if (contentNode == null || contentNode.isNull()) {
            return null;
        }
        if (contentNode.isTextual()) {
            return contentNode.asText();
        }
        if (contentNode.isArray()) {
            StringBuilder builder = new StringBuilder();
            for (JsonNode item : contentNode) {
                JsonNode textNode = item.get("text");
                if (textNode != null && textNode.isTextual()) {
                    builder.append(textNode.asText());
                }
            }
            return builder.toString();
        }
        return contentNode.toString();
    }

    private String normalizeStructuredJson(String content) {
        String trimmed = content.trim();
        if (trimmed.startsWith("```")) {
            trimmed = trimmed.replaceFirst("^```(?:json)?\\s*", "");
            trimmed = trimmed.replaceFirst("\\s*```$", "");
        }
        return trimmed.trim();
    }

    private List<OptionItem> parseOptions(JsonNode optionsNode) {
        if (!optionsNode.isArray()) {
            throw new RuntimeException("AI 返回的选项格式无效");
        }
        List<OptionItem> options = new ArrayList<>();
        for (JsonNode item : optionsNode) {
            String label = item.path("label").asText("").trim();
            String text = item.path("text").asText("").trim();
            if (label.isEmpty() || text.isEmpty()) {
                throw new RuntimeException("AI 返回的选项格式无效");
            }
            options.add(new OptionItem(label, text));
        }
        return options;
    }

    private String serializeOptions(List<OptionItem> options) {
        try {
            List<Map<String, String>> normalized = new ArrayList<>();
            for (OptionItem option : options) {
                Map<String, String> item = new LinkedHashMap<>();
                item.put("label", option.label());
                item.put("text", option.text());
                normalized.add(item);
            }
            return objectMapper.writeValueAsString(normalized);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("AI 返回的选项无法序列化", e);
        }
    }

    private QuestionDTO copyQuestion(QuestionDTO original) {
        QuestionDTO copy = new QuestionDTO();
        copy.setId(original.getId());
        copy.setType(original.getType());
        copy.setChapterId(original.getChapterId());
        copy.setChapterName(original.getChapterName());
        copy.setDifficulty(original.getDifficulty());
        copy.setContent(original.getContent());
        copy.setOptions(original.getOptions());
        copy.setAnswer(original.getAnswer());
        copy.setExplanation(original.getExplanation());
        copy.setDefaultScore(original.getDefaultScore());
        copy.setSource(original.getSource());
        copy.setProjectPath(original.getProjectPath());
        return copy;
    }

    private boolean isChoiceQuestion(QuestionType type) {
        return type == QuestionType.SINGLE_CHOICE || type == QuestionType.MULTIPLE_CHOICE;
    }

    private String readText(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value.trim();
    }

    private String normalizeBaseUrl(String baseUrl) {
        return baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
    }

    private record AiResponse(String content, String answer, String explanation, List<OptionItem> options) {
    }

    private record OptionItem(String label, String text) {
    }
}
