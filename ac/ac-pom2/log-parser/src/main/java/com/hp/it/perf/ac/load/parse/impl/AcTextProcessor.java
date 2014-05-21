package com.hp.it.perf.ac.load.parse.impl;

import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.NoSuchElementException;

import com.hp.it.perf.ac.load.bind.AcBinder;
import com.hp.it.perf.ac.load.common.AcPredicate;
import com.hp.it.perf.ac.load.content.AcContentLineReadable;
import com.hp.it.perf.ac.load.content.AcReaderContent;
import com.hp.it.perf.ac.load.content.AcContentErrorHandler;
import com.hp.it.perf.ac.load.content.AcContentHandler;
import com.hp.it.perf.ac.load.content.AcContentLine;
import com.hp.it.perf.ac.load.content.AcContentMetadata;
import com.hp.it.perf.ac.load.content.AcContentLineReader;
import com.hp.it.perf.ac.load.content.AcLoadException;
import com.hp.it.perf.ac.load.parse.AcParseException;
import com.hp.it.perf.ac.load.parse.AcParseInsufficientException;
import com.hp.it.perf.ac.load.parse.AcProcessorConfig;
import com.hp.it.perf.ac.load.parse.AcStopParseException;
import com.hp.it.perf.ac.load.parse.AcTextElement;
import com.hp.it.perf.ac.load.parse.AcTextParseResult;
import com.hp.it.perf.ac.load.parse.AcTextParser;
import com.hp.it.perf.ac.load.parse.AcTextParserContext;
import com.hp.it.perf.ac.load.parse.AcTextStreamProcessor;
import com.hp.it.perf.ac.load.parse.AcTextToken;

class AcTextProcessor extends AcTextParseBase implements AcTextStreamProcessor {

	private final AcProcessorConfig config;

	private int maxMultipleLines = 2000;

	public AcTextProcessor(AcProcessorConfig config) {
		this.config = config;
	}

	public int getMaxMultipleLines() {
		return maxMultipleLines;
	}

	public void setMaxMultipleLines(int maxMultipleLines) {
		this.maxMultipleLines = maxMultipleLines;
	}

