package com.hp.it.perf.ac.service.spfchain;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.hp.it.perf.ac.app.hpsc.HpscDictionary;
import com.hp.it.perf.ac.common.model.AcCommonDataWithPayLoad;
import com.hp.it.perf.ac.common.model.AcContext;
import com.hp.it.perf.ac.common.model.AcidHelper;
import com.hp.it.perf.ac.common.model.ChainEntry;
import com.hp.it.perf.ac.service.chain.AcRelation;
import com.hp.it.perf.ac.service.chain.ChainContext;
import com.hp.it.perf.ac.service.chain.ChainService;
import com.hp.it.perf.ac.service.chain.impl.ChainServiceImpl;

public class SetupChainToNeo4j {

	public static void main(String[] args) {

		ApplicationContext context = new ClassPathXmlApplicationContext(new String[] { "spring/ac-service-chain.xml" });

		System.out.println(context);

		ChainService cs = context.getBean(ChainServiceImpl.class);
		// clear data
		//cs.clear();

		System.out.println("acid count: " + cs.getRelationCount());

		SpfChainService scs = context.getBean(SpfChainServiceImpl.class);
		System.out.println(scs);

		int profile = 1;
		int sid = 2;
		int category = HpscDictionary.INSTANCE.category("SPFPerformanceLog").code();
		int wsrp_category = HpscDictionary.INSTANCE.category("SPFPerformanceLogDetail").code();
		int type = HpscDictionary.INSTANCE.category("SPFPerformanceLog").type("REQUEST").code();
		int wsrp_type = HpscDictionary.INSTANCE.category("SPFPerformanceLogDetail").type("WSRP_CALL").code();
		int level = 1;

		// init test data
		long acid = AcidHelper.getInstance().getAcid(profile, sid, category, type, level);
		System.out.println("acid: " + acid);

		List<AcContext> globalTrackId = new ArrayList<AcContext>();
		AcContext actrack = new AcContext();
		actrack.setCode(HpscDictionary.HpscContextType.DiagnosticID.code());
		actrack.setValue("123456");
		globalTrackId.add(actrack);

		AcContext actrack2 = new AcContext();
		actrack2.setCode(HpscDictionary.HpscContextType.PortletName.code());
		actrack2.setValue("AAE");
		globalTrackId.add(actrack2);

		AcCommonDataWithPayLoad data = new AcCommonDataWithPayLoad();
		data.setAcid(acid);
		data.setName("http://xxx");
		data.setContexts(globalTrackId);

		// data2
		long acid_wsrp = AcidHelper.getInstance().getAcid(profile, sid, wsrp_category, wsrp_type, level);
		System.out.println("acid_wsrp: " + acid_wsrp);

		List<AcContext> contexts_wsrp = new ArrayList<AcContext>();
		AcContext context_wsrp1 = new AcContext();
		context_wsrp1.setCode(HpscDictionary.HpscContextType.DiagnosticID.code());
		context_wsrp1.setValue("123456");
		contexts_wsrp.add(context_wsrp1);

		AcContext context_wsrp2 = new AcContext();
		context_wsrp2.setCode(HpscDictionary.HpscContextType.PortletName.code());
		context_wsrp2.setValue("AAE");
		contexts_wsrp.add(context_wsrp2);

		AcCommonDataWithPayLoad data2 = new AcCommonDataWithPayLoad();
		data2.setAcid(acid_wsrp);
		data2.setContexts(contexts_wsrp);
		
		// data2-psi
		long acid_wsrp_psi = AcidHelper.getInstance().getAcid(profile, sid+1, wsrp_category, wsrp_type, level);
		System.out.println("acid_wsrp_psi: " + acid_wsrp_psi);

		List<AcContext> contexts_wsrp_psi = new ArrayList<AcContext>();
		AcContext context_wsrp1_psi = new AcContext();
		context_wsrp1_psi.setCode(HpscDictionary.HpscContextType.DiagnosticID.code());
		context_wsrp1_psi.setValue("123456");
		contexts_wsrp_psi.add(context_wsrp1_psi);

		AcContext context_wsrp2_psi = new AcContext();
		context_wsrp2_psi.setCode(HpscDictionary.HpscContextType.PortletName.code());
		context_wsrp2_psi.setValue("PSI");
		contexts_wsrp_psi.add(context_wsrp2_psi);

		AcCommonDataWithPayLoad data2_psi = new AcCommonDataWithPayLoad();
		data2_psi.setAcid(acid_wsrp_psi);
		data2_psi.setContexts(contexts_wsrp_psi);

		// data3
		// category: PortalBizLog(2), type: AAE(2)
		long acid_biz = AcidHelper.getInstance().getAcid(profile, sid, HpscDictionary.INSTANCE.category("PortalBizLog").code(), HpscDictionary.INSTANCE.category("PortalBizLog").type("AAE").code(), level);
		System.out.println("acid_biz: " + acid_biz);

		List<AcContext> contexts_biz = new ArrayList<AcContext>();
		AcContext contexts_biz_dc = new AcContext();
		contexts_biz_dc.setCode(HpscDictionary.HpscContextType.DiagnosticID.code());
		contexts_biz_dc.setValue("123456");
		contexts_biz.add(contexts_biz_dc);

		AcContext contexts_biz_wsrp = new AcContext();
		contexts_biz_wsrp.setCode(HpscDictionary.HpscContextType.PortletName.code());
		contexts_biz_wsrp.setValue("AAE");
		contexts_biz.add(contexts_biz_wsrp);

		AcContext contexts_biz_tran = new AcContext();
		contexts_biz_tran.setCode(HpscDictionary.HpscContextType.PortletTransactionId.code());
		contexts_biz_tran.setValue("tran10086");
		contexts_biz.add(contexts_biz_tran);

		AcCommonDataWithPayLoad data3 = new AcCommonDataWithPayLoad();
		data3.setAcid(acid_biz);
		data3.setContexts(contexts_biz);

		// data4
		// category: PortalPerfLog(3), type: AAE(2)
		long acid_perf = AcidHelper.getInstance().getAcid(profile, sid, HpscDictionary.INSTANCE.category("PortalPerfLog").code(), HpscDictionary.INSTANCE.category("PortalPerfLog").type("AAE").code(), level);
		System.out.println("acid_perf: " + acid_perf);

		List<AcContext> contexts_perf = new ArrayList<AcContext>();
		AcContext contexts_perf_dc = new AcContext();
		contexts_perf_dc.setCode(HpscDictionary.HpscContextType.DiagnosticID.code());
		contexts_perf_dc.setValue("123456");
		contexts_perf.add(contexts_perf_dc);

		AcContext contexts_perf_wsrp = new AcContext();
		contexts_perf_wsrp.setCode(HpscDictionary.HpscContextType.PortletName.code());
		contexts_perf_wsrp.setValue("AAE");
		contexts_perf.add(contexts_perf_wsrp);

		AcContext contexts_perf_tran = new AcContext();
		contexts_perf_tran.setCode(HpscDictionary.HpscContextType.PortletTransactionId.code());
		contexts_perf_tran.setValue("tran10086");
		contexts_perf.add(contexts_perf_tran);

		AcCommonDataWithPayLoad data4 = new AcCommonDataWithPayLoad();
		data4.setAcid(acid_perf);
		data4.setContexts(contexts_perf);

		// data5
		// category: PortalErrorLog(4), type: AAE(2)
		long acid_error = AcidHelper.getInstance().getAcid(profile, sid, HpscDictionary.INSTANCE.category("PortalErrorLog").code(), HpscDictionary.INSTANCE.category("PortalErrorLog").type("AAE").code(), level);
		System.out.println("acid_error: " + acid_error);

		List<AcContext> contexts_error = new ArrayList<AcContext>();
		AcContext contexts_error_dc = new AcContext();
		contexts_error_dc.setCode(HpscDictionary.HpscContextType.DiagnosticID.code());
		contexts_error_dc.setValue("123456");
		contexts_error.add(contexts_error_dc);

		AcContext contexts_error_wsrp = new AcContext();
		contexts_error_wsrp.setCode(HpscDictionary.HpscContextType.PortletName.code());
		contexts_error_wsrp.setValue("AAE");
		contexts_error.add(contexts_error_wsrp);

		AcContext contexts_error_tran = new AcContext();
		contexts_error_tran.setCode(HpscDictionary.HpscContextType.PortletTransactionId.code());
		contexts_error_tran.setValue("tran10086");
		contexts_error.add(contexts_error_tran);

		AcCommonDataWithPayLoad data5 = new AcCommonDataWithPayLoad();
		data5.setAcid(acid_error);
		data5.setContexts(contexts_error);

		// data6
		// category: PortalErrorTraceLog(5), type: AAE(2)
		long acid_errortrace = AcidHelper.getInstance().getAcid(profile, sid, HpscDictionary.INSTANCE.category("PortalErrorTraceLog").code(), HpscDictionary.INSTANCE.category("PortalErrorTraceLog").type("AAE").code(), level);
		System.out.println("acid_errortrace: " + acid_errortrace);

		List<AcContext> contexts_errortrace = new ArrayList<AcContext>();
		AcContext contexts_errortrace_dc = new AcContext();
		contexts_errortrace_dc.setCode(HpscDictionary.HpscContextType.DiagnosticID.code());
		contexts_errortrace_dc.setValue("123456");
		contexts_errortrace.add(contexts_errortrace_dc);

		AcContext contexts_errortrace_wsrp = new AcContext();
		contexts_errortrace_wsrp.setCode(HpscDictionary.HpscContextType.PortletName.code());
		contexts_errortrace_wsrp.setValue("AAE");
		contexts_errortrace.add(contexts_errortrace_wsrp);

		AcContext contexts_errortrace_tran = new AcContext();
		contexts_errortrace_tran.setCode(HpscDictionary.HpscContextType.PortletTransactionId.code());
		contexts_errortrace_tran.setValue("tran10086");
		contexts_errortrace.add(contexts_errortrace_tran);

		AcCommonDataWithPayLoad data6 = new AcCommonDataWithPayLoad();
		data6.setAcid(acid_errortrace);
		data6.setContexts(contexts_errortrace);

		// data7
		long acid7 = AcidHelper.getInstance().getAcid(2, 3, category, type, level);
		System.out.println("acid7: " + acid7);

		List<AcContext> acid7_Contexts = new ArrayList<AcContext>();
		AcContext acid7_Context1 = new AcContext();
		acid7_Context1.setCode(HpscDictionary.HpscContextType.DiagnosticID.code());
		acid7_Context1.setValue("123456_acid7");
		acid7_Contexts.add(acid7_Context1);

		AcContext acid7_Context2 = new AcContext();
		acid7_Context2.setCode(HpscDictionary.HpscContextType.PortletName.code());
		acid7_Context2.setValue("AAE");
		acid7_Contexts.add(acid7_Context2);

		AcCommonDataWithPayLoad data7 = new AcCommonDataWithPayLoad();
		data7.setAcid(acid7);
		data7.setName("http://xxx");
		data7.setContexts(acid7_Contexts);

		// data8
		long acid8 = AcidHelper.getInstance().getAcid(3, 3, category, type, level);
		System.out.println("acid8: " + acid8);

		List<AcContext> acid8_Contexts = new ArrayList<AcContext>();
		AcContext acid8_Context1 = new AcContext();
		acid8_Context1.setCode(HpscDictionary.HpscContextType.DiagnosticID.code());
		acid8_Context1.setValue("123456_acid8");
		acid8_Contexts.add(acid8_Context1);

		AcContext acid8_Context2 = new AcContext();
		acid8_Context2.setCode(HpscDictionary.HpscContextType.PortletName.code());
		acid8_Context2.setValue("AAE");
		acid8_Contexts.add(acid8_Context2);

		AcCommonDataWithPayLoad data8 = new AcCommonDataWithPayLoad();
		data8.setAcid(acid8);
		data8.setName("http://yyy");
		data8.setContexts(acid8_Contexts);

		// data 9
		long acid9 = AcidHelper.getInstance().getAcid(4, 4, HpscDictionary.INSTANCE.category("PortalBizLog").code(), type, level);
		System.out.println("acid9: " + acid9);

		List<AcContext> acid9_Contexts = new ArrayList<AcContext>();
		AcContext acid9_Context1 = new AcContext();
		acid9_Context1.setCode(HpscDictionary.HpscContextType.DiagnosticID.code());
		acid9_Context1.setValue("123456_acid9");
		acid9_Contexts.add(acid9_Context1);

		AcContext acid9_Context2 = new AcContext();
		acid9_Context2.setCode(HpscDictionary.HpscContextType.PortletName.code());
		acid9_Context2.setValue("AAE");
		acid9_Contexts.add(acid9_Context2);

		AcContext acid9_Context3 = new AcContext();
		acid9_Context3.setCode(HpscDictionary.HpscContextType.PortletTransactionId.code());
		acid9_Context3.setValue("tran10087");
		acid9_Contexts.add(acid9_Context3);

		AcCommonDataWithPayLoad data9 = new AcCommonDataWithPayLoad();
		data9.setAcid(acid9);
		data9.setContexts(acid9_Contexts);

		// data 10
		long acid10 = AcidHelper.getInstance().getAcid(4, 4, HpscDictionary.INSTANCE.category("SPFPerformanceLog").code(), type, level);
		System.out.println("acid10: " + acid10);

		List<AcContext> acid10_Contexts = new ArrayList<AcContext>();
		AcContext acid10_Context1 = new AcContext();
		acid10_Context1.setCode(HpscDictionary.HpscContextType.DiagnosticID.code());
		acid10_Context1.setValue("123456_acid9");
		acid10_Contexts.add(acid10_Context1);

		AcContext acid10_Context2 = new AcContext();
		acid10_Context2.setCode(HpscDictionary.HpscContextType.PortletName.code());
		acid10_Context2.setValue("AAE");
		acid10_Contexts.add(acid10_Context2);

		AcCommonDataWithPayLoad data10 = new AcCommonDataWithPayLoad();
		data10.setAcid(acid10);
		data10.setName("http://zzz");
		data10.setContexts(acid10_Contexts);

		// data 11
		long acid11 = AcidHelper.getInstance().getAcid(4, 4, HpscDictionary.INSTANCE.category("SPFPerformanceLogDetail").code(), HpscDictionary.INSTANCE.category("SPFPerformanceLogDetail").type("WSRP_CALL").code(), level);
		System.out.println("acid10: " + acid10);

		List<AcContext> acid11_Contexts = new ArrayList<AcContext>();
		AcContext acid11_Context1 = new AcContext();
		acid11_Context1.setCode(HpscDictionary.HpscContextType.DiagnosticID.code());
		acid11_Context1.setValue("123456_acid9");
		acid11_Contexts.add(acid11_Context1);

		AcContext acid11_Context2 = new AcContext();
		acid11_Context2.setCode(HpscDictionary.HpscContextType.PortletName.code());
		acid11_Context2.setValue("AAE");
		acid11_Contexts.add(acid11_Context2);

		AcCommonDataWithPayLoad data11 = new AcCommonDataWithPayLoad();
		data11.setAcid(acid11);
		data11.setContexts(acid11_Contexts);

		// data12
		long acid12 = AcidHelper.getInstance().getAcid(5, 4, category, type, level);
		System.out.println("acid12: " + acid12);

		List<AcContext> acid12_Contexts = new ArrayList<AcContext>();
		AcContext acid12_Context1 = new AcContext();
		acid12_Context1.setCode(HpscDictionary.HpscContextType.DiagnosticID.code());
		acid12_Context1.setValue("9627");
		acid12_Contexts.add(acid12_Context1);

		AcContext acid12_Context2 = new AcContext();
		acid12_Context2.setCode(HpscDictionary.HpscContextType.PortletName.code());
		acid12_Context2.setValue("PSI");
		acid12_Contexts.add(acid12_Context2);

		AcCommonDataWithPayLoad data12 = new AcCommonDataWithPayLoad();
		data12.setAcid(acid12);
		data12.setName("http://xxx");
		data12.setContexts(acid12_Contexts);

		// data13
		long acid13 = AcidHelper.getInstance().getAcid(5, 4, HpscDictionary.INSTANCE.category("SPFPerformanceLogDetail").code(), HpscDictionary.INSTANCE.category("SPFPerformanceLogDetail").type("WSRP_CALL").code(), level);
		System.out.println("acid13: " + acid13);

		List<AcContext> acid13_Contexts = new ArrayList<AcContext>();
		AcContext acid13_Context1 = new AcContext();
		acid13_Context1.setCode(HpscDictionary.HpscContextType.DiagnosticID.code());
		acid13_Context1.setValue("9627");
		acid13_Contexts.add(acid13_Context1);

		AcContext acid13_Context2 = new AcContext();
		acid13_Context2.setCode(HpscDictionary.HpscContextType.PortletName.code());
		acid13_Context2.setValue("PSI");
		acid13_Contexts.add(acid13_Context2);

		AcCommonDataWithPayLoad data13 = new AcCommonDataWithPayLoad();
		data13.setAcid(acid13);
		data13.setContexts(acid13_Contexts);

		// data14
		long acid14 = AcidHelper.getInstance().getAcid(5, 4, HpscDictionary.INSTANCE.category("PortalBizLog").code(), HpscDictionary.INSTANCE.category("PortalBizLog").type("PSI").code(), level);
		System.out.println("acid14: " + acid14);

		List<AcContext> acid14_Contexts = new ArrayList<AcContext>();
		AcContext acid14_Context1 = new AcContext();
		acid14_Context1.setCode(HpscDictionary.HpscContextType.DiagnosticID.code());
		acid14_Context1.setValue("9627");
		acid14_Contexts.add(acid14_Context1);

		AcContext acid14_Context2 = new AcContext();
		acid14_Context2.setCode(HpscDictionary.HpscContextType.PortletName.code());
		acid14_Context2.setValue("PSI");
		acid14_Contexts.add(acid14_Context2);

		AcContext acid14_Context3 = new AcContext();
		acid14_Context3.setCode(HpscDictionary.HpscContextType.PortletTransactionId.code());
		acid14_Context3.setValue("tran10088");
		acid14_Contexts.add(acid14_Context3);

		AcCommonDataWithPayLoad data14 = new AcCommonDataWithPayLoad();
		data14.setAcid(acid14);
		data14.setContexts(acid14_Contexts);

		// go
		// TODO how to test this?
		// scs.onData(data, data2, data2_psi, data3, data4, data5, data6, data7, data8, data9, data10, data11, data12, data13, data14);

		// query from DB
		System.out.println("data node count: " + cs.getRelationCount());
		System.out.println("link node count: " + cs.getLinkCount());
		System.out.println("get data node: " + cs.getDataNode(acid_wsrp));
		System.out.println("get link node by type, value : " + cs.getLinkNode(2, "123456+AAE"));
		ChainContext cc = new ChainContext();
		cc.setCode(HpscDictionary.HpscContextType.PortletName.code());
		cc.setValue("123456+AAE");
		System.out.println("get link node by ac context: " + cs.getLinkNode(cc));
		ChainEntry<AcRelation, ChainContext> chain1 = cs.getFullChainByPrimaryTrack(cc);
		System.out.println("get 1 full chain by ac context: \n" + chain1.toString());
		ChainEntry<AcRelation, ChainContext> chain2 = cs.getFullChainByIdentifier(acid);
		System.out.println("get 2 full chain by acid:  \n" + chain2.toString());
		ChainEntry<AcRelation, ChainContext> chain3 = cs.getFullChainByIdentifier(acid_wsrp);
		System.out.println("get 3 full chain by acid:  \n" + chain3.toString());
		ChainEntry<AcRelation, ChainContext> chain4 = cs.getFullChainByIdentifier(acid_biz);
		System.out.println("get 4 full chain by acid:  \n" + chain4.toString());

		ChainEntry<AcRelation, ChainContext> chain5 = cs.getFullChainByIdentifier(acid7);
		System.out.println("get 5 full chain by acid:  \n" + chain5.toString());
		ChainEntry<AcRelation, ChainContext> chain6 = cs.getFullChainByIdentifier(acid8);
		System.out.println("get 6 full chain by acid:  \n" + chain6.toString());
		ChainEntry<AcRelation, ChainContext> chain7 = cs.getFullChainByIdentifier(acid9);
		System.out.println("get 7 full chain by acid:  \n" + chain7.toString());

		System.out.println("--------------------------------------------------------------------");
		ChainEntry<AcRelation, ChainContext> aggregatedChain = cs.getAggregatedChainByIdentifiers(acid, acid_wsrp,
				acid7, acid8, acid9, acid10, acid12);
		System.out.println("get aggregated chain:  \n" + aggregatedChain.toString());
	}
}
