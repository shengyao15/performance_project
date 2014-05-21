package com.hp.it.perf.ac.rest.json.serializer;

import java.io.IOException;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;

import com.hp.it.perf.ac.app.hpsc.HpscDictionary;
import com.hp.it.perf.ac.common.realtime.RealTimeBean;
import com.hp.it.perf.ac.rest.util.Utils;

public class RealTimeBeanSerializer extends JsonSerializer<RealTimeBean> {

	@Override
	public void serialize(RealTimeBean value, JsonGenerator jgen,
			SerializerProvider provider) throws IOException,
			JsonProcessingException {
		jgen.writeStartObject();
		jgen.writeStringField("id", value.getId() == null ? "" : value.getId());
		jgen.writeNumberField("granularity", value.getGranularity());
		jgen.writeStringField(
				"category",
				value.getCategory() != 0 ? HpscDictionary.INSTANCE.category(value.getCategory()).name() : "");
		jgen.writeStringField(
				"featureType",
				(value.getCategory() != 0 && value.getFeatureType() != 0) ? HpscDictionary.INSTANCE.category(value.getCategory()).type(value.getFeatureType()).name(): "");
		jgen.writeNumberField("valueType", value.getValueType());
		jgen.writeNumberField("value", (int) value.getValue());
		jgen.writeStringField("startTime",
				Utils.long2String(value.getStartTime()));
		jgen.writeEndObject();
	}
}
