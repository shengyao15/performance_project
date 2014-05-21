package com.hp.it.perf.ac.load.parse.test;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

import org.json.simple.JSONObject;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.hp.it.perf.ac.load.content.AcContentCounter;
import com.hp.it.perf.ac.load.content.AcReaderContent;
import com.hp.it.perf.ac.load.content.AcStringsContent;
import com.hp.it.perf.ac.load.parse.AcTextStreamProcessor;
import com.hp.it.perf.ac.load.parse.impl.AcJsonProcessorConfig;

public class AcJsonParseTest {
	private AcTextStreamProcessor processor;
	private AcContentCounter count;

	private static final String Normal_String = "| 05/07/2012, 05:26:43 | 13363684004186244 | 13363684004186244 | psiSupportOptions | RENDER_PHASE | 0.040 | {Controller(com.hp.it.sp4ts.psi.web.optionsmenu.OptionsMenuController):0.036},{View(psiSupportOptions):0.004} | message";
	private static final String MultiLine_String = "| 05/07/2012, 05:26:42 | 13363684004186244 | 13363684004186244 | multip\nline | RENDER_PHASE | 0.040 | {Controller(com.hp.it.sp4ts.psi.web.optionsmenu.\nOptionsMenuController):0.036},{View(psiSupportOptions):0.004} | abc\ndef\n";
	private static final String DelimIn_String = "| 05/07/2012, 05:26:41 | 13363684004186238 | 13363684004186238 | psiSupportOptions | DUMMY | _PHASE | 0.040 | {Controller(com.hp.it.sp4ts.psi.web.op | tionsmenu.OptionsMenuController):0.036},{View(psiSupportOptions):0.004} | abc | def";
	private static final String RequiredField_String = "| 05/07/2012, 05:26:43 | 13363684004186244 | 13363684004186244 | psiSupportOptions | RENDER_PHASE | 0.040 |  | ";
	private static final String ErrorFull_String = "This is the error text";
	private static final String ErrorDate_String = "| 05/07/2012 | 05:26:43 | 13363684004186244 | 13363684004186244 | psiSupportOptions | RENDER_PHASE | 0.040 |  | ";
	private static final String ErrorNumber_String = "| 05/07/2012, 05:26:43 | 13363684+004186244 | 13363684004186244 | psiSupportOptions | RENDER_PHASE | 0.040 |  | ";
	private static final String Another_String = "| 05/07/2012, 05:26:42 |  13363684004186244  | 13363684004186244 | multip\nline | RENDER_PHASE | 0.040 | {Controller(com.hp.it.sp4ts.psi.web.optionsmenu.\nOptionsMenuController):0.036},{View(psiSupportOptions):0.004} | abc\ndef\n END  OF  LINE";

	@Before
	public void setup() throws Exception {
		processor = AcJsonProcessorConfig
				.createProcessor(readResource(getClass().getResourceAsStream(
						"/input/sample_json.txt")));
		count = new AcContentCounter();
	}

