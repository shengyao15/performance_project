package com.hp.it.perf.ac.load.content;

public interface AcContentHandler extends AcContentErrorHandler {

	public void init(AcContentMetadata metadata);

	public void handle(Object object, AcContentLine contentLine)
			throws AcLoadException;

	public void destroy();

}
