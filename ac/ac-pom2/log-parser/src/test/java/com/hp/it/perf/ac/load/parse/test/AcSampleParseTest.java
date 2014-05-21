package com.hp.it.perf.ac.load.parse.test;

import static junit.framework.Assert.fail;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.Matchers.notNullValue;

import java.io.BufferedReader;
import java.io.FilterReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.util.Date;
import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;

import com.hp.it.perf.ac.load.common.AcPredicate;
import com.hp.it.perf.ac.load.content.AcCachedContent;
import com.hp.it.perf.ac.load.content.AcContentCounter;
import com.hp.it.perf.ac.load.content.AcContentHandler;
import com.hp.it.perf.ac.load.content.AcContentLine;
import com.hp.it.perf.ac.load.content.AcInputStreamContent;
import com.hp.it.perf.ac.load.content.AcLoadException;
import com.hp.it.perf.ac.load.content.AcReaderContent;
import com.hp.it.perf.ac.load.content.AcStringsContent;
import com.hp.it.perf.ac.load.parse.AcTextPipeline;
import com.hp.it.perf.ac.load.parse.AcTextPipelineParseBuilder;
import com.hp.it.perf.ac.load.parse.AcTextStreamProcessor;
import com.hp.it.perf.ac.load.parse.impl.TextPatternScanner;

public class AcSampleParseTest {

	private AcContentCounter count;
	private TimeableComparator timeComparator;
	private Date sampleBizStartTime = null;
	private Date sampleBizEndTime = null;
	private String bizContent;
	private AbortHandler abort;

	private static class AbortHandler extends AcContentCounter {

		@Override
		public void handleLoadError(AcLoadException error,
				AcContentLine contentLine) throws AcLoadException {
			throw error;
		}

	}

	private void processPipeline(AcTextPipelineParseBuilder pipelineBuilder,
			AcReaderContent content, AcPredicate<? super AcContentLine> filter,
			AcContentHandler handler) throws Exception {
		AcTextPipeline pipeline = pipelineBuilder.createPipeline(filter,
				handler);
		pipeline.prepare(content.getMetadata());
		BufferedReader reader = new BufferedReader(content.getContent());
		try {
			String line;
			while ((line = reader.readLine()) != null) {
				pipeline.putLine(line);
			}
			pipeline.close();
		} finally {
			reader.close();
		}
	}

	private void processPipelineBlockPerLine(
			AcTextPipelineParseBuilder pipelineBuilder,
			AcReaderContent content, AcPredicate<? super AcContentLine> filter,
			AcContentHandler handler) throws Exception {
		AcTextPipeline pipeline = pipelineBuilder.createPipeline(filter,
				handler);
		pipeline.prepare(content.getMetadata());
		BufferedReader reader = new BufferedReader(content.getContent());
		String line;
		while ((line = reader.readLine()) != null) {
			pipeline.putLine(line);
			pipeline.markEOB();
		}
		reader.close();
		pipeline.close();
	}

	private void processPipelineBlock(
			AcTextPipelineParseBuilder pipelineBuilder,
			AcReaderContent content, AcPredicate<? super AcContentLine> filter,
			AcContentHandler handler) throws Exception {
		AcTextPipeline pipeline = pipelineBuilder.createPipeline(filter,
				handler);
		pipeline.prepare(content.getMetadata());
		BufferedReader reader = new BufferedReader(content.getContent());
		String line;
		while ((line = reader.readLine()) != null) {
			// data started is new entity
			if (line.length() > 0 && Character.isDigit(line.charAt(0))) {
				pipeline.markEOB();
			}
			pipeline.putLine(line);
		}
		reader.close();
		pipeline.close();
	}

	@Before
	public void setup() throws Exception {
		count = new AcContentCounter();
		timeComparator = new TimeableComparator();
		abort = new AbortHandler();
	}

	@Test
	public void testSampleBusinessParse() throws Exception {
		AcReaderContent content = new AcInputStreamContent(getClass()
				.getResourceAsStream("/input/sample_business.txt"));
		AcTextStreamProcessor processor = TextPatternScanner
				.createProcessor(SampleBusinessLog.class);
		processor.process(content, null, count);
		assertThat("error count", count.getErrorCount(), is(equalTo(0)));
		assertThat("success count", count.getSuccessCount(), is(equalTo(100)));
	}

	@Test
	public void testSampleBusinessPipelineParse() throws Exception {
		AcReaderContent content = new AcInputStreamContent(getClass()
				.getResourceAsStream("/input/sample_business.txt"));
		AcTextPipelineParseBuilder pipelineBuilder = TextPatternScanner
				.createPipelineParseBuilder(SampleBusinessLog.class);
		processPipeline(pipelineBuilder, content, null, count);
		assertThat("error count", count.getErrorCount(), is(equalTo(0)));
		assertThat("success count", count.getSuccessCount(), is(equalTo(100)));
	}

	@Test
	public void testSampleBusinessPipelineBlockParse() throws Exception {
		AcReaderContent content = new AcInputStreamContent(getClass()
				.getResourceAsStream("/input/sample_business.txt"));
		AcTextPipelineParseBuilder pipelineBuilder = TextPatternScanner
				.createPipelineParseBuilder(SampleBusinessLog.class);
		processPipelineBlockPerLine(pipelineBuilder, content, null, count);
		assertThat("error count", count.getErrorCount(), is(equalTo(0)));
		assertThat("success count", count.getSuccessCount(), is(equalTo(100)));
	}

	@Test
	public void testSamplePerformanceParse() throws Exception {
		AcReaderContent content = new AcInputStreamContent(getClass()
				.getResourceAsStream("/input/sample_performance.txt"));
		AcTextStreamProcessor processor = TextPatternScanner
				.createProcessor(SamplePerformanceLog.class);
		processor.process(content, null, count);
		assertThat("error count", count.getErrorCount(), is(equalTo(0)));
		assertThat("success count", count.getSuccessCount(), is(equalTo(100)));
	}

	@Test
	public void testSamplePerformancePipelineParse() throws Exception {
		AcReaderContent content = new AcInputStreamContent(getClass()
				.getResourceAsStream("/input/sample_performance.txt"));
		AcTextPipelineParseBuilder pipelineBuilder = TextPatternScanner
				.createPipelineParseBuilder(SamplePerformanceLog.class);
		processPipeline(pipelineBuilder, content, null, count);
		assertThat("error count", count.getErrorCount(), is(equalTo(0)));
		assertThat("success count", count.getSuccessCount(), is(equalTo(100)));
	}

	@Test
	public void testSamplePerformancePipelineBlockParse() throws Exception {
		AcReaderContent content = new AcInputStreamContent(getClass()
				.getResourceAsStream("/input/sample_performance.txt"));
		AcTextPipelineParseBuilder pipelineBuilder = TextPatternScanner
				.createPipelineParseBuilder(SamplePerformanceLog.class);
		processPipelineBlockPerLine(pipelineBuilder, content, null, count);
		assertThat("error count", count.getErrorCount(), is(equalTo(0)));
		assertThat("success count", count.getSuccessCount(), is(equalTo(100)));
	}

