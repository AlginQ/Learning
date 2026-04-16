package com.fafu.learning.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fafu.learning.entity.Note;
import org.apache.ibatis.annotations.Mapper;

/**
 * 笔记Mapper接口
 */
@Mapper
public interface NoteMapper extends BaseMapper<Note> {
}