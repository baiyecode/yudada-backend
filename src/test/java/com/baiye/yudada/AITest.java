package com.baiye.yudada;

import ai.z.openapi.service.model.ChatMessage;
import ai.z.openapi.service.model.ChatMessageRole;
import com.baiye.yudada.manager.AiManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * ClassName: AITest
 * Package: com.baiye.yudada
 * Description:
 *
 * @Author 白夜
 * @Create 2025/11/11 20:46
 * @Version 1.0
 */

@Slf4j
@SpringBootTest
public class AITest {

    @Resource
    private AiManager aiManager;

    @Test
    public void testAiConnection() {
        List<ChatMessage> conversation = new ArrayList<>();
        ChatMessage systemChatMessage = new ChatMessage(ChatMessageRole.SYSTEM.value(), "你是一个有用的AI助手");
        ChatMessage userChatMessage = new ChatMessage(ChatMessageRole.USER.value(), "测试连接");
        conversation.add(systemChatMessage);
        conversation.add(userChatMessage);
        String result = aiManager.doRequest(conversation,false,0.6f);
        log.info("AI连接测试结果: {} + 你好", result);
    }

}
