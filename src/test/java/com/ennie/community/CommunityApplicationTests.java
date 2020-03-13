package com.ennie.community;

import com.ennie.community.dao.AlphaDao;
import com.ennie.community.service.AlphaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.test.context.ContextConfiguration;

import java.text.SimpleDateFormat;
import java.util.Date;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
class CommunityApplicationTests implements ApplicationContextAware {

	//为了记住spring容器
	private ApplicationContext applicationContext;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	@Test
	public void testApplicationContext(){
		System.out.println(applicationContext);


		//通过容器工厂获取Bean
		//依赖的不是Bean本身，依赖的是接口
		AlphaDao alphaDao = applicationContext.getBean(AlphaDao.class);
		System.out.println(alphaDao.select());


		//默认获取的是object类型，因此需要强制类型转换
		alphaDao = applicationContext.getBean("alphaHibernate",AlphaDao.class);
		System.out.println(alphaDao.select());
	}



	@Test
	public void testBeanManagement(){
		AlphaService alphaService = applicationContext.getBean(AlphaService.class);
		System.out.println(alphaService);


		alphaService = applicationContext.getBean(AlphaService.class);
		System.out.println(alphaService);
	}



	@Test
	public void testBeanConfig(){
		SimpleDateFormat simpleDateFormat = applicationContext.getBean(SimpleDateFormat.class);
		System.out.println(simpleDateFormat.format(new Date()));
	}



	@Autowired
	private AlphaDao alphaDao;

	@Test
	public void testDI(){
		System.out.println(alphaDao);
	}

}
