package com.hp.it.perf.ac.load.parse;

public class AcParserSetting {

	public static class ParserParameter {
		private String name;
		private String value;
		private Class<?> classValue;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

		public Class<?> getClassValue() {
			return classValue;
		}

		public void setClassValue(Class<?> classValue) {
			this.classValue = classValue;
		}

	}

	private String parserName;

	private Class<? extends AcTextParser> parserClass;

	private ParserParameter[] parameters;

	public String getParserName() {
		return parserName;
	}

	public void setParserName(String parserName) {
		this.parserName = parserName;
	}

	public Class<? extends AcTextParser> getParserClass() {
		return parserClass;
	}

	public void setParserClass(Class<? extends AcTextParser> parserClass) {
		this.parserClass = parserClass;
	}

	public ParserParameter[] getParameters() {
		return parameters;
	}

	public void setParameters(ParserParameter[] parameters) {
		this.parameters = parameters;
	}

}
