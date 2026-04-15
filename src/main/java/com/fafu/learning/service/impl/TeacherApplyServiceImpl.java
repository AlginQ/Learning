package com.fafu.learning.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fafu.learning.entity.TeacherApply;
import com.fafu.learning.entity.User;
import com.fafu.learning.mapper.TeacherApplyMapper;
import com.fafu.learning.service.TeacherApplyService;
import com.fafu.learning.service.UserService;
import com.fafu.learning.dto.TeacherApplyDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 教师申请服务实现
 */
@Service
public class TeacherApplyServiceImpl extends ServiceImpl<TeacherApplyMapper, TeacherApply> implements TeacherApplyService {
    
    @Autowired
    private UserService userService;
    
    @Override
    public void submitApply(TeacherApplyDTO applyDTO, Long userId) {
        // 检查用户是否已提交过申请
        TeacherApply existingApply = getApplyByUserId(userId);
        if (existingApply != null) {
            throw new RuntimeException("您已经提交过教师申请，请勿重复提交");
        }
        
        // 创建新的申请
        TeacherApply apply = new TeacherApply();
        apply.setUserId(userId);
        apply.setRealName(applyDTO.getRealName());
        apply.setMajor(applyDTO.getMajor());
        apply.setQualification(applyDTO.getQualification());
        apply.setStatus(0); // 0-待审核
        apply.setCreateTime(new Date());
        apply.setUpdateTime(new Date());
        
        save(apply);
    }
    
    @Override
    public TeacherApply getApplyByUserId(Long userId) {
        return getOne(new QueryWrapper<TeacherApply>().eq("user_id", userId).eq("status", 0));
    }
    
    @Override
    public List<TeacherApply> getPendingApplies() {
        return list(new QueryWrapper<TeacherApply>().eq("status", 0));
    }
    
    @Override
    @Transactional
    public void reviewApply(Long applyId, Integer status, String remark, Long reviewerId) {
        TeacherApply apply = getById(applyId);
        if (apply == null) {
            throw new RuntimeException("申请不存在");
        }
        
        if (apply.getStatus() != 0) {
            throw new RuntimeException("该申请已经审核过");
        }
        
        // 更新申请状态
        apply.setStatus(status);
        apply.setReviewerId(reviewerId);
        apply.setReviewTime(new Date());
        apply.setReviewRemark(remark);
        apply.setUpdateTime(new Date());
        
        updateById(apply);
        
        // 如果审核通过，更新用户角色为TEACHER，并更新用户信息
        if (status == 1) {
            User user = userService.getById(apply.getUserId());
            if (user != null) {
                System.out.println("更新用户信息: userId=" + user.getId() + ", realName=" + apply.getRealName() + ", major=" + apply.getMajor() + ", qualification=" + apply.getQualification());
                user.setRole("TEACHER");
                user.setRealName(apply.getRealName());
                user.setSpecialty(apply.getMajor());
                user.setIntroduction(apply.getQualification());
                boolean updateResult = userService.updateById(user);
                System.out.println("更新结果: " + updateResult);
            } else {
                System.out.println("用户不存在: userId=" + apply.getUserId());
            }
            // 审核通过后删除申请记录
            removeById(applyId);
            System.out.println("审核通过，删除申请记录: applyId=" + applyId);
        }
    }
}