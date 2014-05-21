package com.hp.it.perf.ac.core.context;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;

import com.hp.it.perf.ac.common.core.AcSession;
import com.hp.it.perf.ac.common.core.AcSessionConstants;
import com.hp.it.perf.ac.core.AcCoreContext;
import com.hp.it.perf.ac.core.AcCoreException;
import com.hp.it.perf.ac.core.AcDispatchRegistry;
import com.hp.it.perf.ac.core.AcPreferences;
import com.hp.it.perf.ac.core.AcStatusListenerRegistry;
import com.hp.it.perf.ac.core.service.AcServiceConfig;
import com.hp.it.perf.ac.core.service.AcServiceException;
import com.hp.it.perf.ac.core.service.AcServiceManager;
import com.hp.it.perf.ac.core.service.AcServiceMetaData;
import com.hp.it.perf.ac.core.service.AcServiceProvider;

// initializer for core context
// working with spring framework context (ApplicationContext)
public class AcCoreContextInitializer {

	private static final Logger log = LoggerFactory
			.getLogger(AcCoreContextInitializer.class);

	@Resource
	private ApplicationContext appContext;

	public AcCoreContext initialize(AcSession session) throws AcCoreException {
		log.info("init spring context for core-context");
		// load preferences for the session
		// TODO log loading
		final DefaultAcPreferences corePreferences = new DefaultAcPreferences(
				new File(session
						.getProperty(AcSessionConstants.PREFERENCES_LOCATION)));
		//
		// init context
		//
		GenericXmlApplicationContext context = new GenericXmlApplicationContext();
		context.setParent(appContext);
		context.load("classpath:/spring/ac-core-context.xml");
		log.debug("register bean instance into core-context");
		context.getBeanFactory().registerSingleton(
				AcServiceConfig.SESSION_BEAN_NAME, session);
		context.getBeanFactory().registerSingleton(
				AcServiceConfig.PROFILE_BEAN_NAME, session.getProfile());
		context.getBeanFactory().registerSingleton(
				AcServiceConfig.PREFERENCES_BEAN_NAME, corePreferences);
		//
		// register properties
		//
		Properties beanProp = new Properties();
		beanProp.setProperty("ac.context.sessionId",
				String.valueOf(session.getSessionId()));
		PropertyPlaceholderConfigurer propConfig = new PropertyPlaceholderConfigurer();
		propConfig.setProperties(beanProp);
		context.addBeanFactoryPostProcessor(propConfig);
		//
		// finish context setup
		//
		log.debug("refresh spring app context for core-context");
		context.refresh();
		boolean initSuccess = false;
		final AcCoreContextImpl acCoreContext;
		try {
			acCoreContext = context.getBean(AcCoreContextImpl.class);
			AcServiceManager serviceManager = context
					.getBean(AcServiceManager.class);
			AcStatusBoardImpl statusBoard = context
					.getBean(AcStatusBoardImpl.class);
			// make sure data service is loaded for all
			// because we are using data repository proxy
			List<String> defServices = new ArrayList<String>(
					session.getServices());
			if (!defServices.contains(AcServiceMetaData.DATA_SERVICE_ID)) {
				defServices.add(AcServiceMetaData.DATA_SERVICE_ID);
			}
			//
			// init other service context
			//
			List<String> sortService = serviceManager.sortService(defServices);
			log.info("starting init service for core context by order: {}",
					sortService);
			final AcStatusListenerRegistryImpl serviceStatus = new AcStatusListenerRegistryImpl();
			final AcDispatchRegistryImpl dispatchRegistry = new AcDispatchRegistryImpl();
			for (final String serviceId : sortService) {
				AcServiceProvider provider = serviceManager
						.createProvider(serviceId);
				Properties prop = session.getServiceProperties(serviceId);
				if (prop == null) {
					prop = new Properties();
				}
				final Properties serviceProp = prop;
				AcServiceConfig serviceConfig = new AcServiceConfig() {

					private AcPreferences servicePreferences = corePreferences
							.getServicePreferences(serviceId);

					@Override
					public String getProperty(String key) {
						return servicePreferences.get(key,
								serviceProp.getProperty(key));
					}

					@Override
					public String[] getKeys() {
						List<Object> keys = new ArrayList<Object>();
						keys.addAll(Collections.list(serviceProp.keys()));
						keys.addAll(Arrays.asList(servicePreferences.keys()));
						return keys.toArray(new String[keys.size()]);
					}

					@Override
					public AcCoreContext getCoreContext() {
						return acCoreContext;
					}

					@Override
					public AcStatusListenerRegistry getStatusListenerRegistry() {
						return serviceStatus;
					}

					@Override
					public AcDispatchRegistry getDispatchRegistry() {
						return dispatchRegistry;
					}

					@Override
					public AcPreferences getServicePreferences() {
						return servicePreferences;
					}

				};
				try {
					provider.init(serviceConfig);
					log.info("init service provider done: {}", serviceId);
				} catch (AcServiceException e) {
					e.setServiceId(serviceId);
					throw e;
				} catch (Exception e) {
					throw new AcCoreException(
							"error in provider service init: " + serviceId, e);
				}
				boolean registerSuccess = false;
				try {
					acCoreContext.registerServiceProvider(serviceId, provider);
					registerSuccess = true;
					log.debug("register service provider successful: {}",
							serviceId);
				} catch (AcServiceException e) {
					e.setServiceId(serviceId);
					throw e;
				} catch (Exception e) {
					throw new AcServiceException(
							"error in provider service init: " + serviceId, e)
							.setServiceId(serviceId);
				} finally {
					if (!registerSuccess) {
						provider.destroy();
					}
				}
				statusBoard.addStatusListener(serviceStatus);
			}
			// active context
			acCoreContext.fireActive();
			initSuccess = true;
		} finally {
			if (!initSuccess) {
				context.close();
			}
		}
		log.debug("core context is created for session: {}",
				session.getSessionId());
		return acCoreContext;
	}
}
