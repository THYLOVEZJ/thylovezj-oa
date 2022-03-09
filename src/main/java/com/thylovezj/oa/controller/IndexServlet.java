package com.thylovezj.oa.controller;

import com.thylovezj.oa.entity.Department;
import com.thylovezj.oa.entity.Employee;
import com.thylovezj.oa.entity.Node;
import com.thylovezj.oa.entity.User;
import com.thylovezj.oa.service.DepartmentService;
import com.thylovezj.oa.service.EmployeeService;
import com.thylovezj.oa.service.UserService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "IndexServlet",urlPatterns = "/index")
public class IndexServlet extends HttpServlet {
    private UserService userService=new UserService();
    private EmployeeService employeeService = new EmployeeService();
    private DepartmentService departmentService = new DepartmentService();
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        //得到当前登录用户对象
        User user = (User)session.getAttribute("login_user");
        long employeeId = user.getEmployeeId();
        //获取登录用户可用功能模块列表
        List<Node> nodeList = userService.selectNodeByUserId(user.getUserId());
        //获取登录用户雇佣信息
        Employee employee = employeeService.getEmployeeById(employeeId);
        //获取DepartmentId
        long departmentId = employee.getDepartmentId();
        //获取department对象
        Department department = departmentService.selectById(departmentId);
        //放入请求属性
        request.setAttribute("node_list",nodeList);
        session.setAttribute("current_employee",employee);
        session.setAttribute("current_employee_department",department);
        //请求派发至ftl进行展现
        request.getRequestDispatcher("/index.ftl").forward(request,response);
    }
}
