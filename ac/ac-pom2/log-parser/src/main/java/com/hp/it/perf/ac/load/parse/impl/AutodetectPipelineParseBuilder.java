package com.hp.it.perf.ac.load.parse.impl;

import java.util.BitSet;
import java.util.Properties;

import com.hp.it.perf.ac.load.common.AcPredicate;
import com.hp.it.perf.ac.load.content.AcContent;
import com.hp.it.perf.ac.load.content.AcContentHandler;
import com.hp.it.perf.ac.load.content.AcContentLine;
import com.hp.it.perf.ac.load.content.AcContentMetadata;
import com.hp.it.perf.ac.load.content.AcLoadException;
import com.hp.it.perf.ac.load.parse.AcParseException;
import com.hp.it.perf.ac.load.parse.AcParseInsufficientException;
import com.hp.it.perf.ac.load.parse.AcProcessorConfig;
import com.hp.it.perf.ac.load.parse.AcStopParseException;
import com.hp.it.perf.ac.load.parse.AcTextParseResult;
import com.hp.it.perf.ac.load.parse.AcTextParserContext;
import com.hp.it.perf.ac.load.parse.AcTextPipeline;
import com.hp.it.perf.ac.load.parse.AcTextPipelineParseBuilder;
import com.hp.it.perf.ac.load.parse.AcTextToken;
import com.hp.it.perf.ac.load.parse.parsers.AbstractAcTextParser;
import com.hp.it.perf.ac.load.parse.plugins.AcParsePluginException;
import com.hp.it.perf.ac.load.parse.plugins.AcTextProcessLoggingPlugin;
import com.hp.it.perf.ac.load.parse.plugins.AcTextProcessPluginAdapter;
import com.hp.it.perf.ac.load.parse.plugins.AcTextProcessPluginManager;

