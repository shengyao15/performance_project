package com.hp.it.perf.ac.service.spfchain;

import com.hp.it.perf.ac.common.model.AcCommonData;

public interface AcLinker {

    public void performLink(AcCommonData commonData, AcLinkContext context)
	    throws AcProcessException;

}
