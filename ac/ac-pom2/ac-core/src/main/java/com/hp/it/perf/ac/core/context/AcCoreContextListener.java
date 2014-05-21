package com.hp.it.perf.ac.core.context;

import java.util.EventListener;

import com.hp.it.perf.ac.core.AcCoreContext;

public interface AcCoreContextListener extends EventListener {

	public void onCoreContextActive(AcCoreContext coreContext);

	public void onCoreContextDeactive(AcCoreContext coreContext);

}
