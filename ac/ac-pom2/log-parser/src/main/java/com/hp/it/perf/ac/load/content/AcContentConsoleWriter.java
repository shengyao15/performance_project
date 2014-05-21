package com.hp.it.perf.ac.load.content;

public class AcContentConsoleWriter implements AcContentHandler {

	@Override
	public void handle(Object object, AcContentLine content)
			throws AcLoadException {
		System.out.println(object);
	}

	@Override
	public void handleLoadError(AcLoadException error, AcContentLine content)
			throws AcLoadException {
		System.err.println("catch load error: " + content);
		throw error;
	}

	@Override
	public void init(AcContentMetadata metadata) {
	}

	@Override
	public void destroy() {
	}

}
