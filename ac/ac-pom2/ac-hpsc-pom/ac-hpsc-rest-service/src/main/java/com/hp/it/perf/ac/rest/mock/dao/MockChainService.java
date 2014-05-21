package com.hp.it.perf.ac.rest.mock.dao;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.hp.it.perf.ac.app.hpsc.HpscDictionary;
import com.hp.it.perf.ac.common.model.AcidHelper;
import com.hp.it.perf.ac.common.model.ChainEntry;
import com.hp.it.perf.ac.common.model.ChainEntry.EntryData;
import com.hp.it.perf.ac.service.chain.AcRelation;
import com.hp.it.perf.ac.service.chain.ChainContext;
import com.hp.it.perf.ac.service.chain.ChainService;
import com.hp.it.perf.ac.service.chain.NodeNotFoundException;
import com.hp.it.perf.ac.service.chain.impl.DataNode;
import com.hp.it.perf.ac.service.chain.impl.LinkNode;

@Service
public class MockChainService implements ChainService {

	@Override
	public DataNode createDataNode(long acid) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Long> batchCreateDataNode(List<Long> batchRequest) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LinkNode createLinkNode(ChainContext primaryTrack) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ChainContext> batchCreateLinkNode(
			List<ChainContext> batchRequest) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AcRelation createRelation(long acid, ChainContext primaryTrack) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<Long, AcRelation> batchCreateRelation(
			Map<Long, ChainContext> batchRequest) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<Long, AcRelation> batchCreateNodesAndRelation(
			Map<Long, ChainContext> batchRequest) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void createParentChildTrack(ChainContext parentTrack,
			ChainContext childTrack) {
		// TODO Auto-generated method stub

	}

	@Override
	public void batchCreateParentChildTrack(
			Map<ChainContext, ChainContext> childParentTracks) {
		// TODO Auto-generated method stub

	}

