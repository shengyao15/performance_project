package com.hp.it.perf.ac.app.hpsc;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import com.hp.it.perf.ac.common.model.AcCategory;
import com.hp.it.perf.ac.common.model.AcContextType;
import com.hp.it.perf.ac.common.model.AcDictionary;
import com.hp.it.perf.ac.common.model.AcLevel;
import com.hp.it.perf.ac.common.model.AcType;
import com.hp.it.perf.ac.load.content.AcContentErrorHandler;
import com.hp.it.perf.ac.load.content.AcContentLine;
import com.hp.it.perf.ac.load.content.AcInputStreamContent;
import com.hp.it.perf.ac.load.content.AcLoadException;
import com.hp.it.perf.ac.load.parse.AcTextStreamProcessor;
import com.hp.it.perf.ac.load.parse.ParserPattern;
import com.hp.it.perf.ac.load.parse.ParserPattern.Parameter;
import com.hp.it.perf.ac.load.parse.impl.TextPatternScanner;
import com.hp.it.perf.ac.load.parse.parsers.DelimsListTextParser;
import com.hp.it.perf.ac.load.parse.parsers.DelimsTextParser;

public final class HpscDictionary implements Serializable, AcDictionary {

	private static final long serialVersionUID = 1L;

	static {
		loadDictionary();
	}

	@ParserPattern(value = "CATEGORY", parameters = { @Parameter("Category: {NAME}({CODE}), Types: [{TYPE_LIST}], Levels: [{LEVEL_LIST}], PayloadClass: {PAYLOAD_CLASS}") }, parser = DelimsTextParser.class)
	static class HpscCategory implements AcCategory {

		@Override
		public String toString() {
			return String
					.format("HpscCategory[%s(%s), types=%s, levels=%s]", name,
							code, Arrays.toString(types),
							Arrays.toString(levels));
		}

		@ParserPattern("CODE")
		private int code;

		@ParserPattern("NAME")
		private String name;

		@ParserPattern("PAYLOAD_CLASS")
		private String payloadClass;

		private HpscType[] types;
		private HpscType[] typeIndexs;

		@ParserPattern(value = "TYPE_LIST", parameters = {
				@Parameter(name = DelimsListTextParser.DELIMS, value = ", "),
				@Parameter(name = DelimsListTextParser.KEY_PATTERN, value = "TYPE") }, parser = DelimsListTextParser.class)
		void setTypes(HpscType[] types) {
			this.types = types;
			int maxValue = 0;
			for (HpscType type : types) {
				maxValue = Math.max(maxValue, type.code());
			}
			typeIndexs = new HpscType[maxValue + 1];
			for (HpscType type : types) {
				if (typeIndexs[type.code()] != null) {
					throw new IllegalArgumentException("duplicated Hpsc Type: "
							+ type);
				}
				typeIndexs[type.code()] = type;
			}
		}

		private HpscLevel[] levels;
		private HpscLevel[] levelIndexs;

		@ParserPattern(value = "LEVEL_LIST", parameters = {
				@Parameter(name = DelimsListTextParser.DELIMS, value = ", "),
				@Parameter(name = DelimsListTextParser.KEY_PATTERN, value = "LEVEL") }, parser = DelimsListTextParser.class)
		void setLevels(HpscLevel[] levels) {
			this.levels = levels;
			int maxValue = 0;
			for (HpscLevel level : levels) {
				maxValue = Math.max(maxValue, level.code());
			}
			levelIndexs = new HpscLevel[maxValue + 1];
			for (HpscLevel level : levels) {
				if (levelIndexs[level.code()] != null) {
					throw new IllegalArgumentException(
							"duplicated HpscLevel Type: " + level);
				}
				levelIndexs[level.code()] = level;
			}
		}

		@Override
		public int code() {
			return code;
		}

		@Override
		public String name() {
			return name;
		}

		@Override
		public AcType[] types() {
			return types;
		}

		@Override
		public AcType type(int typeCode) {
			AcType type = typeIndexs[typeCode];
			if (type == null) {
				throw new IllegalArgumentException("invalid type code: "
						+ typeCode);
			}
			return type;
		}

		@Override
		public boolean contains(AcType type) {
			return typeIndexs[type.code()] == type;
		}

		@Override
		public AcLevel[] levels() {
			return levels;
		}

		@Override
		public AcLevel level(int levelCode) {
			AcLevel level = levelIndexs[levelCode];
			if (level == null) {
				throw new IllegalArgumentException("invalid level code: "
						+ levelCode);
			}
			return level;
		}

		@Override
		public boolean contains(AcLevel level) {
			return levelIndexs[level.code()] == level;
		}

		@Override
		public AcType type(String typeName) {
			for (AcType type : types) {
				if (type.name().equals(typeName)) {
					return type;
				}
			}
			throw new IllegalArgumentException("invalid type name: " + typeName);
		}

		@Override
		public AcLevel level(String levelName) {
			for (AcLevel level : levels) {
				if (level.name().equals(levelName)) {
					return level;
				}
			}
			throw new IllegalArgumentException("invalid level name: "
					+ levelName);
		}

		@Override
		public String getPayloadClassName() {
			return payloadClass;
		}

		@Override
		public boolean contains(String typeName) {
			for (AcType type : types) {
				if (type.name().equals(typeName)) {
					return true;
				}
			}
			return false;
		}

