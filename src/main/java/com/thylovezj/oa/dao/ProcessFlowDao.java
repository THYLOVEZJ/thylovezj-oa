package com.thylovezj.oa.dao;

import com.thylovezj.oa.entity.ProcessFlow;

import java.util.List;

public interface ProcessFlowDao {
    public void insert(ProcessFlow processFlow);

    public void update(ProcessFlow processFlow);

    public List<ProcessFlow> selectByFormId(long formId);
}
