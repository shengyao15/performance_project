package com.hp.it.perf.ac.load.parse.impl;

import java.io.IOException;

import com.hp.it.perf.ac.load.bind.AcBinder;
import com.hp.it.perf.ac.load.common.AcPredicate;
import com.hp.it.perf.ac.load.content.AcContentHandler;
import com.hp.it.perf.ac.load.content.AcContentLine;
import com.hp.it.perf.ac.load.content.AcContentLineReadable;
import com.hp.it.perf.ac.load.content.AcContentLineStringQueue;
import com.hp.it.perf.ac.load.content.AcContentMetadata;
import com.hp.it.perf.ac.load.content.AcLoadException;
import com.hp.it.perf.ac.load.content.AcStringQueue;
import com.hp.it.perf.ac.load.content.AcStringQueue.StringQueue;
import com.hp.it.perf.ac.load.parse.AcParseException;
import com.hp.it.perf.ac.load.parse.AcParseInsufficientException;
import com.hp.it.perf.ac.load.parse.AcProcessorConfig;
import com.hp.it.perf.ac.load.parse.AcStopParseException;
import com.hp.it.perf.ac.load.parse.AcTextElement;
import com.hp.it.perf.ac.load.parse.AcTextParseResult;
import com.hp.it.perf.ac.load.parse.AcTextParser;
import com.hp.it.perf.ac.load.parse.AcTextParserContext;
import com.hp.it.perf.ac.load.parse.AcTextPipeline;
import com.hp.it.perf.ac.load.parse.AcTextPipelineParseBuilder;
import com.hp.it.perf.ac.load.parse.AcTextToken;

