package com.baiye.yudada.manager;

import ai.z.openapi.ZhipuAiClient;
import ai.z.openapi.service.model.*;

import ai.z.openapi.core.Constants;
import java.util.Arrays;
import com.baiye.yudada.common.ErrorCode;
import com.baiye.yudada.exception.BusinessException;
import io.reactivex.rxjava3.core.Flowable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


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
@Slf4j
@Component
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
            ChatMessage systemChatMessage = new ChatMessage(ChatMessageRole.SYSTEM.value(), systemMessage);
            ChatMessage userChatMessage = new ChatMessage(ChatMessageRole.USER.value(), userMessage);
            conversation.add(systemChatMessage);
            conversation.add(userChatMessage);
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
                    .model(Constants.ModelChatGLM4)
                    .stream(stream)
                    .messages(conversation)
                    .temperature(temperature)
                    .maxTokens(1000)
                    .build();
            // 发送请求
            ChatCompletionResponse response = zhipuAiClient.chat().createChatCompletion(request);
            if (response.isSuccess()) {
                // 获取 AI 回复
                String result =  response.getData().getChoices().get(0).getMessage().getContent().toString();
                //log.info("AI连接测试结果: {} + 你好", result);
                return result;

            } else {
                return "发生错误: " + response.getMsg();
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, e.getMessage());
        }
    }



    /**
     * 流式通用请求（简化消息传递）
     * @param systemMessage
     * @param userMessage
     * @param temperature
     * @return
     */
    public Flowable<ModelData> doStreamRequest(String systemMessage,String userMessage, Float temperature) {
        try {
            List<ChatMessage> conversation = new ArrayList<>();
            ChatMessage systemChatMessage = new ChatMessage(ChatMessageRole.SYSTEM.value(), systemMessage);
            ChatMessage userChatMessage = new ChatMessage(ChatMessageRole.USER.value(), userMessage);
            conversation.add(systemChatMessage);
            conversation.add(userChatMessage);
            // 创建请求
            return doStreamRequest(conversation,temperature);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, e.getMessage());
        }
    }


    /**
     * 流式通用请求
     * @param conversation
     * @param temperature
     * @return
     */
    public Flowable<ModelData> doStreamRequest(List<ChatMessage> conversation, Float temperature) {
        try {
            // 创建请求
            ChatCompletionCreateParams request = ChatCompletionCreateParams.builder()
                    .model(Constants.ModelChatGLM4)
                    .stream(true)
                    .messages(conversation)
                    .temperature(temperature)
                    .maxTokens(1000)
                    .build();
            // 发送请求
            ChatCompletionResponse response = zhipuAiClient.chat().createChatCompletion(request);
            // 处理流式响应
            //if (response.isSuccess() && response.getFlowable() != null) {
            //    response.getFlowable().subscribe(  // 订阅流式响应
            //            data -> {
            //                // 处理流式数据块
            //                if (data.getChoices() != null && !data.getChoices().isEmpty()) {
            //                    Delta content = data.getChoices().get(0).getDelta();
            //                    System.out.print(content);
            //                }
            //            },
            //            error -> System.err.println("\n 流式错误: " + error.getMessage()),
            //            () -> System.out.println("\n 流式完成")
            //    );
            //}
            return response.getFlowable();
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, e.getMessage());
        }
    }



}


