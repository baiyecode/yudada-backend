package com.baiye.yudada.scoring;

import com.baiye.yudada.common.ErrorCode;
import com.baiye.yudada.exception.BusinessException;
import com.baiye.yudada.model.entity.App;
import com.baiye.yudada.model.entity.UserAnswer;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class ScoringStrategyExecutor {

    // 策略列表
    //Spring 容器会自动查找所有标记了 @Component（或 @Service、@Controller 等）
    // 并且实现了 ScoringStrategy 接口的类，将它们作为 Bean 创建出来，
    // 并将这些 Bean 的实例自动注入到这个 List 中。
    @Resource
    private List<ScoringStrategy> scoringStrategyList;


    /**
     * 评分
     *
     * @param choiceList
     * @param app
     * @return
     * @throws Exception
     */
    public UserAnswer doScore(List<String> choiceList, App app) throws Exception {
        Integer appType = app.getAppType();
        Integer appScoringStrategy = app.getScoringStrategy();
        if (appType == null || appScoringStrategy == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "应用配置有误，未找到匹配的策略");
        }
        // 根据注解获取策略
        for (ScoringStrategy strategy : scoringStrategyList) {
            // 检查当前策略类是否被 ScoringStrategyConfig 注解标记
            if (strategy.getClass().isAnnotationPresent(ScoringStrategyConfig.class)) {
                //获取该策略类上的 ScoringStrategyConfig 注解实例
                ScoringStrategyConfig scoringStrategyConfig = strategy.getClass().getAnnotation(ScoringStrategyConfig.class);
                // 比较注解中的 appType 和 scoringStrategy 是否与当前应用的配置匹配
                if (scoringStrategyConfig.appType() == appType && scoringStrategyConfig.scoringStrategy() == appScoringStrategy) {
                    // 如果匹配，调用该策略的 doScore 方法进行评分计算，并返回结果
                    return strategy.doScore(choiceList, app);
                }
            }
        }
        throw new BusinessException(ErrorCode.SYSTEM_ERROR, "应用配置有误，未找到匹配的策略");
    }
}
