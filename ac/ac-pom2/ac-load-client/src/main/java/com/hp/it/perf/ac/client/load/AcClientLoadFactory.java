package com.hp.it.perf.ac.client.load;

import java.util.Collection;

import com.hp.it.perf.ac.load.content.AcContentMetadata;

public interface AcClientLoadFactory {

	public Collection<Class<?>> getSupportedBeanClassList();

	public String getTransformName(Class<?> beanClass,
			AcContentMetadata location);

	public AcClientBeanFilter getBeanFilter(Class<?> beanClass,
			AcContentMetadata metadata);

}
