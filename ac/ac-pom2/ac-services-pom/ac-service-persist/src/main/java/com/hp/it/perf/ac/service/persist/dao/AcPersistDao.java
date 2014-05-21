package com.hp.it.perf.ac.service.persist.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.hp.it.perf.ac.service.persist.AcDaoContext;
import com.hp.it.perf.ac.service.persist.AcPersistException;

public interface AcPersistDao{
	
	public boolean save(Serializable entity) throws AcPersistException;
	
	public boolean save(List<? extends Serializable> entities) throws AcPersistException;
	
	// return data or null, otherwise error
	public <T extends Serializable> T get(long acid, Class<T> beanClass) throws AcPersistException;
	
	// return list with data or null (should bean class)
	public <T extends Serializable> List<T> get(long[] acids, Class<T> beanClass) throws AcPersistException;
	
	// return null if not found
	public Serializable find(long acid) throws AcPersistException;
	
	// return list with data or null
	public List<Serializable> find(long[] acids) throws AcPersistException;
	
	public void setDaoContext(AcDaoContext daoContext);
	
	public long count(List<AcSearchCriterial> criterials, Class<?> clazz);
	
	public <T extends Serializable> List<T> query(List<AcSearchCriterial> criterials, Class<T> clazz);
	
	public <T extends Serializable> List<T> queryBySQL(String sql, Map<String, String> params, Class<T> clazz);
	
	public void delete(long [] acids) throws AcPersistException;
	
	//public void deleteAll(Class<?> beanClass) throws AcPersistException;

}