	private String readResource(InputStream inputStream) throws IOException {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		byte[] buf = new byte[1024];
		int len;
		while ((len = inputStream.read(buf)) != -1) {
			bytes.write(buf, 0, len);
		}
		inputStream.close();
		return bytes.toString();
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
		AcReaderContent content = new AcStringsContent(Normal_String,
				Normal_String);
		processor.process(content, null, count);
		assertThat("error count", count.getErrorCount(), is(equalTo(0)));
		assertThat("success count", count.getSuccessCount(), is(equalTo(2)));
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
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
			assertThat("bean", bean, is(instanceOf(JSONObject.class)));
			assertThat("bean.toString", bean,
					hasToString(containsString("RENDER_PHASE")));
			JSONObject beanA = (JSONObject) bean;
			assertThat("bean.detail",
					(List<Object>) beanA.get("PERF_DETAIL_LIST"),
					hasItem((anything())));
			assertThat("bean.detail",
					((List) beanA.get("PERF_DETAIL_LIST")).size(),
					is(equalTo(2)));
			assertThat("bean.detail.toString",
					((List) beanA.get("PERF_DETAIL_LIST")).get(0),
					hasToString(containsString("Controller")));
			assertThat("bean.detail.toString",
					((List) beanA.get("PERF_DETAIL_LIST")).get(1),
					hasToString(containsString("View")));
			assertThat("bean.detail.toString",
					((List) beanA.get("PERF_DETAIL_LIST")).get(1),
					hasToString(not(containsString("Controller"))));
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
			assertThat("bean", bean, is(instanceOf(JSONObject.class)));
			JSONObject beanA = (JSONObject) bean;
			assertThat("multi lines", beanA.get("message"),
					hasToString(containsString("\n")));
			assertThat("2 multi lines",
					((String) beanA.get("message")).split("\n").length,
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
			assertThat("bean", bean, is(instanceOf(JSONObject.class)));
			JSONObject beanA = (JSONObject) bean;
			assertThat("delim lines", beanA.get("message"),
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

	@SuppressWarnings({ "unchecked" })
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
			assertThat("bean", bean, is(instanceOf(JSONObject.class)));
			JSONObject beanA = (JSONObject) bean;
			assertThat("bean.message", (String) beanA.get("message"),
					is(equalTo("")));
			assertThat("bean.detail is empty",
					(List<Object>) beanA.get("PERF_DETAIL_LIST"),
					not(hasItem((anything()))));
		}
		assertThat("error count", count.getErrorCount(), equalTo(0));
		assertThat("success count", success, equalTo(1));
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
	@SuppressWarnings("unchecked")
	public void testDelimDefaultPattern() throws Exception {
		AcReaderContent content = new AcStringsContent(Normal_String);
		Iterator<Object> iterator = processor.iterator(content, null, count);
		assertThat("iterator", iterator, is(notNullValue()));
		assertThat("hasNext", iterator.hasNext(), is(equalTo(true)));
		int success = 0;
		while (iterator.hasNext()) {
			Object bean = iterator.next();
			success++;
			assertThat("bean", bean, is(instanceOf(JSONObject.class)));
			JSONObject beanA = (JSONObject) bean;
			assertThat("default pattern is not null",
					beanA.get("PORTLET_NAME"), is(notNullValue()));
			assertThat("bean.detail is list", beanA.get("PERF_DETAIL_LIST"),
					is(instanceOf(List.class)));
			List<Object> detailList = (List<Object>) beanA
					.get("PERF_DETAIL_LIST");
			assertThat("detail list has value", detailList,
					is(hasItem(anything())));
			assertThat("detail list object is json object", detailList.get(0),
					is(instanceOf(JSONObject.class)));
		}
		assertThat("error count", count.getErrorCount(), equalTo(0));
		assertThat("success count", success, equalTo(1));
	}

	@Test
	public void testAnotherParse() throws Exception {
		processor = AcJsonProcessorConfig.createProcessor(
				readResource(getClass().getResourceAsStream(
						"/input/sample_json.txt")), "another");
		AcReaderContent content = new AcStringsContent(Another_String);
		Iterator<Object> iterator = processor.iterator(content, null, count);
		assertThat("iterator", iterator, is(notNullValue()));
		assertThat("hasNext", iterator.hasNext(), is(equalTo(true)));
		int success = 0;
		while (iterator.hasNext()) {
			Object bean = iterator.next();
			success++;
			assertThat("bean", bean, is(instanceOf(JSONObject.class)));
			JSONObject beanA = (JSONObject) bean;
			Object message = beanA.get("message");
			assertThat("bean", message, is(instanceOf(String.class)));
			assertThat("message is multiline", (String) message,
					is(not(containsString("END OF LINE"))));
		}
		assertThat("error count", count.getErrorCount(), equalTo(0));
		assertThat("success count", success, equalTo(1));
	}
}
