package com.thylovezj.oa.dao;

import com.thylovezj.oa.entity.LeaveForm;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;


public interface LeaveFormDao {
    public void insert(LeaveForm leaveForm);

    public List<Map> selectByParams(@Param("operatorId") long operatorId, @Param("state") String state);

    public LeaveForm selectById(long formId);

    public void update(LeaveForm leaveForm);
}
