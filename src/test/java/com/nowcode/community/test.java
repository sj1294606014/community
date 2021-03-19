package com.nowcode.community;

import com.nowcode.community.entity.DiscussPost;
import com.nowcode.community.service.DiscussPostService;
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

    @Test
    public void testbySql(){
        DiscussPost post =new DiscussPost();
        post.setUserId(156);
        post.setTitle("test");
        post.setContent("contenttest");
        post.setCreateTime(new Date());
      int t=  discussPostService.addDiscussPost(post);
        System.out.println(t);
    }
}