public class AcTextPipelineParseBuilderImpl extends AcTextParseBase implements
		AcTextPipelineParseBuilder {

	private final AcProcessorConfig config;

	private int maxMultipleLines = 500;

	private static class PipelineStack {
		AcParseInsufficientException apie;
		Step nextStep = Step.NewEntity;
		AcTextParseResult textParseResult;

		public void gotoStep(Step nextStep) {
			this.nextStep = nextStep;
		}

	}

	private static enum Step {
		NewEntity, PreParse, Parsing, ParseInsufficentError, CheckNextLine, DetectMoreLines, HandleResult, EOF, Filtered, MoveToNewEntity;
	}

	public AcTextPipelineParseBuilderImpl(AcProcessorConfig config) {
		this.config = config;
	}

	public int getMaxMultipleLines() {
		return maxMultipleLines;
	}

	public void setMaxMultipleLines(int maxMultipleLines) {
		this.maxMultipleLines = maxMultipleLines;
	}

	protected void pipelineInternel(AcPredicate<? super AcContentLine> filter,
			AcBinder rootBinder, AcTextParser rootParser,
			AcContentLineReadable reader, AcContentHandler contentHandler,
			AcTextParserContext context, PipelineStack stack)
			throws AcLoadException, AcStopParseException {
		AcContentLine contentLine = reader.getContentLine();
		LOOP: while (true) {
			try {
				switch (stack.nextStep) {
				case EOF: {
					// process EOF
					if (reader != null) {
						try {
							reader.close();
						} catch (IOException ignored) {
						}
					}
					try {
						contentHandler.destroy();
					} finally {
						// make sure it is invoked
						processPluginEnd(context);
					}
					break LOOP;
				}
				case NewEntity: {
					contentLine = reader.getContentLine();
					if (contentLine == null) {
						stack.gotoStep(Step.EOF);
						break;
					} else {
						AcContentLine prevContentLine = contentLine;
						boolean nextLineUnknown = contentLine
								.isNextLineUnknown();
						try {
							contentLine = processPluginRead(contentLine,
									context);
						} catch (AcParseException e) {
							throw e;
						} catch (Exception e) {
							throw new AcParseException("unexpected error.", e);
						}
						if (contentLine == null) {
							// filtered by plug-in
							if (nextLineUnknown) {
								stack.gotoStep(Step.PreParse);
								break LOOP;
							}
						} else {
							if (filter != null && !filter.apply(contentLine)) {
								// not accept by filter
								if (nextLineUnknown) {
									stack.gotoStep(Step.Filtered);
									break LOOP;
								}
							} else {
								// continue parse
								stack.gotoStep(Step.Parsing);
								break;
							}
						}
						// just restore back for "Filtered"
						contentLine = prevContentLine;
						stack.gotoStep(Step.Filtered);
						break;
					}
				}
				case Filtered: {
					if (!contentLine.getNextLineStatus().isReady()) {
						stack.gotoStep(Step.Filtered);
						break LOOP;
					}
					reader.readLines();
					contentLine = reader.getContentLine();
					if (contentLine != null && contentLine.isNextLineUnknown()) {
						// suspend to pre-parse
						stack.gotoStep(Step.PreParse);
						break LOOP;
					} else {
						// continue pre-parse
						stack.gotoStep(Step.PreParse);
						break;
					}
				}
				case PreParse: {
					boolean nextLineReady = contentLine.getNextLineStatus()
							.isReady();
					try {
						contentLine = processPluginRead(contentLine, context);
					} catch (AcParseException e) {
						throw e;
					} catch (Exception e) {
						throw new AcParseException("unexpected error.", e);
					}
					if (contentLine == null) {
						// filtered by plug-in
						if (!nextLineReady) {
							stack.gotoStep(Step.PreParse);
							break LOOP;
						} else {
							reader.readLines();
							contentLine = reader.getContentLine();
							if (contentLine == null) {
								stack.gotoStep(Step.EOF);
								break;
							}
							// set next step for continue
							stack.gotoStep(Step.PreParse);
							break;
						}
					} else {
						if (!rootParser.test(contentLine.getCurrentLines(),
								context)) {
							if (contentLine.getNextLineStatus().isReady()) {
								reader.readLines();
								contentLine = reader.getContentLine();
								// set next step for continue
								stack.gotoStep(Step.PreParse);
								break;
							}
							// suspend to pre-parse
							stack.gotoStep(Step.PreParse);
							break LOOP;
						} else {
							// continue;
							stack.gotoStep(Step.NewEntity);
							break;
						}
					}
				}
				case Parsing: {
					AcTextToken token = new AcTextToken();
					token.setContent(contentLine.getCurrentLines());
					token.setNextToken(contentLine.getNextLine());
					token.setEndOfLine(true);
					AcTextParseResult parseResult = null;
					try {
						parseResult = rootParser.parse(token, context);
						stack.textParseResult = parseResult;
						if (parseResult.isExactMatch()) {
							stack.gotoStep(Step.HandleResult);
						} else {
							stack.gotoStep(Step.CheckNextLine);
						}
						break;
					} catch (AcParseInsufficientException e) {
						if (contentLine.getLineInfo().getMutilLine() >= maxMultipleLines) {
							// exceed max line threshold
							throw e;
						}
						stack.apie = e;
						stack.gotoStep(Step.ParseInsufficentError);
						break;
					} catch (AcParseException e) {
						throw e;
					} catch (Exception e) {
						throw new AcParseException("unexpected error " + token,
								e);
					}
				}
				case ParseInsufficentError: {
					AcParseInsufficientException e = stack.apie;
					// test next line if match (so this insufficient error
					// is treated as error)
					if (!contentLine.isNextLineUnknown()) {
						if (contentLine.isEOB()) {
							// hit EOF
							throw e;
						}
						if (rootParser.test(contentLine.getNextLine(), context)) {
							throw e;
						}
						reader.readMoreLines();
						stack.gotoStep(Step.NewEntity);
						stack.apie = null;
						break;
					} else {
						// next line not available
						stack.gotoStep(Step.ParseInsufficentError);
						break LOOP;
					}
				}
				case CheckNextLine: {
					// try test next line, like error trace
					if (!contentLine.isNextLineUnknown()) {
						if (contentLine.isEOB()
								|| rootParser.test(contentLine.getNextLine(),
										context)) {
							// next line maybe start new one
							stack.gotoStep(Step.HandleResult);
							break;
						} else {
							stack.gotoStep(Step.DetectMoreLines);
							break;
						}
					} else {
						stack.gotoStep(Step.CheckNextLine);
						break LOOP;
					}
				}
				case DetectMoreLines: {
					while (true) {
						if (contentLine.isNextLineUnknown()) {
							stack.gotoStep(Step.DetectMoreLines);
							break LOOP;
						}
						if (contentLine.isEOB()
								|| rootParser.test(contentLine.getNextLine(),
										context)) {
							// next line maybe start new one
							stack.gotoStep(Step.NewEntity);
							break;
						} else {
							if (contentLine.getLineInfo().getMutilLine() >= maxMultipleLines) {
								throw new AcParseException(
										"Detect next block exceeds max lines limitation: "
												+ contentLine.getLineInfo()
														.getMutilLine());
							}
							// try load more
							reader.readMoreLines();
							contentLine = reader.getContentLine();
						}
					}
					break;
				}
				case HandleResult: {
					AcTextParseResult parseResult = stack.textParseResult;
					// next line will start new entity
					AcTextElement element = parseResult.getElement();
					// System.out.println(element.toIndentString(""));
					Object bindResult = element.bind(rootBinder);
					bindResult = processPluginResolve(bindResult, contentLine,
							context);
					if (bindResult != null) {
						contentHandler.handle(bindResult, contentLine);
					} // otherwise ignore it
						// check if can read next entity
					if (contentLine.getNextLineStatus().isReady()) {
						reader.readLines();
						stack.gotoStep(Step.NewEntity);
						break;
					} else {
						stack.gotoStep(Step.MoveToNewEntity);
						break LOOP;
					}
				}
				case MoveToNewEntity: {
					if (contentLine.getNextLineStatus().isReady()) {
						reader.readLines();
						stack.gotoStep(Step.NewEntity);
						break;
					} else {
						stack.gotoStep(Step.MoveToNewEntity);
						break LOOP;
					}
				}
				}
			} catch (IOException e) {
				e = processPluginGeneralError(e, context);
				if (e != null) {
					throw new AcLoadException("io error", e);
				} // otherwise, error is consumed
			} catch (AcStopParseException e) {
				// capture stop parse instruction
				throw e;
			} catch (AcLoadException e) {
				// check if EOF (error in processing EOF)
				if (stack.nextStep == Step.EOF) {
					throw e;
				}
				e = processPluginParseError(e, contentLine, context);
				if (e != null) {
					contentHandler.handleLoadError(e, contentLine);
				} // otherwise, error is consumed
				if (contentLine.getNextLineStatus().isReady()) {
					try {
						reader.readLines();
					} catch (IOException e1) {
						e1 = processPluginGeneralError(e1, context);
						if (e1 != null) {
							throw new AcLoadException("io error", e1);
						} // otherwise, error is consumed
					}
					stack.gotoStep(Step.NewEntity);
				} else {
					stack.gotoStep(Step.MoveToNewEntity);
				}
			}
		}
	}

	@Override
	public AcTextPipeline createPipeline(
			final AcPredicate<? super AcContentLine> filter,
			final AcContentHandler contentHandler) {
		final AcTextParserContext context = initParseContext();
		final AcBinder rootBinder = config.getBinder();
		final AcTextParser rootParser = config.getParser();
		rootParser.setErrorMode(true);
		AcTextPipeline pipeline = new AcTextPipeline() {

			private PipelineStack stack = new PipelineStack();

			private AcContentLineReadable reader;

			private boolean needFirstRead = false;

			private StringQueue contentQueue;

			@Override
			public void prepare(AcContentMetadata metadata)
					throws AcLoadException, AcStopParseException {
				try {
					AcStringQueue content = processPluginStart(
							new AcStringQueue(metadata, new StringQueueImpl()),
							context);
					reader = new AcContentLineStringQueue(content);
					contentHandler.init(content.getMetadata());
					contentQueue = content.getContent();
					needFirstRead = true;
				} catch (AcStopParseException e) {
					e = processPluginGeneralError(e, context);
					if (e != null) {
						// stop parse
						throw e;
					}
				} catch (IOException e) {
					throw new AcLoadException(e);
				} finally {
					if (!needFirstRead) {
						if (reader != null) {
							try {
								reader.close();
							} catch (IOException ignored) {
							}
						}
						try {
							contentHandler.destroy();
						} finally {
							// make sure it is invoked
							processPluginEnd(context);
						}
					}
				}
			}

			@Override
			public void putLine(String line) throws AcLoadException,
					AcStopParseException {
				contentQueue.putLine(line);
				if (needFirstRead) {
					try {
						reader.readLines();
					} catch (IOException never) {
					}
					needFirstRead = false;
				}
				pipelineInternel(filter, rootBinder, rootParser, reader,
						contentHandler, context, stack);
			}

			@Override
			public void close() {
				contentQueue.close();
				if (!needFirstRead) {
					try {
						pipelineInternel(filter, rootBinder, rootParser,
								reader, contentHandler, context, stack);
					} catch (AcLoadException e) {
						// TODO
						e.printStackTrace();
					}
				}
			}

			@Override
			public void markEOB() throws AcLoadException, AcStopParseException {
				contentQueue.markEOB();
				if (!needFirstRead) {
					pipelineInternel(filter, rootBinder, rootParser, reader,
							contentHandler, context, stack);
				}
			}
		};
		return pipeline;
	}
}
