package com.thylovezj.oa.dao;

import com.thylovezj.oa.entity.LeaveForm;
import com.thylovezj.oa.utils.MybatisUtils;
import org.apache.ibatis.annotations.Param;
import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class LeaveFormDaoTest {
    @Test
    public void testInsert(){
        MybatisUtils.executeUpdate(sqlSession -> {
            LeaveFormDao leaveFormDao = sqlSession.getMapper(LeaveFormDao.class);
            LeaveForm leaveForm = new LeaveForm();
            leaveForm.setEmployeeId(2l);
            leaveForm.setFormType(1);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date startTime = null;
            Date endTime = null;
            try {
                startTime = sdf.parse("2020-03-25 12:00:00");
                endTime = sdf.parse("2020-04-01 12:00:00");
            } catch (ParseException e) {
                e.printStackTrace();
            }
            leaveForm.setStartTime(startTime);
            leaveForm.setEndTime(endTime);
            leaveForm.setReason("回家探亲");
            leaveForm.setCreateTime(new Date());
            leaveForm.setState("processing");
            leaveFormDao.insert(leaveForm);
            return null;
        });
    }

    @Test
    public void testSelectByParams(){
        MybatisUtils.executeQuery(sqlSession -> {
            LeaveFormDao leaveFormDao = sqlSession.getMapper(LeaveFormDao.class);
            List<Map> leaveForms = leaveFormDao.selectByParams(2l, "process");
            System.out.println(leaveForms);
            return leaveForms;
        });
    }
}