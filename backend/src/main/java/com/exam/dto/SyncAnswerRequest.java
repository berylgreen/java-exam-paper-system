package com.exam.dto;

import lombok.Data;

@Data
public class SyncAnswerRequest {
    private String answerText;
    private String answerProjectPath;
}
