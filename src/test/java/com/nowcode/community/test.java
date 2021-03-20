package com.nowcode.community;

import com.nowcode.community.entity.Comment;
import com.nowcode.community.entity.DiscussPost;
import com.nowcode.community.service.CommentService;
import com.nowcode.community.service.DiscussPostService;
import com.nowcode.community.util.HostHolder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class test {
    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private HostHolder hostHolder;

    @Test
    public void testbySql(){
       Comment comment=new Comment();
        comment.setUserId(156);
        comment.setStatus(0);
        comment.setCreateTime(new Date());
        comment.setEntityId(275);
        comment.setEntityType(1);
        comment.setContent("13123");
      int i=  commentService.addComment(comment);
        System.out.println(i);
    }
}
