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
        String projectCode = readProjectCode(original.getProjectPath());
        AiResponse aiResponse = callAi(original, request.getPrompt().trim(), projectCode);

        QuestionDTO optimized = copyQuestion(original);
        optimized.setContent(readText(aiResponse.content(), original.getContent()));
        optimized.setAnswer(readText(aiResponse.answer(), original.getAnswer()));
        optimized.setExplanation(readText(aiResponse.explanation(), original.getExplanation()));

        if (isChoiceQuestion(original.getType()) && aiResponse.options() != null) {
            optimized.setOptions(serializeOptions(aiResponse.options()));
        }

        List<String> updatedFiles = new ArrayList<>();
        if (aiResponse.projectFiles() != null && !aiResponse.projectFiles().isEmpty() && original.getProjectPath() != null) {
            Path projectPath = Paths.get(original.getProjectPath().trim());
            if (!projectPath.isAbsolute()) {
                projectPath = Paths.get(System.getProperty("user.dir")).getParent().resolve(projectPath).normalize();
            }
            for (ProjectFileItem pf : aiResponse.projectFiles()) {
                try {
                    Path targetPath = projectPath.resolve(pf.filePath());
                    Files.createDirectories(targetPath.getParent());
                    Files.writeString(targetPath, pf.content());
                    updatedFiles.add(targetPath.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        QuestionOptimizeResponse response = new QuestionOptimizeResponse();
        response.setOptimizedQuestion(optimized);
        response.setUpdatedProjectFiles(updatedFiles);
        return response;
    }

    private String readProjectCode(String projectPathStr) {
        if (projectPathStr == null || projectPathStr.isBlank()) return "";
        Path projectPath = Paths.get(projectPathStr.trim());
        if (!projectPath.isAbsolute()) {
            projectPath = Paths.get(System.getProperty("user.dir")).getParent().resolve(projectPath).normalize();
        }
        if (!Files.exists(projectPath)) return "";

        StringBuilder code = new StringBuilder();
        final Path finalProjectPath = projectPath;
        try (Stream<Path> paths = Files.walk(finalProjectPath, 3)) {
            paths.filter(Files::isRegularFile)
                 .filter(p -> p.toString().endsWith(".java"))
                 .forEach(p -> {
                     try {
                         code.append("--- File: ").append(finalProjectPath.relativize(p)).append(" ---\n");
                         code.append(Files.readString(p)).append("\n\n");
                     } catch (Exception e) {
                         // ignore
                     }
                 });
        } catch (Exception e) {
            return "";
        }
        return code.toString();
    }

    public SyncAnswerResponse syncAnswerToProject(SyncAnswerRequest request) {
        if (request == null || request.getAnswerText() == null || request.getAnswerProjectPath() == null) {
            throw new RuntimeException("参数不能为空");
        }
        return syncTextToProject(request.getAnswerText(), request.getAnswerProjectPath(), "标准答案");
    }

    public SyncAnswerResponse syncTextToProject(String text, String projectPathStr, String textType) {
        if (text == null || text.isBlank() || projectPathStr == null || projectPathStr.isBlank()) {
            return new SyncAnswerResponse();
        }
        
        String initialPathStr = projectPathStr.trim();
        Path initialPath = Paths.get(initialPathStr);
        if (!initialPath.isAbsolute()) {
            initialPath = Paths.get(System.getProperty("user.dir")).getParent().resolve(initialPath).normalize();
        }
        final Path projectPath = initialPath;
        String absProjectPathStr = projectPath.toString();
        
        if (!Files.exists(projectPath)) {
            try {
                Files.createDirectories(projectPath);
            } catch (Exception e) {
                throw new RuntimeException("无法创建目标工程目录: " + absProjectPathStr);
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

        String prompt = "你是一个代码同步助手。以下是一道题目的" + textType + "文本，其中可能包含Java代码（如markdown标记）。\n"
                + "目标工程路径: " + absProjectPathStr + "\n"
                + "该目录下已有的Java文件结构:\n" + structure.toString() + "\n\n"
                + "请剥离说明性文字，提取纯正的Java代码。并结合目标工程路径，推理出代码应该存放的完整绝对路径。\n"
                + "【重要警告】：\n"
                + "1. Java代码（.java文件）必须放在正确的源码包目录下（例如 src/main/java/... 或 src/...），请参考现有的文件结构来推断存放路径。\n"
                + "2. 即使代码片段中没有 package 声明，也绝对不允许将 .java 文件直接存放在工程的根目录下！\n"
                + "3. 如果已有同名文件在 src 深层目录中，请覆盖对应的文件。\n"
                + "返回严格的JSON数组格式，最外层不要```json标记。\n"
                + "结构要求：\n[\n  {\"filePath\": \"绝对路径\", \"content\": \"纯Java代码\"}\n]";

        AiResponse aiResponse = callAiForSync(text, prompt);
        
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

    private AiResponse parseAiResponseForSync(String rawBody) {
        if (rawBody == null || rawBody.isBlank()) {
            throw new RuntimeException("AI 服务未返回结果");
        }
        try {
            String content = extractStructuredContent(rawBody);
            if (content == null || content.isBlank()) {
                throw new RuntimeException("AI 返回内容为空");
            }
            return new AiResponse(content, null, null, null, null);
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

    private AiResponse callAi(QuestionDTO question, String prompt, String projectCode) {
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
                Map.of("role", "user", "content", buildUserPrompt(question, prompt, projectCode))
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
                + "请根据用户给定的题目与优化要求，优化题干、答案、解析，以及选择题选项。"
                + "如果用户传入了 projectCode (当前代码工程的内容)，请结合题目和优化要求，判断是否需要修改代码工程中的代码。"
                + "如果需要修改，请将修改后的完整文件内容放入 projectFiles 数组中。如果不需要修改代码，可以省略 projectFiles 字段或返回空数组。"
                + "不要修改题型、章节、难度、来源、分值、工程路径等元数据。"
                + "必须只返回 JSON，最外层不要使用 ```json 这样的 Markdown 标记，也不要包含任何额外的说明文字。"
                + "注意：在 JSON 内部的 answer 和 explanation 字段中，如果涉及代码展示，请务必保留或使用 Markdown 格式（如 ```java 等代码块标记）进行排版。"
                + "JSON 结构必须为 {\"content\":string,\"answer\":string,\"explanation\":string,\"options\":[{\"label\":string,\"text\":string}], \"projectFiles\":[{\"filePath\":\"相对路径\",\"content\":\"完整的Java代码\"}]}。"
                + "如果不是单选题或多选题，可省略 options 字段或返回 null。";
    }

    private String buildUserPrompt(QuestionDTO question, String prompt, String projectCode) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("prompt", prompt);
        payload.put("question", question);
        if (projectCode != null && !projectCode.isEmpty()) {
            payload.put("projectCode", projectCode);
        }
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
            List<ProjectFileItem> projectFiles = null;
            JsonNode filesNode = node.get("projectFiles");
            if (filesNode != null && filesNode.isArray()) {
                projectFiles = parseProjectFiles(filesNode);
            }
            return new AiResponse(
                    node.path("content").asText(null),
                    node.path("answer").asText(null),
                    node.path("explanation").asText(null),
                    options,
                    projectFiles
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
        
        int firstBracket = trimmed.indexOf('[');
        int lastBracket = trimmed.lastIndexOf(']');
        int firstBrace = trimmed.indexOf('{');
        int lastBrace = trimmed.lastIndexOf('}');
        
        if (firstBracket != -1 && lastBracket != -1 && firstBracket < lastBracket && (firstBrace == -1 || firstBracket < firstBrace)) {
            return trimmed.substring(firstBracket, lastBracket + 1);
        }
        if (firstBrace != -1 && lastBrace != -1 && firstBrace < lastBrace) {
            return trimmed.substring(firstBrace, lastBrace + 1);
        }
        
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

    private List<ProjectFileItem> parseProjectFiles(JsonNode filesNode) {
        List<ProjectFileItem> list = new ArrayList<>();
        for (JsonNode item : filesNode) {
            String path = item.path("filePath").asText(null);
            String content = item.path("content").asText(null);
            if (path != null && !path.isBlank() && content != null && !content.isBlank()) {
                list.add(new ProjectFileItem(path.trim(), content));
            }
        }
        return list;
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

    private record AiResponse(String content, String answer, String explanation, List<OptionItem> options, List<ProjectFileItem> projectFiles) {
    }

    private record OptionItem(String label, String text) {
    }

    private record ProjectFileItem(String filePath, String content) {
    }
}
