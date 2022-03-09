package com.thylovezj.oa.service;

import com.thylovezj.oa.dao.RbacDao;
import com.thylovezj.oa.dao.UserDao;
import com.thylovezj.oa.entity.Node;
import com.thylovezj.oa.entity.User;
import com.thylovezj.oa.service.exception.BusinessException;
import com.thylovezj.oa.utils.MybatisUtils;

import java.util.List;

public class UserService {
    private UserDao userDao = new UserDao();
    private RbacDao rbacDao = new RbacDao();
    /**
     * 根据前台输入进行登录校验
     * @param username 前台输入的用户名
     * @param password 前台输入的密码
     * @return 校验通过后，包含用户数据的User实体类
     * @throws BusinessException L001 用户名不存在 L002 密码错误
     */
    public User checkLogin(String username , String password){
        User user = userDao.selectByUsername(username);
        if (user == null){
            //如果user返回null，则抛出用户不存在
            throw new BusinessException("L001","用户名不存在");
        }
        if (!password.equals(user.getPassword())){
            throw new BusinessException("L002","密码错误");
        }
        return user;
    }

    public List<Node> selectNodeByUserId(Long userId){
        List<Node> nodeList = rbacDao.selectNodeByUserId(userId);
        return nodeList;
    }
}
