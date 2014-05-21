package com.hp.it.perf.ac.app.hpsc.storm.spout;

import java.util.List;

import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;

import com.hp.it.perf.ac.common.model.AcCommonData;
import com.hp.it.perf.ac.common.model.AcContext;

public class AcCommonDataTupleProducer implements TupleProducer<AcCommonData> {

	private static final long serialVersionUID = -6255265396290314348L;

	@Override
	public Values toEmitValues(AcCommonData acData) {
		String errorMessage = null;
		int category = acData.getCategory();
		int level = acData.getLevel();
		boolean hasNoWSRP = false;
		
		if(category == 1) {
			// track the request that whether it has WSRP call
			List<AcContext> contexts = acData.getContexts();
			if(contexts != null && contexts.size() > 0) {
				for (AcContext context : contexts) {
					if (context.getCode() == 8) {
						hasNoWSRP = true;
						break;
					}
				}
			}
		} else if(category == 6) {
			// get error message if WSRP call has error
			List<AcContext> contexts = acData.getContexts();
			if(contexts != null && contexts.size() > 0) {
				for (AcContext context : contexts) {
					if (context.getCode() == 7) {
						errorMessage = context.getValue();
						break;
					}
				}
			} 
			if(level == 3 && errorMessage == null) {
				// put error message as "unknown" if level of the WSRP call is 3
				// refer to ac-hpsc-core/src/main/resources/hpsc_category.txt
				errorMessage = "unknown";
			}
		}
		return new Values(category, acData.getType(), level,
				acData.getCreated(), acData.getDuration(), hasNoWSRP,
				errorMessage);
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("category", "type", "level", "created",
				"duration", "hasNoWSRP", "errorMessage"));
		
	}

}
