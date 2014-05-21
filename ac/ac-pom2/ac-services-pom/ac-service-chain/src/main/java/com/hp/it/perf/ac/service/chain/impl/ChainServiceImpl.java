package com.hp.it.perf.ac.service.chain.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.neo4j.kernel.impl.transaction.XaDataSourceManager;
import org.neo4j.kernel.impl.transaction.xaframework.XaDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.neo4j.conversion.EndResult;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.data.neo4j.support.Neo4jTemplate;
import org.springframework.stereotype.Service;

import com.hp.it.perf.ac.common.model.AcCommonData;
import com.hp.it.perf.ac.common.model.ChainEntry;
import com.hp.it.perf.ac.common.model.ChainEntry.EntryData;
import com.hp.it.perf.ac.core.AcDataRepository;
import com.hp.it.perf.ac.service.chain.AcRelation;
import com.hp.it.perf.ac.service.chain.ChainContext;
import com.hp.it.perf.ac.service.chain.ChainService;
import com.hp.it.perf.ac.service.chain.NodeNotFoundException;

@Service
@DependsOn("neo4jNodeBacking")
public class ChainServiceImpl implements ChainService {

	@Autowired
	Neo4jTemplate template;

	private GraphRepository<DataNode> dataRepo;

	private GraphRepository<LinkNode> linkRepo;
	
	@Inject
	private AcDataRepository acDataRepository;
	
	private LinkNodeIdCache<String, Long> nodeIdCache;

	private static final Logger logger = LoggerFactory
			.getLogger(ChainServiceImpl.class);

	@PostConstruct
	public void initRepos() {
		updateNeo4jSetting();
		dataRepo = template.repositoryFor(DataNode.class);
		linkRepo = template.repositoryFor(LinkNode.class);
		nodeIdCache = new LinkNodeIdCache<String, Long>(Integer.getInteger("chainLinkCache", 10000));
	}

	private void updateNeo4jSetting() {
		GraphDatabaseService graphDb = template.getGraphDatabaseService();
		if (graphDb instanceof EmbeddedGraphDatabase) {
			XaDataSourceManager xaDsMgr = ((EmbeddedGraphDatabase) graphDb)
					.getXaDataSourceManager();
			XaDataSource xaDs = xaDsMgr.getXaDataSource("nioneodb");
			if (xaDs != null) {
				// or to increase log target size to 100MB (default 10MB)
				xaDs.setLogicalLogTargetSize(100 * 1024 * 1024L);
			}
			XaDataSource lucenceDs = xaDsMgr.getXaDataSource("lucene-index");
			if (lucenceDs != null) {
				lucenceDs.setLogicalLogTargetSize(100 * 1024 * 1024L);
			}
		}
	}

	@PreDestroy
	public void removeRepos() {
		dataRepo = null;
		linkRepo = null;
	}

	private AcRelation convert(DataNode dataNode) {
		AcRelation acDataNode = new AcRelation();
		acDataNode.setAcid(dataNode.getIdentifier());
		// store back to context
		acDataNode.setAttachment(acDataNode);
		return acDataNode;
	}

	private ChainContext convert(LinkNode link) {
		ChainContext context = new ChainContext();
		context.setCode(link.getType());
		context.setValue(link.getValue());
		// store back to context
		context.setAttachment(link);
		return context;
	}

	private LinkNode convertToLink(ChainContext context) {
		LinkNode link = new LinkNode();
		link.setTypeValue(context.getCode(), context.getValue());
		// store back to context
		context.setAttachment(link);
		return link;
	}

	@Override
	public DataNode createDataNode(long acid) {
		DataNode dataNode;

		// data node does not exist, create it
		dataNode = new DataNode();
		dataNode.setIdentifier(acid);
		dataRepo.save(dataNode);
		logger.debug("persist-data node: {}", dataNode);
		return dataNode;
	}

	@Override
	public LinkNode createLinkNode(ChainContext context) {
		// verify if link node exist
		LinkNode linkNode = findLinkNode(context, false);

		if (linkNode != null) {
			logger.debug("Find the link node in graph: {}", linkNode);
			return linkNode;
		}

		linkNode = convertToLink(context);
		linkNode = linkRepo.save(linkNode);
		nodeIdCache.put(linkNode.getTypeValue(), linkNode.getId());
		logger.debug("persist-link node: {}", linkNode);
		return linkNode;
	}

