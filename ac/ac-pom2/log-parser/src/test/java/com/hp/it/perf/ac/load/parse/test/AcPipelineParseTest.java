package com.hp.it.perf.ac.load.parse.test;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import java.io.BufferedReader;

import org.junit.Before;
import org.junit.Test;

import com.hp.it.perf.ac.load.common.AcPredicate;
import com.hp.it.perf.ac.load.content.AcContentCounter;
import com.hp.it.perf.ac.load.content.AcContentHandler;
import com.hp.it.perf.ac.load.content.AcContentLine;
import com.hp.it.perf.ac.load.content.AcReaderContent;
import com.hp.it.perf.ac.load.content.AcStringsContent;
import com.hp.it.perf.ac.load.parse.AcTextPipeline;
import com.hp.it.perf.ac.load.parse.AcTextPipelineParseBuilder;
import com.hp.it.perf.ac.load.parse.impl.DelimsMapProcessConfig;
import com.hp.it.perf.ac.load.parse.impl.TextPatternScanner;

public class AcPipelineParseTest {

	private AcTextPipelineParseBuilder pipelineBuilder;
	private AcContentCounter count;

	private static final String Normal_String = "| 05/07/2012, 05:26:43 | 13363684004186244 | 13363684004186244 | psiSupportOptions | RENDER_PHASE | 0.040 | {Controller(com.hp.it.sp4ts.psi.web.optionsmenu.OptionsMenuController):0.036},{View(psiSupportOptions):0.004} | message";
	private static final String MultiLine_String = "| 05/07/2012, 05:26:42 | 13363684004186244 | 13363684004186244 | multip\nline | RENDER_PHASE | 0.040 | {Controller(com.hp.it.sp4ts.psi.web.optionsmenu.\nOptionsMenuController):0.036},{View(psiSupportOptions):0.004} | abc\ndef\n";
	private static final String DelimIn_String = "| 05/07/2012, 05:26:41 | 13363684004186238 | 13363684004186238 | psiSupportOptions | DUMMY | _PHASE | 0.040 | {Controller(com.hp.it.sp4ts.psi.web.op | tionsmenu.OptionsMenuController):0.036},{View(psiSupportOptions):0.004} | abc | def";
	private static final String RequiredField_String = "| 05/07/2012, 05:26:43 | 13363684004186244 | 13363684004186244 | psiSupportOptions | RENDER_PHASE | 0.040 |  | ";
	private static final String ErrorFull_String = "This is the error text";
	private static final String ErrorDate_String = "| 05/07/2012 | 05:26:43 | 13363684004186244 | 13363684004186244 | psiSupportOptions | RENDER_PHASE | 0.040 |  | ";
	private static final String ErrorNumber_String = "| 05/07/2012, 05:26:43 | 13363684+004186244 | 13363684004186244 | psiSupportOptions | RENDER_PHASE | 0.040 |  | ";

	@Before
	public void setup() {
		pipelineBuilder = TextPatternScanner
				.createPipelineParseBuilder(AcLoadTestBeanA.class);
		count = new AcContentCounter();
	}

	private void process(AcReaderContent content,
			AcPredicate<? super AcContentLine> filter, AcContentHandler handler)
			throws Exception {
		AcTextPipeline pipeline = pipelineBuilder.createPipeline(filter,
				handler);
		pipeline.prepare(content.getMetadata());
		BufferedReader reader = new BufferedReader(content.getContent());
		String line;
		while ((line = reader.readLine()) != null) {
			pipeline.putLine(line);
		}
		reader.close();
		pipeline.close();
	}

