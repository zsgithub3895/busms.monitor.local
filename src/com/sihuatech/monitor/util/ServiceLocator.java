package com.sihuatech.monitor.util;

import org.springframework.context.ApplicationContext;

import com.onewaveinc.mip.spring.ApplicationContextHolder;

public class ServiceLocator {
	private static ApplicationContext context = ApplicationContextHolder.getApplicationContext();

	public static Object getBean(String beanName) {
		return context.getBean(beanName);
	}

}
