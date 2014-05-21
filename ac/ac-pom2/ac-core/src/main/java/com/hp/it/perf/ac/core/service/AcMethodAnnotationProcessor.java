package com.hp.it.perf.ac.core.service;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

abstract class AcMethodAnnotationProcessor implements BeanFactoryPostProcessor,
		ApplicationListener<ContextRefreshedEvent> {

	private List<String> beanNameList = new ArrayList<String>();
	private Class<? extends Annotation> annotationType;
	private ConfigurableListableBeanFactory beanFctory;

	public AcMethodAnnotationProcessor(
			Class<? extends Annotation> annotationType) {
		this.annotationType = annotationType;
	}

	@Override
	public void postProcessBeanFactory(
			ConfigurableListableBeanFactory beanFactory) throws BeansException {
		this.beanFctory = beanFactory;
		String[] beanNames = beanFactory.getBeanDefinitionNames();
		for (String beanName : beanNames) {
			Class<?> beanType = beanFactory.getType(beanName);
			if (beanType != null) {
				for (Method method : beanType.getMethods()) {
					if (method.isAnnotationPresent(annotationType)) {
						// not get bean via factory
						// (otherwise, some lifecyle method will not be called)
						beanNameList.add(beanName);
						break;
					}
				}
			}
		}
	}

	protected abstract void processBean(Object bean);

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		for (String beanName : beanNameList) {
			Object bean = beanFctory.getBean(beanName);
			processBean(bean);
		}
	}

}
