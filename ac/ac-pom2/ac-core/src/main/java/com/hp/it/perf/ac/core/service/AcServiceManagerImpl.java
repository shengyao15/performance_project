package com.hp.it.perf.ac.core.service;

import static com.hp.it.perf.ac.core.AcCoreConstants.CORE_DOMAIN_NAME;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedOperationParameter;
import org.springframework.jmx.export.annotation.ManagedOperationParameters;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.stereotype.Component;

import com.hp.it.perf.ac.core.AcCoreException;

@ManagedResource(objectName = CORE_DOMAIN_NAME + ":type=AcServiceManager")
@Component
public class AcServiceManagerImpl implements AcServiceManager {

	private static final Logger log = LoggerFactory
			.getLogger(AcServiceManagerImpl.class);

	// service id <-> provider
	private Map<String, AcServiceProvider> providers = new HashMap<String, AcServiceProvider>();

	// service class name <-> service id
	private Map<String, String> services = new HashMap<String, String>();

	// service id <-> service class name
	private Map<String, String> serviceIds = new HashMap<String, String>();

	private DependsSorter<String> serviceDependencySorter;

	@Override
	@ManagedOperation
	@ManagedOperationParameters({ @ManagedOperationParameter(name = "serviceId", description = "service id") })
	public String getProviderClassName(String serviceId)
			throws AcServiceException {
		AcServiceProvider provider = providers.get(serviceId);
		if (provider == null) {
			throw new AcServiceException(
					"service provider not found by service id: " + serviceId);
		}
		return provider.getClass().getName();
	}

	@Override
	@ManagedAttribute
	public String[] getServiceIdList() {
		return providers.keySet().toArray(new String[providers.size()]);
	}

	@PostConstruct
	public void loadServiceProviders() {
		Map<String, Set<String>> initDependsOn = new HashMap<String, Set<String>>();
		serviceDependencySorter = new DependsSorter<String>();

		Iterator<AcServiceProvider> ps = ServiceLoader.load(
				AcServiceProvider.class).iterator();
		while (ps.hasNext()) {
			AcServiceProvider provider = ps.next();
			AcServiceMetaData metadata = provider.metadata();
			if (metadata == null) {
				throw new AcCoreException("no meta data found for provider: "
						+ provider.getClass().getName());
			}
			if (metadata.getServiceId() == null) {
				throw new AcCoreException(
						"no service id defined for provider: "
								+ provider.getClass().getName());
			}
			providers.put(metadata.getServiceId(), provider);
			log.info("found service provider for service [{}]: {}",
					metadata.getServiceId(), provider.getClass().getName());
			if (metadata.getServiceClassName() == null) {
				throw new AcCoreException(
						"no service class name defined for provider: "
								+ provider.getClass().getName());
			}
			// TODO check duplicate class name, service id
			// TODO check class name valid
			services.put(metadata.getServiceClassName(),
					metadata.getServiceId());
			serviceIds.put(metadata.getServiceId(),
					metadata.getServiceClassName());
			log.info("service class name for service [{}]: {}",
					metadata.getServiceId(), metadata.getServiceClassName());
			initDependsOn.put(metadata.getServiceId(),
					metadata.getDependsServiceClassNames());
			serviceDependencySorter.addNode(metadata.getServiceId());
		}

		for (String serviceId : initDependsOn.keySet()) {
			Set<String> clzSet = initDependsOn.get(serviceId);
			for (String dependsOnServiceClz : clzSet) {
				String dependsOnServiceId = services.get(dependsOnServiceClz);
				if (dependsOnServiceId == null) {
					throw new AcServiceException("No service id defined for: "
							+ dependsOnServiceClz);
				}
				serviceDependencySorter.addDependency(serviceId,
						dependsOnServiceId);
			}
		}

		try {
			serviceDependencySorter.sort();
		} catch (IllegalStateException e) {
			throw new AcServiceException("detect incorrect service dependency",
					e);
		}

	}

	@Override
	@ManagedOperation
	@ManagedOperationParameters({ @ManagedOperationParameter(name = "serviceClassName", description = "service class name") })
	public String getServiceIdByClassName(String serviceClassName)
			throws AcServiceException {
		String serviceId = services.get(serviceClassName);
		if (serviceId == null) {
			throw new AcServiceException("service not loaded: "
					+ serviceClassName);
		}
		return serviceId;
	}

	@Override
	@ManagedOperation
	@ManagedOperationParameters({ @ManagedOperationParameter(name = "serviceId", description = "service id") })
	public String getServiceClassNameById(String serviceId) {
		String serviceClassName = serviceIds.get(serviceId);
		if (serviceClassName == null) {
			throw new AcServiceException("service not loaded or defined: "
					+ serviceId);
		}
		return serviceClassName;
	}

	@Override
	public AcServiceProvider createProvider(String serviceId)
			throws AcServiceException {
		Iterator<AcServiceProvider> ps = ServiceLoader.load(
				AcServiceProvider.class).iterator();
		while (ps.hasNext()) {
			AcServiceProvider provider = ps.next();
			AcServiceMetaData metadata = provider.metadata();
			if (serviceId.equals(metadata.getServiceId())) {
				return provider;
			}
		}
		throw new AcServiceException(
				"service provider not found by service id: " + serviceId);
	}

	@Override
	public List<String> sortService(List<String> services) {
		try {
			return serviceDependencySorter.resolveSortedList(services);
		} catch (IllegalArgumentException e) {
			throw new AcServiceException("some service not defined: "
					+ e.getMessage(), e);
		}
	}

}
