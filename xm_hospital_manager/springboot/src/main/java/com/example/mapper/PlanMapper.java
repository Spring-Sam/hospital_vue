package com.example.mapper;

import com.example.entity.Plan;

import java.util.List;

/**
 * 操作醫生排班相关数据接口
*/
public interface PlanMapper {

    /**
      * 新增
    */
    int insert(Plan notice);

    /**
      * 删除
    */
    int deleteById(Integer id);

    /**
      * 修改
    */
    int updateById(Plan notice);

    /**
      * 根据ID查询
    */
    Plan selectById(Integer id);

    /**
      * 查询所有
    */
    List<Plan> selectAll(Plan notice);

}