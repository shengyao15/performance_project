package com.hp.it.perf.ac.load.content;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;

public class AcHttpContent implements AcReaderContent {

	private URL httpUrl;
	private long length = -1; // default is unknown
	private long lastModified = -1; // default is unknown
	private URI uri;

	public AcHttpContent(URL url) {
		this.httpUrl = url;
		if (!url.getProtocol().startsWith("http")) {
			throw new IllegalArgumentException("not http based url: " + url);
		}
	}

	@Override
	public Reader getContent() throws IOException {
		URLConnection urlConnection = httpUrl.openConnection();
		urlConnection.connect();
		length = urlConnection.getContentLength();
		lastModified = urlConnection.getLastModified();
		try {
			uri = httpUrl.toURI();
		} catch (URISyntaxException e) {
			throw new IOException(e);
		}
		return new InputStreamReader(urlConnection.getInputStream());
	}

	@Override
	public AcContentMetadata getMetadata() {
		AcContentMetadata metadata = new AcContentMetadata();
		metadata.setBasename(httpUrl.getFile());
		metadata.setLastModified(lastModified);
		metadata.setLocation(uri);
		metadata.setSize(length);
		metadata.setSignature(null);
		// consider HTTP is slower than parse (during range check)
		metadata.setReloadable(false);
		return metadata;
	}

}
