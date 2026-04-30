package com.fafu.learning.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fafu.learning.entity.Chapter;
import org.apache.ibatis.annotations.Mapper;

/**
 * 章节Mapper接口
 */
@Mapper
public interface ChapterMapper extends BaseMapper<Chapter> {
}