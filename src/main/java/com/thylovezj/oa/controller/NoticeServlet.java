package com.thylovezj.oa.controller;

import com.alibaba.fastjson.JSON;
import com.thylovezj.oa.entity.Notice;
import com.thylovezj.oa.entity.User;
import com.thylovezj.oa.service.NoticeService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(name = "NoticeServlet",urlPatterns = "/notice/list")
public class NoticeServlet extends HttpServlet {
    private NoticeService noticeService = new NoticeService();

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute("login_user");
        List<Notice> noticeList = noticeService.getNoticeList(user.getEmployeeId());
        Map res = new HashMap<>();
        res.put("code", 0);
        res.put("msg", "");
        res.put("count", noticeList.size());
        res.put("data", noticeList);
        String json = JSON.toJSONString(res);
        response.setContentType("text/html;charset=utf-8");
        response.getWriter().println(json);
    }
}
