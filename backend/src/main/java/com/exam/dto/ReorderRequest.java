package com.exam.dto;

import lombok.Data;
import java.util.List;

@Data
public class ReorderRequest {
    private List<ReorderItem> questions;

    @Data
    public static class ReorderItem {
        private Long questionId;
        private Integer questionOrder;
    }
}
