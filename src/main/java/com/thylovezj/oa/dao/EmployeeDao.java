package com.thylovezj.oa.dao;

import com.thylovezj.oa.entity.Employee;
import org.apache.ibatis.annotations.Param;

public interface EmployeeDao {
    public Employee getEmployeeById(long employeeId);

    /**
     *
     * @param employee 传入员工对象
     * @return 返回上级员工对象
     */
    public Employee selectLeader(@Param("emp") Employee employee);
}
