package com.racingquiz.backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("users")
public class User {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String username;
    @TableField("nick_name")
    private String nickName;
    @TableField("last_checkin_date")
    private java.time.LocalDate lastCheckinDate;
    @TableField("total_checkin_days")
    private Integer totalCheckinDays;
    @JsonIgnore
    private String password;

    @TableField("created_at")
    private LocalDateTime createdAt;
}
