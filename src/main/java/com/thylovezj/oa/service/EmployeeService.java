package com.thylovezj.oa.service;

import com.thylovezj.oa.dao.EmployeeDao;
import com.thylovezj.oa.entity.Employee;
import com.thylovezj.oa.utils.MybatisUtils;

public class EmployeeService {
    public Employee getEmployeeById(long employeeId){
        return  (Employee)MybatisUtils.executeQuery(sqlSession -> {
            EmployeeDao employeeDao = sqlSession.getMapper(EmployeeDao.class);
            return employeeDao.getEmployeeById(employeeId);
        });
    }
}
