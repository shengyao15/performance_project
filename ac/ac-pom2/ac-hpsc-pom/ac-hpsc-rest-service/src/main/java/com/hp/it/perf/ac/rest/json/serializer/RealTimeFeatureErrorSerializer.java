package com.hp.it.perf.ac.rest.json.serializer;

import java.io.IOException;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;

import com.hp.it.perf.ac.app.hpsc.HpscDictionary;
import com.hp.it.perf.ac.rest.model.RealTimeFeatureError;

public class RealTimeFeatureErrorSerializer extends
		JsonSerializer<RealTimeFeatureError> {

	@Override
	public void serialize(RealTimeFeatureError value, JsonGenerator jgen,
			SerializerProvider provider) throws IOException,
			JsonProcessingException {
		jgen.writeStartObject();
		jgen.writeNumberField("granularity", value.getGranularity());
		jgen.writeStringField(
				"category",
				value.getCategory() != 0 ? HpscDictionary.INSTANCE.category(
						value.getCategory()).name() : "");
		jgen.writeStringField(
				"featureType",
				(value.getCategory() != 0 && value.getFeatureType() != 0) ? HpscDictionary.INSTANCE
						.category(value.getCategory())
						.type(value.getFeatureType()).name()
						: "");
		jgen.writeStringField("errorType", value.getErrorType());
		jgen.writeNumberField("count", value.getCount());
		jgen.writeEndObject();
	}
}
