package com.hp.it.perf.ac.service.chain;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.hp.it.perf.ac.common.model.ChainEntry;
import com.hp.it.perf.ac.core.AcService;
import com.hp.it.perf.ac.service.chain.impl.DataNode;
import com.hp.it.perf.ac.service.chain.impl.LinkNode;

public interface ChainService extends AcService{

	public DataNode createDataNode(long acid);

	public List<Long> batchCreateDataNode(List<Long> batchRequest);

	public LinkNode createLinkNode(ChainContext primaryTrack);

	public List<ChainContext> batchCreateLinkNode(List<ChainContext> batchRequest);

	// Linkage Operations
	public AcRelation createRelation(long acid, ChainContext primaryTrack);

	public Map<Long, AcRelation> batchCreateRelation(Map<Long, ChainContext> batchRequest);
	
	public Map<Long, AcRelation> batchCreateNodesAndRelation(Map<Long, ChainContext> batchRequest);

	public void createParentChildTrack(ChainContext parentTrack, ChainContext childTrack);

	// map of child, parent
	public void batchCreateParentChildTrack(Map<ChainContext, ChainContext> childParentTracks);

	// get data node by identifier
	public DataNode getDataNode(long acid);

	// get link node by type and value
	public LinkNode getLinkNode(int type, String value);

	// get link node by chain context
	public LinkNode getLinkNode(ChainContext context);

	// Get or Find Operations
	public AcRelation findRelation(long acid);

	public AcRelation getRelation(long acid) throws NodeNotFoundException;

	public ChainContext getPrimaryTrack(AcRelation node) throws NodeNotFoundException;

	public AcRelation[] listRelationByPrimaryTrack(ChainContext primaryTrack);

	// Get Chain Operations
	public ChainEntry<AcRelation, ChainContext> getDownstreamChainByPrimaryTrack(ChainContext primaryTrack)
			throws NodeNotFoundException;

	public ChainEntry<AcRelation, ChainContext> getFullChainByPrimaryTrack(ChainContext primaryTrack)
			throws NodeNotFoundException;

	public ChainEntry<AcRelation, ChainContext> getFullChainByIdentifier(long acid) throws NodeNotFoundException;

	public ChainEntry<AcRelation, ChainContext> getUpstreamChainByPrimaryTrack(ChainContext primaryTrack)
			throws NodeNotFoundException;

	public ChainEntry<AcRelation, ChainContext> getAggregatedChainByIdentifiers(long... acids)
			throws NodeNotFoundException;

	// Management Operations
	public void clear();

	public Iterator<AcRelation> listAllRelations();

	public long getRelationCount();

	public long getLinkCount();
	
	// delete data node and link node by identifier
	public boolean deletedNode(long acid);
	
	// delete data node and link node by a list of identifiers
	public int batchDeletedNode(List<Long> batchAcids);
}
