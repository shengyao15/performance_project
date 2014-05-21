package com.hp.it.perf.ac.load.parse.test;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;

import com.hp.it.perf.ac.load.common.AcPredicate;
import com.hp.it.perf.ac.load.content.AcReaderContent;
import com.hp.it.perf.ac.load.content.AcContentCounter;
import com.hp.it.perf.ac.load.content.AcContentLine;
import com.hp.it.perf.ac.load.content.AcStringsContent;
import com.hp.it.perf.ac.load.parse.AcTextStreamProcessor;
import com.hp.it.perf.ac.load.parse.impl.DelimsMapProcessConfig;
import com.hp.it.perf.ac.load.parse.impl.TextPatternScanner;

public class AcLoadParseTest {

	private AcTextStreamProcessor processor;
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
		processor = TextPatternScanner.createProcessor(AcLoadTestBeanA.class);
		count = new AcContentCounter();
	}

	@Test
	public void testParseSuccess() throws Exception {
		AcReaderContent content = new AcStringsContent(Normal_String);
		processor.process(content, null, count);
		assertThat("error count", count.getErrorCount(), is(equalTo(0)));
		assertThat("success count", count.getSuccessCount(), is(equalTo(1)));
	}

	@Test
	public void testParseError() throws Exception {
		AcReaderContent content = new AcStringsContent(ErrorFull_String);
		processor.process(content, null, count);
		assertThat("error count", count.getErrorCount(), is(equalTo(1)));
		assertThat("success count", count.getSuccessCount(), is(equalTo(0)));
	}

	@Test
	public void testParseTwoSuccess() throws Exception {
		AcReaderContent content = new AcStringsContent(Normal_String, Normal_String);
		processor.process(content, null, count);
		assertThat("error count", count.getErrorCount(), is(equalTo(0)));
		assertThat("success count", count.getSuccessCount(), is(equalTo(2)));
	}

	@Test
	public void testParseIteratorSuccss() throws Exception {
		AcReaderContent content = new AcStringsContent(Normal_String);
		Iterator<Object> iterator = processor.iterator(content, null, count);
		assertThat("iterator", iterator, is(notNullValue()));
		assertThat("hasNext", iterator.hasNext(), is(equalTo(true)));
		int success = 0;
		while (iterator.hasNext()) {
			Object bean = iterator.next();
			success++;
			assertThat("bean", bean, is(instanceOf(AcLoadTestBeanA.class)));
			assertThat("bean.toString", bean,
					hasToString(containsString("RENDER_PHASE")));
			AcLoadTestBeanA beanA = (AcLoadTestBeanA) bean;
			assertThat("bean.detail", beanA.getDetailList(),
					hasItemInArray(anything()));
			assertThat("bean.detail", beanA.getDetailList().length,
					is(equalTo(2)));
			assertThat("bean.detail.toString", beanA.getDetailList()[0],
					hasToString(containsString("Controller")));
		}
		assertThat("success count", success, is(equalTo(1)));
		assertThat("error count", count.getErrorCount(), is(equalTo(0)));
	}

	@Test
	public void testParseIteratorError() throws Exception {
		AcReaderContent content = new AcStringsContent(ErrorFull_String);
		Iterator<Object> iterator = processor.iterator(content, null, count);
		assertThat("iterator", iterator, is(notNullValue()));
		assertThat("hasNext", iterator.hasNext(), is(equalTo(false)));
		assertThat("error count", count.getErrorCount(), is(equalTo(1)));
	}

	@Test
	public void testMultiLineParseSuccess() throws Exception {
		AcReaderContent content = new AcStringsContent(MultiLine_String);
		processor.process(content, null, count);
		assertThat("error count", count.getErrorCount(), is(equalTo(0)));
		assertThat("success count", count.getSuccessCount(), is(equalTo(1)));
	}

	@Test
	public void testMultiLineParseSuccessWithMutliLines() throws Exception {
		AcReaderContent content = new AcStringsContent(MultiLine_String);
		Iterator<Object> iterator = processor.iterator(content, null, count);
		assertThat("iterator", iterator, is(notNullValue()));
		assertThat("hasNext", iterator.hasNext(), is(equalTo(true)));
		int success = 0;
		while (iterator.hasNext()) {
			Object bean = iterator.next();
			success++;
			assertThat("bean", bean, is(instanceOf(AcLoadTestBeanA.class)));
			AcLoadTestBeanA beanA = (AcLoadTestBeanA) bean;
			assertThat("multi lines", beanA.getMessage(),
					hasToString(containsString("\n")));
			assertThat("2 multi lines", beanA.getMessage().split("\n").length,
					is(equalTo(2)));
		}
		assertThat("error count", count.getErrorCount(), is(equalTo(0)));
		assertThat("success count", success, is(equalTo(1)));
	}

	@Test
	public void testContainsDelimParseSuccess() throws Exception {
		AcReaderContent content = new AcStringsContent(DelimIn_String);
		processor.process(content, null, count);
		assertThat("error count", count.getErrorCount(), equalTo(0));
		assertThat("success count", count.getSuccessCount(), equalTo(1));
	}

	@Test
	public void testContainsDelimParseSuccessWithDelim() throws Exception {
		AcReaderContent content = new AcStringsContent(DelimIn_String);
		Iterator<Object> iterator = processor.iterator(content, null, count);
		assertThat("iterator", iterator, is(notNullValue()));
		assertThat("hasNext", iterator.hasNext(), is(equalTo(true)));
		int success = 0;
		while (iterator.hasNext()) {
			Object bean = iterator.next();
			success++;
			assertThat("bean", bean, is(instanceOf(AcLoadTestBeanA.class)));
			AcLoadTestBeanA beanA = (AcLoadTestBeanA) bean;
			assertThat("delim lines", beanA.getMessage(),
					hasToString(containsString("|")));
		}
		assertThat("error count", count.getErrorCount(), equalTo(0));
		assertThat("success count", success, equalTo(1));
	}

	@Test
	public void testRequiredFieldsParseSuccess() throws Exception {
		AcReaderContent content = new AcStringsContent(RequiredField_String);
		processor.process(content, null, count);
		assertThat("error count", count.getErrorCount(), equalTo(0));
		assertThat("success count", count.getSuccessCount(), equalTo(1));
	}

	@Test
	public void testRequiredFieldsParseSuccessWithNoOptionalFields()
			throws Exception {
		AcReaderContent content = new AcStringsContent(RequiredField_String);
		Iterator<Object> iterator = processor.iterator(content, null, count);
		assertThat("iterator", iterator, is(notNullValue()));
		assertThat("hasNext", iterator.hasNext(), is(equalTo(true)));
		int success = 0;
		while (iterator.hasNext()) {
			Object bean = iterator.next();
			success++;
			assertThat("bean", bean, is(instanceOf(AcLoadTestBeanA.class)));
			AcLoadTestBeanA beanA = (AcLoadTestBeanA) bean;
			assertThat("bean.message", beanA.getMessage(), is(equalTo("")));
			assertThat("bean.details is empty", beanA.getDetailList(),
					not(hasItemInArray(anything())));
		}
		assertThat("error count", count.getErrorCount(), equalTo(0));
		assertThat("success count", success, equalTo(1));
	}

	@Test
	public void testSimpleMapParseSuccess() throws Exception {
		processor = DelimsMapProcessConfig
				.createProcessor("| {DATE_TIME} | {TRANSACTION_ID} | {SESSION_ID} | {PORTLET_NAME} | {PORTLET_METHOD} | {DURATION} | {PERF_DETAIL_LIST} | {MESSAGE}");
		processor.process(new AcStringsContent(Normal_String), null, count);
		assertThat("error count", count.getErrorCount(), equalTo(0));
		assertThat("success count", count.getSuccessCount(), equalTo(1));
	}

	@Test
	public void testParseDateError() throws Exception {
		AcReaderContent content = new AcStringsContent(ErrorDate_String);
		processor.process(content, null, count);
		assertThat("error count", count.getErrorCount(), is(equalTo(1)));
		assertThat("success count", count.getSuccessCount(), is(equalTo(0)));
	}

	@Test
	public void testParseNumberError() throws Exception {
		AcReaderContent content = new AcStringsContent(ErrorNumber_String);
		processor.process(content, null, count);
		assertThat("error count", count.getErrorCount(), is(equalTo(1)));
		assertThat("success count", count.getSuccessCount(), is(equalTo(0)));
	}

	@Test
	public void testFilterParse() throws Exception {
		AcReaderContent content = new AcStringsContent(Normal_String, DelimIn_String,
				MultiLine_String);
		processor.process(content, new AcPredicate<AcContentLine>() {

			@Override
			public boolean apply(AcContentLine data) {
				return !data.getCurrentLines().contains(" psiSupportOptions ");
			}
		}, count);
		assertThat("error count", count.getErrorCount(), is(equalTo(0)));
		assertThat("success count", count.getSuccessCount(), is(equalTo(1)));
	}

}
