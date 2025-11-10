package com.baiye.yudada;

import ai.z.openapi.ZhipuAiClient;
import ai.z.openapi.service.model.ChatCompletionCreateParams;
import ai.z.openapi.service.model.ChatCompletionResponse;
import ai.z.openapi.service.model.ChatMessage;
import ai.z.openapi.service.model.ChatMessageRole;
import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/**
 * ClassName: QuickStart
 * Package: com.baiye.yudada
 * Description: 智谱AI测试
 *
 * @Author 白夜
 * @Create 2025/11/9 21:17
 * @Version 1.0
 */

@SpringBootTest
public class QuickStart {

    @Resource
    private ZhipuAiClient zhipuAiClient;


    @Test
    public void test() {
            // 初始化客户端
            //ZhipuAiClient client = ZhipuAiClient.builder()
            //        .apiKey("YOUR_API_KEY")
            //        .build();

            // 创建聊天完成请求
            ChatCompletionCreateParams request = ChatCompletionCreateParams.builder()
                    .model("glm-4.6")
                    .messages(Arrays.asList(
                            ChatMessage.builder()
                                    .role(ChatMessageRole.USER.value())
                                    .content("你好，请介绍一下自己")
                                    .build()
                    ))
                    .build();

            // 发送请求
            ChatCompletionResponse response = zhipuAiClient.chat().createChatCompletion(request);

            // 获取回复
            if (response.isSuccess()) {
                Object reply = response.getData().getChoices().get(0).getMessage();
                System.out.println("AI 回复: " + reply);
            } else {
                System.err.println("错误: " + response.getMsg());
            }
    }



}
