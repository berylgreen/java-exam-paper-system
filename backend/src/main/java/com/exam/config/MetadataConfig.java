package com.exam.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class MetadataConfig {

    private final ObjectMapper objectMapper;
    private final Metadata metadata = new Metadata();

    public MetadataConfig(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    public void init() {
        try {
            InputStream is = new ClassPathResource("metadata.json").getInputStream();
            Map<String, Object> data = objectMapper.readValue(is, new TypeReference<>() {});
            
            if (data.containsKey("chapters")) {
                List<Map<String, Object>> chaptersRaw = (List<Map<String, Object>>) data.get("chapters");
                for (Map<String, Object> raw : chaptersRaw) {
                    ChapterPreset preset = new ChapterPreset();
                    preset.setName((String) raw.get("name"));
                    preset.setSortOrder((Integer) raw.get("sortOrder"));
                    metadata.getChapters().add(preset);
                }
            }
            
            if (data.containsKey("sources")) {
                List<String> sourcesRaw = (List<String>) data.get("sources");
                metadata.getSources().addAll(sourcesRaw);
            }
            
            log.info("加载 metadata.json 成功: 预设了 {} 个章节和 {} 个来源", 
                    metadata.getChapters().size(), metadata.getSources().size());
        } catch (Exception e) {
            log.error("加载 metadata.json 失败", e);
        }
    }

    public List<ChapterPreset> getChapters() {
        return metadata.getChapters();
    }

    public List<String> getSources() {
        return metadata.getSources();
    }

    @Data
    public static class Metadata {
        private List<ChapterPreset> chapters = new ArrayList<>();
        private List<String> sources = new ArrayList<>();
    }

    @Data
    public static class ChapterPreset {
        private String name;
        private Integer sortOrder;
    }
}
