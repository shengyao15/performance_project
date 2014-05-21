package com.hp.it.perf.ac.client.load;

import com.hp.it.perf.ac.load.content.AcContentLineInfo;

public interface AcClientBeanFilter {

	public Object filter(Object beanInstance, AcContentLineInfo lineInfo);

}
