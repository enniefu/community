package com.ennie.community;


import com.ennie.community.Util.SensitiveFilter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class SensitiveTests {

    @Autowired
    SensitiveFilter sensitiveFilter;

    @Test
    public void testSensitiveFilter(){
        String text  ="这里可以赌博、嫖娼、吸毒、开票，哈哈";
        text = sensitiveFilter.filter(text);
        System.out.println(text);


         text  ="这里可以赌🤨博、嫖&娼、吸^毒、开票，哈哈";
        text = sensitiveFilter.filter(text);
        System.out.println(text);
    }
}
