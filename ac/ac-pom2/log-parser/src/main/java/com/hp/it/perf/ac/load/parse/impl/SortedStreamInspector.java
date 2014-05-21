package com.hp.it.perf.ac.load.parse.impl;

import java.io.IOException;
import java.io.Reader;
import java.util.Collections;
import java.util.Iterator;
import java.util.Properties;

import com.hp.it.perf.ac.load.bind.AcBinder;
import com.hp.it.perf.ac.load.common.AcPredicate;
import com.hp.it.perf.ac.load.content.AcContentLineReadable;
import com.hp.it.perf.ac.load.content.AcReaderContent;
import com.hp.it.perf.ac.load.content.AcContentErrorHandler;
import com.hp.it.perf.ac.load.content.AcContentHandler;
import com.hp.it.perf.ac.load.content.AcContentLine;
import com.hp.it.perf.ac.load.content.AcContentLineReader;
import com.hp.it.perf.ac.load.content.AcLoadException;
import com.hp.it.perf.ac.load.content.ReloadableReader;
import com.hp.it.perf.ac.load.parse.AcParseException;
import com.hp.it.perf.ac.load.parse.AcParseInsufficientException;
import com.hp.it.perf.ac.load.parse.AcProcessorConfig;
import com.hp.it.perf.ac.load.parse.AcTextParseResult;
import com.hp.it.perf.ac.load.parse.AcTextParser;
import com.hp.it.perf.ac.load.parse.AcTextParserContext;
import com.hp.it.perf.ac.load.parse.AcTextStreamProcessor;
import com.hp.it.perf.ac.load.parse.AcTextToken;
import com.hp.it.perf.ac.load.parse.plugins.AcBeanRangeCheckPlugin;
import com.hp.it.perf.ac.load.parse.plugins.AcTextProcessPlugin;
import com.hp.it.perf.ac.load.parse.plugins.AcTextProcessPluginManager;

public class SortedStreamInspector implements AcTextStreamProcessor {

	private Properties properties;

	private Comparable<Object> rangeComparator;

	private int maxMultipleLines = 500;

	private AcProcessorConfig config;

	private AcTextStreamProcessor processor;

	private AcTextProcessPluginManager pluginManager;

	private AcTextProcessPlugin rangeCheckPlugin;

	private boolean inspectMode = true;

	public SortedStreamInspector(Comparable<Object> rangeComparator,
			AcProcessorConfig config, AcTextStreamProcessor processor) {
		this.rangeComparator = rangeComparator;
		this.config = config;
		this.processor = processor;
		this.rangeCheckPlugin = new AcBeanRangeCheckPlugin(rangeComparator);
	}

	public void setProcessProperties(Properties properties) {
		this.properties = properties;
	}

	public boolean isInspectMode() {
		return inspectMode;
	}

	public void setInspectMode(boolean inspectMode) {
		this.inspectMode = inspectMode;
	}

	protected AcTextStreamProcessor inspect(AcReaderContent content) {
		if (!isInspectMode()) {
			return processor;
		}
		if (content.getMetadata().isReloadable()) {
			boolean ignoreContent;
			try {
				ignoreContent = checkIgnoreBySequence(processor,
						content.getContent());
			} catch (IOException e) {
				// fall back to real processor
				return processor;
			}
			if (ignoreContent) {
				return new AcTextStreamProcessor() {

					@Override
					public void setProcessProperties(Properties properties) {
						// no-op
					}

					@Override
					public void process(AcReaderContent content,
							AcPredicate<? super AcContentLine> filter,
							AcContentHandler contentHandler)
							throws AcLoadException {
						// no-op
					}

					@Override
					public Iterator<Object> iterator(AcReaderContent content,
							AcPredicate<? super AcContentLine> filter,
							AcContentErrorHandler errorHandler)
							throws AcLoadException {
						return Collections.emptyList().iterator();
					}

					@Override
					public void setPluginManager(
							AcTextProcessPluginManager manager) {
						// no-op
					}
				};
			} else {
				return processor;
			}
		} else {
			return processor;
		}
	}