	private void processBlock(AcReaderContent content,
			AcPredicate<? super AcContentLine> filter, AcContentHandler handler)
			throws Exception {
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

	@Test
	public void testParseSuccess() throws Exception {
		AcReaderContent content = new AcStringsContent(Normal_String);
		process(content, null, count);
		assertThat("error count", count.getErrorCount(), is(equalTo(0)));
		assertThat("success count", count.getSuccessCount(), is(equalTo(1)));
	}

	@Test
	public void testParseStepSuccess() throws Exception {
		AcReaderContent content = new AcStringsContent(Normal_String);
		AcTextPipeline pipeline = pipelineBuilder.createPipeline(null,
				count);
		pipeline.prepare(content.getMetadata());
		assertThat("error count", count.getErrorCount(), is(equalTo(0)));
		assertThat("success count", count.getSuccessCount(), is(equalTo(0)));
		pipeline.putLine(Normal_String);
		assertThat("error count", count.getErrorCount(), is(equalTo(0)));
		assertThat("success count", count.getSuccessCount(), is(equalTo(0)));
		pipeline.close();
		assertThat("error count", count.getErrorCount(), is(equalTo(0)));
		assertThat("success count", count.getSuccessCount(), is(equalTo(1)));
	}

	@Test
	public void testParseBlockSuccess() throws Exception {
		AcReaderContent content = new AcStringsContent(Normal_String);
		processBlock(content, null, count);
		assertThat("error count", count.getErrorCount(), is(equalTo(0)));
		assertThat("success count", count.getSuccessCount(), is(equalTo(1)));
	}
	
	@Test
	public void testParseBlockStepSuccess() throws Exception {
		AcReaderContent content = new AcStringsContent(Normal_String);
		AcTextPipeline pipeline = pipelineBuilder.createPipeline(null,
				count);
		pipeline.prepare(content.getMetadata());
		assertThat("error count", count.getErrorCount(), is(equalTo(0)));
		assertThat("success count", count.getSuccessCount(), is(equalTo(0)));
		pipeline.putLine(Normal_String);
		assertThat("error count", count.getErrorCount(), is(equalTo(0)));
		assertThat("success count", count.getSuccessCount(), is(equalTo(0)));
		pipeline.markEOB();
		assertThat("error count", count.getErrorCount(), is(equalTo(0)));
		assertThat("success count", count.getSuccessCount(), is(equalTo(1)));
		pipeline.close();
		assertThat("error count", count.getErrorCount(), is(equalTo(0)));
		assertThat("success count", count.getSuccessCount(), is(equalTo(1)));
	}

	@Test
	public void testParseError() throws Exception {
		AcReaderContent content = new AcStringsContent(ErrorFull_String);
		process(content, null, count);
		assertThat("error count", count.getErrorCount(), is(equalTo(1)));
		assertThat("success count", count.getSuccessCount(), is(equalTo(0)));
	}

	@Test
	public void testParseBlockError() throws Exception {
		AcReaderContent content = new AcStringsContent(ErrorFull_String);
		processBlock(content, null, count);
		assertThat("error count", count.getErrorCount(), is(equalTo(1)));
		assertThat("success count", count.getSuccessCount(), is(equalTo(0)));
	}

	@Test
	public void testParseTwoSuccess() throws Exception {
		AcReaderContent content = new AcStringsContent(Normal_String,
				Normal_String);
		process(content, null, count);
		assertThat("error count", count.getErrorCount(), is(equalTo(0)));
		assertThat("success count", count.getSuccessCount(), is(equalTo(2)));
	}

	@Test
	public void testParseTwoBlockStepSuccess() throws Exception {
		AcReaderContent content = new AcStringsContent(Normal_String,
				Normal_String);
		AcTextPipeline pipeline = pipelineBuilder.createPipeline(null,
				count);
		pipeline.prepare(content.getMetadata());
		pipeline.putLine(Normal_String);
		pipeline.markEOB();
		assertThat("error count", count.getErrorCount(), is(equalTo(0)));
		assertThat("success count", count.getSuccessCount(), is(equalTo(1)));
		pipeline.putLine(Normal_String);
		pipeline.markEOB();
		assertThat("error count", count.getErrorCount(), is(equalTo(0)));
		assertThat("success count", count.getSuccessCount(), is(equalTo(2)));
		pipeline.close();
		assertThat("error count", count.getErrorCount(), is(equalTo(0)));
		assertThat("success count", count.getSuccessCount(), is(equalTo(2)));
	}

	@Test
	public void testMultiLineParseSuccess() throws Exception {
		AcReaderContent content = new AcStringsContent(MultiLine_String);
		process(content, null, count);
		assertThat("error count", count.getErrorCount(), is(equalTo(0)));
		assertThat("success count", count.getSuccessCount(), is(equalTo(1)));
	}

	@Test
	public void testMultiLineAsOneLineParseSuccess() throws Exception {
		AcReaderContent content = new AcStringsContent(MultiLine_String);
		AcTextPipeline pipeline = pipelineBuilder.createPipeline(null,
				count);
		pipeline.prepare(content.getMetadata());
		pipeline.putLine(MultiLine_String);
		pipeline.close();
		assertThat("error count", count.getErrorCount(), is(equalTo(0)));
		assertThat("success count", count.getSuccessCount(), is(equalTo(1)));
	}

	@Test
	public void testMultiLineBlockStepParseSuccess() throws Exception {
		AcReaderContent content = new AcStringsContent(MultiLine_String);
		AcTextPipeline pipeline = pipelineBuilder.createPipeline(null,
				count);
		pipeline.prepare(content.getMetadata());
		pipeline.putLine(MultiLine_String);
		pipeline.markEOB();
		assertThat("error count", count.getErrorCount(), is(equalTo(0)));
		assertThat("success count", count.getSuccessCount(), is(equalTo(1)));
		pipeline.close();
		assertThat("error count", count.getErrorCount(), is(equalTo(0)));
		assertThat("success count", count.getSuccessCount(), is(equalTo(1)));
	}

	@Test
	public void testContainsDelimParseSuccess() throws Exception {
		AcReaderContent content = new AcStringsContent(DelimIn_String);
		process(content, null, count);
		assertThat("error count", count.getErrorCount(), equalTo(0));
		assertThat("success count", count.getSuccessCount(), equalTo(1));
	}

	@Test
	public void testContainsDelimBlockParseSuccess() throws Exception {
		AcReaderContent content = new AcStringsContent(DelimIn_String);
		processBlock(content, null, count);
		assertThat("error count", count.getErrorCount(), equalTo(0));
		assertThat("success count", count.getSuccessCount(), equalTo(1));
	}

	@Test
	public void testRequiredFieldsParseSuccess() throws Exception {
		AcReaderContent content = new AcStringsContent(RequiredField_String);
		process(content, null, count);
		assertThat("error count", count.getErrorCount(), equalTo(0));
		assertThat("success count", count.getSuccessCount(), equalTo(1));
	}

	@Test
	public void testSimpleMapParseSuccess() throws Exception {
		pipelineBuilder = DelimsMapProcessConfig
				.createPipelineBuilder("| {DATE_TIME} | {TRANSACTION_ID} | {SESSION_ID} | {PORTLET_NAME} | {PORTLET_METHOD} | {DURATION} | {PERF_DETAIL_LIST} | {MESSAGE}");
		process(new AcStringsContent(Normal_String), null, count);
		assertThat("error count", count.getErrorCount(), equalTo(0));
		assertThat("success count", count.getSuccessCount(), equalTo(1));
	}

	@Test
	public void testParseDateError() throws Exception {
		AcReaderContent content = new AcStringsContent(ErrorDate_String);
		process(content, null, count);
		assertThat("error count", count.getErrorCount(), is(equalTo(1)));
		assertThat("success count", count.getSuccessCount(), is(equalTo(0)));
	}

	@Test
	public void testParseNumberError() throws Exception {
		AcReaderContent content = new AcStringsContent(ErrorNumber_String);
		process(content, null, count);
		assertThat("error count", count.getErrorCount(), is(equalTo(1)));
		assertThat("success count", count.getSuccessCount(), is(equalTo(0)));
	}

	@Test
	public void testFilterParse() throws Exception {
		AcReaderContent content = new AcStringsContent(Normal_String,
				DelimIn_String, MultiLine_String);
		process(content, new AcPredicate<AcContentLine>() {

			@Override
			public boolean apply(AcContentLine data) {
				return !data.getCurrentLines().contains(" psiSupportOptions ");
			}
		}, count);
		assertThat("error count", count.getErrorCount(), is(equalTo(0)));
		assertThat("success count", count.getSuccessCount(), is(equalTo(1)));
	}

	@Test
	public void testFilterBlockStepParse() throws Exception {
		AcReaderContent content = new AcStringsContent(Normal_String,
				DelimIn_String, MultiLine_String);
		AcTextPipeline pipeline = pipelineBuilder.createPipeline(new AcPredicate<AcContentLine>() {

			@Override
			public boolean apply(AcContentLine data) {
				return !data.getCurrentLines().contains(" psiSupportOptions ");
			}
		},
				count);
		pipeline.prepare(content.getMetadata());
		pipeline.putLine(Normal_String);
		pipeline.markEOB();
		assertThat("error count", count.getErrorCount(), is(equalTo(0)));
		assertThat("success count", count.getSuccessCount(), is(equalTo(0)));
		pipeline.putLine(DelimIn_String);
		pipeline.markEOB();
		assertThat("error count", count.getErrorCount(), is(equalTo(0)));
		assertThat("success count", count.getSuccessCount(), is(equalTo(0)));
		pipeline.putLine(MultiLine_String);
		pipeline.markEOB();
		assertThat("error count", count.getErrorCount(), is(equalTo(0)));
		assertThat("success count", count.getSuccessCount(), is(equalTo(1)));
		pipeline.close();
		assertThat("success count", count.getSuccessCount(), is(equalTo(1)));
		assertThat("error count", count.getErrorCount(), is(equalTo(0)));
	}

}
