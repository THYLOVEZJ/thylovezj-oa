package com.thylovezj.oa.service;

import com.thylovezj.oa.dao.NoticeDao;
import com.thylovezj.oa.entity.Notice;
import com.thylovezj.oa.utils.MybatisUtils;

import java.util.List;

public class NoticeService {
    public List<Notice> getNoticeList(Long recieverId){
        return (List)MybatisUtils.executeQuery(sqlSession -> {
            NoticeDao noticeDao = sqlSession.getMapper(NoticeDao.class);
            return noticeDao.selectByReceiverId(recieverId);
        });
    }
}