	@Override
	public DataNode getDataNode(long acid) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LinkNode getLinkNode(int type, String value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LinkNode getLinkNode(ChainContext context) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AcRelation findRelation(long acid) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AcRelation getRelation(long acid) throws NodeNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ChainContext getPrimaryTrack(AcRelation node)
			throws NodeNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AcRelation[] listRelationByPrimaryTrack(ChainContext primaryTrack) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ChainEntry<AcRelation, ChainContext> getDownstreamChainByPrimaryTrack(
			ChainContext primaryTrack) throws NodeNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ChainEntry<AcRelation, ChainContext> getFullChainByPrimaryTrack(
			ChainContext primaryTrack) throws NodeNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ChainEntry<AcRelation, ChainContext> getFullChainByIdentifier(
			long acid) throws NodeNotFoundException {
		// TODO Auto-generated method stub
		int profile = 1;
		int sid = 2;
		int category = HpscDictionary.INSTANCE.category("SPFPerformanceLog")
				.code();
		int wsrp_category = HpscDictionary.INSTANCE.category(
				"SPFPerformanceLogDetail").code();
		int type = HpscDictionary.INSTANCE.category("SPFPerformanceLog")
				.type("REQUEST").code();
		int wsrp_type = HpscDictionary.INSTANCE
				.category("SPFPerformanceLogDetail").type("WSRP_CALL").code();
		int level = 1;

		// request
		ChainEntry<AcRelation, ChainContext> chain = new ChainEntry<AcRelation, ChainContext>();
		chain.setCurrent(true);
		chain.setParent(null);
		ChainContext request = new ChainContext();
//		request.setName("http://www.hp.com/go/hpsc/public");
		request.setCode(HpscDictionary.HpscContextType.DiagnosticID.code());
		request.setValue("123456");
		chain.setValue(request);

		AcRelation data = new AcRelation();
		data.setAcid(acid);
		EntryData<AcRelation> dataNode = new EntryData<AcRelation>();
		dataNode.setData(data);
		chain.getDataNodes().add(dataNode);

		// wsrp
		ChainEntry<AcRelation, ChainContext> wsrp = new ChainEntry<AcRelation, ChainContext>();
		wsrp.setCurrent(false);
		wsrp.setParent(chain);
		ChainContext wsrp_context = new ChainContext();
//		wsrp_context.setName("123456 + AAE");
		wsrp_context.setCode(HpscDictionary.HpscContextType.PortletName.code());
		wsrp_context.setValue("AAE");
		wsrp.setValue(wsrp_context);

		AcRelation data2 = new AcRelation();
		long acid_wsrp = AcidHelper.getInstance().getAcid(profile, sid,
				wsrp_category, wsrp_type, level);
		data2.setAcid(acid_wsrp);
		EntryData<AcRelation> dataNode2 = new EntryData<AcRelation>();
		dataNode2.setData(data2);
		wsrp.getDataNodes().add(dataNode2);

		// portlet biz
		ChainEntry<AcRelation, ChainContext> portlet_biz = new ChainEntry<AcRelation, ChainContext>();
		portlet_biz.setCurrent(false);
		portlet_biz.setParent(wsrp);
		ChainContext portlet_biz_context = new ChainContext();
//		portlet_biz_context.setName("123456 + AAE");
		portlet_biz_context
				.setCode(HpscDictionary.HpscContextType.PortletTransactionId
						.code());
		portlet_biz_context.setValue("tran10086");
		portlet_biz.setValue(portlet_biz_context);

		AcRelation data3 = new AcRelation();
		// category: PortalBizLog(2), type: AAE(2)
		long acid_biz = AcidHelper.getInstance().getAcid(
				profile,
				sid,
				HpscDictionary.INSTANCE.category("PortalBizLog").code(),
				HpscDictionary.INSTANCE.category("PortalBizLog").type("AAE")
						.code(), level);
		data3.setAcid(acid_biz);
		EntryData<AcRelation> dataNode3 = new EntryData<AcRelation>();
		dataNode3.setData(data3);
		portlet_biz.getDataNodes().add(dataNode3);

		// portlet perf
		ChainEntry<AcRelation, ChainContext> portlet_perf = new ChainEntry<AcRelation, ChainContext>();
		portlet_perf.setCurrent(false);
		portlet_perf.setParent(wsrp);
		ChainContext portlet_perf_context = new ChainContext();
//		portlet_perf_context.setName("123456 + AAE");
		portlet_perf_context
				.setCode(HpscDictionary.HpscContextType.PortletTransactionId
						.code());
		portlet_perf_context.setValue("tran10086");
		portlet_perf.setValue(portlet_perf_context);

		AcRelation data4 = new AcRelation();
		// category: PortalPerfLog(3), type: AAE(2)
		long acid_perf = AcidHelper.getInstance().getAcid(
				profile,
				sid,
				HpscDictionary.INSTANCE.category("PortalPerfLog").code(),
				HpscDictionary.INSTANCE.category("PortalPerfLog").type("AAE")
						.code(), level);
		data4.setAcid(acid_perf);
		EntryData<AcRelation> dataNode4 = new EntryData<AcRelation>();
		dataNode4.setData(data4);
		portlet_perf.getDataNodes().add(dataNode4);

		// portlet error
		ChainEntry<AcRelation, ChainContext> portlet_error = new ChainEntry<AcRelation, ChainContext>();
		portlet_error.setCurrent(false);
		portlet_error.setParent(wsrp);
		ChainContext portlet_error_context = new ChainContext();
//		portlet_error_context.setName("123456 + AAE");
		portlet_error_context
				.setCode(HpscDictionary.HpscContextType.PortletTransactionId
						.code());
		portlet_error_context.setValue("tran10086");
		portlet_error.setValue(portlet_error_context);

		AcRelation data5 = new AcRelation();
		// category: PortalErrorLog(4), type: AAE(2)
		long acid_error = AcidHelper.getInstance().getAcid(
				profile,
				sid,
				HpscDictionary.INSTANCE.category("PortalErrorLog").code(),
				HpscDictionary.INSTANCE.category("PortalErrorLog").type("AAE")
						.code(), level);
		data5.setAcid(acid_error);
		EntryData<AcRelation> dataNode5 = new EntryData<AcRelation>();
		dataNode5.setData(data5);
		portlet_error.getDataNodes().add(dataNode5);

		AcRelation data6 = new AcRelation();
		// category: PortalErrorTraceLog(5), type: AAE(2)
		long acid_errortrace = AcidHelper.getInstance().getAcid(
				profile,
				sid,
				HpscDictionary.INSTANCE.category("PortalErrorTraceLog").code(),
				HpscDictionary.INSTANCE.category("PortalErrorTraceLog")
						.type("AAE").code(), level);
		data6.setAcid(acid_errortrace);
		EntryData<AcRelation> dataNode6 = new EntryData<AcRelation>();
		dataNode6.setData(data6);
		portlet_error.getDataNodes().add(dataNode6);

		wsrp.getChildEntryNodes().add(portlet_biz);
		wsrp.getChildEntryNodes().add(portlet_perf);
		wsrp.getChildEntryNodes().add(portlet_error);
		chain.getChildEntryNodes().add(wsrp);
		return chain;
	}

	@Override
	public ChainEntry<AcRelation, ChainContext> getUpstreamChainByPrimaryTrack(
			ChainContext primaryTrack) throws NodeNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ChainEntry<AcRelation, ChainContext> getAggregatedChainByIdentifiers(
			long... acids) throws NodeNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub

	}

	@Override
	public Iterator<AcRelation> listAllRelations() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getRelationCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getLinkCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean deletedNode(long acid) {
		// TODO Auto-generated method stub
		return false;
		
	}

	@Override
	public int batchDeletedNode(List<Long> batchAcids) {
		// TODO Auto-generated method stub
		return 0;
	}

}
