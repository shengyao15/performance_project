package com.hp.it.perf.ac.rest.json;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.codehaus.jackson.map.ser.StdSerializerProvider;

import com.hp.it.perf.ac.common.realtime.MessageBean;
import com.hp.it.perf.ac.common.realtime.RealTimeBean;
import com.hp.it.perf.ac.rest.json.module.AcModule;
import com.hp.it.perf.ac.rest.json.serializer.MessageBeanSerializer;
import com.hp.it.perf.ac.rest.json.serializer.NullSerializer;
import com.hp.it.perf.ac.rest.json.serializer.RealTimeBeanSerializer;
import com.hp.it.perf.ac.rest.json.serializer.RealTimeFeatureErrorSerializer;
import com.hp.it.perf.ac.rest.model.RealTimeFeatureError;
import com.hp.it.perf.ac.rest.util.Constant;

public class JsonUtils {

	private static final ObjectMapper objectMapper = new ObjectMapper();
	
	static {
		enableAcConfiguration(objectMapper);
	}

	public static ObjectMapper getMapperInstance(boolean newInstance) {
		if (newInstance) {
			return new ObjectMapper();
		}
		return objectMapper;
	}

	public static void enableAcConfiguration(ObjectMapper objectMapper) {
		// objectMapper configuration
		SimpleDateFormat sdf = new SimpleDateFormat(
				Constant.PATTERN_DATE_TIME_TIMEZONE);
		sdf.setTimeZone(TimeZone.getTimeZone(Constant.TIMEZONE_UTC));
		objectMapper.setDateFormat(sdf);
		objectMapper.setSerializationInclusion(Inclusion.ALWAYS);
		StdSerializerProvider sp = new StdSerializerProvider();
		// customize null Serializer
		sp.setNullValueSerializer(new NullSerializer());
		objectMapper.setSerializerProvider(sp);
		objectMapper.disable(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS);
		// set customize module
		AcModule acModule = new AcModule();
		// customize realtime bean serializer
		acModule.addSerializer(RealTimeBean.class, new RealTimeBeanSerializer());
		acModule.addSerializer(MessageBean.class, new MessageBeanSerializer());
		acModule.addSerializer(RealTimeFeatureError.class, new RealTimeFeatureErrorSerializer());
		objectMapper.registerModule(acModule);
	}
}
