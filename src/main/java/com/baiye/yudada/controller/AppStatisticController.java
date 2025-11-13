package com.baiye.yudada.controller;

import com.baiye.yudada.common.BaseResponse;
import com.baiye.yudada.common.ErrorCode;
import com.baiye.yudada.common.ResultUtils;
import com.baiye.yudada.exception.ThrowUtils;
import com.baiye.yudada.mapper.UserAnswerMapper;
import com.baiye.yudada.model.dto.statistic.AppAnswerCountDTO;
import com.baiye.yudada.model.dto.statistic.AppAnswerResultCountDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * ClassName: AppStatisticController
 * Package: com.baiye.yudada.controller
 * Description:
 *
 * @Author 白夜
 * @Create 2025/11/13 16:35
 * @Version 1.0
 */
@RestController
@RequestMapping("/app/statistic")
@Slf4j
public class AppStatisticController {

    @Resource
    private UserAnswerMapper userAnswerMapper;

    /**
     * 热门应用及回答数统计（top 10）
     *
     * @return
     */
    @GetMapping("/answer_count")
    public BaseResponse<List<AppAnswerCountDTO>> getAppAnswerCount() {
        return ResultUtils.success(userAnswerMapper.doAppAnswerCount());
    }

    /**
     * 某应用回答结果分布统计
     *
     * @param appId
     * @return
     */
    @GetMapping("/answer_result_count")
    public BaseResponse<List<AppAnswerResultCountDTO>> getAppAnswerResultCount(Long appId) {
        ThrowUtils.throwIf(appId == null || appId <= 0, ErrorCode.PARAMS_ERROR);
        return ResultUtils.success(userAnswerMapper.doAppAnswerResultCount(appId));
    }
}