	@Test
	public void testSamplePortalPerformanceParse() throws Exception {
		AcReaderContent content = new AcInputStreamContent(getClass()
				.getResourceAsStream("/input/sample_portal_performance.txt"));
		AcTextStreamProcessor processor = TextPatternScanner
				.createProcessor(SamplePortalPerformanceLog.class);
		processor.process(content, null, count);
		assertThat("error count", count.getErrorCount(), is(equalTo(0)));
		assertThat("success count", count.getSuccessCount(), is(equalTo(100)));
	}

	@Test
	public void testSamplePortalPerformancePipelineParse() throws Exception {
		AcReaderContent content = new AcInputStreamContent(getClass()
				.getResourceAsStream("/input/sample_portal_performance.txt"));
		AcTextPipelineParseBuilder pipelineBuilder = TextPatternScanner
				.createPipelineParseBuilder(SamplePortalPerformanceLog.class);
		processPipeline(pipelineBuilder, content, null, count);
		assertThat("error count", count.getErrorCount(), is(equalTo(0)));
		assertThat("success count", count.getSuccessCount(), is(equalTo(100)));
	}

	@Test
	public void testSamplePortalPerformancePipelineBlockParse()
			throws Exception {
		AcReaderContent content = new AcInputStreamContent(getClass()
				.getResourceAsStream("/input/sample_portal_performance.txt"));
		AcTextPipelineParseBuilder pipelineBuilder = TextPatternScanner
				.createPipelineParseBuilder(SamplePortalPerformanceLog.class);
		processPipelineBlockPerLine(pipelineBuilder, content, null, count);
		assertThat("error count", count.getErrorCount(), is(equalTo(0)));
		assertThat("success count", count.getSuccessCount(), is(equalTo(100)));
	}

	@Test
	public void testSamplePortalParse() throws Exception {
		AcReaderContent content = new AcInputStreamContent(getClass()
				.getResourceAsStream("/input/sample_portal.txt"));
		AcTextStreamProcessor processor = TextPatternScanner
				.createProcessor(SamplePortalLog.class);
		processor.process(content, null, count);
		assertThat("error count", count.getErrorCount(), is(equalTo(0)));
		assertThat("success count", count.getSuccessCount(), is(equalTo(4)));
	}

	@Test
	public void testSamplePortalPipelineParse() throws Exception {
		AcReaderContent content = new AcInputStreamContent(getClass()
				.getResourceAsStream("/input/sample_portal.txt"));
		AcTextPipelineParseBuilder pipelineBuilder = TextPatternScanner
				.createPipelineParseBuilder(SamplePortalLog.class);
		processPipeline(pipelineBuilder, content, null, count);
		assertThat("error count", count.getErrorCount(), is(equalTo(0)));
		assertThat("success count", count.getSuccessCount(), is(equalTo(4)));
	}

	@Test
	public void testSamplePortalPipelineBlockErrorParse() throws Exception {
		AcReaderContent content = new AcInputStreamContent(getClass()
				.getResourceAsStream("/input/sample_portal.txt"));
		AcTextPipelineParseBuilder pipelineBuilder = TextPatternScanner
				.createPipelineParseBuilder(SamplePortalLog.class);
		processPipelineBlockPerLine(pipelineBuilder, content, null, count);
		assertThat("success count", count.getSuccessCount(), is(equalTo(4)));
		assertThat("error count", count.getErrorCount(), is(equalTo(152)));
	}

	@Test
	public void testSamplePortalPipelineBlockParse() throws Exception {
		AcReaderContent content = new AcInputStreamContent(getClass()
				.getResourceAsStream("/input/sample_portal.txt"));
		AcTextPipelineParseBuilder pipelineBuilder = TextPatternScanner
				.createPipelineParseBuilder(SamplePortalLog.class);
		processPipelineBlock(pipelineBuilder, content, null, count);
		assertThat("error count", count.getErrorCount(), is(equalTo(0)));
		assertThat("success count", count.getSuccessCount(), is(equalTo(4)));
	}

	@Test
	public void testSampleErrorParse() throws Exception {
		AcReaderContent content = new AcInputStreamContent(getClass()
				.getResourceAsStream("/input/sample_error.txt"));
		AcTextStreamProcessor processor = TextPatternScanner
				.createProcessor(SampleErrorLog.class);
		processor.process(content, null, count);
		assertThat("error count", count.getErrorCount(), is(equalTo(0)));
		assertThat("success count", count.getSuccessCount(), is(equalTo(4)));
	}

	@Test
	public void testSampleErrorParseWithMethod() throws Exception {
		AcReaderContent content = new AcInputStreamContent(getClass()
				.getResourceAsStream("/input/sample_error.txt"));
		AcTextStreamProcessor processor = TextPatternScanner
				.createProcessor(SampleErrorLog.class);
		Iterator<Object> iter = processor.iterator(content, null, null);
		int count = 0;
		while(iter.hasNext()) {
			count++;
			SampleErrorLog errorLog = (SampleErrorLog) iter.next();
			assertThat("context info", errorLog.getContextInfos(), is(notNullValue()));
		}
		assertThat("success count", count, is(equalTo(4)));
	}
	
	@Test
	public void testSampleErrorPipelineParse() throws Exception {
		AcReaderContent content = new AcInputStreamContent(getClass()
				.getResourceAsStream("/input/sample_error.txt"));
		AcTextPipelineParseBuilder pipelineBuilder = TextPatternScanner
				.createPipelineParseBuilder(SampleErrorLog.class);
		processPipeline(pipelineBuilder, content, null, count);
		assertThat("error count", count.getErrorCount(), is(equalTo(0)));
		assertThat("success count", count.getSuccessCount(), is(equalTo(4)));
	}

	@Test
	public void testSampleErrorPipelineBlockErrorParse() throws Exception {
		AcReaderContent content = new AcInputStreamContent(getClass()
				.getResourceAsStream("/input/sample_error.txt"));
		AcTextPipelineParseBuilder pipelineBuilder = TextPatternScanner
				.createPipelineParseBuilder(SampleErrorLog.class);
		processPipelineBlockPerLine(pipelineBuilder, content, null, count);
		assertThat("error count", count.getErrorCount(), is(equalTo(668)));
		assertThat("success count", count.getSuccessCount(), is(equalTo(2)));
	}

	@Test
	public void testSampleErrorPipelineBlockParse() throws Exception {
		AcReaderContent content = new AcInputStreamContent(getClass()
				.getResourceAsStream("/input/sample_error.txt"));
		AcTextPipelineParseBuilder pipelineBuilder = TextPatternScanner
				.createPipelineParseBuilder(SampleErrorLog.class);
		processPipelineBlock(pipelineBuilder, content, null, count);
		assertThat("error count", count.getErrorCount(), is(equalTo(0)));
		assertThat("success count", count.getSuccessCount(), is(equalTo(4)));
	}

	@Test
	public void testSampleErrortraceParse() throws Exception {
		AcReaderContent content = new AcInputStreamContent(getClass()
				.getResourceAsStream("/input/sample_errortrace.txt"));
		AcTextStreamProcessor processor = TextPatternScanner
				.createProcessor(SampleErrortraceLog.class);
		processor.process(content, null, count);
		assertThat("error count", count.getErrorCount(), is(equalTo(0)));
		assertThat("success count", count.getSuccessCount(), is(equalTo(8)));
	}

