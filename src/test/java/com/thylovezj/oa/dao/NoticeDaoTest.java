package com.thylovezj.oa.dao;

import com.thylovezj.oa.entity.Notice;
import com.thylovezj.oa.utils.MybatisUtils;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.*;

public class NoticeDaoTest {
    @Test
    public void testInsert(){
        MybatisUtils.executeUpdate(sqlSession -> {
            NoticeDao noticeDao = sqlSession.getMapper(NoticeDao.class);
            Notice notice = new Notice();
            notice.setReceiverId(2l);
            notice.setContent("测试消息");
            notice.setCreateTime(new Date());
            noticeDao.insert(notice);
            return null;
        });
    }
}