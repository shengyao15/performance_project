package com.hp.it.perf.ac.load.parse.plugins;

import java.util.concurrent.TimeUnit;

import com.hp.it.perf.ac.common.logging.AcLogger;
import com.hp.it.perf.ac.load.content.AcContentLine;
import com.hp.it.perf.ac.load.content.AcContentMetadata;
import com.hp.it.perf.ac.load.content.AcContent;
import com.hp.it.perf.ac.load.parse.AcStopParseException;
import com.hp.it.perf.ac.load.parse.AcTextParserContext;

public class AcTextProcessLoggingPlugin extends AcTextProcessPluginAdapter {

	private static final AcLogger logger = AcLogger
			.getLogger(AcTextProcessLoggingPlugin.class);

	private static final AcLogger errorLogger = AcLogger
			.getLogger(AcTextProcessLoggingPlugin.class.getName() + ".error");

	private static final Object Counts = new Object();

	private static final Object Metadata = new Object();

	public static final Object LogStats = new Object();

	@Override
	public <T extends AcContent<?>> T processStart(T content,
			AcTextParserContext context) throws AcParsePluginException {
		if (logger.isDebugEnabled()) {
			logger.debug("start process content: {} with metadata: {}",
					content, content.getMetadata());
		}
		long[] counts = new long[3];
		counts[0] = System.nanoTime();
		counts[1] = counts[2] = 0;
		context.setAttribute(Counts, counts);
		context.setAttribute(Metadata, content.getMetadata());
		return content;
	}

	@Override
	public Object processResolve(Object bean, AcContentLine line,
			AcTextParserContext context) throws AcParsePluginException {
		((long[]) context.getAttribute(Counts))[1]++;
		return bean;
	}

	@Override
	public void processEnd(AcTextParserContext context)
			throws AcParsePluginException {
		long endTime = System.nanoTime();
		long[] counts = (long[]) context.getAttribute(Counts);
		AcContentMetadata metadata = (AcContentMetadata) context
				.getAttribute(Metadata);
		long length = metadata.getSize();
		if (logger.isDebugEnabled()) {
			logger.debug("end process content with metadata: {}", metadata);
		}
		if (logger.isInfoEnabled()
				&& !Boolean.FALSE.equals(context.getAttribute(LogStats))) {
			if (counts[1] + counts[2] == 0) {
				// use debug to ignore this
				logger.debug(
						"Process Statistics- total:{}, success:{}, error:{}, duration:{}ms, length:{}, read-rates:{}KB/S, process-rates:{}/S (for {})",
						counts[1] + counts[2], counts[1], counts[2],
						TimeUnit.NANOSECONDS.toMillis(endTime - counts[0]),
						length, 0, 0, metadata);
			} else {
				logger.info(
						"Process Statistics- total:{}, success:{}, error:{}, duration:{}ms, length:{}, read-rates:{}KB/S, process-rates:{}/S (for {})",
						counts[1] + counts[2], counts[1], counts[2],
						TimeUnit.NANOSECONDS.toMillis(endTime - counts[0]),
						length, length * TimeUnit.SECONDS.toNanos(1)
								/ (endTime - counts[0]) / 1024,
						(counts[1] + counts[2]) * TimeUnit.SECONDS.toNanos(1)
								/ (endTime - counts[0]), metadata);
			}
		}
	}

	@Override
	public <T extends Throwable> T processParseError(T throwable,
			AcContentLine line, AcTextParserContext context)
			throws AcParsePluginException {
		((long[]) context.getAttribute(Counts))[2]++;
		if (errorLogger.isDebugEnabled()) {
			errorLogger.debug("parse error", throwable);
		}
		return throwable;
	}

	@Override
	public <T extends Throwable> T processGeneralError(T throwable,
			AcTextParserContext context) throws AcParsePluginException {
		if (throwable instanceof AcStopParseException) {
			if (logger.isDebugEnabled()) {
				logger.debug("process stop: {}", throwable.getMessage());
			}
		} else {
			if (logger.isDebugEnabled()) {
				logger.debug("process general error", throwable);
			}
		}
		return throwable;
	}

	// keep it same in hashset/list
	@Override
	public int hashCode() {
		return getClass().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return obj != null && obj.getClass() == getClass();
	}

}
