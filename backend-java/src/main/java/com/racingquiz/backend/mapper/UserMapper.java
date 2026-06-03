package com.racingquiz.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.racingquiz.backend.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
