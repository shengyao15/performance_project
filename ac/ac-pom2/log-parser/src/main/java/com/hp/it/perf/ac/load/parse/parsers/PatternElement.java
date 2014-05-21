package com.hp.it.perf.ac.load.parse.parsers;

import com.hp.it.perf.ac.load.parse.AcParseException;
import com.hp.it.perf.ac.load.parse.AcParseInsufficientException;
import com.hp.it.perf.ac.load.parse.AcTextParseResult;
import com.hp.it.perf.ac.load.parse.AcTextParser;
import com.hp.it.perf.ac.load.parse.AcTextParserContext;
import com.hp.it.perf.ac.load.parse.AcTextToken;
import com.hp.it.perf.ac.load.parse.element.AcObjectElement;

class PatternElement implements TextElement {
	private AcTextParser parser;

	private String patternId;

	@Override
	public void initText(String text) {
		patternId = text;
	}

	public AcTextParseResult parse(AcTextToken text, AcTextParserContext contxt)
			throws AcParseException, AcParseInsufficientException {
		if (this.parser == null) {
			// ignore parser
			return new AcTextParseResult(new AcObjectElement(patternId,
					text.getContent()), text.getContent());
		}
		return this.parser.parse(text, contxt);
	}

	public boolean test(String text, AcTextParserContext contxt) {
		if (this.parser == null) {
			// ignore parser
			return true;
		}
		return this.parser.test(text, contxt);
	}

	@Override
	public String textValue() {
		return patternId;
	}

	public String toString() {
		return "{" + textValue() + "}";
	}

	public void setParser(AcTextParser parser) {
		this.parser = parser;
	}

}