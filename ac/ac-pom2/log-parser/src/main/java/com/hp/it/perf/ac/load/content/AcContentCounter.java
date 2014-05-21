package com.hp.it.perf.ac.load.content;

public class AcContentCounter implements AcContentHandler {

	protected int successCount;
	protected int errorCount;
	protected long startTime;
	protected long time;
	protected AcContentMetadata metadata;
	protected Class<?> beanClass;

	@Override
	public void init(AcContentMetadata metadata) {
		this.metadata = metadata;
		this.successCount = 0;
		this.errorCount = 0;
		this.time = 0;
		startTime = System.currentTimeMillis();
	}

	@Override
	public void handle(Object object, AcContentLine contentLine)
			throws AcLoadException {
		beanClass = object.getClass();
		successCount++;
	}

	@Override
	public void handleLoadError(AcLoadException error, AcContentLine contentLine)
			throws AcLoadException {
		errorCount++;
	}

	@Override
	public void destroy() {
		time = System.currentTimeMillis() - startTime;
	}

	public int getSuccessCount() {
		return successCount;
	}

	public int getErrorCount() {
		return errorCount;
	}

	public long getTime() {
		return time;
	}

	public AcContentMetadata getMetadata() {
		return metadata;
	}
	
	public Class<?> getBeanClass() {
		return beanClass;
	}

	@Override
	public String toString() {
		return String
				.format("AcContentCounter [successCount=%s, errorCount=%s, time=%s, beanClass=%s, metadata=%s]",
						successCount, errorCount, time,
						beanClass == null ? "UNKNOWN" : beanClass.getName(),
						metadata);
	}

}
