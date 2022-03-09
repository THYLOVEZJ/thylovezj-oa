package com.thylovezj.oa.dao;

import com.thylovezj.oa.entity.Notice;

import java.util.List;

public interface NoticeDao {
    public void insert(Notice notice);

    public List<Notice> selectByReceiverId(long reciverId);
}
