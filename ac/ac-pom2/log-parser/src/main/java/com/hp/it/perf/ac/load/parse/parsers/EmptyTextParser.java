package com.hp.it.perf.ac.load.parse.parsers;

import com.hp.it.perf.ac.load.parse.AcParseException;
import com.hp.it.perf.ac.load.parse.AcTextParseResult;
import com.hp.it.perf.ac.load.parse.AcTextParser;
import com.hp.it.perf.ac.load.parse.AcTextParserConfig;
import com.hp.it.perf.ac.load.parse.AcTextParserConstant;
import com.hp.it.perf.ac.load.parse.AcTextParserContext;
import com.hp.it.perf.ac.load.parse.AcTextToken;
import com.hp.it.perf.ac.load.parse.element.AcObjectElement;

public class EmptyTextParser extends AbstractAcTextParser implements
		AcTextParser {

	private boolean exactMatch = true;

	@Override
	public void init(AcTextParserConfig config) {
		super.init(config);
		if (hasDefaultInitParameter(config, AcTextParserConstant.KEY_FORMAT)) {
			exactMatch = !AcTextParserConstant.MATCH_ANY
					.equals(getDefaultInitParameter(config,
							AcTextParserConstant.KEY_FORMAT));
		}
	}

	@Override
	public AcTextParseResult parse(AcTextToken text, AcTextParserContext conext)
			throws AcParseException {
		String content = text.getContent();
		return new AcTextParseResult(new AcObjectElement(name, content),
				content, exactMatch);
	}

	@Override
	public boolean test(String textFragement, AcTextParserContext context) {
		return true;
	}

}
