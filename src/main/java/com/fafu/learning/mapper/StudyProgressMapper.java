package com.fafu.learning.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fafu.learning.entity.StudyProgress;
import org.apache.ibatis.annotations.Mapper;

/**
 * 学习进度Mapper接口
 */
@Mapper
public interface StudyProgressMapper extends BaseMapper<StudyProgress> {
}