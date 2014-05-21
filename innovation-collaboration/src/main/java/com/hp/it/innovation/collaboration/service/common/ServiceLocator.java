package com.hp.it.innovation.collaboration.service.common;

import org.springframework.beans.factory.access.BeanFactoryLocator;
import org.springframework.beans.factory.access.BeanFactoryReference;
import org.springframework.context.access.ContextSingletonBeanFactoryLocator;

public class ServiceLocator {
	private static BeanFactoryReference beanFactoryRef;

	static {
		try {
			BeanFactoryLocator beanFactoryLocator = ContextSingletonBeanFactoryLocator
					.getInstance("/config/spring-defs.xml");
			beanFactoryRef = beanFactoryLocator.useBeanFactory("beanDefContext");
		} catch (Throwable th) {
			th.printStackTrace();
		}
	}

	public static Object getBean(String name) {
		return beanFactoryRef.getFactory().getBean(name);
	}

	public static void dispose() {
		if (beanFactoryRef != null) {
			beanFactoryRef.release();
		}
	}
}