	@Override
	public AcRelation createRelation(long acid, ChainContext context) {
		DataNode dataNode = null;
		LinkNode linkNode = null;

		// if data node does not exist, throw NodeNotFoundException
		try {
			dataNode = findDataNode(acid, true);
		} catch (NodeNotFoundException e1) {
			logger.info(
					"Cannot find data node for acid: {}, when try to create relation with ac context: {}.",
					acid, context);
			throw new NodeNotFoundException("Cannot find data node for acid: "
					+ acid + ", when try to create relation with ac context: "
					+ context);
		}

		// if link node does not exist, throw NodeNotFoundException
		try {
			linkNode = findLinkNode(context, true);
		} catch (NodeNotFoundException e2) {
			logger.info(
					"Cannot find link node for ac context: {}, when try to create relation with acid: {}.",
					context, acid);
			throw new NodeNotFoundException(
					"Cannot find link node for ac context: " + context
							+ ", when try to create relation with acid: "
							+ acid);
		}

		// create relation
		return createRelation(dataNode, linkNode);
	}

	private AcRelation createRelation(DataNode dataNode, LinkNode linkNode) {
		// create relation
		dataNode.setLink(linkNode);
		dataRepo.save(dataNode);
		logger.debug("persist-releation for data node: {}", dataNode);
		return convert(dataNode);
	}

	@Override
	public void createParentChildTrack(ChainContext parentContext,
			ChainContext childContext) {
		LinkNode pLink = null;
		LinkNode cLink = null;

		// if child link node does not exist, throw NodeNotFoundException
		try {
			cLink = findLinkNode(childContext, true);
		} catch (NodeNotFoundException ex) {
			logger.info("Cannot find link node for child context: {}",
					childContext);
			throw new NodeNotFoundException(
					"Cannot find link node for child context: " + childContext);
		}

		pLink = findLinkNode(parentContext, false);
		if (pLink == null) {
			// not found, create parent link
			pLink = convertToLink(parentContext);
			logger.debug("create-parent-link node: {}", pLink);
		}

		cLink.setParent(pLink);
		linkRepo.save(cLink);
		logger.debug("persist-child-link: {}", cLink);
	}

	private DataNode findDataNode(long acid, boolean errorIfNotFound)
			throws NodeNotFoundException {
		DataNode dataNode = dataRepo.findByPropertyValue("identifier", acid);

		if (dataNode == null && errorIfNotFound) {
			throw new NodeNotFoundException("Cannot find data node for acid: "
					+ acid);
		}
		return dataNode;
	}

	private LinkNode findLinkNode(ChainContext context, boolean errorIfNotFound)
			throws NodeNotFoundException {
		if (context == null) {
			throw new NodeNotFoundException("context is null");
		}

		// check attachment
		if (context.getAttachment() instanceof LinkNode) {
			return (LinkNode) context.getAttachment();
		}

		LinkNode result;
		String typeValue = LinkNode.toTypeValue(context.getCode(), context.getValue());
		// check quick cache
		Long linkId = nodeIdCache.get(typeValue);
		if (linkId != null) {
			result = linkRepo.findOne(linkId);
		} else {
		// check graph store
			result = linkRepo.findByPropertyValue("tValue", typeValue);
		}

		if (result != null) {
			// store back to context
			context.setAttachment(result);
			nodeIdCache.put(result.getTypeValue(), result.getId());
		}

		if (result==null && errorIfNotFound) {
			throw new NodeNotFoundException("Cannot find link node for "
					+ context.toString());
		}
		return result;
	}

	private ChainEntry<AcRelation, ChainContext> traverseDown(LinkNode link,
			LinkNode refLink) {
		ChainEntry<AcRelation, ChainContext> chain = new ChainEntry<AcRelation, ChainContext>();
		chain.setValue(convert(link));
		for (DataNode data : link.getDataList()) {
			chain.getDataNodes().add(convertToEntryData(data));
		}
		for (LinkNode child : link.getChildLinkList()) {
			chain.getChildEntryNodes().add(traverseDown(child, refLink));
		}
		chain.setCurrent(link == refLink);
		return chain;
	}

