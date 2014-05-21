package com.hp.it.perf.ac.service.spfchain;

import java.util.List;

import com.hp.it.perf.ac.core.AcService;

public interface SpfChainService extends AcService {
	
	public void deleteChainData(List<Long> acids);
	
	public void deleteAll();

}