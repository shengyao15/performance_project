package com.hp.it.perf.ac.rest.mock.dao;

import java.util.List;

import com.hp.it.perf.ac.common.model.AcCommonData;
import com.hp.it.perf.ac.common.model.AcCommonDataWithPayLoad;
import com.hp.it.perf.ac.service.data.AcRepositoryService;
import com.hp.it.perf.ac.service.data.cache.search.AcSearchCriterial;

public class MockRepositoryService implements AcRepositoryService {

	@Override
	public AcCommonData getCommonData(long acid) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long count() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public AcCommonDataWithPayLoad getCommonDataWithPayLoad(long arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AcCommonDataWithPayLoad[] getCommonDataWithPayLoad(long[] arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AcCommonData getCommonDataWithoutPayLoad(long arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AcCommonData[] getCommonDataWithoutPayLoad(long[] arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<AcCommonData> getResults(List<AcSearchCriterial> arg0,
			int arg1, int arg2) {
		// TODO Auto-generated method stub
		return null;
	}

}