	@Test
	public void testSampleErrortracePipelineParse() throws Exception {
		AcReaderContent content = new AcInputStreamContent(getClass()
				.getResourceAsStream("/input/sample_errortrace.txt"));
		AcTextPipelineParseBuilder pipelineBuilder = TextPatternScanner
				.createPipelineParseBuilder(SampleErrortraceLog.class);
		processPipeline(pipelineBuilder, content, null, count);
		assertThat("error count", count.getErrorCount(), is(equalTo(0)));
		assertThat("success count", count.getSuccessCount(), is(equalTo(8)));
	}

	@Test
	public void testSampleErrortracePipelineBlockErrorParse() throws Exception {
		AcReaderContent content = new AcInputStreamContent(getClass()
				.getResourceAsStream("/input/sample_errortrace.txt"));
		AcTextPipelineParseBuilder pipelineBuilder = TextPatternScanner
				.createPipelineParseBuilder(SampleErrortraceLog.class);
		processPipelineBlockPerLine(pipelineBuilder, content, null, count);
		assertThat("error count", count.getErrorCount(), is(equalTo(826)));
		assertThat("success count", count.getSuccessCount(), is(equalTo(8)));
	}

	@Test
	public void testSampleErrortracePipelineBlockParse() throws Exception {
		AcReaderContent content = new AcInputStreamContent(getClass()
				.getResourceAsStream("/input/sample_errortrace.txt"));
		AcTextPipelineParseBuilder pipelineBuilder = TextPatternScanner
				.createPipelineParseBuilder(SampleErrortraceLog.class);
		processPipelineBlock(pipelineBuilder, content, null, count);
		assertThat("error count", count.getErrorCount(), is(equalTo(0)));
		assertThat("success count", count.getSuccessCount(), is(equalTo(8)));
	}

	@Test
	public void testAutodetectParse1() throws Exception {
		AcReaderContent content = new AcInputStreamContent(getClass()
				.getResourceAsStream("/input/sample_performance.txt"));
		AcTextStreamProcessor processor = TextPatternScanner
				.createAutodetectProcessor(SampleErrortraceLog.class,
						SampleErrorLog.class, SamplePortalPerformanceLog.class,
						SampleBusinessLog.class, SamplePerformanceLog.class);
		AcContentCounter handler = new AcContentCounter() {

			@Override
			public void handle(Object object, AcContentLine contentLine)
					throws AcLoadException {
				if (object instanceof SamplePerformanceLog) {
					super.handle(object, contentLine);
				} else {
					throw new AcLoadException(new ClassCastException(object
							.getClass().toString()));
				}
			}

		};
		processor.process(content, null, handler);
		assertThat("error count", handler.getErrorCount(), is(equalTo(0)));
		assertThat("success count", handler.getSuccessCount(), is(equalTo(100)));
	}

	@Test
	public void testAutodetectParse2() throws Exception {
		AcReaderContent content = new AcInputStreamContent(getClass()
				.getResourceAsStream("/input/sample_errortrace.txt"));
		AcTextStreamProcessor processor = TextPatternScanner
				.createAutodetectProcessor(SampleErrorLog.class,
						SampleErrortraceLog.class,
						SamplePortalPerformanceLog.class,
						SampleBusinessLog.class, SamplePerformanceLog.class);
		AcContentCounter handler = new AcContentCounter() {

			@Override
			public void handle(Object object, AcContentLine contentLine)
					throws AcLoadException {
				if (object instanceof SampleErrortraceLog) {
					super.handle(object, contentLine);
				} else {
					throw new AcLoadException(new ClassCastException(object
							.getClass().toString()));
				}
			}

		};
		processor.process(content, null, handler);
		assertThat("error count", handler.getErrorCount(), is(equalTo(0)));
		assertThat("success count", handler.getSuccessCount(), is(equalTo(8)));
	}

	@Test
	public void testAutodetectParse3() throws Exception {
		AcReaderContent content = new AcInputStreamContent(getClass()
				.getResourceAsStream("/input/sample_business.txt"));
		AcTextStreamProcessor processor = TextPatternScanner
				.createAutodetectProcessor(SampleErrortraceLog.class,
						SampleErrorLog.class, SamplePortalPerformanceLog.class,
						SampleBusinessLog.class, SamplePerformanceLog.class);
		AcContentCounter handler = new AcContentCounter() {

			@Override
			public void handle(Object object, AcContentLine contentLine)
					throws AcLoadException {
				if (object instanceof SampleBusinessLog) {
					super.handle(object, contentLine);
				} else {
					throw new AcLoadException(new ClassCastException(object
							.getClass().toString()));
				}
			}

		};
		processor.process(content, null, handler);
		assertThat("error count", handler.getErrorCount(), is(equalTo(0)));
		assertThat("success count", handler.getSuccessCount(), is(equalTo(100)));
	}

	@Test
	public void testAutodetectParseIterator() throws Exception {
		AcReaderContent content = new AcInputStreamContent(getClass()
				.getResourceAsStream("/input/sample_performance.txt"));
		AcTextStreamProcessor processor = TextPatternScanner
				.createAutodetectProcessor(SampleErrortraceLog.class,
						SampleErrorLog.class, SamplePortalPerformanceLog.class,
						SampleBusinessLog.class, SamplePerformanceLog.class);
		Iterator<Object> iterator = processor.iterator(content, null, count);
		assertThat("iterator", iterator, is(notNullValue()));
		assertThat("iterator.hasNext", iterator.hasNext(), is(true));
		int index = 0;
		while (iterator.hasNext()) {
			Object object = iterator.next();
			index++;
			assertThat("type", object,
					is(instanceOf(SamplePerformanceLog.class)));
		}
		assertThat("error count", count.getErrorCount(), is(equalTo(0)));
		assertThat("success count", index, is(equalTo(100)));
	}

	@Test
	public void testAutodetectParseError() throws Exception {
		AcReaderContent content = new AcInputStreamContent(getClass()
				.getResourceAsStream("/input/sample_portal_performance.txt"));
		AcTextStreamProcessor processor = TextPatternScanner
				.createAutodetectProcessor(SampleBusinessLog.class,
						SamplePerformanceLog.class);
		processor.process(content, null, count);
		assertThat("error count", count.getErrorCount(), is(equalTo(100)));
		assertThat("success count", count.getSuccessCount(), is(equalTo(0)));
	}

	@Test
	public void testAutodetectParsePipeline1() throws Exception {
		AcReaderContent content = new AcInputStreamContent(getClass()
				.getResourceAsStream("/input/sample_performance.txt"));
		AcTextPipelineParseBuilder pipelineBuilder = TextPatternScanner
				.createAutodetectPipelineParseBuilder(
						SampleErrortraceLog.class, SampleErrorLog.class,
						SamplePortalPerformanceLog.class,
						SampleBusinessLog.class, SamplePerformanceLog.class);
		AcContentCounter handler = new AcContentCounter() {

			@Override
			public void handle(Object object, AcContentLine contentLine)
					throws AcLoadException {
				if (object instanceof SamplePerformanceLog) {
					super.handle(object, contentLine);
				} else {
					throw new AcLoadException(new ClassCastException(object
							.getClass().toString()));
				}
			}

		};
		processPipeline(pipelineBuilder, content, null, handler);
		assertThat("error count", handler.getErrorCount(), is(equalTo(0)));
		assertThat("success count", handler.getSuccessCount(), is(equalTo(100)));
	}