	protected boolean checkIgnoreBySequence(AcTextStreamProcessor processor,
			Reader in) {
		AcTextParserContext context = new AcTextParserContext();
		if (properties != null) {
			context.setProperties(properties);
		}
		AcContentLineReadable reader = new AcContentLineReader(in);
		boolean closeReloadableReader = false;
		try {
			reader.readLines();
			AcContentLine contentLine = reader.getContentLine();
			if (contentLine == null) {
				// no data found, fall back
				return false;
			}
			Object result = quickParseResult(contentLine, context, reader);
			int checkRange = rangeComparator.compareTo(result);
			if (checkRange == 0) {
				// in range, return processor
				return false;
			} else if (checkRange < 0) {
				// start line is later than end of range, ignore all
				closeReloadableReader = true;
				return true;
			} else {
				// start line is earlier than start of range,
				// check last line with range.
				// keep it simple, do not consider multiple lines
				AcContentLine lastLine = contentLine;
				while ((contentLine = reader.getContentLine()) != null) {
					lastLine = contentLine;
					reader.readLines();
				}
				result = quickParseResult(lastLine, context, reader);
				checkRange = rangeComparator.compareTo(result);
				if (checkRange == 0) {
					// in range, return processor
					return false;
				} else if (checkRange < 0) {
					// end line is later than end of range, should inside
					// content
					return false;
				} else {
					// end line is earlier than start of range,ignore all
					closeReloadableReader = true;
					return true;
				}
			}
		} catch (AcLoadException e) {
			// any parse error treat fall back
			return false;
		} catch (IOException e) {
			// parse last line get IO error
			// fall back to range check
			return false;
		} catch (RuntimeException e) {
			closeReloadableReader = true;
			throw e;
		} catch (Error e) {
			closeReloadableReader = true;
			throw e;
		} finally {
			if (in instanceof ReloadableReader) {
				((ReloadableReader) in).setNotClose(!closeReloadableReader);
			}
			try {
				reader.close();
			} catch (IOException ignored) {
			}
			if (in instanceof ReloadableReader) {
				((ReloadableReader) in).setNotClose(false);
			}
		}
	}

	private Object quickParseResult(AcContentLine contentLine,
			AcTextParserContext context, AcContentLineReadable reader)
			throws AcLoadException, IOException {
		AcTextParser rootParser = config.getParser();
		AcBinder rootBinder = config.getBinder();
		rootParser.setErrorMode(true);
		while (true) {
			AcTextToken token = new AcTextToken();
			token.setContent(contentLine.getCurrentLines());
			token.setNextToken(contentLine.getNextLine());
			token.setEndOfLine(true);
			AcTextParseResult parseResult = null;
			try {
				parseResult = rootParser.parse(token, context);
			} catch (AcParseInsufficientException e) {
				if (contentLine.getLineInfo().getMutilLine() >= maxMultipleLines) {
					// exceed max line threshold
					throw e;
				}
				if (contentLine.isEOB()
						|| rootParser.test(contentLine.getNextLine(),
								context)) {
					throw e;
				}
				reader.readMoreLines();
				contentLine = reader.getContentLine();
				continue;
			} catch (AcParseException e) {
				throw e;
			} catch (Exception e) {
				throw new AcParseException("unexpected error " + token, e);
			}
			return parseResult.getElement().bind(rootBinder);
		}
	}

	@Override
	public void process(AcReaderContent content,
			AcPredicate<? super AcContentLine> filter,
			AcContentHandler contentHandler) throws AcLoadException {
		AcTextStreamProcessor inspectedProcessor = inspect(content);
		configPluginManagers(inspectedProcessor);
		inspectedProcessor.process(content, filter, contentHandler);
	}

	@Override
	public Iterator<Object> iterator(AcReaderContent content,
			AcPredicate<? super AcContentLine> filter,
			AcContentErrorHandler errorHandler) throws AcLoadException {
		AcTextStreamProcessor inspectedProcessor = inspect(content);
		configPluginManagers(inspectedProcessor);
		return inspectedProcessor.iterator(content, filter, errorHandler);
	}

	private void configPluginManagers(AcTextStreamProcessor inspectedProcessor) {
		AcTextProcessPluginManager newPluginManager = new AcTextProcessPluginManager();
		if (pluginManager != null) {
			for (AcTextProcessPlugin plugin : pluginManager.getPlugins()) {
				newPluginManager.addPlugin(plugin);
			}
		}
		if (isInspectMode()) {
			newPluginManager.addPlugin(rangeCheckPlugin);
		}
		inspectedProcessor.setPluginManager(newPluginManager);
	}

	@Override
	public void setPluginManager(AcTextProcessPluginManager manager) {
		this.pluginManager = manager;
	}

}
