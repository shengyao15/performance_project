package com.hp.it.perf.ac.rest.json.mixin;

import org.codehaus.jackson.annotate.JsonValue;

public interface BasicConsumerReportJsonMixIn {

	@JsonValue
	abstract Object[] toValue();
}
