package com.thylovezj.oa.controller;

import com.alibaba.fastjson.JSON;
import com.thylovezj.oa.entity.User;
import com.thylovezj.oa.service.UserService;
import com.thylovezj.oa.service.exception.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet(name = "LoginServlet",urlPatterns = "/check_login")
public class LoginServlet extends HttpServlet {
    Logger logger = LoggerFactory.getLogger(LoginServlet.class);
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=utf-8");
        //获取用户输入
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        UserService userService = new UserService();
        Map<String,Object> result = new HashMap<String,Object>();
        try {
            //业务逻辑
            User user = userService.checkLogin(username, password);
            HttpSession session = request.getSession();
            session.setAttribute("login_user",user);
            result.put("code","0");
            result.put("message","success");
            result.put("redirect_url","/index");
        } catch (BusinessException ex) {
            logger.error(ex.getMessage(),ex);
            result.put("code",ex.getCode());
            result.put("message",ex.getMessage());
        } catch (Exception ex){
            logger.error(ex.getMessage(),ex);
            result.put("code",ex.getClass().getSimpleName());
            result.put("message",ex.getMessage());
        }
        //返回输出
            String res = JSON.toJSONString(result);
            response.getWriter().println(res);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
