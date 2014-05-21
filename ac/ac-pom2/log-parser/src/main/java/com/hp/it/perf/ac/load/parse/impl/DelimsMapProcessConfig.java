package com.hp.it.perf.ac.load.parse.impl;

import com.hp.it.perf.ac.load.bind.AcMapBinder;
import com.hp.it.perf.ac.load.parse.AcParseSyntaxException;
import com.hp.it.perf.ac.load.parse.AcParserSetting;
import com.hp.it.perf.ac.load.parse.AcParserSetting.ParserParameter;
import com.hp.it.perf.ac.load.parse.AcProcessorConfig;
import com.hp.it.perf.ac.load.parse.AcTextPipelineParseBuilder;
import com.hp.it.perf.ac.load.parse.AcTextParser;
import com.hp.it.perf.ac.load.parse.AcTextParserConstant;
import com.hp.it.perf.ac.load.parse.AcTextStreamProcessor;
import com.hp.it.perf.ac.load.parse.parsers.DelimsTextParser;
import com.hp.it.perf.ac.load.parse.parsers.EmptyTextParser;

public class DelimsMapProcessConfig extends AcProcessorConfig {

	public DelimsMapProcessConfig(String delimiterDef) {
		DelimsTextParser parser = new DelimsTextParser();
		AcTextParserConfigBuilder configBuilder = new AcTextParserConfigBuilder() {

			@Override
			protected AcTextParser getParser(String id)
					throws AcParseSyntaxException {
				return new EmptyTextParser();
			}

		};
		AcParserSetting setting = new AcParserSetting();
		setting.setParserName("");
		setting.setParserClass(DelimsTextParser.class);
		ParserParameter parameter = new ParserParameter();
		parameter.setName(AcTextParserConstant.KEY_PATTERN);
		parameter.setValue(delimiterDef);
		setting.setParameters(new ParserParameter[] { parameter });
		configBuilder.addParser(setting);
		parser.init(configBuilder.createConfig(""));
		setParser(parser);
		setBinder(new AcMapBinder());
	}

	public static AcTextStreamProcessor createProcessor(String delimiterDef) {
		return new AcTextProcessor(new DelimsMapProcessConfig(delimiterDef));
	}

	public static AcTextPipelineParseBuilder createPipelineBuilder(
			String delimiterDef) {
		return new AcTextPipelineParseBuilderImpl(new DelimsMapProcessConfig(
				delimiterDef));
	}

}
