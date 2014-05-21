package com.hp.it.perf.ac.rest.json.serializer;

import java.io.IOException;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;

import com.hp.it.perf.ac.app.hpsc.HpscDictionary;
import com.hp.it.perf.ac.common.realtime.MessageBean;
import com.hp.it.perf.ac.rest.util.Utils;

public class MessageBeanSerializer extends JsonSerializer<MessageBean> {

	@Override
	public void serialize(MessageBean value, JsonGenerator jgen,
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
				(value.getCategory() != 0 && value.getFeatureType() != 0) ? HpscDictionary.INSTANCE.category(value.getCategory()).type(value.getFeatureType()).name() : "");
		jgen.writeStringField("startTime",
				Utils.long2String(value.getStartTime()));
		String[] msg = value.getMessage().split(":", 3);
		jgen.writeStringField("message",
				(msg != null && msg.length > 1) ? msg[1] : value.getMessage());
		jgen.writeNumberField("count", value.getCount());
		jgen.writeEndObject();
	}

}