	@Test
	public void testAutodetectParsePipeline2() throws Exception {
		AcReaderContent content = new AcInputStreamContent(getClass()
				.getResourceAsStream("/input/sample_errortrace.txt"));
		AcTextPipelineParseBuilder pipelineBuilder = TextPatternScanner
				.createAutodetectPipelineParseBuilder(
						SampleErrorLog.class,
						SampleErrortraceLog.class,
						SamplePortalPerformanceLog.class,
						SampleBusinessLog.class, SamplePerformanceLog.class);
		AcContentCounter handler = new AcContentCounter() {

			@Override
			public void handle(Object object, AcContentLine contentLine)
					throws AcLoadException {
				if (object instanceof SampleErrortraceLog) {
					super.handle(object, contentLine);
					SampleErrortraceLog errortrace = (SampleErrortraceLog) object;
					assertThat("error trace", errortrace.getErrorMessage(),
							is(containsString("\n")));
				} else {
					throw new AcLoadException(new ClassCastException(object
							.getClass().toString()));
				}
			}

		};
		processPipeline(pipelineBuilder, content, null, handler);
		assertThat("error count", handler.getErrorCount(), is(equalTo(0)));
		assertThat("success count", handler.getSuccessCount(), is(equalTo(8)));
	}

	@Test
	public void testAutodetectParsePipeline3() throws Exception {
		AcReaderContent content = new AcInputStreamContent(getClass()
				.getResourceAsStream("/input/sample_business.txt"));
		AcTextPipelineParseBuilder pipelineBuilder = TextPatternScanner
				.createAutodetectPipelineParseBuilder(
						SampleErrortraceLog.class, SampleErrorLog.class,
						SamplePortalPerformanceLog.class,
						SampleBusinessLog.class, SamplePerformanceLog.class);
		AcContentCounter handler = new AcContentCounter() {

			@Override
			public void handle(Object object, AcContentLine contentLine)
					throws AcLoadException {
				if (object instanceof SampleBusinessLog) {
					super.handle(object, contentLine);
				} else {
					throw new AcLoadException(new ClassCastException(object
							.getClass().toString()));
				}
			}

		};
		processPipeline(pipelineBuilder, content, null, handler);
		assertThat("error count", handler.getErrorCount(), is(equalTo(0)));
		assertThat("success count", handler.getSuccessCount(), is(equalTo(100)));
	}

	@Test
	public void testAutodetectParsePipelineError() throws Exception {
		AcReaderContent content = new AcInputStreamContent(getClass()
				.getResourceAsStream("/input/sample_portal_performance.txt"));
		AcTextPipelineParseBuilder pipelineBuilder = TextPatternScanner
				.createAutodetectPipelineParseBuilder(SampleBusinessLog.class,
						SamplePerformanceLog.class);
		processPipeline(pipelineBuilder, content, null, count);
		assertThat("error count", count.getErrorCount(), is(equalTo(100)));
		assertThat("success count", count.getSuccessCount(), is(equalTo(0)));
	}

	@Test
	public void testAutodetectParsePipelineBlock1() throws Exception {
		AcReaderContent content = new AcInputStreamContent(getClass()
				.getResourceAsStream("/input/sample_performance.txt"));
		AcTextPipelineParseBuilder pipelineBuilder = TextPatternScanner
				.createAutodetectPipelineParseBuilder(
						SampleErrortraceLog.class, SampleErrorLog.class,
						SamplePortalPerformanceLog.class,
						SampleBusinessLog.class, SamplePerformanceLog.class);
		AcContentCounter handler = new AcContentCounter() {

			@Override
			public void handle(Object object, AcContentLine contentLine)
					throws AcLoadException {
				if (object instanceof SamplePerformanceLog) {
					super.handle(object, contentLine);
				} else {
					throw new AcLoadException(new ClassCastException(object
							.getClass().toString()));
				}
			}

		};
		processPipelineBlock(pipelineBuilder, content, null, handler);
		assertThat("error count", handler.getErrorCount(), is(equalTo(0)));
		assertThat("success count", handler.getSuccessCount(), is(equalTo(100)));
	}

	@Test
	public void testAutodetectParsePipelineBlock2() throws Exception {
		AcReaderContent content = new AcInputStreamContent(getClass()
				.getResourceAsStream("/input/sample_errortrace.txt"));
		AcTextPipelineParseBuilder pipelineBuilder = TextPatternScanner
				.createAutodetectPipelineParseBuilder(SampleErrorLog.class,
						SampleErrortraceLog.class,
						SamplePortalPerformanceLog.class,
						SampleBusinessLog.class, SamplePerformanceLog.class);
		AcContentCounter handler = new AcContentCounter() {

			@Override
			public void handle(Object object, AcContentLine contentLine)
					throws AcLoadException {
				if (object instanceof SampleErrortraceLog) {
					super.handle(object, contentLine);
				} else {
					throw new AcLoadException(new ClassCastException(object
							.getClass().toString()));
				}
			}

		};
		processPipelineBlock(pipelineBuilder, content, null, handler);
		assertThat("error count", handler.getErrorCount(), is(equalTo(0)));
		assertThat("success count", handler.getSuccessCount(), is(equalTo(8)));
	}

	@Test
	public void testAutodetectParsePipelineBlock3() throws Exception {
		AcReaderContent content = new AcInputStreamContent(getClass()
				.getResourceAsStream("/input/sample_business.txt"));
		AcTextPipelineParseBuilder pipelineBuilder = TextPatternScanner
				.createAutodetectPipelineParseBuilder(
						SampleErrortraceLog.class, SampleErrorLog.class,
						SamplePortalPerformanceLog.class,
						SampleBusinessLog.class, SamplePerformanceLog.class);
		AcContentCounter handler = new AcContentCounter() {

			@Override
			public void handle(Object object, AcContentLine contentLine)
					throws AcLoadException {
				if (object instanceof SampleBusinessLog) {
					super.handle(object, contentLine);
				} else {
					throw new AcLoadException(new ClassCastException(object
							.getClass().toString()));
				}
			}

		};
		processPipelineBlock(pipelineBuilder, content, null, handler);
		assertThat("error count", handler.getErrorCount(), is(equalTo(0)));
		assertThat("success count", handler.getSuccessCount(), is(equalTo(100)));
	}

	@Test
	public void testAutodetectParsePipelineBlockError() throws Exception {
		AcReaderContent content = new AcInputStreamContent(getClass()
				.getResourceAsStream("/input/sample_portal_performance.txt"));
		AcTextPipelineParseBuilder pipelineBuilder = TextPatternScanner
				.createAutodetectPipelineParseBuilder(SampleBusinessLog.class,
						SamplePerformanceLog.class);
		processPipelineBlock(pipelineBuilder, content, null, count);
		assertThat("error count", count.getErrorCount(), is(equalTo(100)));
		assertThat("success count", count.getSuccessCount(), is(equalTo(0)));
	}

	private void setupTimeableData() throws Exception {
		readBizContent();
		AcReaderContent content = new AcStringsContent(bizContent);
		AcTextStreamProcessor processor = TextPatternScanner
				.createProcessor(SampleBusinessLog.class);
		processor.process(content, null, count);
		assertThat(count.getSuccessCount(), is(equalTo(100)));
		assertThat(count.getErrorCount(), is(equalTo(0)));
		Iterator<Object> iterator = processor.iterator(content, null, count);
		int index = 0;
		while (iterator.hasNext()) {
			Date dateTime = ((Timeable) iterator.next()).getDateTime();
			if (sampleBizStartTime == null) {
				sampleBizStartTime = dateTime;
			}
			sampleBizEndTime = dateTime;
			index++;
		}
		assertThat(index, is(equalTo(100)));
		assertThat(count.getErrorCount(), is(equalTo(0)));
		assertThat(sampleBizStartTime, is(notNullValue()));
		assertThat(sampleBizEndTime, is(notNullValue()));
		assertThat(sampleBizStartTime, is(lessThan(sampleBizEndTime)));
	}

