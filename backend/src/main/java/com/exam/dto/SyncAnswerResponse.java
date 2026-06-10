package com.exam.dto;

import lombok.Data;
import java.util.List;

@Data
public class SyncAnswerResponse {
    private List<String> updatedFiles;
}
