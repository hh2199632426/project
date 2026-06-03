package com.racingquiz.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.racingquiz.backend.entity.Question;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface QuestionMapper extends BaseMapper<Question> {
}