	protected void readBizContent() throws IOException {
		InputStream stream = getClass().getResourceAsStream(
				"/input/sample_business.txt");
		StringWriter writer = new StringWriter();
		InputStreamReader reader = new InputStreamReader(stream);
		char[] cbuf = new char[1024];
		int len;
		while ((len = reader.read(cbuf)) != -1) {
			writer.write(cbuf, 0, len);
		}
		stream.close();
		writer.close();
		bizContent = writer.toString();
	}

	@Test
	public void testSingleTimeableSameRange() throws Exception {
		setupTimeableData();
		AcReaderContent content = new AcInputStreamContent(getClass()
				.getResourceAsStream("/input/sample_business.txt"));
		AcTextStreamProcessor processor = TextPatternScanner.createProcessor(
				timeComparator, SampleBusinessLog.class);
		timeComparator.setStartTime(new Date(sampleBizStartTime.getTime()));
		timeComparator.setEndTime(new Date(sampleBizEndTime.getTime()));
		processor.process(content, null, count);
		assertThat("error count", count.getErrorCount(), is(equalTo(0)));
		assertThat("success count", count.getSuccessCount(), is(equalTo(100)));
		// reloadable
		content = new AcStringsContent(bizContent);
		processor.process(content, null, count);
		assertThat("error count", count.getErrorCount(), is(equalTo(0)));
		assertThat("success count", count.getSuccessCount(), is(equalTo(100)));
		// cacheable
		content = new AcCachedContent(new AcInputStreamContent(getClass()
				.getResourceAsStream("/input/sample_business.txt")));
		processor.process(content, null, count);
		assertThat("error count", count.getErrorCount(), is(equalTo(0)));
		assertThat("success count", count.getSuccessCount(), is(equalTo(100)));
	}

	@Test
	public void testSingleTimeableInRange() throws Exception {
		setupTimeableData();
		AcReaderContent content = new AcInputStreamContent(getClass()
				.getResourceAsStream("/input/sample_business.txt"));
		AcTextStreamProcessor processor = TextPatternScanner.createProcessor(
				timeComparator, SampleBusinessLog.class);
		timeComparator.setStartTime(new Date(
				sampleBizStartTime.getTime() + 2000L));
		timeComparator.setEndTime(new Date(sampleBizEndTime.getTime() - 2000L));
		processor.process(content, null, count);
		assertThat("error count", count.getErrorCount(), is(equalTo(0)));
		assertThat("success count", count.getSuccessCount(), is(equalTo(98)));
		// reloadable
		content = new AcStringsContent(bizContent);
		processor.process(content, null, count);
		assertThat("error count", count.getErrorCount(), is(equalTo(0)));
		assertThat("success count", count.getSuccessCount(), is(equalTo(98)));
		// cacheable
		content = new AcCachedContent(new AcInputStreamContent(getClass()
				.getResourceAsStream("/input/sample_business.txt")));
		processor.process(content, null, count);
		assertThat("error count", count.getErrorCount(), is(equalTo(0)));
		assertThat("success count", count.getSuccessCount(), is(equalTo(98)));
	}

	@Test
	public void testSingleTimeableCrossRange() throws Exception {
		setupTimeableData();
		AcReaderContent content = new AcInputStreamContent(getClass()
				.getResourceAsStream("/input/sample_business.txt"));
		AcTextStreamProcessor processor = TextPatternScanner.createProcessor(
				timeComparator, SampleBusinessLog.class);
		timeComparator.setStartTime(new Date(
				sampleBizStartTime.getTime() - 2000L));
		timeComparator.setEndTime(new Date(sampleBizEndTime.getTime() - 2000L));
		processor.process(content, null, count);
		assertThat("success count", count.getSuccessCount(), is(equalTo(99)));
		assertThat("error count", count.getErrorCount(), is(equalTo(0)));
		// reloadable
		content = new AcStringsContent(bizContent);
		processor.process(content, null, count);
		assertThat("success count", count.getSuccessCount(), is(equalTo(99)));
		assertThat("error count", count.getErrorCount(), is(equalTo(0)));
		// cacheable
		content = new AcCachedContent(new AcInputStreamContent(getClass()
				.getResourceAsStream("/input/sample_business.txt")));
		processor.process(content, null, count);
		assertThat("success count", count.getSuccessCount(), is(equalTo(99)));
		assertThat("error count", count.getErrorCount(), is(equalTo(0)));

		content = new AcInputStreamContent(getClass().getResourceAsStream(
				"/input/sample_business.txt"));
		timeComparator.setStartTime(new Date(
				sampleBizStartTime.getTime() + 2000L));
		timeComparator.setEndTime(new Date(sampleBizEndTime.getTime() + 2000L));
		processor.process(content, null, count);
		assertThat("success count", count.getSuccessCount(), is(equalTo(99)));
		assertThat("error count", count.getErrorCount(), is(equalTo(0)));
		// reloadable
		content = new AcStringsContent(bizContent);
		processor.process(content, null, count);
		assertThat("success count", count.getSuccessCount(), is(equalTo(99)));
		assertThat("error count", count.getErrorCount(), is(equalTo(0)));
		// cacheable
		content = new AcCachedContent(new AcInputStreamContent(getClass()
				.getResourceAsStream("/input/sample_business.txt")));
		processor.process(content, null, count);
		assertThat("success count", count.getSuccessCount(), is(equalTo(99)));
		assertThat("error count", count.getErrorCount(), is(equalTo(0)));
	}

	@Test
	public void testSingleTimeableAfterRange() throws Exception {
		setupTimeableData();
		AcReaderContent content = new AcInputStreamContent(getClass()
				.getResourceAsStream("/input/sample_business.txt"));
		AcTextStreamProcessor processor = TextPatternScanner.createProcessor(
				timeComparator, SampleBusinessLog.class);
		timeComparator.setStartTime(new Date(
				sampleBizStartTime.getTime() - 2000L));
		timeComparator
				.setEndTime(new Date(sampleBizStartTime.getTime() - 1000L));
		processor.process(content, null, count);
		assertThat("success count", count.getSuccessCount(), is(equalTo(0)));
		assertThat("error count", count.getErrorCount(), is(equalTo(0)));
		// reloadable
		content = new AcStringsContent(bizContent);
		processor.process(content, null, count);
		assertThat("success count", count.getSuccessCount(), is(equalTo(0)));
		assertThat("error count", count.getErrorCount(), is(equalTo(0)));
		// cacheable
		content = new AcCachedContent(new AcInputStreamContent(getClass()
				.getResourceAsStream("/input/sample_business.txt")));
		processor.process(content, null, count);
		assertThat("success count", count.getSuccessCount(), is(equalTo(0)));
		assertThat("error count", count.getErrorCount(), is(equalTo(0)));
	}

