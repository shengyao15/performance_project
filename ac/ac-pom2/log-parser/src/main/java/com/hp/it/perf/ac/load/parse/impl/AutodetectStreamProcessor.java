package com.hp.it.perf.ac.load.parse.impl;

import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;
import java.util.Properties;

import com.hp.it.perf.ac.load.common.AcPredicate;
import com.hp.it.perf.ac.load.content.AcContent;
import com.hp.it.perf.ac.load.content.AcContentErrorHandler;
import com.hp.it.perf.ac.load.content.AcContentHandler;
import com.hp.it.perf.ac.load.content.AcContentLine;
import com.hp.it.perf.ac.load.content.AcContentMetadata;
import com.hp.it.perf.ac.load.content.AcLoadException;
import com.hp.it.perf.ac.load.content.AcReaderContent;
import com.hp.it.perf.ac.load.content.ReloadableReader;
import com.hp.it.perf.ac.load.parse.AcParseException;
import com.hp.it.perf.ac.load.parse.AcParseInsufficientException;
import com.hp.it.perf.ac.load.parse.AcProcessorConfig;
import com.hp.it.perf.ac.load.parse.AcTextParseResult;
import com.hp.it.perf.ac.load.parse.AcTextParserContext;
import com.hp.it.perf.ac.load.parse.AcTextStreamProcessor;
import com.hp.it.perf.ac.load.parse.AcTextToken;
import com.hp.it.perf.ac.load.parse.parsers.AbstractAcTextParser;
import com.hp.it.perf.ac.load.parse.plugins.AcParsePluginException;
import com.hp.it.perf.ac.load.parse.plugins.AcTextProcessLoggingPlugin;
import com.hp.it.perf.ac.load.parse.plugins.AcTextProcessPluginAdapter;
import com.hp.it.perf.ac.load.parse.plugins.AcTextProcessPluginManager;

public class AutodetectStreamProcessor implements AcTextStreamProcessor {

	private static final int ERROR_DETECT_LIMIT = 5;
	private AcTextStreamProcessor[] processors;
	private AcTextProcessPluginManager pluginManager;

	private static class ParseDetectedException extends AcParseException {
		private static final long serialVersionUID = 1L;
		private boolean errorDetected;

		ParseDetectedException(boolean errorDetected) {
			this.errorDetected = errorDetected;
		}

		public boolean isErrorDetected() {
			return errorDetected;
		}

	}

	private static class ReloadableContent implements AcReaderContent {

		private final AcReaderContent content;

		private ReloadableReader reader;

		private boolean disableCache = false;

		private boolean useContentReader = false;

		public ReloadableContent(AcReaderContent content) {
			this.content = content;
		}

		@Override
		public Reader getContent() throws IOException {
			if (useContentReader) {
				return this.content.getContent();
			}
			if (reader == null) {
				reader = new ReloadableReader(this.content.getContent());
				reader.setNotClose(true);
				reader.setCacheable(!disableCache);
			}
			return reader;
		}

		@Override
		public AcContentMetadata getMetadata() {
			return content.getMetadata();
		}

		// notify reader to back normal stage
		public void disableCache() {
			disableCache = true;
			if (reader != null) {
				reader.setNotClose(false);
				reader.setCacheable(false);
			}
		}

		public void close() {
			disableCache = true;
			if (reader != null) {
				reader.setCacheable(false);
				reader.setNotClose(false);
				try {
					reader.close();
				} catch (IOException ignored) {
				}
			}
		}

		void reset() {
			if (content.getMetadata().isReloadable() && reader != null) {
				// use real reader because it can reloadable
				// close previous (not closed)
				try {
					reader.closeOrReset();
				} catch (IOException ignored) {
				}
				useContentReader = true;
				reader = null;
			}
		}

	}

	private static class AutodetectHandler implements AcContentHandler {

		private int errorCount;
		private int errorCountLimit;

		AutodetectHandler(int errorCountLimit) {
			this.errorCountLimit = errorCountLimit;
		}

		@Override
		public void handleLoadError(AcLoadException error,
				AcContentLine contentLine) throws AcLoadException {
			if (error instanceof ParseDetectedException) {
				throw error;
			} else {
				errorCount++;
			}
			if (errorCount >= errorCountLimit) {
				throw new ParseDetectedException(true);
			}
		}

		@Override
		public void init(AcContentMetadata metadata) {
			this.errorCount = 0;
		}

		@Override
		public void handle(Object object, AcContentLine contentLine)
				throws AcLoadException {
			throw new ParseDetectedException(false);
		}