		@Override
		public boolean contains(int typeCode) {
			AcType type = typeIndexs[typeCode];
			return type != null;
		}

	}

	@ParserPattern(value = "TYPE", parameters = { @Parameter("{NAME}({CODE})") }, parser = DelimsTextParser.class)
	static class HpscType implements AcType {

		@ParserPattern("CODE")
		private int code;

		@ParserPattern("NAME")
		private String name;

		@Override
		public int code() {
			return code;
		}

		@Override
		public String toString() {
			return String.format("HpscType[%s(%s)]", name, code);
		}

		@Override
		public String name() {
			return name;
		}

	}

	@ParserPattern(value = "LEVEL", parameters = { @Parameter("{NAME}({CODE})") }, parser = DelimsTextParser.class)
	static class HpscLevel implements AcLevel {

		@ParserPattern("CODE")
		private int code;

		@ParserPattern("NAME")
		private String name;

		@Override
		public int code() {
			return code;
		}

		@Override
		public String name() {
			return name;
		}

		@Override
		public String toString() {
			return String.format("HpscLevel[%s(%s)]", name, code);
		}

	}

	public static enum HpscContextType implements AcContextType {
		DiagnosticID(1), PortletName(2), PortletDiagnostic(3), PortletTransactionId(
				4), Location(5), UserLogin(6), ErrorDetail(7), NoWSRP(8);

		private int code;

		private HpscContextType(int code) {
			this.code = code;
		}

		@Override
		public int code() {
			return code;
		}

	}

	private HpscCategory[] categorys;
	private HpscCategory[] categoryIndexs;

	private HpscContextType[] contextTypes;
	private HpscContextType[] contextTypeIndexs;

	public final static AcDictionary INSTANCE = loadDictionary();
	
	private static final SerializeHolder SerializeHolderInstance = new SerializeHolder();
	
	private static class SerializeHolder implements Serializable {
		private static final long serialVersionUID = HpscDictionary.serialVersionUID;
		
		private Object readResolve() {
			return HpscDictionary.INSTANCE;
		}
		
	}

	private HpscDictionary() {
	}

	@Override
	public AcCategory category(int code) {
		AcCategory category = categoryIndexs[code];
		if (category == null) {
			throw new IllegalArgumentException("invalid category code: " + code);
		}
		return category;
	}

	private static AcDictionary loadDictionary() {
		HpscDictionary dictionary = new HpscDictionary();
		ClassLoader loader = dictionary.getClass().getClassLoader();
		AcTextStreamProcessor categoryParser = TextPatternScanner
				.createProcessor(HpscCategory.class);
		try {
			Iterator<Object> iterator = categoryParser.iterator(
					new AcInputStreamContent(loader
							.getResourceAsStream("hpsc_category.txt")), null,
					new AcContentErrorHandler() {

						@Override
						public void handleLoadError(AcLoadException error,
								AcContentLine contentLine)
								throws AcLoadException {
							throw error;
						}
					});
			int maxValue = 0;
			List<HpscCategory> list = new ArrayList<HpscCategory>();
			while (iterator.hasNext()) {
				HpscCategory category = (HpscCategory) iterator.next();
				list.add(category);
				maxValue = Math.max(maxValue, category.code());
			}
			dictionary.categorys = list.toArray(new HpscCategory[list.size()]);
			dictionary.categoryIndexs = new HpscCategory[maxValue + 1];
			for (HpscCategory category : dictionary.categorys) {
				if (dictionary.categoryIndexs[category.code()] != null) {
					throw new IllegalArgumentException(
							"duplicated Hpsc Category: " + category);
				}
				dictionary.categoryIndexs[category.code()] = category;
			}
		} catch (AcLoadException e) {
			throw new IllegalArgumentException(e);
		}
		dictionary.contextTypes = HpscContextType.values();
		int maxValue = 0;
		for (HpscContextType contextType : dictionary.contextTypes) {
			maxValue = Math.max(maxValue, contextType.code());
		}
		dictionary.contextTypeIndexs = new HpscContextType[maxValue + 1];
		for (HpscContextType contextType : dictionary.contextTypes) {
			if (dictionary.contextTypeIndexs[contextType.code()] != null) {
				throw new IllegalArgumentException(
						"duplicated Hpsc context type: " + contextType);
			}
			dictionary.contextTypeIndexs[contextType.code()] = contextType;
		}
		return dictionary;
	}

	@Override
	public AcCategory[] categorys() {
		return categorys.clone();
	}

	@Override
	public AcContextType contextType(int type) {
		AcContextType contextType = contextTypeIndexs[type];
		if (contextType == null) {
			throw new IllegalArgumentException("invalid context type code: "
					+ type);
		}
		return contextType;
	}

	@Override
	public AcContextType[] contextTypes() {
		return contextTypes.clone();
	}

	@Override
	public String toString() {
		return String.format("HpscDictionary [categorys=%s, contextTypes=%s]",
				Arrays.toString(categorys), Arrays.toString(contextTypes));
	}

	@Override
	public AcCategory category(String categoryName) {
		for (AcCategory category : categorys) {
			if (category.name().equals(categoryName)) {
				return category;
			}
		}
		throw new IllegalArgumentException("invalid category name: "
				+ categoryName);
	}

	@Override
	public AcContextType contextType(String typeName) {
		return HpscContextType.valueOf(typeName);
	}
	
	private Object writeReplace() {
		return SerializeHolderInstance;
	}

}