	@Test
	public void testSingleTimeableBeforeRange() throws Exception {
		setupTimeableData();
		AcReaderContent content = new AcInputStreamContent(getClass()
				.getResourceAsStream("/input/sample_business.txt"));
		AcTextStreamProcessor processor = TextPatternScanner.createProcessor(
				timeComparator, SampleBusinessLog.class);
		timeComparator
				.setStartTime(new Date(sampleBizEndTime.getTime() + 1000L));
		timeComparator.setEndTime(new Date(sampleBizEndTime.getTime() + 2000L));
		processor.process(content, null, count);
		assertThat("success count", count.getSuccessCount(), is(equalTo(0)));
		assertThat("error count", count.getErrorCount(), is(equalTo(0)));
		// reloadable
		content = new AcStringsContent(bizContent);
		processor.process(content, null, count);
		assertThat("success count", count.getSuccessCount(), is(equalTo(0)));
		assertThat("error count", count.getErrorCount(), is(equalTo(0)));
		// cacheable
		content = new AcCachedContent(new AcInputStreamContent(getClass()
				.getResourceAsStream("/input/sample_business.txt")));
		processor.process(content, null, count);
		assertThat("success count", count.getSuccessCount(), is(equalTo(0)));
		assertThat("error count", count.getErrorCount(), is(equalTo(0)));
	}

	@Test
	public void testAutodetectTimeableSameRange() throws Exception {
		setupTimeableData();
		AcReaderContent content = new AcInputStreamContent(getClass()
				.getResourceAsStream("/input/sample_business.txt"));
		AcTextStreamProcessor processor = TextPatternScanner
				.createAutodetectProcessor(timeComparator,
						SamplePerformanceLog.class,
						SamplePortalPerformanceLog.class,
						SampleBusinessLog.class);
		timeComparator.setStartTime(new Date(sampleBizStartTime.getTime()));
		timeComparator.setEndTime(new Date(sampleBizEndTime.getTime()));
		processor.process(content, null, count);
		assertThat("error count", count.getErrorCount(), is(equalTo(0)));
		assertThat("success count", count.getSuccessCount(), is(equalTo(100)));
		// reloadable
		content = new AcStringsContent(bizContent);
		processor.process(content, null, count);
		assertThat("error count", count.getErrorCount(), is(equalTo(0)));
		assertThat("success count", count.getSuccessCount(), is(equalTo(100)));
		// cacheable
		content = new AcCachedContent(new AcInputStreamContent(getClass()
				.getResourceAsStream("/input/sample_business.txt")));
		processor.process(content, null, count);
		assertThat("error count", count.getErrorCount(), is(equalTo(0)));
		assertThat("success count", count.getSuccessCount(), is(equalTo(100)));
	}

	@Test
	public void testAutodetectTimeableInRange() throws Exception {
		setupTimeableData();
		AcReaderContent content = new AcInputStreamContent(getClass()
				.getResourceAsStream("/input/sample_business.txt"));
		AcTextStreamProcessor processor = TextPatternScanner
				.createAutodetectProcessor(timeComparator,
						SamplePerformanceLog.class,
						SamplePortalPerformanceLog.class,
						SampleBusinessLog.class);
		timeComparator.setStartTime(new Date(
				sampleBizStartTime.getTime() + 2000L));
		timeComparator.setEndTime(new Date(sampleBizEndTime.getTime() - 2000L));
		processor.process(content, null, count);
		assertThat("error count", count.getErrorCount(), is(equalTo(0)));
		assertThat("success count", count.getSuccessCount(), is(equalTo(98)));
		// reloadable
		content = new AcStringsContent(bizContent);
		processor.process(content, null, count);
		assertThat("error count", count.getErrorCount(), is(equalTo(0)));
		assertThat("success count", count.getSuccessCount(), is(equalTo(98)));
		// cacheable
		content = new AcCachedContent(new AcInputStreamContent(getClass()
				.getResourceAsStream("/input/sample_business.txt")));
		processor.process(content, null, count);
		assertThat("error count", count.getErrorCount(), is(equalTo(0)));
		assertThat("success count", count.getSuccessCount(), is(equalTo(98)));
	}

	@Test
	public void testAutodetectTimeableCrossRange() throws Exception {
		setupTimeableData();
		AcReaderContent content = new AcInputStreamContent(getClass()
				.getResourceAsStream("/input/sample_business.txt"));
		AcTextStreamProcessor processor = TextPatternScanner
				.createAutodetectProcessor(timeComparator,
						SamplePerformanceLog.class,
						SamplePortalPerformanceLog.class,
						SampleBusinessLog.class);
		timeComparator.setStartTime(new Date(
				sampleBizStartTime.getTime() - 2000L));
		timeComparator.setEndTime(new Date(sampleBizEndTime.getTime() - 2000L));
		processor.process(content, null, count);
		assertThat("success count", count.getSuccessCount(), is(equalTo(99)));
		assertThat("error count", count.getErrorCount(), is(equalTo(0)));
		// reloadable
		content = new AcStringsContent(bizContent);
		processor.process(content, null, count);
		assertThat("success count", count.getSuccessCount(), is(equalTo(99)));
		assertThat("error count", count.getErrorCount(), is(equalTo(0)));
		// cacheable
		content = new AcCachedContent(new AcInputStreamContent(getClass()
				.getResourceAsStream("/input/sample_business.txt")));
		processor.process(content, null, count);
		assertThat("success count", count.getSuccessCount(), is(equalTo(99)));
		assertThat("error count", count.getErrorCount(), is(equalTo(0)));

		content = new AcInputStreamContent(getClass().getResourceAsStream(
				"/input/sample_business.txt"));
		timeComparator.setStartTime(new Date(
				sampleBizStartTime.getTime() + 2000L));
		timeComparator.setEndTime(new Date(sampleBizEndTime.getTime() + 2000L));
		processor.process(content, null, count);
		assertThat("success count", count.getSuccessCount(), is(equalTo(99)));
		assertThat("error count", count.getErrorCount(), is(equalTo(0)));
		// reloadable
		content = new AcStringsContent(bizContent);
		processor.process(content, null, count);
		assertThat("success count", count.getSuccessCount(), is(equalTo(99)));
		assertThat("error count", count.getErrorCount(), is(equalTo(0)));
		// cacheable
		content = new AcCachedContent(new AcInputStreamContent(getClass()
				.getResourceAsStream("/input/sample_business.txt")));
		processor.process(content, null, count);
		assertThat("success count", count.getSuccessCount(), is(equalTo(99)));
		assertThat("error count", count.getErrorCount(), is(equalTo(0)));
	}

	@Test
	public void testAutodetectTimeableAfterRange() throws Exception {
		setupTimeableData();
		AcReaderContent content = new AcInputStreamContent(getClass()
				.getResourceAsStream("/input/sample_business.txt"));
		AcTextStreamProcessor processor = TextPatternScanner
				.createAutodetectProcessor(timeComparator,
						SamplePerformanceLog.class,
						SamplePortalPerformanceLog.class,
						SampleBusinessLog.class);
		timeComparator.setStartTime(new Date(
				sampleBizStartTime.getTime() - 2000L));
		timeComparator
				.setEndTime(new Date(sampleBizStartTime.getTime() - 1000L));
		processor.process(content, null, count);
		assertThat("success count", count.getSuccessCount(), is(equalTo(0)));
		assertThat("error count", count.getErrorCount(), is(equalTo(0)));
		// reloadable
		content = new AcStringsContent(bizContent);
		processor.process(content, null, count);
		assertThat("success count", count.getSuccessCount(), is(equalTo(0)));
		assertThat("error count", count.getErrorCount(), is(equalTo(0)));
		// cacheable
		content = new AcCachedContent(new AcInputStreamContent(getClass()
				.getResourceAsStream("/input/sample_business.txt")));
		processor.process(content, null, count);
		assertThat("success count", count.getSuccessCount(), is(equalTo(0)));
		assertThat("error count", count.getErrorCount(), is(equalTo(0)));
	}

