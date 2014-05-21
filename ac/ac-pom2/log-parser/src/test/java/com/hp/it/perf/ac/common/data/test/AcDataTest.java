package com.hp.it.perf.ac.common.data.test;

import static junit.framework.Assert.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.junit.BeforeClass;
import org.junit.Test;

import com.hp.it.perf.ac.common.data.AcDataInput;
import com.hp.it.perf.ac.common.data.AcDataOutput;
import com.hp.it.perf.ac.common.data.AcDataUtils;
import com.hp.it.perf.ac.common.data.test.AcDataTestBean.Detail;
import com.hp.it.perf.ac.common.data.test.AcDataTestBean.Level;

public class AcDataTest {

	private static List<AcDataTestBean> testData;

	private static interface Op<T> {
		public T read(DataInput in) throws IOException;

		public void write(DataOutput out, T data) throws IOException;
	}

	private static interface AcDataOp<T> {
		public T read(AcDataInput in) throws IOException,
				ClassNotFoundException;

		public void write(AcDataOutput out, T data) throws IOException;
	}

	private static interface ObjectOp<T> {
		public T read(ObjectInput in) throws IOException,
				ClassNotFoundException;

		public void write(ObjectOutput out, T data) throws IOException;
	}

	private static Op<Integer> IntOp = new Op<Integer>() {

		@Override
		public void write(DataOutput out, Integer data) throws IOException {
			AcDataUtils.writeVInt(out, data.intValue());
		}

		@Override
		public Integer read(DataInput in) throws IOException {
			return AcDataUtils.readVInt(in);
		}
	};

	private static Op<Long> LongOp = new Op<Long>() {

		@Override
		public void write(DataOutput out, Long data) throws IOException {
			AcDataUtils.writeVLong(out, data.longValue());
		}

		@Override
		public Long read(DataInput in) throws IOException {
			return AcDataUtils.readVLong(in);
		}
	};

	private static Op<String> StringOp = new Op<String>() {

		@Override
		public void write(DataOutput out, String data) throws IOException {
			AcDataUtils.writeString(out, data);
		}

		@Override
		public String read(DataInput in) throws IOException {
			return AcDataUtils.readString(in);
		}
	};

	private static AcDataOp<Object> AcObjectOp = new AcDataOp<Object>() {

		@Override
		public Object read(AcDataInput in) throws IOException,
				ClassNotFoundException {
			return in.readObject();
		}

		@Override
		public void write(AcDataOutput out, Object data) throws IOException {
			out.writeObject(data);
		}

	};

	private static ObjectOp<Object> ObjectOp = new ObjectOp<Object>() {

		@Override
		public Object read(ObjectInput in) throws IOException,
				ClassNotFoundException {
			return in.readObject();
		}

		@Override
		public void write(ObjectOutput out, Object data) throws IOException {
			out.writeObject(data);
		}

	};

	@BeforeClass
	public static void prepareData() {
		testData = new ArrayList<AcDataTestBean>();
		Random random = new Random();
		for (int j = 0, n = random.nextInt(10); j < n; j++) {
			AcDataTestBean bean;
			bean = new AcDataTestBean();
			bean.setDateTime(random.nextBoolean() ? new Date(random.nextLong())
					: null);
			bean.setHpscDiagnosticId(random.nextBoolean() ? String
					.valueOf(random.nextLong()) : null);
			bean.setThreadName(random.nextBoolean() ? Thread.currentThread()
					.getName() : null);
			bean.setSessionId(random.nextInt());
			bean.setThreadId(random.nextLong());
			bean.setDuration(random.nextDouble());
			bean.setLevel(random.nextBoolean() ? Level.values()[random
					.nextInt(Level.values().length)] : null);
			Detail[] details = new Detail[random.nextInt(10)];
			for (int i = 0; i < details.length; i++) {
				Detail detail;
				detail = new Detail();
				detail.setName(String.valueOf(random.nextLong()));
				detail.setValue(String.valueOf(random.nextLong()));
				details[i] = detail;
			}
			bean.setDetailList(details);
			testData.add(bean);
		}
	}

	@Test
	public void testSimpleTypes() throws Exception {
		Random random = new Random();
		for (int i = 0, n = random.nextInt(10); i < n; i++) {
			testReadWrite(random.nextInt(), true, IntOp);
		}
		testReadWrite(Integer.MAX_VALUE, true, IntOp);
		testReadWrite(Integer.MIN_VALUE, true, IntOp);
		testReadWrite(0, true, IntOp);
		for (int i = 0, n = random.nextInt(10); i < n; i++) {
			testReadWrite(random.nextLong(), true, LongOp);
		}
		testReadWrite(Long.MAX_VALUE, true, LongOp);
		testReadWrite(Long.MIN_VALUE, true, LongOp);
		testReadWrite(0L, true, LongOp);
		for (int i = 0, n = random.nextInt(1024); i < n; i++) {
			byte[] bytes = new byte[random.nextInt(1024)];
			random.nextBytes(bytes);
			testReadWrite(new String(bytes, "ISO-8859-1"), true, StringOp);
		}
		testReadWrite("", true, StringOp);
		testReadWrite(" ", true, StringOp);
		testReadWrite(null, true, StringOp);
	}

	@Test
	public void testBeanList() throws Exception {
		testReadWrite(testData, true, AcObjectOp);
	}

	@SuppressWarnings("unchecked")
	private static <T> T testReadWrite(T origin, boolean testSame, Op<T> op)
			throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		DataOutputStream output = new DataOutputStream(bos);
		op.write(output, origin);
		output.close();

		ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
		DataInputStream input = new DataInputStream(bis);
		Object result = op.read(input);
		assertThat("eof", input.read(), is(equalTo(-1)));
		input.close();
		if (origin != null) {
			assertThat("type of " + origin.getClass(), result,
					is(instanceOf(origin.getClass())));
		}
		if (testSame) {
			assertThat("same", (T) result, is(equalTo(origin)));
		}
		return (T) result;
	}

	@SuppressWarnings("unchecked")
	private static <T> T testReadWrite(T origin, boolean testSame,
			AcDataOp<T> op) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		AcDataOutput output = new AcDataOutput(new DataOutputStream(bos));
		op.write(output, origin);
		bos.close();

		ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
		AcDataInput input = new AcDataInput(new DataInputStream(bis));
		Object result;
		try {
			result = op.read(input);
		} catch (ClassNotFoundException e) {
			fail(e.getMessage());
			return null;
		}
		assertThat("eof", bis.read(), is(equalTo(-1)));
		bis.close();
		if (origin != null) {
			assertThat("type of " + origin.getClass(), result,
					is(instanceOf(origin.getClass())));
		}
		if (testSame) {
			assertThat("same", (T) result, is(equalTo(origin)));
		}
		return (T) result;
	}
}
