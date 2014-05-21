package com.hp.hpsc.logview.retrievers;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;


public class HttpContentRetriever {
	
	public static final String DEFAULT_CHARSET = "ISO-8859-1";
	public static final String GZIP_FILE = ".gz";
	
	private boolean gzip = false;
	private GetMethod httpGet = null;
	private InputStream in = null;
	private String url = null;

	public HttpContentRetriever(){
		this.url = null;
		this.gzip = false;
	}

	public HttpContentRetriever(String url){
		if(url != null){
			this.url = url.trim();
			if(url.endsWith(GZIP_FILE)){
				gzip = true;
			}
		}
	}
	
	public void close(){
		if(in != null){
			try{
				in.close();
			}catch(IOException ioe){}
			finally{
				if(in != null){
					try {
						in.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
		
		if(httpGet != null){
			httpGet.releaseConnection();
		}
	}
	
	public InputStream getInputStream() throws HttpException, IOException{
		if(in == null){
			InputStream out = null;
			httpGet = new GetMethod(url);
			HttpConnectionManager.getHttpClient().executeMethod(httpGet);
			out = httpGet.getResponseBodyAsStream();
			if(gzip){
				GzipCompressorInputStream gzIn = new GzipCompressorInputStream(out);
				in = gzIn;
			}else{
				in  = out;
			}
		}	
		return in;
	}
	
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
}