	@Test
	public void testAutodetectTimeableBeforeRange() throws Exception {
		setupTimeableData();
		AcReaderContent content = new AcInputStreamContent(getClass()
				.getResourceAsStream("/input/sample_business.txt"));
		AcTextStreamProcessor processor = TextPatternScanner
				.createAutodetectProcessor(timeComparator,
						SamplePerformanceLog.class,
						SamplePortalPerformanceLog.class,
						SampleBusinessLog.class);
		timeComparator
				.setStartTime(new Date(sampleBizEndTime.getTime() + 1000L));
		timeComparator.setEndTime(new Date(sampleBizEndTime.getTime() + 2000L));
		processor.process(content, null, count);
		assertThat("success count", count.getSuccessCount(), is(equalTo(0)));
		assertThat("error count", count.getErrorCount(), is(equalTo(0)));
		// reloadable
		content = new AcStringsContent(bizContent);
		processor.process(content, null, count);
		assertThat("success count", count.getSuccessCount(), is(equalTo(0)));
		assertThat("error count", count.getErrorCount(), is(equalTo(0)));
		// cacheable
		content = new AcCachedContent(new AcInputStreamContent(getClass()
				.getResourceAsStream("/input/sample_business.txt")));
		processor.process(content, null, count);
		assertThat("success count", count.getSuccessCount(), is(equalTo(0)));
		assertThat("error count", count.getErrorCount(), is(equalTo(0)));
	}

	private AcReaderContent prepareCachedContentForClose(final int[] status,
			final String text) {
		status[0] = 0;
		status[1] = 0;
		AcReaderContent content = new AcCachedContent(
				new AcStringsContent(text) {

					@Override
					public Reader getContent() throws IOException {
						status[0]++;
						return new FilterReader(super.getContent()) {

							@Override
							public void close() throws IOException {
								status[1]++;
								super.close();
							}

						};
					}
				});
		return content;
	}

	private AcReaderContent prepareContentForClose(final int[] status,
			final String text) {
		status[0] = 0;
		status[1] = 0;
		AcReaderContent content = new AcStringsContent(text) {

			@Override
			public Reader getContent() throws IOException {
				status[0]++;
				return new FilterReader(super.getContent()) {

					@Override
					public void close() throws IOException {
						status[1]++;
						super.close();
					}

				};
			}
		};
		return content;
	}

	@Test
	public void testSimpleParseReaderStatus() throws Exception {
		readBizContent();
		int[] status = new int[2];
		AcReaderContent content = prepareContentForClose(status, bizContent);
		AcTextStreamProcessor processor = TextPatternScanner
				.createProcessor(SampleBusinessLog.class);
		processor.process(content, null, count);
		assertThat("success count", count.getSuccessCount(), is(equalTo(100)));
		assertThat("error count", count.getErrorCount(), is(equalTo(0)));
		assertThat("open count", status[0], is(equalTo(1)));
		assertThat("close count", status[1], is(equalTo(1)));
	}

	@Test
	public void testSimpleParseCacheReaderStatus() throws Exception {
		readBizContent();
		int[] status = new int[2];
		AcReaderContent content = prepareCachedContentForClose(status,
				bizContent);
		AcTextStreamProcessor processor = TextPatternScanner
				.createProcessor(SampleBusinessLog.class);
		processor.process(content, null, count);
		assertThat("success count", count.getSuccessCount(), is(equalTo(100)));
		assertThat("error count", count.getErrorCount(), is(equalTo(0)));
		assertThat("open count", status[0], is(equalTo(1)));
		assertThat("close count", status[1], is(equalTo(1)));
	}

	@Test
	public void testAutodetectParseReaderStatus() throws Exception {
		readBizContent();
		int[] status = new int[2];
		AcReaderContent content = prepareContentForClose(status, bizContent);
		AcTextStreamProcessor processor = TextPatternScanner
				.createAutodetectProcessor(SamplePerformanceLog.class,
						SamplePortalPerformanceLog.class,
						SampleBusinessLog.class);
		processor.process(content, null, count);
		assertThat("success count", count.getSuccessCount(), is(equalTo(100)));
		assertThat("error count", count.getErrorCount(), is(equalTo(0)));
		assertThat("open count", status[0], is(equalTo(2)));
		assertThat("close count", status[1], is(equalTo(2)));
	}

	@Test
	public void testAutodetectParseCacheReaderStatus() throws Exception {
		readBizContent();
		int[] status = new int[2];
		AcReaderContent content = prepareCachedContentForClose(status,
				bizContent);
		AcTextStreamProcessor processor = TextPatternScanner
				.createAutodetectProcessor(SamplePerformanceLog.class,
						SamplePortalPerformanceLog.class,
						SampleBusinessLog.class);
		processor.process(content, null, count);
		assertThat("success count", count.getSuccessCount(), is(equalTo(100)));
		assertThat("error count", count.getErrorCount(), is(equalTo(0)));
		assertThat("open count", status[0], is(equalTo(1)));
		assertThat("close count", status[1], is(equalTo(1)));
	}

	@Test
	public void testTimableParseReaderStatus() throws Exception {
		setupTimeableData();
		int[] status = new int[2];
		AcReaderContent content = prepareContentForClose(status, bizContent);
		AcTextStreamProcessor processor = TextPatternScanner.createProcessor(
				timeComparator, SampleBusinessLog.class);
		processor.process(content, null, count);
		assertThat("success count", count.getSuccessCount(), is(equalTo(100)));
		assertThat("error count", count.getErrorCount(), is(equalTo(0)));
		assertThat("open count", status[0], is(equalTo(2)));
		assertThat("close count", status[1], is(equalTo(2)));
	}

	@Test
	public void testTimableParseCacheReaderStatus() throws Exception {
		setupTimeableData();
		int[] status = new int[2];
		AcReaderContent content = prepareCachedContentForClose(status,
				bizContent);
		AcTextStreamProcessor processor = TextPatternScanner.createProcessor(
				timeComparator, SampleBusinessLog.class);
		processor.process(content, null, count);
		assertThat("success count", count.getSuccessCount(), is(equalTo(100)));
		assertThat("error count", count.getErrorCount(), is(equalTo(0)));
		assertThat("open count", status[0], is(equalTo(1)));
		assertThat("close count", status[1], is(equalTo(1)));
	}

	@Test
	public void testAutodetectTimableParseReaderStatus() throws Exception {
		setupTimeableData();
		int[] status = new int[2];
		AcReaderContent content = prepareContentForClose(status, bizContent);
		AcTextStreamProcessor processor = TextPatternScanner
				.createAutodetectProcessor(timeComparator,
						SamplePerformanceLog.class,
						SamplePortalPerformanceLog.class,
						SampleBusinessLog.class);
		processor.process(content, null, count);
		assertThat("success count", count.getSuccessCount(), is(equalTo(100)));
		assertThat("error count", count.getErrorCount(), is(equalTo(0)));
		assertThat("open count", status[0], is(equalTo(3)));
		assertThat("close count", status[1], is(equalTo(3)));
	}

