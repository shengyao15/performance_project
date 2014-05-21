package com.hp.it.perf.ac.rest.json.mixin;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties("contexts")
@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE, isGetterVisibility = Visibility.NONE)
public interface BasicAcCommonDataJsonMixIn {
	// empty
}
