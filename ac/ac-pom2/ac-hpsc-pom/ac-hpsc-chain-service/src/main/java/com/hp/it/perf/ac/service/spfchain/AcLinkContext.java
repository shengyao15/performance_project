package com.hp.it.perf.ac.service.spfchain;

import com.hp.it.perf.ac.service.chain.ChainContext;

public interface AcLinkContext {

	public void linkParentChild(ChainContext parent, ChainContext child);
	
	public void createNodeAndRelation(long acid, ChainContext primaryTrack);

}
