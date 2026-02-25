package com.fafu.learning.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fafu.learning.entity.Teacher;
import org.apache.ibatis.annotations.Mapper;

/**
 * 讲师Mapper接口
 */
@Mapper
public interface TeacherMapper extends BaseMapper<Teacher> {

}