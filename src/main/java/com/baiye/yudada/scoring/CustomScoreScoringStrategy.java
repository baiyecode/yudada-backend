package com.baiye.yudada.scoring;

import cn.hutool.json.JSONUtil;
import com.baiye.yudada.common.ErrorCode;
import com.baiye.yudada.exception.BusinessException;
import com.baiye.yudada.model.dto.question.QuestionContentDTO;
import com.baiye.yudada.model.entity.App;
import com.baiye.yudada.model.entity.Question;
import com.baiye.yudada.model.entity.ScoringResult;
import com.baiye.yudada.model.entity.UserAnswer;
import com.baiye.yudada.model.vo.QuestionVO;
import com.baiye.yudada.service.QuestionService;
import com.baiye.yudada.service.ScoringResultService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * ClassName: CustomScoreScoringStrategy
 * Package: com.baiye.yudada.scoring
 * Description:
 *
 * @Author 白夜
 * @Create 2025/11/8 20:04
 * @Version 1.0
 */
@ScoringStrategyConfig(appType = 0, scoringStrategy = 0)
public class CustomScoreScoringStrategy implements ScoringStrategy {

    @Resource
    private QuestionService questionService;

    @Resource
    private ScoringResultService scoringResultService;

    /**
     * 执行评分方法
     * @param choices 用户选择的答案列表
     * @param app 应用信息对象
     * @return UserAnswer 用户答案对象，包含评分结果
     * @throws Exception 可能抛出的异常
     */
    @Override
    public UserAnswer doScore(List<String> choices, App app) throws Exception {
        Long appId = app.getId();
        // 1. 根据 id 查询到题目和题目结果信息（按分数降序排序）
        Question question = questionService.getOne(
                Wrappers.lambdaQuery(Question.class).eq(Question::getAppId, appId)
        );
        List<ScoringResult> scoringResultList = scoringResultService.list(
                Wrappers.lambdaQuery(ScoringResult.class)
                        .eq(ScoringResult::getAppId, appId)
                        .orderByDesc(ScoringResult::getResultScoreRange)
        );

        // 2. 统计用户的总得分
        int totalScore = 0;
        QuestionVO questionVO = QuestionVO.objToVo(question);
        List<QuestionContentDTO> questionContent = questionVO.getQuestionContent();
        // 校验数量
        if (questionContent.size() != choices.size()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "题目和用户答案数量不一致");
        }
        // 遍历题目列表
        for (int i = 0; i < questionContent.size(); i++) {
            // 将当前题目的选项 key 和 score 映射成 Map，例如 {"A": 10, "B": 5, "C": 0}
            Map<String, Integer> resultMap = questionContent.get(i).getOptions().stream()
                    .collect(Collectors.toMap(QuestionContentDTO.Option::getKey, QuestionContentDTO.Option::getScore));

            // 获取用户对第 i 题的答案,从 Map 中查找该答案对应的分数，如果找不到则返回 0
            Integer score = Optional.ofNullable(resultMap.get(choices.get(i))).orElse(0);
            totalScore += score;
        }

        // 3. 遍历得分结果，找到第一个用户分数大于得分范围的结果，作为最终结果
        ScoringResult maxScoringResult = scoringResultList.get(0);// 初始化结果为排序后的第一个 (最高分范围)
        // 遍历已按分数降序排列的结果列表
        for (ScoringResult scoringResult : scoringResultList) {
            // 检查用户总分是否大于等于当前结果的最低分要求
            if (totalScore >= scoringResult.getResultScoreRange()) {
                // 找到第一个满足条件的结果，即为最匹配的（因为列表是降序的）
                maxScoringResult = scoringResult;
                break;
            }
        }

        // 4. 构造返回值，填充答案对象的属性
        UserAnswer userAnswer = new UserAnswer();
        userAnswer.setAppId(appId);
        userAnswer.setAppType(app.getAppType());
        userAnswer.setScoringStrategy(app.getScoringStrategy());
        userAnswer.setChoices(JSONUtil.toJsonStr(choices));
        userAnswer.setResultId(maxScoringResult.getId());
        userAnswer.setResultName(maxScoringResult.getResultName());
        userAnswer.setResultDesc(maxScoringResult.getResultDesc());
        userAnswer.setResultPicture(maxScoringResult.getResultPicture());
        userAnswer.setResultScore(totalScore);
        return userAnswer;
    }
}
