package com.baiye.yudada.manager;

import ai.z.openapi.ZhipuAiClient;
import ai.z.openapi.service.model.ChatCompletionCreateParams;
import ai.z.openapi.service.model.ChatCompletionResponse;
import ai.z.openapi.service.model.ChatMessage;
import ai.z.openapi.service.model.ChatMessageRole;
import com.baiye.yudada.common.ErrorCode;
import com.baiye.yudada.exception.BusinessException;


import javax.annotation.Resource;
import java.util.*;
import java.util.ArrayList;

/**
 * ClassName: AiManager
 * Package: com.baiye.yudada.manager
 * Description:
 *
 * @Author 白夜
 * @Create 2025/11/9 22:48
 * @Version 1.0
 */
public class AiManager {

    @Resource
    private ZhipuAiClient zhipuAiClient;


    // 较稳定的随机数
    private static final float STABLE_TEMPERATURE = 0.05f;

    // 不稳定的随机数
    private static final float UNSTABLE_TEMPERATURE = 0.99f;


    /**
     * 同步请求温度高（创建题目需要创意，随机）
     * @param systemMessage
     * @param userMessage
     * @return
     */
    public String doSyncUnStableRequest(String systemMessage,String userMessage) {
        try {
            // 创建请求
            return doRequest(systemMessage,userMessage,false,UNSTABLE_TEMPERATURE);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, e.getMessage());
        }
    }



    /**
     * 同步请求温度低（答案需要严谨稳定）
     * @param systemMessage
     * @param userMessage
     * @return
     */
    public String doSyncStableRequest(String systemMessage,String userMessage) {
        try {
            // 创建请求
            return doRequest(systemMessage,userMessage,false,STABLE_TEMPERATURE);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, e.getMessage());
        }
    }


    /**
     * 同步请求（不使用流式）
     * @param systemMessage
     * @param userMessage
     * @param temperature
     * @return
     */
    public String doSyncRequest(String systemMessage,String userMessage, Float temperature) {
        try {
            // 创建请求
            return doRequest(systemMessage,userMessage,false,temperature);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, e.getMessage());
        }
    }



    /**
     * 通用请求（简化消息传递）
     * @param systemMessage
     * @param userMessage
     * @param stream
     * @param temperature
     * @return
     */
    public String doRequest(String systemMessage,String userMessage, Boolean stream, Float temperature) {
        try {
            List<ChatMessage> conversation = new ArrayList<>();
            // 添加系统消息
            conversation.add(ChatMessage.builder()
                    .role(ChatMessageRole.SYSTEM.value())
                    .content(systemMessage)
                    .build());
            // 添加用户消息
            conversation.add(ChatMessage.builder()
                    .role(ChatMessageRole.USER.value())
                    .content(userMessage)
                    .build());
            // 创建请求
            return doRequest(conversation,stream,temperature);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, e.getMessage());
        }
    }



    /**
     * 通用请求
     * @param conversation
     * @param stream
     * @param temperature
     * @return
     */
    public String doRequest(List<ChatMessage> conversation, Boolean stream, Float temperature) {
        try {
            // 创建请求
            ChatCompletionCreateParams request = ChatCompletionCreateParams.builder()
                    .model("glm-4.6")
                    .stream(stream)
                    .messages(conversation)
                    .temperature(temperature)
                    .maxTokens(1000)
                    .build();
            // 发送请求
            ChatCompletionResponse response = zhipuAiClient.chat().createChatCompletion(request);
            if (response.isSuccess()) {
                // 获取 AI 回复
                return response.getData().getChoices().get(0).getMessage().getContent().toString();
            } else {
                return "发生错误: " + response.getMsg();
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, e.getMessage());
        }
    }


}


