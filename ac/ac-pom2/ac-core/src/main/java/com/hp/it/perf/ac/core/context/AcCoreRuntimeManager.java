package com.hp.it.perf.ac.core.context;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PreDestroy;

import com.hp.it.perf.ac.common.core.AcSession;
import com.hp.it.perf.ac.core.AcCoreContext;

public class AcCoreRuntimeManager implements AcCoreContextListener {

	private Set<AcCoreContext> coreContexts = new HashSet<AcCoreContext>();
	private Map<Number, AcCoreContext> sessionCoreContextMap = new HashMap<Number, AcCoreContext>();

	@Override
	public void onCoreContextActive(AcCoreContext acCoreContext) {
		coreContexts.add(acCoreContext);
		sessionCoreContextMap.put(acCoreContext.getSession().getSessionId(),
				acCoreContext);
	}

	@Override
	public void onCoreContextDeactive(AcCoreContext acCoreContext) {
		coreContexts.remove(acCoreContext);
		sessionCoreContextMap.remove(acCoreContext.getSession().getSessionId());
	}

	@PreDestroy
	public void destory() {
		for (AcCoreContext coreContext : new ArrayList<AcCoreContext>(
				coreContexts)) {
			coreContext.close();
		}
		coreContexts.clear();
	}

	public AcCoreContext lookupCoreContextBySessionId(int sessionId) {
		return sessionCoreContextMap.get(sessionId);
	}

	public int[] getSessionIdList() {
		Collection<Number> ids = sessionCoreContextMap.keySet();
		int[] ret = new int[ids.size()];
		int i = 0;
		for (Number s : ids) {
			ret[i++] = s.intValue();
		}
		return ret;
	}

	public List<AcSession> getSessionList() {
		Collection<AcCoreContext> list = sessionCoreContextMap.values();
		List<AcSession> ret = new ArrayList<AcSession>();
		for (AcCoreContext s : list) {
			ret.add(s.getSession());
		}
		return ret;
	}

}
