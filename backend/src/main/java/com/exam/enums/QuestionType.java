package com.exam.enums;

/**
 * 题目类型枚举
 */
public enum QuestionType {
    SINGLE_CHOICE("单选题"),
    MULTIPLE_CHOICE("多选题"),
    TRUE_FALSE("判断题"),
    FILL_BLANK("填空题"),
    SHORT_ANSWER("简答题"),
    PROGRAMMING("编程题");

    private final String label;

    QuestionType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
