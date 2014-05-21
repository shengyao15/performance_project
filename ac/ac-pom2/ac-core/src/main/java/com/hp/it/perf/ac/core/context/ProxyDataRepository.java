package com.hp.it.perf.ac.core.context;

import javax.inject.Inject;

import com.hp.it.perf.ac.common.model.AcCommonData;
import com.hp.it.perf.ac.core.AcCoreContext;
import com.hp.it.perf.ac.core.AcCoreException;
import com.hp.it.perf.ac.core.AcDataRepository;
import com.hp.it.perf.ac.core.AcService;

class ProxyDataRepository implements AcDataRepository {

	@Inject
	private AcCoreContext coreContext;

	private volatile AcDataRepository internal;

	protected AcDataRepository getRepository() {
		if (internal == null) {
			synchronized (this) {
				// double check with volatile modifier
				if (internal == null) {
					for (String serviceId : coreContext.getLoadedServiceIds()) {
						AcService acService = coreContext
								.getServiceById(serviceId);
						if (acService instanceof AcDataRepository) {
							internal = (AcDataRepository) acService;
							return internal;
						}
					}
					if (internal == null) {
						throw new AcCoreException(
								"no internal AcDataRepository service found");
					}
				}
			}
		}
		return internal;
	}

	@Override
	public AcCommonData getCommonData(long acid) {
		return getRepository().getCommonData(acid);
	}

	@Override
	public long count() {
		return getRepository().count();
	}

//	@Override
//	public boolean isFullLoaded() {
//		return getRepository().isFullLoaded();
//	}

}
