package com.nowcode.community;

import com.nowcode.community.util.SensitiveFilter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class sensitiveTest {
    @Autowired
    private SensitiveFilter sensitiveFilter;

   @Test
   public void sensitiveTest(){
       String t ="这里可以赌博，嫖娼，哈哈";
        t=sensitiveFilter.filter(t);
       System.out.println(t);
   }

}
