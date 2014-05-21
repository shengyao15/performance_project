package com.hp.it.perf.ac.service.spfchain;

import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.it.perf.ac.service.chain.ChainContext;
import com.hp.it.perf.ac.service.chain.ChainService;

class AcBatchLinkContext implements AcLinkContext {
	private static Logger log = LoggerFactory
			.getLogger(AcBatchLinkContext.class);

	private ChainService chainService;
	private Map<ChainContext, ChainContext> childParentTracksBuffer = new LinkedHashMap<ChainContext, ChainContext>();
	private Map<Long, ChainContext> nodeRelationBuffer = new LinkedHashMap<Long, ChainContext>();

	public void setChainService(ChainService chainService) {
		this.chainService = chainService;
	}

	@Override
	public void linkParentChild(ChainContext parent, ChainContext child) {
		childParentTracksBuffer.put(child, parent);
	}

	public void flush() {
		log.debug(
				"start flush batch data [data-node-relations: {}, parent-child-relations: {}",
				new Object[] { nodeRelationBuffer.size(),
						childParentTracksBuffer.size() });
		if (!nodeRelationBuffer.isEmpty()) {
			chainService.batchCreateNodesAndRelation(nodeRelationBuffer);
		}
		if (!childParentTracksBuffer.isEmpty()) {
			chainService.batchCreateParentChildTrack(childParentTracksBuffer);
		}
		log.debug("finish flush batch data");
	}

	@Override
	public void createNodeAndRelation(long acid, ChainContext primaryTrack) {
		nodeRelationBuffer.put(acid, primaryTrack);
	}
}
