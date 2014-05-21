package com.hp.it.perf.ac.core.context;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import com.hp.it.perf.ac.common.core.AcStatusEvent;
import com.hp.it.perf.ac.common.core.AcStatusListener;
import com.hp.it.perf.ac.core.AcCoreContext;
import com.hp.it.perf.ac.core.AcDataStatusEvent;
import com.hp.it.perf.ac.core.AcStatusBoard;

class AcStatusBoardImpl implements AcStatusBoard, AcCoreContextListener {

	@Inject
	private AcCoreContext coreContext;

	private List<AcStatusListener> listeners = new ArrayList<AcStatusListener>();

	@Override
	public void addStatusListener(AcStatusListener listener) {
		if (listener == null) {
			throw new IllegalArgumentException("null listener");
		}
		if (!listeners.contains(listener)) {
			listeners.add(listener);
		}
	}

	@Override
	public void removeStatusListener(AcStatusListener listener) {
		int index = listeners.indexOf(listener);
		if (index >= 0) {
			listeners.remove(index);
		}
	}

	@Override
	public void onCoreContextActive(AcCoreContext coreContext) {
		if (this.coreContext == coreContext) {
			AcStatusEvent event = new AcDataStatusEvent(coreContext);
			for (AcStatusListener l : listeners) {
				l.onActive(event);
			}
		}
	}

	@Override
	public void onCoreContextDeactive(AcCoreContext coreContext) {
		if (this.coreContext == coreContext) {
			AcStatusEvent event = new AcDataStatusEvent(coreContext);
			for (AcStatusListener l : listeners) {
				l.onDeactive(event);
			}
		}
	}

	@Override
	public void sendStatusEvent(String status, AcStatusEvent event) {
		for (AcStatusListener l : listeners) {
			l.onStatus(status, event);
		}
	}

}
