package com.thylovezj.oa.service;

import com.thylovezj.oa.dao.EmployeeDao;
import com.thylovezj.oa.dao.LeaveFormDao;
import com.thylovezj.oa.dao.NoticeDao;
import com.thylovezj.oa.dao.ProcessFlowDao;
import com.thylovezj.oa.entity.Employee;
import com.thylovezj.oa.entity.LeaveForm;
import com.thylovezj.oa.entity.Notice;
import com.thylovezj.oa.entity.ProcessFlow;
import com.thylovezj.oa.service.exception.BusinessException;
import com.thylovezj.oa.utils.MybatisUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class LeaveFormService {
    /**
     * @param leaveForm 从前端输入的请假单数据
     * @return 持久化后的请假单对象
     */
    public LeaveForm createLeaveForm(LeaveForm leaveForm) {

        LeaveForm savedForm = (LeaveForm) MybatisUtils.executeUpdate(sqlSession -> {
            //1.持久化form表单数据，8级以下员工表单状态为processing，8级(总经理)状态为approved
            long employeeId = leaveForm.getEmployeeId();
            EmployeeDao employeeDao = sqlSession.getMapper(EmployeeDao.class);
            Employee employee = employeeDao.getEmployeeById(employeeId);
            if (employee.getLevel() == 8) {
                leaveForm.setState("approved");
            } else {
                leaveForm.setState("processing");
            }
            LeaveFormDao leaveFormDao = sqlSession.getMapper(LeaveFormDao.class);
            leaveFormDao.insert(leaveForm);
            //2.增加第一条流程数据，说明表单已经提交，状态为complete
            ProcessFlowDao processFlowDao = sqlSession.getMapper(ProcessFlowDao.class);
            ProcessFlow flow1 = new ProcessFlow();
            flow1.setFormId(leaveForm.getFormId());
            flow1.setoperatorId(leaveForm.getEmployeeId());
            flow1.setAction("apply");
            flow1.setCreateTime(new Date());
            flow1.setOrderNo(1);
            flow1.setState("complete");
            flow1.setIsLast(0);
            processFlowDao.insert(flow1);
            //3.分情况创建其余流程数据
            //3.1 7级以下员工，生成部门经理审批任务，请假大于36小时，还需生成总经理审批任务
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH时");
            NoticeDao noticeDao = sqlSession.getMapper(NoticeDao.class);
            if (employee.getLevel() < 7) {
                Employee dmanager = employeeDao.selectLeader(employee);
                ProcessFlow flow2 = new ProcessFlow();
                flow2.setFormId(leaveForm.getFormId());
                flow2.setoperatorId(dmanager.getEmployeeId());
                flow2.setAction("audit");
                flow2.setCreateTime(new Date());
                flow2.setOrderNo(2);
                flow2.setState("process");
                long diff = leaveForm.getEndTime().getTime() - leaveForm.getStartTime().getTime();
                float hours = diff / (1000 * 60 * 60) * 1f;
                if (hours >= BussinessConstants.MANAGER_AUDIT_HOURS) {
                    flow2.setIsLast(0);
                    processFlowDao.insert(flow2);
                    Employee manager = employeeDao.selectLeader(dmanager);
                    ProcessFlow flow3 = new ProcessFlow();
                    flow3.setFormId(leaveForm.getFormId());
                    flow3.setoperatorId(manager.getEmployeeId());
                    flow3.setAction("audit");
                    flow3.setCreateTime(new Date());
                    //等待部门经理审批中
                    flow3.setState("ready");
                    flow3.setOrderNo(3);
                    flow3.setIsLast(1);
                    processFlowDao.insert(flow3);
                } else {
                    flow2.setIsLast(1);
                    processFlowDao.insert(flow2);
                }
                //请假单已提交消息
                String noticeContent = String.format("您的请假申请[%s-%s]已提交，请等待上级审批.", sdf.format(leaveForm.getStartTime()), sdf.format(leaveForm.getEndTime()));
                noticeDao.insert(new Notice(employee.getEmployeeId(), noticeContent));
                noticeContent = String.format("%s-%s提起请假申请[%s-%s],请尽快审批",
                        employee.getTitle(), employee.getEmployeeName(), sdf.format(leaveForm.getStartTime()), leaveForm.getEndTime());
                noticeDao.insert(new Notice(dmanager.getEmployeeId(), noticeContent));
            } else if (employee.getLevel() == 7) {
                //3.2 7级员工，生成总经理审批任务
                Employee manager = employeeDao.selectLeader(employee);
                ProcessFlow processFlow = new ProcessFlow();
                processFlow.setFormId(leaveForm.getFormId());
                processFlow.setoperatorId(manager.getEmployeeId());
                processFlow.setAction("audit");
                processFlow.setCreateTime(new Date());
                processFlow.setState("process");
                processFlow.setOrderNo(2);
                processFlow.setIsLast(1);
                processFlowDao.insert(processFlow);
                String noticeContent = String.format("您的请假申请[%s-%s]已提交，请等待上级审批.", sdf.format(leaveForm.getStartTime()), sdf.format(leaveForm.getEndTime()));
                noticeDao.insert(new Notice(employee.getEmployeeId(), noticeContent));
                //通知总经理审批消息
                noticeContent = String.format("%s-%s提起请假申请[%s-%s],请尽快审批",
                        employee.getTitle(), employee.getEmployeeName(), sdf.format(leaveForm.getStartTime()), leaveForm.getEndTime());
                noticeDao.insert(new Notice(manager.getEmployeeId(), noticeContent));
            } else if (employee.getLevel() == 8) {
                //3.3 8级员工，生成总经理审批任务，系统自动通过
                ProcessFlow flow = new ProcessFlow();
                flow.setFormId(leaveForm.getFormId());
                flow.setoperatorId(employee.getEmployeeId());
                flow.setAction("audit");
                flow.setResult("approved");
                flow.setReason("自动通过");
                flow.setCreateTime(new Date());
                flow.setAuditTime(new Date());
                flow.setState("complete");
                flow.setOrderNo(2);
                flow.setIsLast(1);
                processFlowDao.insert(flow);
                String noticeContent = String.format("您的请假申请[%s-%s]系统已经自动审批通过", sdf.format(leaveForm.getStartTime()), sdf.format(leaveForm.getEndTime()));
                noticeDao.insert(new Notice(employee.getEmployeeId(), noticeContent));
            }
            return leaveForm;
        });
        return savedForm;
    }

    /**
     * @param operatorId 指定经办人Id
     * @param state      ProcessFlow状态
     * @return 请假单与相关数据列表
     */
    public List<Map> getLeaveFormList(long operatorId, String state) {
        return (List<Map>) MybatisUtils.executeQuery(sqlSession -> {
            LeaveFormDao leaveFormDao = sqlSession.getMapper(LeaveFormDao.class);
            List<Map> leaveForms = leaveFormDao.selectByParams(operatorId, state);
            return leaveForms;
        });
    }

    public void audit(long formId, long operateId, String result, String reason) {
        MybatisUtils.executeUpdate(sqlSession -> {
            //1.无论同意或者驳回，当前状态变为complete
            ProcessFlowDao processFlowDao = sqlSession.getMapper(ProcessFlowDao.class);
            List<ProcessFlow> processFlows = processFlowDao.selectByFormId(formId);
            if (processFlows.size() == 0) {
                throw new BusinessException("PF001", "无效的审批流程");
            }
            ProcessFlow processFlow = null;
            //获取当前任务ProcessFlow对象
            List<ProcessFlow> processList = processFlows.stream().filter(p -> p.getoperatorId() == operateId && p.getState().equals("process")).collect(Collectors.toList());
            if (processList.size() == 0) {
                throw new BusinessException("PF002", "未找到待处理任务");
            } else {
                //如果有多个ready，那么获取第一个ready的processFlow
                processFlow = processList.get(0);
                processFlow.setState("complete");
                processFlow.setResult(result);
                processFlow.setReason(reason);
                processFlow.setAuditTime(new Date());
                processFlowDao.update(processFlow);
            }
            LeaveFormDao leaveFormDao = sqlSession.getMapper(LeaveFormDao.class);
            LeaveForm leaveForm = leaveFormDao.selectById(formId);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd-HH时");
            EmployeeDao employeeDao = sqlSession.getMapper(EmployeeDao.class);
            Employee employee = employeeDao.getEmployeeById(leaveForm.getEmployeeId());//表单提交人信息
            Employee operator = employeeDao.getEmployeeById(operateId);
//            LeaveForm form = leaveFormDao.selectById(employee.getEmployeeId());
            NoticeDao noticeDao = sqlSession.getMapper(NoticeDao.class);
//2.如果当前任务是最后一个节点，代表流程结束，更新请假单状态为对应的approved/refused
            if (processFlow.getIsLast() == 1) {
                leaveForm.setState(result);//approved|refused
                leaveFormDao.update(leaveForm);
                String strResult = null;
                if (result.equals("approved")) {
                    strResult = "批准";
                } else if (result.equals("refused")) {
                    strResult = "驳回";
                }
                String noticeContent = String.format("您的请假申请[%s-%s]%s%s已%s,审批意见:%s,审批流程已结束"
                        , simpleDateFormat.format(leaveForm.getStartTime()), simpleDateFormat.format(leaveForm.getEndTime())
                        , operator.getTitle(), operator.getEmployeeName()
                        , strResult, reason);
                noticeDao.insert(new Notice(leaveForm.getEmployeeId(), noticeContent));

                noticeContent = String.format("%s-%s提起的请假申请[%s-%s]您已%s,审批意见:%s,审批流程已结束"
                        , employee.getTitle(), employee.getEmployeeName()
                        , simpleDateFormat.format(leaveForm.getStartTime()), simpleDateFormat.format(leaveForm.getEndTime())
                        , strResult, reason);//发给审批人的通知
                noticeDao.insert(new Notice(operator.getEmployeeId(), noticeContent));
            } else {
                //3.如果当前任务不是最后一个节点且审批通过，那下一个的节点的状态从ready变为process
                //processFlows1包含后续所有节点
                List<ProcessFlow> processFlows1 = processFlows.stream().filter(p -> p.getState().equals("ready")).collect(Collectors.toList());
                if (result.equals("approved")) {
                    ProcessFlow processFlow1 = processFlows1.get(0);
                    processFlow1.setState("process");
                    processFlowDao.update(processFlow1);
                    //消息一:通知表单提交人，部门经理已经审批通过，交由上级继续审批
                    String noticeContent1 = String.format("您的请假申请[%s-%s]%s%s已批准,审批意见:%s,审批流程已结束"
                            , simpleDateFormat.format(leaveForm.getStartTime()), simpleDateFormat.format(leaveForm.getEndTime())
                            , operator.getTitle(), operator.getEmployeeName()
                            , reason);
                    noticeDao.insert(new Notice(leaveForm.getEmployeeId(),noticeContent1));
                    //消息二.通知总经理有新的审批任务
                    String noticeContent2 = String.format("%s-%s提起请假申请[%s-%s],请尽快审批"
                    ,employee.getTitle(),employee.getEmployeeName()
                    ,simpleDateFormat.format(leaveForm.getStartTime()),simpleDateFormat.format(leaveForm.getEndTime())
                    ,operator.getTitle(),operator.getEmployeeName());
                    noticeDao.insert(new Notice(processFlow1.getoperatorId(),noticeContent2));
                    //消息三.通知部门经理(当前经办人),员工的申请单你已批准，交由上级继续审批
                    String noticeContent3 = String.format("%s-%s提起的请假申请[%s-%s]您已批准,审批意见:%s"
                    ,employee.getTitle(),employee.getEmployeeName()
                    ,simpleDateFormat.format(leaveForm.getStartTime()),simpleDateFormat.format(leaveForm.getEndTime())
                    ,reason);
                    noticeDao.insert(new Notice(operator.getEmployeeId(),noticeContent3));
                } else if (result.equals("refused")) {
                    //4.如果当前任务不是最后一个节点且审批驳回，则后续所有任务的状态变为cancel请假单状态变为refused
                    for (ProcessFlow p : processFlows1) {
                        p.setState("cancel");
                        processFlowDao.update(p);
                    }
                    leaveForm.setState("refused");
                    leaveFormDao.update(leaveForm);
                    //消息一:通知申请人表单已被驳回
                    String noticeContent1 = String.format("您的请假申请[%s-%s]%s%s已驳回,审批意见:%s"
                    ,simpleDateFormat.format(leaveForm.getStartTime()),simpleDateFormat.format(leaveForm.getEndTime())
                    ,employee.getTitle(),employee.getEmployeeName()
                    ,reason);
                    noticeDao.insert(new Notice(operator.getEmployeeId(),noticeContent1));
                    //消息二:通知经办人表单"您已驳回"
                    String noticeContent3 = String.format("%s-%s提起的请假申请[%s-%s]您已驳回,审批意见:%s"
                            ,employee.getTitle(),employee.getEmployeeName()
                            ,simpleDateFormat.format(leaveForm.getStartTime()),simpleDateFormat.format(leaveForm.getEndTime())
                            ,reason);
                    noticeDao.insert(new Notice(operator.getEmployeeId(),noticeContent3));
                }
            }
            return null;
        });
    }
}