	private ChainEntry<AcRelation, ChainContext> traverseUp(LinkNode link,
			ChainEntry<AcRelation, ChainContext> chain) {
		chain.setValue(convert(link));
		for (DataNode data : link.getDataList()) {
			chain.getDataNodes().add(convertToEntryData(data));
		}
		LinkNode parent = link.getParent();
		if (parent != null) {
			ChainEntry<AcRelation, ChainContext> parentChain = new ChainEntry<AcRelation, ChainContext>();
			parentChain.getChildEntryNodes().add(chain);
			return traverseUp(parent, parentChain);
		}
		return chain;
	}

	private EntryData<AcRelation> convertToEntryData(DataNode data) {
		EntryData<AcRelation> entryData = new EntryData<AcRelation>();
		entryData.setData(convert(data));
		return entryData;
	}

	@Override
	public ChainContext getPrimaryTrack(AcRelation node)
			throws NodeNotFoundException {
		DataNode dataNode = findDataNode(node.getAcid(), true);
		return convert(dataNode.getLink());
	}

	@Override
	public AcRelation[] listRelationByPrimaryTrack(ChainContext context) {
		LinkNode link = findLinkNode(context, false);
		if (link == null) {
			return new AcRelation[0];
		}
		List<AcRelation> list = new ArrayList<AcRelation>();
		for (DataNode dataNode : link.getDataList()) {
			list.add(convert(dataNode));
		}
		return list.toArray(new AcRelation[list.size()]);
	}

	@Override
	public ChainEntry<AcRelation, ChainContext> getDownstreamChainByPrimaryTrack(
			ChainContext primaryTrack) throws NodeNotFoundException {
		LinkNode link = findLinkNode(primaryTrack, true);
		ChainEntry<AcRelation, ChainContext> chain = traverseDown(link, link);
		return chain;
	}

	@Override
	public ChainEntry<AcRelation, ChainContext> getFullChainByPrimaryTrack(
			ChainContext context) throws NodeNotFoundException {
		LinkNode link = findLinkNode(context, true);
		LinkNode ancestor = link.getAncestor();
		return traverseDown(ancestor, link);
	}

	@Override
	public ChainEntry<AcRelation, ChainContext> getFullChainByIdentifier(
			long acid) throws NodeNotFoundException {
		DataNode data = findDataNode(acid, true);
		LinkNode link = data.getLink();
		if (link == null) {
			throw new NodeNotFoundException(
					"Cannot find link node related to acid: " + acid);
		}
		LinkNode ancestor = link.getAncestor();
		return traverseDown(ancestor, link);
	}

	@Override
	public ChainEntry<AcRelation, ChainContext> getUpstreamChainByPrimaryTrack(
			ChainContext context) throws NodeNotFoundException {
		LinkNode link = findLinkNode(context, true);
		ChainEntry<AcRelation, ChainContext> chain = new ChainEntry<AcRelation, ChainContext>();
		chain.setCurrent(true);
		return traverseUp(link, chain);
	}

	@Override
	public AcRelation findRelation(long acid) {
		return getRelation(acid, false);
	}

	@Override
	public AcRelation getRelation(long acid) throws NodeNotFoundException {
		return getRelation(acid, true);
	}

	private AcRelation getRelation(long acid, boolean errorIfNotFound)
			throws NodeNotFoundException {
		DataNode dataNode = dataRepo.findByPropertyValue("identifier", acid);

		if (dataNode != null) {
			return convert(dataNode);
		}
		if (errorIfNotFound) {
			throw new NodeNotFoundException("relation is not found by acid: "
					+ acid);
		}
		return null;
	}

	@Override
	public Iterator<AcRelation> listAllRelations() {
		final EndResult<DataNode> all = dataRepo.findAll();
		final Iterator<DataNode> allIds = all.iterator();
		return new Iterator<AcRelation>() {
			boolean end;

			@Override
			public void remove() {
				throw new UnsupportedOperationException("remove");
			}

			@Override
			public AcRelation next() {
				if (end) {
					throw new NoSuchElementException();
				}
				DataNode dataNode = allIds.next();
				return convert(dataNode);
			}

			@Override
			public boolean hasNext() {
				boolean hasNext = allIds.hasNext();
				end = !hasNext;
				if (end) {
					all.finish();
				}
				return hasNext;
			}

			@Override
			protected void finalize() throws Throwable {
				if (!end) {
					all.finish();
				}
			}

		};
	}

