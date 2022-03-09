package com.thylovezj.oa.controller;

import com.alibaba.fastjson.JSON;
import com.thylovezj.oa.entity.LeaveForm;
import com.thylovezj.oa.entity.User;
import com.thylovezj.oa.service.LeaveFormService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(name = "LeaveFormServlet", urlPatterns = "/leave/*")
public class LeaveFormServlet extends HttpServlet {
    private LeaveFormService leaveFormService = new LeaveFormService();
    private Logger logger = LoggerFactory.getLogger(LeaveFormServlet.class);

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");
        response.setContentType("text/html;charset=utf-8");
        // http://localhost/leave/create
        String requestURI = request.getRequestURI();
        String methodName = requestURI.substring(requestURI.lastIndexOf("/") + 1);
        if (methodName.equals("create")) {
            this.create(request, response);
        }else if (methodName.equals("list")){
            this.getLeaveFormList(request,response);
        }else if (methodName.equals("audit")){
            this.audit(request,response);
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.doPost(request, response);
    }

    /**
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    protected void create(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //接收各项请假单的数据
        HttpSession session = request.getSession();
        User login_user = (User) session.getAttribute("login_user");
        String formType = request.getParameter("formType");
        String startTime = request.getParameter("startTime");
        String endTime = request.getParameter("endTime");
        String reason = request.getParameter("reason");


        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd-HH");

        Map result = new HashMap<>();
        try {
            LeaveForm form = new LeaveForm();
            form.setEmployeeId(login_user.getEmployeeId());
            form.setStartTime(simpleDateFormat.parse(startTime));
            form.setEndTime(simpleDateFormat.parse(endTime));
            form.setFormType(Integer.parseInt(formType));
            form.setReason(reason);
            form.setCreateTime(new Date());
            //2.调用逻辑方法
            leaveFormService.createLeaveForm(form);
            result.put("code", 0);
            result.put("message", "success");
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("请假申请异常");
            result.put("code", e.getClass().getSimpleName());
            result.put("message", e.getMessage());
        }
        //3.组织响应输出
        String json = JSON.toJSONString(result);
        response.getWriter().println(json);
    }

    private void getLeaveFormList(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute("login_user");
        List<Map> leaveFormList = leaveFormService.getLeaveFormList(user.getEmployeeId(), "process");
        Map map = new HashMap<>();
        map.put("code", 0);
        map.put("msg", "");
        map.put("count", leaveFormList.size());
        map.put("data", leaveFormList);
        String json = JSON.toJSONString(map);
        response.getWriter().println(json);
    }

    protected void audit(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String formId = request.getParameter("formId");
        String result = request.getParameter("result");
        String reason = request.getParameter("reason");
        User user = (User)request.getSession().getAttribute("login_user");
        Map mpResult = new HashMap();
        try {
            leaveFormService.audit(Long.parseLong(formId),user.getEmployeeId(),result,reason);
            mpResult.put("code",0);
            mpResult.put("message","success");
        }catch (Exception e){
            logger.error("请假单审核失败");
            mpResult.put("code",e.getClass().getSimpleName());
            mpResult.put("message",e.getMessage());
        }
        String json = JSON.toJSONString(mpResult);
        response.getWriter().println(json);
    }
}
