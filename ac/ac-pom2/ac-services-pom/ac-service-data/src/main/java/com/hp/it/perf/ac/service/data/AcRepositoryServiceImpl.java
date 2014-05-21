package com.hp.it.perf.ac.service.data;

import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.hp.it.perf.ac.common.core.AcSession;
import com.hp.it.perf.ac.common.model.AcCommonData;
import com.hp.it.perf.ac.common.model.AcCommonDataWithPayLoad;
import com.hp.it.perf.ac.core.AcCoreContext;
import com.hp.it.perf.ac.core.AcDataStatusEvent;
import com.hp.it.perf.ac.core.AcDataSubscriber;
import com.hp.it.perf.ac.core.AcStatusSubscriber;
import com.hp.it.perf.ac.core.AcStatusSubscriber.Status;
import com.hp.it.perf.ac.service.data.cache.search.AcSearchCriterial;
import com.hp.it.perf.ac.service.persist.AcPersistService;

@Service
public class AcRepositoryServiceImpl implements AcRepositoryService {

	private static final Logger log = LoggerFactory
			.getLogger(AcRepositoryServiceImpl.class);

	@Inject
	private AcSession session;

	@Inject
	private AcCoreContext coreContext;

	private AcPersistService persist;

	private long totalCount = 0;

	@Override
	public AcCommonData getCommonDataWithoutPayLoad(long acid) {
		return getPersist().readCommonData(acid);
	}

	@Override
	public AcCommonData[] getCommonDataWithoutPayLoad(long[] acid) {
		if (acid == null || acid.length == 0) {
			throw new IllegalArgumentException(
					"Input acid array cannot be null or empty");
		}
		return getPersist().readCommonData(acid);
	}

	@Override
	public AcCommonDataWithPayLoad getCommonDataWithPayLoad(long acid) {
		return getPersist().read(acid);
	}

	@Override
	public AcCommonDataWithPayLoad[] getCommonDataWithPayLoad(long[] acid) {
		if (acid == null || acid.length == 0) {
			throw new IllegalArgumentException(
					"Input acid array cannot be null or empty");
		}
		return getPersist().read(acid);
	}

	protected AcPersistService getPersist() {
		if (persist == null) {
			persist = coreContext.getService(AcPersistService.class);
		}
		return persist;
	}

	@AcDataSubscriber
	public void onData(AcCommonDataWithPayLoad... data) {
		totalCount += data.length;
	}

	@Override
	public List<AcCommonData> getResults(List<AcSearchCriterial> criterials,
			int currentPage, int maxCountPerPage) {
		throw new UnsupportedOperationException();
	}

	@Override
	public AcCommonData getCommonData(long acid) {
		return getCommonDataWithoutPayLoad(acid);
	}

	@Override
	public long count() {
		return totalCount;
	}

	@AcStatusSubscriber(Status.ACTIVE)
	public void onActive(AcDataStatusEvent dataEvent) {
		AcPersistService acPersist = getPersist();
		if (acPersist == null) {
			log.warn("No persist service enabled, Not load previous data into repository");
			return;
		}
		totalCount = acPersist.count();
		log.info("get count of persist data into repository: {}", totalCount);
	}

}
