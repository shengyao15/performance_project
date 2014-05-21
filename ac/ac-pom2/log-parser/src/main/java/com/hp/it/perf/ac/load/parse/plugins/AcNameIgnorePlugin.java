package com.hp.it.perf.ac.load.parse.plugins;

import com.hp.it.perf.ac.load.content.AcContentMetadata;
import com.hp.it.perf.ac.load.content.AcContent;
import com.hp.it.perf.ac.load.parse.AcStopParseException;
import com.hp.it.perf.ac.load.parse.AcTextParserContext;

public class AcNameIgnorePlugin extends AcTextProcessPluginAdapter {

	private String[] filterStrings;

	public AcNameIgnorePlugin(String... filterStrings) {
		this.filterStrings = filterStrings;
	}

	@Override
	public <T extends AcContent<?>> T processStart(T content,
			AcTextParserContext context) throws AcParsePluginException {
		AcContentMetadata metadata = content.getMetadata();
		String name = metadata.getBasename();
		for (String s : filterStrings) {
			if (name != null && name.contains(s)) {
				throw new AcParsePluginException(new AcStopParseException(
						"stop parsing content " + s));
			}
		}
		return content;
	}

}
