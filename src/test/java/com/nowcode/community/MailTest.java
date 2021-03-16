package com.nowcode.community;

import com.nowcode.community.dao.UserMapper;
import com.nowcode.community.entity.User;
import com.nowcode.community.service.UserService;
import com.nowcode.community.util.MailClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class MailTest {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;
    @Test
    public void testTestMail(){
        mailClient.sendMail("2645044053@qq.com","test","welcome");
    }
    @Test
    public void testHtmlMail(){
        Context context =new Context();
        context.setVariable("username","sunday");
        String content = templateEngine.process("/mail/demo",context);
        System.out.println(content);
        mailClient.sendMail("2645044053@qq.com","html",content);

    }
    @Test
    public void findByUsername(){
        User user= userMapper.selectByName("niuke");
       if(user!=null){
            System.out.println(user);
        }else{
           System.out.println("null");
        }
    }
}
