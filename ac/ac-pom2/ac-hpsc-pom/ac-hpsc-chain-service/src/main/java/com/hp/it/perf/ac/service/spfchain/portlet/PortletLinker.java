package com.hp.it.perf.ac.service.spfchain.portlet;

import org.springframework.stereotype.Service;

import com.hp.it.perf.ac.app.hpsc.HpscDictionary;
import com.hp.it.perf.ac.common.model.AcCommonData;
import com.hp.it.perf.ac.common.model.AcContext;
import com.hp.it.perf.ac.service.chain.ChainContext;
import com.hp.it.perf.ac.service.spfchain.AcLinkContext;
import com.hp.it.perf.ac.service.spfchain.AcLinker;
import com.hp.it.perf.ac.service.spfchain.AcProcessException;
import com.hp.it.perf.ac.service.spfchain.PerfLinkTarget;

@Service
@PerfLinkTarget({ "PortalBizLog", "PortalPerfLog", "PortalErrorLog",
		"PortalErrorTraceLog" })
public class PortletLinker implements AcLinker {

	@Override
	public void performLink(AcCommonData data, AcLinkContext context)
			throws AcProcessException {

		// portlet transaction id
		AcContext transcationContext = data
				.getAcContext(HpscDictionary.INSTANCE
						.contextType(HpscDictionary.HpscContextType.PortletTransactionId
								.code()));

		if (transcationContext == null) {
			throw new AcProcessException("no transaction id found");
		}

		ChainContext transcationChainContext = new ChainContext();
		transcationChainContext.setCode(transcationContext.getCode());
		transcationChainContext.setValue(transcationContext.getValue());

		// create nodes operation
		context.createNodeAndRelation(data.getAcid(), transcationChainContext);

		// process biz log
		if ("PortalBizLog".equals(data.getCategory(HpscDictionary.INSTANCE)
				.name())) {
			// spf diagnostic id
			AcContext spfPerfContext = data
					.getAcContext(HpscDictionary.INSTANCE
							.contextType(HpscDictionary.HpscContextType.DiagnosticID
									.code()));
			if (spfPerfContext == null) {
				throw new AcProcessException("no spf dc id found");
			}

			// wsrp context
			AcContext spfWsrpContext = data
					.getAcContext(HpscDictionary.INSTANCE
							.contextType(HpscDictionary.HpscContextType.PortletName
									.code()));

			if (spfWsrpContext == null) {
				throw new AcProcessException("no wsrp context found");
			}

			ChainContext wsrpChainContext = new ChainContext();
			wsrpChainContext.setCode(spfWsrpContext.getCode());
			wsrpChainContext.setValue(spfPerfContext.getValue() + "+"
					+ spfWsrpContext.getValue());
			// spfWsrpContext.setValue(spfPerfContext.getValue() +
			// spfWsrpContext.getValue());

			context.linkParentChild(wsrpChainContext, transcationChainContext);
		}

	}
}
