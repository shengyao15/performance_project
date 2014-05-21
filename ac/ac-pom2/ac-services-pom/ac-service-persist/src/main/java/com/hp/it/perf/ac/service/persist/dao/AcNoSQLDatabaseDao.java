package com.hp.it.perf.ac.service.persist.dao;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

public interface AcNoSQLDatabaseDao<K, V extends Serializable> {

	public V findByKey(K id);

	public void add(Collection<? extends V> data);

	public void delete(V... data);
	
	public void deleteByCriteria(List<AcSearchCriterial> criteria);
	
	public void deleteAll();

	public long count();

	public long count(List<AcSearchCriterial> criteria);

	public List<V> findByCriteria(List<AcSearchCriterial> criteria,
			String orderByField, boolean ascOrderBy, int limit);

	public List<V> findByKeys(K[] ids);
}
