package com.hp.it.perf.ac.load.content;

public interface AcContentErrorHandler {

	public void handleLoadError(AcLoadException error, AcContentLine contentLine)
			throws AcLoadException;

}
