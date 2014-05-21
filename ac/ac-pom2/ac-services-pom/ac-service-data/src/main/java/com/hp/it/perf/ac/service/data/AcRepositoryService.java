package com.hp.it.perf.ac.service.data;

import java.util.List;

import com.hp.it.perf.ac.common.model.AcCommonData;
import com.hp.it.perf.ac.common.model.AcCommonDataWithPayLoad;
import com.hp.it.perf.ac.core.AcDataRepository;
import com.hp.it.perf.ac.core.AcService;
import com.hp.it.perf.ac.service.data.cache.search.AcSearchCriterial;

public interface AcRepositoryService extends AcService, AcDataRepository {

	public AcCommonData getCommonDataWithoutPayLoad(long acid);

	public AcCommonData[] getCommonDataWithoutPayLoad(long[] acid);

	public AcCommonDataWithPayLoad getCommonDataWithPayLoad(long acid);

	public AcCommonDataWithPayLoad[] getCommonDataWithPayLoad(long[] acid);

	public List<AcCommonData> getResults(List<AcSearchCriterial> criterials,
			int currentPage, int maxCountPerPage);

}
