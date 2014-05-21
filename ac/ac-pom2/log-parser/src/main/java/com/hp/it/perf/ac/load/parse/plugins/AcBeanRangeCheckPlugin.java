package com.hp.it.perf.ac.load.parse.plugins;

import com.hp.it.perf.ac.load.content.AcContentLine;
import com.hp.it.perf.ac.load.parse.AcStopParseException;
import com.hp.it.perf.ac.load.parse.AcTextParserContext;

public class AcBeanRangeCheckPlugin extends AcTextProcessPluginAdapter
		implements AcTextProcessPlugin {

	private Comparable<Object> rangeCheck;

	public AcBeanRangeCheckPlugin(Comparable<Object> rangeCheck) {
		this.rangeCheck = rangeCheck;
	}

	@Override
	public Object processResolve(Object bean, AcContentLine line,
			AcTextParserContext context) throws AcParsePluginException {
		int checkResult = rangeCheck.compareTo(bean);
		if (checkResult == 0) {
			// in range
			return bean;
		} else if (checkResult < 0) {
			// large than range
			throw new AcParsePluginException(new AcStopParseException(
					"stop parse because out of range"));
		} else {
			// less than range, and ignore this bean
			return null;
		}
	}

}
