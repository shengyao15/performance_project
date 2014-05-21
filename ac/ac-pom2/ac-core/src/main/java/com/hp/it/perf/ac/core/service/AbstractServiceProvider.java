package com.hp.it.perf.ac.core.service;

import java.util.Properties;

import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.core.io.Resource;

import com.hp.it.perf.ac.core.AcCoreContext;
import com.hp.it.perf.ac.core.AcDataSubscriber;
import com.hp.it.perf.ac.core.AcService;
import com.hp.it.perf.ac.core.AcStatusSubscriber;

public abstract class AbstractServiceProvider implements AcServiceProvider {

	private ConfigurableApplicationContext appContext;

	protected void initClassPathApplicationContext(
			AcServiceConfig serviceConfig, String... configLocations) {
		appContext = new ClassPathXmlApplicationContext(configLocations, false,
				prepareParentContext(serviceConfig));
		postSetupApplicationContext(appContext, serviceConfig);
		appContext.refresh();
	}

	protected void initXmlApplicationContext(AcServiceConfig serviceConfig,
			boolean autoScan, Resource... configResources) {
		GenericXmlApplicationContext xmlContext = new GenericXmlApplicationContext();
		appContext = xmlContext;
		xmlContext.setParent(prepareParentContext(serviceConfig));
		xmlContext.load(configResources);
		if (autoScan) {
			ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner(
					xmlContext);
			// scan this package from provider hosting
			scanner.scan(this.getClass().getPackage().getName());
		}
		postSetupApplicationContext(appContext, serviceConfig);
		appContext.refresh();
	}

	protected void postSetupApplicationContext(
			ConfigurableApplicationContext xmlContext,
			AcServiceConfig serviceConfig) {
		initPropertiesPlaceHolder(xmlContext, serviceConfig);
		registerAcAnnotations(xmlContext, serviceConfig);
		registerDispatchDownstream(xmlContext, serviceConfig);
	}

	protected void registerDispatchDownstream(
			ConfigurableApplicationContext xmlContext,
			AcServiceConfig serviceConfig) {
	}

	protected void registerAcAnnotations(
			ConfigurableApplicationContext context,
			final AcServiceConfig serviceConfig) {
		// Process Annotation AcStatusSubscriber
		AcMethodAnnotationProcessor statusSubscriberAnnotationProcessor = new AcMethodAnnotationProcessor(
				AcStatusSubscriber.class) {

			@Override
			protected void processBean(Object bean) {
				serviceConfig.getStatusListenerRegistry().processStatusListener(bean);

			}
		};
		context.addBeanFactoryPostProcessor(statusSubscriberAnnotationProcessor);
		context.addApplicationListener(statusSubscriberAnnotationProcessor);
		// Process Annotation AcDataSubscriber
		AcMethodAnnotationProcessor dataSubscriberAnnotationProcessor = new AcMethodAnnotationProcessor(
				AcDataSubscriber.class) {

			@Override
			protected void processBean(Object bean) {
				serviceConfig.getDispatchRegistry().processRegister(bean,
						metadata().getServiceId());

			}
		};
		context.addBeanFactoryPostProcessor(dataSubscriberAnnotationProcessor);
		context.addApplicationListener(dataSubscriberAnnotationProcessor);
	}

	protected void initPropertiesPlaceHolder(
			ConfigurableApplicationContext context,
			AcServiceConfig serviceConfig) {
		// prepare properties from service config
		Properties prop = new Properties();
		for (String key : serviceConfig.getKeys()) {
			// NOTE: use data from service property (preferences + default)
			prop.setProperty(key, serviceConfig.getProperty(key));
		}
		// TODO (other better way) other core reserved properties
		prop.setProperty(
				"ac.context.sessionId",
				String.valueOf(serviceConfig.getCoreContext().getSession()
						.getSessionId()));
		prop.setProperty(
				"ac.context.profileId",
				String.valueOf(serviceConfig.getCoreContext().getSession()
						.getProfile().getProfileId()));
		PropertyPlaceholderConfigurer propConfig = new PropertyPlaceholderConfigurer();
		propConfig.setProperties(prop);
		context.addBeanFactoryPostProcessor(propConfig);
	}

	protected void validateConfig(AcServiceConfig serviceConfig, String key)
			throws AcServiceException {
		if (serviceConfig.getProperty(key) == null) {
			throw new AcServiceException("service property '" + key
					+ "' not found");
		}
	}

	protected ConfigurableApplicationContext getApplicationContext() {
		return appContext;
	}

	protected ApplicationContext prepareParentContext(
			final AcServiceConfig serviceConfig) throws AcServiceException {
		AcCoreContext coreContext = serviceConfig.getCoreContext();
		GenericApplicationContext parentContext = new GenericApplicationContext();
		DefaultListableBeanFactory beanFactory = parentContext
				.getDefaultListableBeanFactory();

		// add service config
		beanFactory.registerSingleton(AcServiceConfig.SERVICE_CONFIG_BEAN_NAME,
				serviceConfig);

		// add core context
		beanFactory.registerSingleton(AcServiceConfig.CORE_CONTEXT_BEAN_NAME,
				coreContext);

		// add session
		beanFactory.registerSingleton(AcServiceConfig.SESSION_BEAN_NAME,
				coreContext.getSession());

		// add profile
		beanFactory.registerSingleton(AcServiceConfig.PROFILE_BEAN_NAME,
				coreContext.getSession().getProfile());
		
		// add data repository
		beanFactory.registerSingleton(AcServiceConfig.REPOSITORY_BEAN_NAME,
				coreContext.getDataRepository());
		
		// add service preferences
		beanFactory.registerSingleton(
				AcServiceConfig.PREFERENCES_BEAN_NAME,
				serviceConfig.getServicePreferences());
		
		// add dictionary
		beanFactory.registerSingleton(AcServiceConfig.DICTIONARY_BEAN_NAME,
				coreContext.getSession().getProfile().getDictionary());

		for (String serviceClassName : metadata().getDependsServiceClassNames()) {
			try {
				AcService dependService = coreContext
						.getServiceByClassName(serviceClassName);
				beanFactory.registerSingleton(serviceClassName, dependService);
			} catch (Exception e) {
				throw new AcServiceException("load depend service error: "
						+ serviceClassName, e);
			}
		}

		// ready for parent context
		parentContext.refresh();
		return parentContext;
	}

	@Override
	public void destroy() {
		appContext.close();
		if (appContext.getParent() instanceof ConfigurableApplicationContext) {
			((ConfigurableApplicationContext) appContext.getParent()).close();
		}
		appContext = null;
	}

}
