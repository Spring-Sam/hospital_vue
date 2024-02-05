package com.example.service;

import cn.hutool.core.util.ObjectUtil;
import com.example.common.Constants;
import com.example.common.enums.ResultCodeEnum;
import com.example.common.enums.RoleEnum;
import com.example.entity.Account;
import com.example.entity.Admin;
import com.example.entity.Department;
import com.example.entity.Doctor;
import com.example.exception.CustomException;
import com.example.mapper.AdminMapper;
import com.example.mapper.DepartmentMapper;
import com.example.mapper.DoctorMapper;
import com.example.utils.TokenUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.tomcat.jni.Local;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

/**
 * 管理员业务处理
 **/
@Service
public class DoctorService {

    @Resource
    private DoctorMapper doctorMapper;

    @Resource
    private DepartmentMapper departmentMapper;

    /**
     * 新增
     */
    public void add(Doctor admin) {
        Doctor dbAdmin = doctorMapper.selectByUsername(admin.getUsername());
        if (ObjectUtil.isNotNull(dbAdmin)) {
            throw new CustomException(ResultCodeEnum.USER_EXIST_ERROR);
        }
        if (ObjectUtil.isEmpty(admin.getPassword())) {
            admin.setPassword(Constants.USER_DEFAULT_PASSWORD);
        }
        if (ObjectUtil.isEmpty(admin.getName())) {
            admin.setName(admin.getUsername());
        }
        admin.setRole(RoleEnum.DOCTOR.name());
        doctorMapper.insert(admin);
    }

    /**
     * 删除
     */
    public void deleteById(Integer id) {
        doctorMapper.deleteById(id);
    }

    /**
     * 批量删除
     */
    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            doctorMapper.deleteById(id);
        }
    }

    /**
     * 修改
     */
    public void updateById(Doctor admin) {
        doctorMapper.updateById(admin);
    }

    /**
     * 根据ID查询
     */
    public Doctor selectById(Integer id) {
        return doctorMapper.selectById(id);
    }

    /**
     * 查询所有
     */
    public List<Doctor> selectAll(Doctor admin) {
        return doctorMapper.selectAll(admin);
    }

    /**
     * 分页查询
     */
    public PageInfo<Doctor> selectPage(Doctor admin, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Doctor> list = doctorMapper.selectAll(admin);
        return PageInfo.of(list);
    }

    /**
     * 登录
     */
    public Account login(Account account) {
        Doctor dbAdmin = doctorMapper.selectByUsername(account.getUsername());
        if (ObjectUtil.isNull(dbAdmin)) {
            throw new CustomException(ResultCodeEnum.USER_NOT_EXIST_ERROR);
        }
        if (!account.getPassword().equals(dbAdmin.getPassword())) {
            throw new CustomException(ResultCodeEnum.USER_ACCOUNT_ERROR);
        }
        // 生成token
        String tokenData = dbAdmin.getId() + "-" + RoleEnum.DOCTOR.name();
        String token = TokenUtils.createToken(tokenData, dbAdmin.getPassword());
        dbAdmin.setToken(token);

        if(ObjectUtil.isNotEmpty(dbAdmin.getDepartmentId())){
            Department department = departmentMapper.selectById(dbAdmin.getDepartmentId());
            if(ObjectUtil.isNotEmpty(department)){
                dbAdmin.setDepartmentName(department.getName());
            }
        }


        return dbAdmin;
    }

    /**
     * 注册
     */
    public void register(Account account) {
        Doctor admin = new Doctor();
        BeanUtils.copyProperties(account, admin);
        add(admin);
    }

    /**
     * 修改密码
     */
        public void updatePassword(Account account) {
            Doctor dbAdmin = doctorMapper.selectByUsername(account.getUsername());
            if (ObjectUtil.isNull(dbAdmin)) {
                throw new CustomException(ResultCodeEnum.USER_NOT_EXIST_ERROR);
            }
            if (!account.getPassword().equals(dbAdmin.getPassword())) {
                throw new CustomException(ResultCodeEnum.PARAM_PASSWORD_ERROR);
            }
            dbAdmin.setPassword(account.getNewPassword());
            doctorMapper.updateById(dbAdmin);
        }

    public PageInfo<Doctor> selectPageCard(Doctor doctor, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        String week = getTodayWeek();
        doctor.setWeek(week);
        List<Doctor> list = doctorMapper.selectAll(doctor);
        return PageInfo.of(list);
    }

    /**
     * 獲取今天是星期幾
     * @return
     */
    private String getTodayWeek(){
        LocalDate today = LocalDate.now();
        DayOfWeek dayOfWeek = today.getDayOfWeek();
        return dayOfWeek.getDisplayName(TextStyle.FULL_STANDALONE, Locale.CHINA);

    }



}