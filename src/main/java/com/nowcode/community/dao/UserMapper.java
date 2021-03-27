package com.nowcode.community.dao;

import com.nowcode.community.entity.Comment;
import com.nowcode.community.entity.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserMapper {
    User selectById(int id);

    User selectByName(String username);

    User selectByEmail(String email);

    int insertUser(User user);

    int updateStatus(int id,int status);

    int updateHeader(int id,String headerUrl);

    int updatePassword(int id,String password);
        //查询我的回复
    List<Comment> selectCommentAndPost(int userId,int offset,int limit);

    int selectCommentAndPostCount(int userId);
}