		@Override
		public void destroy() {
			errorCount = 0;
		}

	}

	public AutodetectStreamProcessor(AcTextStreamProcessor... processors) {
		this.processors = processors;
		if (processors.length < 1) {
			throw new IllegalArgumentException("require at least processor");
		}
	}

	@Override
	public void setProcessProperties(Properties properties) {
		for (AcTextStreamProcessor processor : processors) {
			processor.setProcessProperties(properties);
		}
	}

	protected AcTextStreamProcessor detectProcessor(
			ReloadableContent reloadableContent,
			AcPredicate<? super AcContentLine> filter,
			AcContentHandler contentHandler) throws AcLoadException {
		AcTextStreamProcessor detectedProcessor = null;
		boolean detectDone = false;
		if (processors.length > 1) {
			AutodetectHandler detectHandler = new AutodetectHandler(
					ERROR_DETECT_LIMIT);
			try {
				AcTextProcessPluginManager manager = new AcTextProcessPluginManager();
				manager.addPlugin(new AcTextProcessPluginAdapter() {

					@Override
					public <T extends AcContent<?>> T processStart(T content,
							AcTextParserContext context)
							throws AcParsePluginException {
						context.setAttribute(
								AcTextProcessLoggingPlugin.LogStats,
								Boolean.FALSE);
						return super.processStart(content, context);
					}

				});
				for (AcTextStreamProcessor processor : processors) {
					try {
						startDetect(processor);
						processor.setPluginManager(manager);
						processor.process(reloadableContent, filter,
								detectHandler);
					} catch (ParseDetectedException e) {
						if (!e.isErrorDetected()) {
							detectedProcessor = processor;
							detectDone = true;
							break;
						}
					} catch (AcLoadException e) {
						// other non-parse issue found
						throw e;
					} finally {
						processor.setPluginManager(null);
						finishDetect(processor);
					}
				}
				// no processor detected, report fail
				if (!detectDone) {
					detectDone = true;
					AcProcessorConfig fakeConfig = new AcProcessorConfig();
					fakeConfig.setParser(new AbstractAcTextParser() {

						@Override
						public AcTextParseResult parse(AcTextToken text,
								AcTextParserContext context)
								throws AcParseInsufficientException,
								AcParseException {
							return createParseError("no suitable parser",
									context);
						}

						@Override
						public boolean test(String textFragement,
								AcTextParserContext context) {
							return false;
						}

					});
					detectedProcessor = new AcTextProcessor(fakeConfig);
				}
			} finally {
				if (!detectDone) {
					// get other failure, need to fulfill life-cycle
					if (contentHandler != null) {
						contentHandler.init(reloadableContent.getMetadata());
					}
					reloadableContent.close();
					if (contentHandler != null) {
						contentHandler.destroy();
					}
				}
			}
		} else {
			// no detect if only one
			detectedProcessor = processors[0];
		}
		reloadableContent.disableCache();
		return detectedProcessor;
	}

	protected void startDetect(AcTextStreamProcessor processor) {
	}

	protected void finishDetect(AcTextStreamProcessor processor) {
	}

	@Override
	public void process(AcReaderContent content,
			AcPredicate<? super AcContentLine> filter,
			AcContentHandler contentHandler) throws AcLoadException {
		// detecting
		ReloadableContent reloadableContent = new ReloadableContent(content);
		AcTextStreamProcessor detectedProcessor = detectProcessor(
				reloadableContent, filter, contentHandler);
		configPluginManagers(detectedProcessor);
		reloadableContent.reset();
		detectedProcessor.process(reloadableContent, filter, contentHandler);
	}

	@Override
	public Iterator<Object> iterator(AcReaderContent content,
			AcPredicate<? super AcContentLine> filter,
			AcContentErrorHandler errorHandler) throws AcLoadException {
		// detecting
		ReloadableContent reloadableContent = new ReloadableContent(content);
		AcTextStreamProcessor detectedProcessor = detectProcessor(
				reloadableContent, filter, null);
		configPluginManagers(detectedProcessor);
		reloadableContent.reset();
		return detectedProcessor.iterator(reloadableContent, filter,
				errorHandler);
	}

	private void configPluginManagers(AcTextStreamProcessor detectedProcessor) {
		detectedProcessor
				.setPluginManager(pluginManager == null ? AcTextProcessPluginManager
						.getDefaultManager() : pluginManager);
	}

	@Override
	public void setPluginManager(AcTextProcessPluginManager manager) {
		this.pluginManager = manager;
	}

}
