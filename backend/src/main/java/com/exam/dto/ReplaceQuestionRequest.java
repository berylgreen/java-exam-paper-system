package com.exam.dto;

import lombok.Data;

@Data
public class ReplaceQuestionRequest {
    private Long oldQuestionId;
    private Long newQuestionId;
}
