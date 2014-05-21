package com.hp.it.perf.ac.service.spfchain.portal;

import org.springframework.stereotype.Service;

import com.hp.it.perf.ac.app.hpsc.HpscDictionary;
import com.hp.it.perf.ac.common.model.AcCategory;
import com.hp.it.perf.ac.common.model.AcCommonData;
import com.hp.it.perf.ac.common.model.AcContext;
import com.hp.it.perf.ac.common.model.AcType;
import com.hp.it.perf.ac.service.chain.ChainContext;
import com.hp.it.perf.ac.service.spfchain.AcLinkContext;
import com.hp.it.perf.ac.service.spfchain.AcLinker;
import com.hp.it.perf.ac.service.spfchain.AcProcessException;
import com.hp.it.perf.ac.service.spfchain.PerfLinkTarget;

@Service
@PerfLinkTarget({ "SPFPerformanceLog", "SPFPerformanceLogDetail" })
public class SPFLinker implements AcLinker {

	private static final AcCategory SPF_PerfLog_Detail = HpscDictionary.INSTANCE
			.category("SPFPerformanceLogDetail");
	private static final AcType SPF_PerfLog_WSRP = SPF_PerfLog_Detail
			.type("WSRP_CALL");
	private static final AcCategory SPF_PerfLog = HpscDictionary.INSTANCE
			.category("SPFPerformanceLog");
	private static final AcType SPF_PerfLog_Request = SPF_PerfLog
			.type("REQUEST");

	@Override
	public void performLink(AcCommonData data, AcLinkContext context)
			throws AcProcessException {

		// SPF performance Log
		if (data.getCategory(HpscDictionary.INSTANCE) == SPF_PerfLog) {

			// REQUEST
			AcType type = data.getType(HpscDictionary.INSTANCE);
			AcContext spfPerfContext = data
					.getAcContext(HpscDictionary.INSTANCE
							.contextType(HpscDictionary.HpscContextType.DiagnosticID
									.code()));
			if (spfPerfContext == null) {
				throw new AcProcessException("no spf dc id found");
			}

			// Perf chain context
			ChainContext perfChainContext = new ChainContext();
			// set code: 1: DiagnosticID
			perfChainContext.setCode(spfPerfContext.getCode());
			// set value:
			perfChainContext.setValue(spfPerfContext.getValue());

			if (type == SPF_PerfLog_Request) {
				// REQUEST
				context.createNodeAndRelation(data.getAcid(), perfChainContext);
			} else {
				throw new IllegalArgumentException("unknown type: " + type);
			}

		} else if (data.getCategory(HpscDictionary.INSTANCE) == SPF_PerfLog_Detail) {

			// WSRP, PROFILE, ...
			AcType type = data.getType(HpscDictionary.INSTANCE);
			AcContext spfPerfContext = data
					.getAcContext(HpscDictionary.INSTANCE
							.contextType(HpscDictionary.HpscContextType.DiagnosticID
									.code()));
			if (spfPerfContext == null) {
				throw new AcProcessException("no spf dc id found");
			}

			// Perf chain context
			ChainContext perfChainContext = new ChainContext();
			// set code: 1: DiagnosticID
			perfChainContext.setCode(spfPerfContext.getCode());
			// set value:
			perfChainContext.setValue(spfPerfContext.getValue());

			if (type == SPF_PerfLog_WSRP) {
				AcContext spfWsrpContext = data
						.getAcContext(HpscDictionary.INSTANCE
								.contextType(HpscDictionary.HpscContextType.PortletName
										.code()));
				if (spfWsrpContext == null) {
					throw new AcProcessException("no wsrp context found");
				}

				ChainContext wsrpChainContext = new ChainContext();
				// set code: 2: PortletName
				wsrpChainContext.setCode(spfWsrpContext.getCode());
				// set value: DiagnosticID + PortletName
				wsrpChainContext.setValue(spfPerfContext.getValue() + "+"
						+ spfWsrpContext.getValue());
				// spfWsrpContext.setValue(spfPerfContext.getValue() +
				// spfWsrpContext.getValue());
				context.createNodeAndRelation(data.getAcid(), wsrpChainContext);
				context.linkParentChild(perfChainContext, wsrpChainContext);
			}
		}
	}
}