	@Override
	public long getRelationCount() {
		return dataRepo.count();
	}

	@Override
	public long getLinkCount() {
		return linkRepo.count();
	}

	@Override
	public Map<Long, AcRelation> batchCreateRelation(
			Map<Long, ChainContext> batchRequest) {
		Transaction transaction = template.getGraphDatabaseService().beginTx();
		Map<Long, AcRelation> result = new LinkedHashMap<Long, AcRelation>();
		try {
			for (Map.Entry<Long, ChainContext> entry : batchRequest.entrySet()) {
				result.put(entry.getKey(),
						createRelation(entry.getKey(), entry.getValue()));
			}
			transaction.success();
		} finally {
			transaction.finish();
		}
		return result;
	}

	@Override
	public void batchCreateParentChildTrack(
			Map<ChainContext, ChainContext> childParentTracks) {
		Transaction transaction = template.getGraphDatabaseService().beginTx();
		try {
			for (Map.Entry<ChainContext, ChainContext> entry : childParentTracks
					.entrySet()) {
				createParentChildTrack(entry.getValue(), entry.getKey());
			}
			transaction.success();
		} finally {
			transaction.finish();
		}
	}

	@Override
	public List<Long> batchCreateDataNode(List<Long> batchRequest) {
		Transaction transaction = template.getGraphDatabaseService().beginTx();
		List<Long> result = new ArrayList<Long>();
		try {
			for (Long acid : batchRequest) {
				result.add(acid);
				createDataNode(acid);
			}
			transaction.success();
		} finally {
			transaction.finish();
		}
		return result;
	}

	@Override
	public List<ChainContext> batchCreateLinkNode(
			List<ChainContext> batchRequest) {
		Transaction transaction = template.getGraphDatabaseService().beginTx();
		List<ChainContext> result = new ArrayList<ChainContext>();
		try {
			for (ChainContext context : batchRequest) {
				result.add(context);
				createLinkNode(context);
			}
			transaction.success();
		} finally {
			transaction.finish();
		}
		return result;
	}

	@Override
	public DataNode getDataNode(long acid) {
		return findDataNode(acid, false);
	}

	@Override
	public LinkNode getLinkNode(int type, String value) {
		ChainContext context = new ChainContext();
		context.setCode(type);
		context.setValue(value);
		return findLinkNode(context, false);
	}

	@Override
	public LinkNode getLinkNode(ChainContext context) {
		return findLinkNode(context, false);
	}

	@Override
	public void clear() {
		Transaction tx = template.getGraphDatabaseService().beginTx();

		try {
			linkRepo.deleteAll();
			dataRepo.deleteAll();

			tx.success();
		} finally {
			tx.finish();
		}
	}

	@Override
	public ChainEntry<AcRelation, ChainContext> getAggregatedChainByIdentifiers(
			long... acids) throws NodeNotFoundException {
		ChainEntry<AcRelation, ChainContext> root = new ChainEntry<AcRelation, ChainContext>();
		for (long acid : acids) {
			aggregated(root, acid);
		}
		return root;
	}

	private ChainEntry<AcRelation, ChainContext> aggregated(
			ChainEntry<AcRelation, ChainContext> root, long acid) {
		ChainEntry<AcRelation, ChainContext> chain = getFullChainByIdentifier(acid);
		ChainContext context = chain.getValue();
		String dataName = getAcCommonDataName(chain.getDataNodes());
		if (dataName!=null) {
			context.setValue(dataName); // set the name to the value
		}
		chain.setValue(context);
		aggregated(root, chain);
		return root;
	}

	private String getAcCommonDataName(List<EntryData<AcRelation>> list) {
		// TODO use first (this is known issue)
		AcCommonData commonData = acDataRepository.getCommonData(list.get(0).getData().getAcid());
		if (commonData != null) {
			// found
			return commonData.getName();
		} else {
			return null;
		}
	}

