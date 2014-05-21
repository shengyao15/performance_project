package com.hp.it.perf.ac.app.hpsc.transform;

import java.util.ArrayList;
import java.util.List;

import com.hp.it.perf.ac.app.hpsc.HpscDictionary;
import com.hp.it.perf.ac.app.hpsc.beans.SPFPerformanceDBLog;
import com.hp.it.perf.ac.app.hpsc.beans.SPFPerformanceLog;
import com.hp.it.perf.ac.app.hpsc.beans.SPFPerformanceLog.Detail;
import com.hp.it.perf.ac.common.model.AcCommonDataWithPayLoad;
import com.hp.it.perf.ac.service.transform.AcTransformContext;
import com.hp.it.perf.ac.service.transform.AcTransformException;
import com.hp.it.perf.ac.service.transform.AcTransformer;

public class SPFPerfLogBeanTransformer implements AcTransformer {

	private LogBeanTransformerAgent transformAgent = null;

	public SPFPerfLogBeanTransformer() {
		transformAgent = new LogBeanTransformerAgent(SPFPerformanceDBLog.class,
				HpscDictionary.INSTANCE);
		transformAgent.registerDbBean(SPFPerformanceDBLog.Detail.class,
				HpscDictionary.INSTANCE);
	}

	@Override
	public void transform(Object source, AcTransformContext collector)
			throws AcTransformException {
		SPFPerformanceLog logBean = (SPFPerformanceLog) source;
		List<SPFPerformanceLog.Detail> otherDetails = new ArrayList<SPFPerformanceLog.Detail>(
				logBean.getDetailList().length - 1);
		Detail reqDetail = null;
		for (SPFPerformanceLog.Detail logDetail : logBean.getDetailList()) {
			if (logDetail.getType() == SPFPerformanceLog.Detail.Type.REQUEST) {
				reqDetail = logDetail;
				continue;
			}
			otherDetails.add(logDetail);
		}

		SPFPerformanceDBLog reqDbBean = new SPFPerformanceDBLog();
		reqDbBean.setDateTime(logBean.getDateTime());
		reqDbBean.setDuration(reqDetail.getDuration());
		reqDbBean.setHpscDiagnosticId(logBean.getHpscDiagnosticId());
		reqDbBean.setName(reqDetail.getName());
		reqDbBean.setStatus(reqDetail.getStatus());
		reqDbBean.setStatusDetail(reqDetail.getStatusDetail());
		reqDbBean.setThreadName(logBean.getThreadName());
		reqDbBean.setLocation(logBean.getLocation());

		AcCommonDataWithPayLoad[] results = new AcCommonDataWithPayLoad[otherDetails
				.size() + 1];
		int index = 0;
		// prepare detail bean (payload and common data bean)
		SPFPerformanceDBLog.Detail[] dbDetails = new SPFPerformanceDBLog.Detail[otherDetails
				.size()];
		for (int j = 0, n = otherDetails.size(); j < n; j++) {
			SPFPerformanceLog.Detail logDetail = otherDetails.get(j);
			SPFPerformanceDBLog.Detail dbDetail = new SPFPerformanceDBLog.Detail();
			dbDetails[j] = dbDetail;
			dbDetail.setDuration(logDetail.getDuration());
			dbDetail.setName(SPFPerformanceLog.Detail.Type.WSRP_CALL == logDetail
					.getType() ? logDetail.getName() : logDetail.getType()
					.name());
			dbDetail.setStatus(logDetail.getStatus());
			dbDetail.setStatusDetail(logDetail.getStatusDetail());
			dbDetail.setType(logDetail.getType().name());
			dbDetail.setHpscDiagnosticId(logBean.getHpscDiagnosticId());
			AcCommonDataWithPayLoad detailPayload = transformAgent.transformTo(
					dbDetail, collector)[0];
			results[index++] = detailPayload;
		}
		reqDbBean.setDetails(dbDetails);
		
		// prepare master bean (payload and common data bean)
		AcCommonDataWithPayLoad mainPayload = transformAgent.transformTo(
				reqDbBean, collector)[0];

		for (AcCommonDataWithPayLoad detailPayload : results) {
			if (detailPayload != null) {
				// set detail created as same as main
				detailPayload.setCreated(mainPayload.getCreated());
				// set refacid
				detailPayload.setRefAcid(mainPayload.getAcid());
			}
		}
		results[index++] = mainPayload;

		collector.collect(results);
	}

	@Override
	public String getDefaultName() {
		return "log.spf.portal.spfperformance";
	}

}
