package com.baiye.yudada.mapper;

import com.baiye.yudada.model.dto.statistic.AppAnswerCountDTO;
import com.baiye.yudada.model.dto.statistic.AppAnswerResultCountDTO;
import com.baiye.yudada.model.entity.UserAnswer;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author Admin
 * @description 针对表【user_answer(用户答题记录)】的数据库操作Mapper
 * @createDate 2025-11-07 17:26:35
 * @Entity com.baiye.yudada.model.entity.UserAnswer
 */
public interface UserAnswerMapper extends BaseMapper<UserAnswer> {


    @Select("select appId, count(userId) as answerCount from user_answer\n " +
            "group by appId order by answerCount desc limit 10")
    List<AppAnswerCountDTO> doAppAnswerCount();


    @Select("select resultName, count(resultName) as resultCount from user_answer " +
            "where appId = #{appId} group by resultName order by resultCount desc")
    List<AppAnswerResultCountDTO> doAppAnswerResultCount(Long appId);


}