	private ChainEntry<AcRelation, ChainContext> aggregated(
			ChainEntry<AcRelation, ChainContext> root,
			ChainEntry<AcRelation, ChainContext> chain) {
		ChainContext context = chain.getValue();
		List<EntryData<AcRelation>> dataNodes = chain.getDataNodes();

		ChainEntry<AcRelation, ChainContext> nextRoot = null;
		for (ChainEntry<AcRelation, ChainContext> child : root
				.getChildEntryNodes()) {
			if (context.getValue().equals(child.getValue().getValue())) {
				// same value exist
				List<EntryData<AcRelation>> temp = new ArrayList<EntryData<AcRelation>>();
				for (EntryData<AcRelation> d : dataNodes) {
					boolean acid_exist = false;
					for (EntryData<AcRelation> data : child.getDataNodes()) {
						if (d.getData().getAcid() == data.getData().getAcid()) {
							acid_exist = true;
						}
					}
					if (!acid_exist) {
						temp.add(d);
					}
				}
				child.getDataNodes().addAll(temp);
				nextRoot = child;
				break;
			}
		}
		if (nextRoot == null) {
			ChainEntry<AcRelation, ChainContext> temp = new ChainEntry<AcRelation, ChainContext>();
			temp.setValue(context);
			// temp.setCurrent(chain.isCurrent());
			temp.getDataNodes().addAll(dataNodes);
			root.getChildEntryNodes().add(temp);
			nextRoot = temp;
		}

		// aggregated next chain entry if any
		for (ChainEntry<AcRelation, ChainContext> child : chain
				.getChildEntryNodes()) {
			aggregated(nextRoot, child);
		}
		return root;
	}

	@Override
	public Map<Long, AcRelation> batchCreateNodesAndRelation(
			Map<Long, ChainContext> batchRequest) {
		Transaction transaction = template.getGraphDatabaseService().beginTx();
		try {
			Map<Long, AcRelation> result = new LinkedHashMap<Long, AcRelation>();
			for (Map.Entry<Long, ChainContext> entry : batchRequest.entrySet()) {
				LinkNode linkNode = createLinkNode(entry.getValue());
				result.put(entry.getKey(), createDataRelation(entry.getKey(), linkNode));
			}
			transaction.success();
			return result;
		} finally {
			transaction.finish();
		}
	}
	
	private AcRelation createDataRelation(Long acid, LinkNode linkNode) {
		DataNode dataNode = new DataNode();
		dataNode.setIdentifier(acid);
		dataNode.setLink(linkNode);
		dataRepo.save(dataNode);
		logger.debug("persist-data/releation for data node: {}", dataNode);
		return convert(dataNode);
	}

	@Override
	public boolean deletedNode(long acid){
		Transaction tx = template.getGraphDatabaseService().beginTx();
		DataNode dataNode = dataRepo.findByPropertyValue("identifier", acid);

		if (dataNode == null) {
			logger.debug("Cannot find data node for acid: "
					+ acid);
			return false;
		}
		LinkNode linkNode = dataNode.getLink();

		try {
			if(linkNode != null){
				try{
					template.deleteRelationshipBetween(dataNode, linkNode, "LINK");
				} finally{
					linkRepo.delete(linkNode);
					logger.debug("delete-link node: {}", linkNode);
				}
			}
			dataRepo.delete(dataNode);
			logger.debug("delete-data node: {}", dataNode);
			tx.success();
		} finally {
			tx.finish();
		}
		return true;
	}
	
	@Override
	public int batchDeletedNode(List<Long> batchAcids){
		if(batchAcids == null || batchAcids.size() == 0){
			logger.error("Acid list is null or acid list size is 0.");
			return 0;
		}
		
		int count = 0;
		List<Long> ids = new ArrayList<Long>(batchAcids.size());
		for (Long acid : batchAcids) {
			DataNode dataNode = dataRepo.findByPropertyValue("identifier",
					acid);

			if (dataNode == null) {
				logger.debug("Cannot find data node for acid: " + acid);
				continue;
			} else {
				ids.add(dataNode.getId());
				count++;
			}
		}
		
		if(ids.size() > 0) {
			Transaction tx = template.getGraphDatabaseService().beginTx();
			try {
				StringBuffer statement = new StringBuffer(62 + ids.size() << 3);
				statement.append("START n=node(");
				for (Long id : ids) {
					statement.append(id).append(",");
				}
				statement.deleteCharAt(statement.length() - 1);
				statement.append(") MATCH n-[r?:LINK]->ln-[r1?]-() delete r,r1,n,ln");
				template.query(statement.toString(), null);
				tx.success();
			} catch (Exception e) {
				logger.error("Error when deleting the nodes");
			} finally {
				tx.finish();
			}
		}
		return count;
	}
	
}
