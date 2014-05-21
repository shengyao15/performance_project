package com.hp.it.perf.ac.core.access;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.springframework.jmx.export.MBeanExporter;
import org.springframework.jmx.export.assembler.InterfaceBasedMBeanInfoAssembler;
import org.springframework.jmx.export.naming.ObjectNamingStrategy;
import org.springframework.jmx.support.ObjectNameManager;

import com.hp.it.perf.ac.core.AcCoreContext;
import com.hp.it.perf.ac.core.AcDataRepository;
import com.hp.it.perf.ac.core.AcService;
import com.hp.it.perf.ac.core.service.AcServiceManager;

class JmxAcCoreAccess {

	@Inject
	private JmxAcCoreConnectionServer coreConnection;

	// use specified class
	@Inject
	private JmxAcDataRepository dataRepository;

	@Inject
	private AcCoreContext coreContext;

	@Inject
	private AcServiceManager serviceManager;

	private Map<String, AcService> services = new HashMap<String, AcService>();

	private MBeanExporter mbeanExporter;

	private InterfaceBasedMBeanInfoAssembler mbeanInfoAssembler;

	private Map<Object, String> objectNamingMap = new IdentityHashMap<Object, String>();

	public void setMbeanExporter(MBeanExporter mbeanExporter) {
		this.mbeanExporter = mbeanExporter;
	}

	public void setMbeanInfoAssembler(
			InterfaceBasedMBeanInfoAssembler mbeanInfoAssembler) {
		this.mbeanInfoAssembler = mbeanInfoAssembler;
	}

	@PostConstruct
	void initManagedResources() {
		String objNamePrefix = JmxAcCoreAccess.class.getPackage().getName()
				+ ":instance=session#"
				+ coreContext.getSession().getSessionId() + ",";
		mbeanExporter.setNamingStrategy(new ObjectNamingStrategy() {

			@Override
			public ObjectName getObjectName(Object managedBean, String beanKey)
					throws MalformedObjectNameException {
				return ObjectNameManager.getInstance(objectNamingMap
						.get(managedBean));
			}
		});
		List<Class<?>> infList = initLoadedService();
		infList.add(AcCoreAccess.class);
		infList.add(AcDataRepository.class);
		mbeanInfoAssembler.setManagedInterfaces(infList
				.toArray(new Class[infList.size()]));
		objectNamingMap.put(coreConnection, objNamePrefix + "name="
				+ AcCoreAccess.class.getSimpleName());
		mbeanExporter.registerManagedResource(coreConnection);
		objectNamingMap.put(dataRepository, objNamePrefix + "name="
				+ AcDataRepository.class.getSimpleName());
		mbeanExporter.registerManagedResource(dataRepository);
		for (Map.Entry<String, AcService> entry : services.entrySet()) {
			objectNamingMap.put(entry.getValue(),
					objNamePrefix + "name=" + AcService.class.getSimpleName()
							+ ",serviceId=" + entry.getKey());
			mbeanExporter.registerManagedResource(entry.getValue());
		}
	}

	private List<Class<?>> initLoadedService() {
		List<Class<?>> serviceInfClassList = new ArrayList<Class<?>>();
		for (String loadedServiceId : coreContext.getLoadedServiceIds()) {
			AcService service = coreContext.getServiceById(loadedServiceId);
			Class<?> serviceInfClass = findClass(service.getClass(),
					serviceManager.getServiceClassNameById(loadedServiceId));
			serviceInfClassList.add(serviceInfClass);
			services.put(loadedServiceId, service);
		}
		return serviceInfClassList;
	}

	private Class<?> findClass(Class<? extends AcService> realServiceClass,
			String intfServiceClassName) {
		for (Class<?> inf : realServiceClass.getInterfaces()) {
			if (inf.getName().equals(intfServiceClassName)) {
				return inf;
			}
		}
		throw new IllegalArgumentException(intfServiceClassName);
	}
}
