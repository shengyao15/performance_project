package com.hp.it.perf.ac.service.persist;

import com.hp.it.perf.ac.common.model.AcCommonData;
import com.hp.it.perf.ac.common.model.AcCommonDataWithPayLoad;
import com.hp.it.perf.ac.core.AcService;

public interface AcPersistService extends AcService {

	public AcCommonDataWithPayLoad read(long acid) throws AcPersistException;

	public AcCommonDataWithPayLoad[] read(long[] acids)
			throws AcPersistException;

	public AcCommonData readCommonData(long acid) throws AcPersistException;

	public AcCommonData[] readCommonData(long[] acids)
			throws AcPersistException;

	public long count() throws AcPersistException;

	public AcCommonData[] readCommonData(AcPersistCondition condition);
	
	public void delete(long[] acids) throws AcPersistException;
	
	public void deleteAll() throws AcPersistException;
}
