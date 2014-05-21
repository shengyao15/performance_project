package com.hp.it.perf.ac.rest.json.module;

import org.codehaus.jackson.Version;
import org.codehaus.jackson.map.module.SimpleModule;

import com.hp.it.perf.ac.app.hpsc.search.bean.ConsumerDetailReport;
import com.hp.it.perf.ac.app.hpsc.search.bean.ConsumerRequestReport;
import com.hp.it.perf.ac.app.hpsc.search.bean.ProducerReport;
import com.hp.it.perf.ac.common.model.AcCommonData;
import com.hp.it.perf.ac.rest.json.mixin.BasicAcCommonDataJsonMixIn;
import com.hp.it.perf.ac.rest.json.mixin.BasicConsumerReportJsonMixIn;
import com.hp.it.perf.ac.rest.json.mixin.BasicProducerReportJsonMixIn;
import com.hp.it.perf.ac.rest.json.mixin.BasicWsrpReportJsonMixIn;

public class AcModule extends SimpleModule {

	public AcModule() {
		super("AC-Module", new Version(0, 0, 1, null));
	}

	@Override
	public void setupModule(SetupContext context) {
		context.setMixInAnnotations(ConsumerRequestReport.class, BasicConsumerReportJsonMixIn.class);
		context.setMixInAnnotations(ConsumerDetailReport.class, BasicProducerReportJsonMixIn.class);
		context.setMixInAnnotations(ProducerReport.class, BasicWsrpReportJsonMixIn.class);
		context.setMixInAnnotations(AcCommonData.class, BasicAcCommonDataJsonMixIn.class);
		super.setupModule(context);
	}
}