	@Test
	public void testAutodetectTimableParseCacheReaderStatus() throws Exception {
		setupTimeableData();
		int[] status = new int[2];
		AcReaderContent content = prepareCachedContentForClose(status,
				bizContent);
		AcTextStreamProcessor processor = TextPatternScanner
				.createAutodetectProcessor(timeComparator,
						SamplePerformanceLog.class,
						SamplePortalPerformanceLog.class,
						SampleBusinessLog.class);
		processor.process(content, null, count);
		assertThat("success count", count.getSuccessCount(), is(equalTo(100)));
		assertThat("error count", count.getErrorCount(), is(equalTo(0)));
		assertThat("open count", status[0], is(equalTo(1)));
		assertThat("close count", status[1], is(equalTo(1)));
	}

	@Test
	public void testSimpleParseAbortReaderStatus() throws Exception {
		readBizContent();
		int[] status = new int[2];
		AcReaderContent content = prepareContentForClose(status, bizContent
				+ "\nthis is error\n" + bizContent);
		AcTextStreamProcessor processor = TextPatternScanner
				.createProcessor(SampleBusinessLog.class);
		try {
			processor.process(content, null, abort);
			fail();
		} catch (Exception e) {
		}
		assertThat("success count", abort.getSuccessCount(), is(equalTo(100)));
		assertThat("error count", abort.getErrorCount(), is(equalTo(0)));
		assertThat("open count", status[0], is(equalTo(1)));
		assertThat("close count", status[1], is(equalTo(1)));
	}

	@Test
	public void testSimpleParseCacheAbortReaderStatus() throws Exception {
		readBizContent();
		int[] status = new int[2];
		AcReaderContent content = prepareCachedContentForClose(status,
				bizContent + "\nthis is error\n" + bizContent);
		AcTextStreamProcessor processor = TextPatternScanner
				.createProcessor(SampleBusinessLog.class);
		try {
			processor.process(content, null, abort);
			fail();
		} catch (Exception e) {
		}
		assertThat("success count", abort.getSuccessCount(), is(equalTo(100)));
		assertThat("error count", abort.getErrorCount(), is(equalTo(0)));
		assertThat("open count", status[0], is(equalTo(1)));
		assertThat("close count", status[1], is(equalTo(1)));
	}

	@Test
	public void testAutodetectParseAbortReaderStatus() throws Exception {
		readBizContent();
		int[] status = new int[2];
		AcReaderContent content = prepareContentForClose(status, bizContent
				+ "\nthis is error\n" + bizContent);
		AcTextStreamProcessor processor = TextPatternScanner
				.createAutodetectProcessor(SamplePerformanceLog.class,
						SamplePortalPerformanceLog.class,
						SampleBusinessLog.class);
		try {
			processor.process(content, null, abort);
			fail();
		} catch (Exception e) {
		}
		assertThat("success count", abort.getSuccessCount(), is(equalTo(100)));
		assertThat("error count", abort.getErrorCount(), is(equalTo(0)));
		assertThat("open count", status[0], is(equalTo(2)));
		assertThat("close count", status[1], is(equalTo(2)));
	}

	@Test
	public void testAutodetectParseCacheAbortReaderStatus() throws Exception {
		readBizContent();
		int[] status = new int[2];
		AcReaderContent content = prepareCachedContentForClose(status,
				bizContent + "\nthis is error\n" + bizContent);
		AcTextStreamProcessor processor = TextPatternScanner
				.createAutodetectProcessor(SamplePerformanceLog.class,
						SamplePortalPerformanceLog.class,
						SampleBusinessLog.class);
		try {
			processor.process(content, null, abort);
			fail();
		} catch (Exception e) {
		}
		assertThat("success count", abort.getSuccessCount(), is(equalTo(100)));
		assertThat("error count", abort.getErrorCount(), is(equalTo(0)));
		assertThat("open count", status[0], is(equalTo(1)));
		assertThat("close count", status[1], is(equalTo(1)));
	}

	@Test
	public void testTimableParseAbortReaderStatus() throws Exception {
		setupTimeableData();
		int[] status = new int[2];
		AcReaderContent content = prepareContentForClose(status, bizContent
				+ "\nthis is error\n" + bizContent);
		AcTextStreamProcessor processor = TextPatternScanner.createProcessor(
				timeComparator, SampleBusinessLog.class);
		try {
			processor.process(content, null, abort);
			fail();
		} catch (Exception e) {
		}
		assertThat("success count", abort.getSuccessCount(), is(equalTo(100)));
		assertThat("error count", abort.getErrorCount(), is(equalTo(0)));
		assertThat("open count", status[0], is(equalTo(2)));
		assertThat("close count", status[1], is(equalTo(2)));
	}

	@Test
	public void testTimeableParseCacheAbortReaderStatus() throws Exception {
		setupTimeableData();
		int[] status = new int[2];
		AcReaderContent content = prepareCachedContentForClose(status,
				bizContent + "\nthis is error\n" + bizContent);
		AcTextStreamProcessor processor = TextPatternScanner.createProcessor(
				timeComparator, SampleBusinessLog.class);
		try {
			processor.process(content, null, abort);
			fail();
		} catch (Exception e) {
		}
		assertThat("success count", abort.getSuccessCount(), is(equalTo(100)));
		assertThat("error count", abort.getErrorCount(), is(equalTo(0)));
		assertThat("open count", status[0], is(equalTo(1)));
		assertThat("close count", status[1], is(equalTo(1)));
	}

	@Test
	public void testAutodetectTimeableParseAbortReaderStatus() throws Exception {
		setupTimeableData();
		int[] status = new int[2];
		AcReaderContent content = prepareContentForClose(status, bizContent
				+ "\nthis is error\n" + bizContent);
		AcTextStreamProcessor processor = TextPatternScanner
				.createAutodetectProcessor(timeComparator,
						SamplePerformanceLog.class,
						SamplePortalPerformanceLog.class,
						SampleBusinessLog.class);
		try {
			processor.process(content, null, abort);
			fail();
		} catch (Exception e) {
		}
		assertThat("success count", abort.getSuccessCount(), is(equalTo(100)));
		assertThat("error count", abort.getErrorCount(), is(equalTo(0)));
		assertThat("open count", status[0], is(equalTo(3)));
		assertThat("close count", status[1], is(equalTo(3)));
	}

	@Test
	public void testAutodetectTimeableParseCacheAbortReaderStatus()
			throws Exception {
		setupTimeableData();
		int[] status = new int[2];
		AcReaderContent content = prepareCachedContentForClose(status,
				bizContent + "\nthis is error\n" + bizContent);
		AcTextStreamProcessor processor = TextPatternScanner
				.createAutodetectProcessor(timeComparator,
						SamplePerformanceLog.class,
						SamplePortalPerformanceLog.class,
						SampleBusinessLog.class);
		try {
			processor.process(content, null, abort);
			fail();
		} catch (Exception e) {
		}
		assertThat("success count", abort.getSuccessCount(), is(equalTo(100)));
		assertThat("error count", abort.getErrorCount(), is(equalTo(0)));
		assertThat("open count", status[0], is(equalTo(1)));
		assertThat("close count", status[1], is(equalTo(1)));
	}
}
