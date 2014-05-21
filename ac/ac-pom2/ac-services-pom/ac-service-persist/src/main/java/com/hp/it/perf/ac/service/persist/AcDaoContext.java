package com.hp.it.perf.ac.service.persist;

import java.io.Serializable;
import java.util.List;

public interface AcDaoContext {
	
	public Class<?> mapBeanClass(long acid);
	
	public void dataRollbacked(List<? extends Serializable> entities, Throwable cause);
	
}
