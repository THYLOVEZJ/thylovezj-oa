package com.thylovezj.oa.service;

import com.thylovezj.oa.entity.Node;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class UserServiceTest {
    private UserService userService = new UserService();

    @Test
    public void checkLogin1() {
        userService.checkLogin("uu", "1234");
    }

    @Test
    public void checkLogin2() {
        userService.checkLogin("m8", "1234");
    }

    @Test
    public void checkLogin3() {
        userService.checkLogin("m8", "test");
    }

    @Test
    public void selectNodeByUserId(){
        //这里传递的是一个长整型，因此记得末尾加l
        List<Node> nodeList = userService.selectNodeByUserId(2l);
        System.out.println(nodeList);
    }
}