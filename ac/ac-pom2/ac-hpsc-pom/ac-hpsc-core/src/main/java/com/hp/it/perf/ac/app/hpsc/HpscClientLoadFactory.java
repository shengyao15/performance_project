package com.hp.it.perf.ac.app.hpsc;

import java.net.URI;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.hp.it.perf.ac.app.hpsc.beans.LocationBasedBean;
import com.hp.it.perf.ac.app.hpsc.beans.PortletBusinessLog;
import com.hp.it.perf.ac.app.hpsc.beans.PortletErrorLog;
import com.hp.it.perf.ac.app.hpsc.beans.PortletErrortraceLog;
import com.hp.it.perf.ac.app.hpsc.beans.PortletPerformanceLog;
import com.hp.it.perf.ac.app.hpsc.beans.SPFPerformanceLog;
import com.hp.it.perf.ac.client.load.AcClientBeanFilter;
import com.hp.it.perf.ac.client.load.AcClientLoadFactory;
import com.hp.it.perf.ac.load.content.AcContentLineInfo;
import com.hp.it.perf.ac.load.content.AcContentMetadata;

public class HpscClientLoadFactory implements AcClientLoadFactory {

	private Map<Class<?>, String> beanClasses = new LinkedHashMap<Class<?>, String>();

	private void registerAllBeanClass() {
		registerLogClass(PortletErrortraceLog.class,
				"log.spf.portlet.errortrace");
		registerLogClass(SPFPerformanceLog.class,
				"log.spf.portal.spfperformance");
		registerLogClass(PortletBusinessLog.class, "log.spf.portlet.business");
		registerLogClass(PortletErrorLog.class, "log.spf.portlet.error");
		registerLogClass(PortletPerformanceLog.class,
				"log.spf.portlet.performance");
	}

	private void registerLogClass(Class<?> beanClass, final String transformName) {
		beanClasses.put(beanClass, transformName);
	}

	private static Pattern producerPortletLogNamePattern = Pattern
			.compile("(?:.*/)?(\\w+)/(?:main|service|debug|input)/.+");

	private static Pattern producerPortletLocationPattern = Pattern
			.compile("/(g.*?)/opt/.*/([^/]+)/(?:main|service|debug|input)/");

	private static Pattern consumerLocationPattern = Pattern
			.compile("/(g.*?)/opt/.*/logs/.*(spf[^/]+)/");

	{
		registerAllBeanClass();
	}

	@Override
	public Collection<Class<?>> getSupportedBeanClassList() {
		return beanClasses.keySet();
	}

	@Override
	public String getTransformName(Class<?> beanClass,
			AcContentMetadata location) {
		String transformName = beanClasses.get(beanClass);
		if (transformName == null) {
			throw new IllegalArgumentException(
					"transform name mapping is not found for " + beanClass);
		}
		// append app type
		// if like xxx/sp4tspsi/main/business/business.log
		String filePath = location.getLocation().toString();
		Matcher matcher = producerPortletLogNamePattern.matcher(filePath);
		if (matcher.matches()) {
			transformName += "." + matcher.group(1);
		}
		return transformName;
	}

	@Override
	public AcClientBeanFilter getBeanFilter(Class<?> beanClass,
			AcContentMetadata metadata) {
		URI location = metadata.getLocation();
		if (location != null && LocationBasedBean.class.isAssignableFrom(beanClass)) {
			String loc = location.toString();
			Matcher matcher;
			String hostName = null;
			String moduleName = null;
			matcher = producerPortletLocationPattern.matcher(loc);
			if (matcher.find()) {
				hostName = matcher.group(1);
				moduleName = matcher.group(2);
			} else {
				matcher = consumerLocationPattern.matcher(loc);
				if (matcher.find()) {
					hostName = matcher.group(1);
					moduleName = matcher.group(2);
				} else {
					return null;
				}
			}
			final String beanLocation = hostName + ":" + moduleName;
			return new AcClientBeanFilter() {

				@Override
				public Object filter(Object beanInstance,
						AcContentLineInfo lineInfo) {
					LocationBasedBean locationBean = (LocationBasedBean) beanInstance;
					locationBean.setLocation(beanLocation);
					return locationBean;
				}
			};
		} else {
			return null;
		}
	}

}
