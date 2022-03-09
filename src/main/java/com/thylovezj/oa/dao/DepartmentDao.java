package com.thylovezj.oa.dao;

import com.thylovezj.oa.entity.Department;


public interface DepartmentDao {
    public Department selectById(long departmentId);
}
