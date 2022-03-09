package com.thylovezj.oa.dao;

import com.thylovezj.oa.entity.Node;
import com.thylovezj.oa.utils.MybatisUtils;

import java.util.List;

public class RbacDao {
    public List<Node> selectNodeByUserId(Long userId){
        return (List) MybatisUtils.executeQuery(sqlSession -> sqlSession.selectList("rbacmapper.selectNodeByUserId",userId));
    }
}