	@Override
	public void process(AcReaderContent content,
			AcPredicate<? super AcContentLine> filter,
			AcContentHandler contentHandler) throws AcLoadException {
		final AcTextParserContext context = initParseContext();
		AcBinder rootBinder = config.getBinder();
		AcTextParser rootParser = config.getParser();
		rootParser.setErrorMode(true);
		AcContentLineReader reader = null;
		try {
			content = processPluginStart(content, context);
			contentHandler.init(content.getMetadata());
			reader = new AcContentLineReader(content.getContent());
			reader.readLines();
			processInternel(filter, rootBinder, rootParser, reader,
					contentHandler, true, context);
		} catch (AcStopParseException e) {
			e = processPluginGeneralError(e, context);
			if (e != null) {
				// stop parse
				if (!e.isNormalStop()) {
					throw e;
				}
			} // otherwise, error is consumed
		} catch (IOException e) {
			e = processPluginGeneralError(e, context);
			if (e != null) {
				throw new AcLoadException("io error", e);
			} // otherwise, error is consumed
		} finally {
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

	protected void processInternel(AcPredicate<? super AcContentLine> filter,
			AcBinder rootBinder, AcTextParser rootParser,
			AcContentLineReadable reader, AcContentHandler contentHandler,
			boolean inLoop, AcTextParserContext context) throws IOException,
			AcLoadException {
		AcContentLine contentLine = null;
		while ((contentLine = reader.getContentLine()) != null) {
			// test thread interrupted
			AcTextToken token = new AcTextToken();
			token.setContent(contentLine.getCurrentLines());
			token.setNextToken(contentLine.getNextLine());
			token.setEndOfLine(true);
			AcTextParseResult parseResult = null;
			try {
				try {
					contentLine = processPluginRead(contentLine, context);
					if (contentLine == null
							|| (filter != null && !filter.apply(contentLine))) {
						reader.readLines();
						// try find next start
						while ((contentLine = reader.getContentLine()) != null) {
							contentLine = processPluginRead(contentLine,
									context);
							if (contentLine != null
									&& rootParser.test(
											contentLine.getCurrentLines(),
											context)) {
								// may start next entity
								break;
							} else {
								// not next entity starting
								reader.readLines();
							}
						}
						continue;
					}
					parseResult = rootParser.parse(token, context);
				} catch (AcParseInsufficientException e) {
					if (contentLine.getLineInfo().getMutilLine() >= maxMultipleLines) {
						// exceed max line threshold
						throw e;
					}
					// test next line if match (so this insufficient error
					// is treated as error)
					if (contentLine.isEOB()
							|| rootParser.test(contentLine.getNextLine(),
									context)) {
						throw e;
					}
					reader.readMoreLines();
					continue;
				} catch (AcParseException e) {
					throw e;
				} catch (Exception e) {
					throw new AcParseException("unexpected error " + token, e);
				}
				// try test next line, like error trace
				if (!parseResult.isExactMatch()) {
					boolean retry = false;
					while (!contentLine.isEOB()) {
						if (rootParser.test(contentLine.getNextLine(), context)) {
							// next line maybe start new one
							break;
						} else {
							retry = true;
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
					if (retry) {
						continue;
					}
				}
				// next line will start new entity
				AcTextElement element = parseResult.getElement();
				// System.out.println(element.toIndentString(""));
				Object bindResult = element.bind(rootBinder);
				bindResult = processPluginResolve(bindResult, contentLine,
						context);
				if (bindResult != null) {
					contentHandler.handle(bindResult, contentLine);
				} // otherwise ignore it
			} catch (AcStopParseException e) {
				// capture stop parse instruction
				throw e;
			} catch (AcLoadException e) {
				e = processPluginParseError(e, contentLine, context);
				if (e != null) {
					contentHandler.handleLoadError(e, contentLine);
				}// otherwise, error is consumed
			}
			reader.readLines();
			if (!inLoop) {
				break;
			}
		}
	}

	@Override
	public Iterator<Object> iterator(final AcReaderContent content,
			final AcPredicate<? super AcContentLine> filter,
			final AcContentErrorHandler errorHandler) throws AcLoadException {
		final AcTextParserContext context = initParseContext();
		final AcBinder rootBinder = config.getBinder();
		final AcTextParser rootParser = config.getParser();
		rootParser.setErrorMode(true);
		final AcContentLineReadable reader;
		try {
			AcReaderContent theContent = (AcReaderContent) processPluginStart(content, context);
			reader = new AcContentLineReader(theContent.getContent());
			reader.readLines();
		} catch (AcStopParseException e) {
			e = processPluginGeneralError(e, context);
			if (e != null) {
				// stop parse
				if (!e.isNormalStop()) {
					throw e;
				}
			}
			return Collections.emptySet().iterator();
		} catch (IOException e) {
			e = processPluginGeneralError(e, context);
			if (e != null) {
				throw new AcLoadException("io error", e);
			} else {
				return Collections.emptySet().iterator();
			}
		}
		return new Iterator<Object>() {
			private Object currentObject;
			private Boolean pendingNext = Boolean.FALSE;

			@Override
			public boolean hasNext() {
				if (pendingNext == null) {
					return false;
				}
				if (pendingNext.booleanValue())
					return true;
				try {
					processInternel(filter, rootBinder, rootParser, reader,
							new AcContentHandler() {

								@Override
								public void init(AcContentMetadata metadata) {
									// no-op;
								}

								@Override
								public void handleLoadError(
										AcLoadException error,
										AcContentLine contentLine)
										throws AcLoadException {
									if (errorHandler != null) {
										errorHandler.handleLoadError(error,
												contentLine);
									}
								}

								@Override
								public void handle(Object object,
										AcContentLine contentLine)
										throws AcLoadException {
									currentObject = object;
									pendingNext = Boolean.TRUE;
								}

								@Override
								public void destroy() {
									// no-op;
								}
							}, false, context);
				} catch (IOException e) {
					e = processPluginGeneralError(e, context);
					if (e != null) {
						throw (NoSuchElementException) (new NoSuchElementException(
								"io error").initCause(e));
					} else {
						// stop parsing
						pendingNext = Boolean.FALSE;
					}
				} catch (AcStopParseException e) {
					e = processPluginGeneralError(e, context);
					if (e != null) {
						// stop parsing
						pendingNext = Boolean.FALSE;
					}
				} catch (AcParseException e) {
					e = processPluginGeneralError(e, context);
					if (e != null) {
						throw (NoSuchElementException) (new NoSuchElementException(
								"parse error").initCause(e));
					} else {
						// stop parsing
						pendingNext = Boolean.FALSE;
					}
				} catch (AcLoadException e) {
					e = processPluginGeneralError(e, context);
					if (e != null) {
						throw (NoSuchElementException) (new NoSuchElementException(
								"load error").initCause(e));
					} else {
						// stop parsing
						pendingNext = Boolean.FALSE;
					}
				}
				if (pendingNext == Boolean.FALSE) {
					closeReader();
				}
				return pendingNext == Boolean.TRUE;
			}

			private void closeReader() {
				try {
					reader.close();
				} catch (IOException ignored) {
				}
				if (pendingNext != null) {
					processPluginEnd(context);
				}
				pendingNext = null;
			}

			protected void finalize() {
				closeReader();
			}

			@Override
			public Object next() {
				if (pendingNext == Boolean.TRUE) {
					Object ret = currentObject;
					currentObject = null;
					pendingNext = Boolean.FALSE;
					return ret;
				} else if (hasNext()) {
					return next();
				} else {
					throw new NoSuchElementException();
				}
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException("remove()");
			}
		};
	}

}
