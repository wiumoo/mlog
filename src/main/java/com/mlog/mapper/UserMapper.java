package com.mlog.mapper;

import com.mlog.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {

    @Select("select * from user where phone = #{phone}")
    User findByPhone(String phone);

    @Insert("insert into user (phone, nickname, created_at, updated_at)" +
            "values (#{phone}, #{nickname}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(User user);

    User selectById(@Param("id") Long id);
}
