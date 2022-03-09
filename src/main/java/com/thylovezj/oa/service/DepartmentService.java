package com.thylovezj.oa.service;

import com.thylovezj.oa.dao.DepartmentDao;
import com.thylovezj.oa.dao.EmployeeDao;
import com.thylovezj.oa.entity.Department;
import com.thylovezj.oa.entity.Employee;
import com.thylovezj.oa.utils.MybatisUtils;

public class DepartmentService {
    public Department selectById(long departmentId){
        return (Department)MybatisUtils.executeQuery(sqlSession -> {
            DepartmentDao departmentDao = sqlSession.getMapper(DepartmentDao.class);
            return departmentDao.selectById(departmentId);
        });
    }
}
