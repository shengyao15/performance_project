package com.hp.it.perf.ac.app.hpsc.transform;

import java.util.HashMap;
import java.util.Map;

import com.hp.it.perf.ac.app.hpsc.HpscDictionary;
import com.hp.it.perf.ac.app.hpsc.beans.PortletBusinessLog;
import com.hp.it.perf.ac.app.hpsc.beans.PortletErrorLog;
import com.hp.it.perf.ac.app.hpsc.beans.PortletErrortraceLog;
import com.hp.it.perf.ac.app.hpsc.beans.PortletPerformanceLog;
import com.hp.it.perf.ac.common.model.AcCategory;
import com.hp.it.perf.ac.common.model.AcCommonDataWithPayLoad;
import com.hp.it.perf.ac.common.model.AcType;
import com.hp.it.perf.ac.common.model.AcidHelper;
import com.hp.it.perf.ac.service.transform.AcTransformContext;
import com.hp.it.perf.ac.service.transform.AcTransformException;
import com.hp.it.perf.ac.service.transform.AcTransformer;

public class PortletLogBeanTransformer implements AcTransformer {

	private LogBeanTransformerAgent bizAgent = new LogBeanTransformerAgent(
			PortletBusinessLog.class, HpscDictionary.INSTANCE);
	private LogBeanTransformerAgent perfAgent = new LogBeanTransformerAgent(
			PortletPerformanceLog.class, HpscDictionary.INSTANCE);
	private LogBeanTransformerAgent errorAgent = new LogBeanTransformerAgent(
			PortletErrorLog.class, HpscDictionary.INSTANCE);
	private LogBeanTransformerAgent errorTraceAgent = new LogBeanTransformerAgent(
			PortletErrortraceLog.class, HpscDictionary.INSTANCE);

	{
		// no detail
		// perfAgent.registerDbBean(PortletPerformanceLog.Detail.class,
		// HpscDictionary.INSTANCE);
	}

	@Override
	public void transform(Object source, AcTransformContext collector)
			throws AcTransformException {
		if (source == null) {
			throw new AcTransformException("null source");
		}
		Class<?> clz = source.getClass();
		AcCommonDataWithPayLoad[] commonDataList;
		LogBeanTransformerAgent agent;
		if (clz == PortletBusinessLog.class) {
			agent = bizAgent;
		} else if (clz == PortletPerformanceLog.class) {
			agent = perfAgent;
		} else if (clz == PortletErrorLog.class) {
			agent = errorAgent;
		} else if (clz == PortletErrortraceLog.class) {
			agent = errorTraceAgent;
		} else {
			throw new AcTransformException("invalid source " + clz);
		}
		commonDataList = agent.transformTo(source, collector);
		// set type
		String transformName = collector.getTransformName();
		String typeName = transformName.substring(transformName
				.lastIndexOf('.') + 1);
		if (typeName.startsWith("sp4ts")) {
			typeName = typeName.substring("sp4ts".length());
		}
		Map<Long, Long> acidUpdateMapping = new HashMap<Long, Long>();
		for (AcCommonDataWithPayLoad commonData : commonDataList) {
			if (commonData.getType() == 0) {
				AcCategory acCategory = commonData
						.getCategory(HpscDictionary.INSTANCE);
				AcType acType;
				if (!acCategory.contains(typeName.toUpperCase())) {
					acType = acCategory.type("UNKNOWN");
				} else {
					acType = acCategory.type(typeName.toUpperCase());
				}
				long oldAcid = commonData.getAcid();
				commonData.setAcid(AcidHelper.getInstance().setType(
						commonData.getAcid(), acType.code()));
				long newAcid = commonData.getAcid();
				// save acid mapping
				if (oldAcid != newAcid) {
					acidUpdateMapping.put(oldAcid, newAcid);
				}
				// update refacid if has mapped
				if (commonData.getPayLoad() != null) {
					agent.syncPayloadAcid(commonData.getPayLoad(), commonData);
				}
			}

			long refAcid = commonData.getRefAcid();
			if (refAcid != 0) {
				Long updatedacid = acidUpdateMapping.get(refAcid);
				if (updatedacid != null) {
					commonData.setRefAcid(updatedacid);
				}
			}
		}
		collector.collect(commonDataList);
	}

	@Override
	public String getDefaultName() {
		return "log.spf.portlet";
	}

}
