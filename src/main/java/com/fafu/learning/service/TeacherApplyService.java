package com.fafu.learning.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fafu.learning.entity.TeacherApply;
import com.fafu.learning.dto.TeacherApplyDTO;

import java.util.List;

/**
 * 教师申请服务接口
 */
public interface TeacherApplyService extends IService<TeacherApply> {
    
    /**
     * 提交教师申请
     */
    void submitApply(TeacherApplyDTO applyDTO, Long userId);
    
    /**
     * 获取用户的申请状态
     */
    TeacherApply getApplyByUserId(Long userId);
    
    /**
     * 获取所有待审核的申请
     */
    List<TeacherApply> getPendingApplies();
    
    /**
     * 审核教师申请
     */
    void reviewApply(Long applyId, Integer status, String remark, Long reviewerId);
}