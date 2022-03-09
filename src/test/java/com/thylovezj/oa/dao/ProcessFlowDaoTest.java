package com.thylovezj.oa.dao;

import com.thylovezj.oa.entity.ProcessFlow;
import com.thylovezj.oa.utils.MybatisUtils;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.*;

public class ProcessFlowDaoTest {
    @Test
    public void insert(){
        MybatisUtils.executeUpdate(sqlSession -> {
            ProcessFlowDao processFlowDao = sqlSession.getMapper(ProcessFlowDao.class);
            ProcessFlow processFlow = new ProcessFlow();
            processFlow.setFormId(1l);
            processFlow.setFormId(2l);
            processFlow.setAction("apply");
            processFlow.setResult("approved");
            processFlow.setReason("同意请假");
            processFlow.setAuditTime(new Date());
            processFlow.setCreateTime(new Date());
            processFlow.setOrderNo(1);
            processFlow.setIsLast(0);
            processFlow.setState("ready");
            processFlowDao.insert(processFlow);
            return null;
        });
    }
}