public class AutodetectPipelineParseBuilder implements
		AcTextPipelineParseBuilder {

	private static final int ERROR_DETECT_LIMIT = 5;
	private AcTextPipelineParseBuilder[] builders;
	private AcTextProcessPluginManager pluginManager;

	private final class AcTextPipelineWrapper implements AcTextPipeline {

		private volatile AcTextPipeline internal;

		@Override
		public void putLine(String line) throws AcLoadException,
				AcStopParseException {
			internal.putLine(line);
		}

		@Override
		public void prepare(AcContentMetadata metadata) throws AcLoadException,
				AcStopParseException {
			internal.prepare(metadata);
		}

		@Override
		public void markEOB() throws AcLoadException, AcStopParseException {
			internal.markEOB();
		}

		@Override
		public void close() {
			internal.close();
		}

		void updateInernal(AcTextPipeline pipeline) {
			this.internal = pipeline;
		}
	}

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

	public AutodetectPipelineParseBuilder(
			AcTextPipelineParseBuilder... builders) {
		this.builders = builders;
		if (builders.length < 1) {
			throw new IllegalArgumentException(
					"require at least pipeline builder");
		}
	}

	@Override
	public void setProcessProperties(Properties properties) {
		for (AcTextPipelineParseBuilder builder : builders) {
			builder.setProcessProperties(properties);
		}
	}

	@Override
	public void setPluginManager(AcTextProcessPluginManager manager) {
		this.pluginManager = manager;
	}

	@Override
	public AcTextPipeline createPipeline(
			final AcPredicate<? super AcContentLine> filter,
			final AcContentHandler contentHandler) {
		if (builders.length > 1) {
			final AcTextProcessPluginManager manager = new AcTextProcessPluginManager();
			manager.addPlugin(new AcTextProcessPluginAdapter() {

				@Override
				public <T extends AcContent<?>> T processStart(T content,
						AcTextParserContext context)
						throws AcParsePluginException {
					context.setAttribute(AcTextProcessLoggingPlugin.LogStats,
							Boolean.FALSE);
					return super.processStart(content, context);
				}

			});
			final AcTextPipelineWrapper pipelineWrapper = new AcTextPipelineWrapper();
			// detecting
			AcTextPipeline detectingPipeline = new AcTextPipeline() {

				private BitSet detector = new BitSet(builders.length);
				private AcTextPipeline[] pipelines;
				private AutodetectHandler[] detectHandlers;
				private StringQueueImpl history = new StringQueueImpl();
				private AcContentMetadata metadata;

				{
					pipelines = new AcTextPipeline[builders.length];
					detectHandlers = new AutodetectHandler[builders.length];
					for (int i = 0; i < pipelines.length; i++) {
						// TODO
						AcTextPipelineParseBuilder builder = builders[i];
						builder.setPluginManager(manager);
						detectHandlers[i] = new AutodetectHandler(
								ERROR_DETECT_LIMIT);
						pipelines[i] = builder.createPipeline(filter,
								detectHandlers[i]);
					}
					detector.set(0, builders.length);
				}

				@Override
				public void putLine(String line) throws AcLoadException {
					history.putLine(line);
					for (int i = detector.nextSetBit(0); i >= 0; i = detector
							.nextSetBit(i + 1)) {
						AcTextPipeline pipeline = pipelines[i];
						try {
							pipeline.putLine(line);
						} catch (ParseDetectedException e) {
							boolean detected = handleDetededError(e, i);
							if (detected) {
								break;
							}
						} catch (AcLoadException e) {
							// other non-parse issue found
							throw e;
						}
					}
					checkAllFailed();
				}

				private boolean handleDetededError(ParseDetectedException e,
						int index) throws AcLoadException {
					if (!e.isErrorDetected()) {
						switchPipeline(builders[index]);
						return true;
					} else {
						detector.clear(index);
						return false;
					}
				}

				private void switchPipeline(AcTextPipelineParseBuilder builder)
						throws AcStopParseException, AcLoadException {
					builder.setPluginManager(pluginManager);
					AcTextPipeline pipeline = builder.createPipeline(filter,
							contentHandler);
					pipelineWrapper.updateInernal(pipeline);
					// redo;
					pipeline.prepare(metadata);
					while (true) {
						String line = history.pollLine();
						if (line != null) {
							pipeline.putLine(line);
						} else if (history.wasEOB()) {
							pipeline.markEOB();
						} else if (history.isClosed()) {
							pipeline.close();
							break;
						} else {
							// null means no data
							break;
						}
					}
				}

				@Override
				public void prepare(AcContentMetadata metadata)
						throws AcLoadException, AcStopParseException {
					this.metadata = metadata;
					for (int i = 0; i < pipelines.length; i++) {
						AcTextPipeline pipeline = pipelines[i];
						pipeline.prepare(metadata);
					}
				}

				@Override
				public void markEOB() throws AcLoadException,
						AcStopParseException {
					history.markEOB();
					for (int i = detector.nextSetBit(0); i >= 0; i = detector
							.nextSetBit(i + 1)) {
						AcTextPipeline pipeline = pipelines[i];
						try {
							pipeline.markEOB();
						} catch (ParseDetectedException e) {
							boolean detected = handleDetededError(e, i);
							if (detected) {
								break;
							}
						} catch (AcLoadException e) {
							// other non-parse issue found
							throw e;
						}
					}
					checkAllFailed();
				}

				private void checkAllFailed() throws AcStopParseException,
						AcLoadException {
					if (detector.cardinality() == 0) {
						// all failed, use fake pipeline for create error
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
						AcTextPipelineParseBuilder fakeBuilder = new AcTextPipelineParseBuilderImpl(
								fakeConfig);
						switchPipeline(fakeBuilder);
					}
				}

				@Override
				public void close() {
					history.close();
					for (int i = detector.nextSetBit(0); i >= 0; i = detector
							.nextSetBit(i + 1)) {
						AcTextPipeline pipeline = pipelines[i];
						// TODO
						pipeline.close();
					}
				}
			};
			pipelineWrapper.updateInernal(detectingPipeline);
			return pipelineWrapper;
		} else {
			builders[0].setPluginManager(pluginManager);
			return builders[0].createPipeline(filter, contentHandler);
		}
	}

}
