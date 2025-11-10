package com.baiye.yudada.scoring;

import cn.hutool.json.JSONUtil;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ClassName: CustomTestScoringStrategy
 * Package: com.baiye.yudada.scoring
 * Description:
 *
 * @Author 白夜
 * @Create 2025/11/8 19:28
 * @Version 1.0
 */
@ScoringStrategyConfig(appType = 1, scoringStrategy = 0)
public class CustomTestScoringStrategy implements ScoringStrategy {

    @Resource
    private QuestionService questionService;

    @Resource
    private ScoringResultService scoringResultService;


    @Override
    public UserAnswer doScore(List<String> choices, App app) throws Exception {
        Long appId = app.getId();
        // 1. 根据 id 查询到题目和题目结果信息
        Question question = questionService.getOne(
                Wrappers.lambdaQuery(Question.class).eq(Question::getAppId, appId)
        );
        List<ScoringResult> scoringResultList = scoringResultService.list(
                Wrappers.lambdaQuery(ScoringResult.class)
                        .eq(ScoringResult::getAppId, appId)
        );

        // 2. 统计用户每个选择对应的属性个数，如 I = 10 个，E = 5 个
        // 初始化一个Map，用于存储每个选项的计数
        Map<String, Integer> optionCount = new HashMap<>();

        QuestionVO questionVO = QuestionVO.objToVo(question);
        List<QuestionContentDTO> questionContent = questionVO.getQuestionContent();

        // 遍历题目列表
        for (QuestionContentDTO questionContentDTO : questionContent) {
            // 遍历答案列表
            for (String answer : choices) {
                // 遍历题目中的选项
                for (QuestionContentDTO.Option option : questionContentDTO.getOptions()) {
                    // 如果答案和选项的key匹配
                    if (option.getKey().equals(answer)) {
                        // 获取选项的result属性
                        String result = option.getResult();

                        // 如果result属性不在optionCount中，初始化为0
                        if (!optionCount.containsKey(result)) {
                            optionCount.put(result, 0);
                        }

                        // 在optionCount中增加计数
                        optionCount.put(result, optionCount.get(result) + 1);
                    }
                }
            }
        }

        // 3. 遍历每种评分结果，计算哪个结果的得分更高
        // 初始化最高分数和最高分数对应的评分结果
        int maxScore = 0;
        ScoringResult maxScoringResult = scoringResultList.get(0);

        // 遍历评分结果列表
        for (ScoringResult scoringResult : scoringResultList) {
            List<String> resultProp = JSONUtil.toList(scoringResult.getResultProp(), String.class);
            // 计算当前评分结果的分数，[I, E] => [10, 5] => 15
            //作用：对 resultProp 中的每一个属性 (prop)，去查找用户实际选择了多少次这个属性。
            //prop：代表 resultProp 列表中的一个元素，比如第一次循环是 "E"，第二次是 "I"，以此类推。
            //optionCount.get(prop)：尝试从 optionCount 这个 Map 中获取键为 prop 的值。例如，如果 prop 是 "E"，就获取 optionCount 中 "E" 对应的计数（比如是 7）。
            //.getOrDefault(..., 0)：如果 optionCount 中没有这个 prop 键（比如用户一个都没选 "A"），则返回默认值 0。
            //.mapToInt(...)：将每个 prop 映射成一个 int 类型的计数值。
            int score = resultProp.stream()
                    .mapToInt(prop -> optionCount.getOrDefault(prop, 0))
                    .sum();//将上一步 mapToInt 产生的整数流（例如 [7, 3, 6, 3]）中的所有数字相加起来。

            // 如果分数高于当前最高分数，更新最高分数和最高分数对应的评分结果
            if (score > maxScore) {
                maxScore = score;
                maxScoringResult = scoringResult;
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
        return userAnswer;
    }
}
