package com.baiye.yudada;

import com.baiye.yudada.controller.QuestionController;
import com.baiye.yudada.model.dto.question.AiGenerateQuestionRequest;
import org.junit.jupiter.api.Test;

import javax.annotation.Resource;

/**
 * ClassName: QuestionControllerTest
 * Package: com.baiye.yudada
 * Description:
 *
 * @Author 白夜
 * @Create 2025/11/13 15:44
 * @Version 1.0
 */
public class QuestionControllerTest {

    @Resource
    private QuestionController questionController;

    @Test
    void aiGenerateQuestionSSETest() throws InterruptedException {
        // 模拟调用
        AiGenerateQuestionRequest aiGenerateQuestionRequest = new AiGenerateQuestionRequest();
        aiGenerateQuestionRequest.setAppId(3L);
        aiGenerateQuestionRequest.setQuestionNumber(10);
        aiGenerateQuestionRequest.setOptionNumber(2);

        // 模拟普通用户
        questionController.aiGenerateQuestionSSETest(aiGenerateQuestionRequest, false);
        // 模拟普通用户
        questionController.aiGenerateQuestionSSETest(aiGenerateQuestionRequest, false);
        // 模拟会员用户
        questionController.aiGenerateQuestionSSETest(aiGenerateQuestionRequest, true);

        // 模拟主线程一直启动
        Thread.sleep(1000000L);
    }
}
