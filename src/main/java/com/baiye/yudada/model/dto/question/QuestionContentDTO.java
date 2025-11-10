package com.baiye.yudada.model.dto.question;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * ClassName: QuestionContentDTO
 * Package: com.baiye.yudada.model.dto.question
 * Description:
 *
 * @Author 白夜
 * @Create 2025/11/7 19:59
 * @Version 1.0
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionContentDTO {

    /**
     * 题目标题
     */
    private String title;

    /**
     * 题目选项列表
     */
    private List<Option> options;

    /**
     * 题目选项
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Option {
        private String result;
        private int score;
        private String value;
        private String key;
    }
}
