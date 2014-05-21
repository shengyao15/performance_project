package com.hp.hpsc.logview.retrievers;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.httpclient.methods.GetMethod;

import com.hp.hpsc.logview.client.LogviewClient;

public class HttpRetriever implements IRetriever {
	
	public HttpRetriever(){}
	
	/* (non-Javadoc)
	 * @see com.hp.hpsc.logview.retrievers.IRetriever#retrieve(java.lang.String)
	 */
	@Override
	public String retrieve(String url){
		GetMethod httpGet = new GetMethod(url);
		String result = null;
		
		try {
			HttpConnectionManager.getHttpClient().executeMethod(httpGet);
			InputStream in = httpGet.getResponseBodyAsStream();
			result = LogviewClient.InputStreamTOString(in);
			in.close();	
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			httpGet.releaseConnection();
		}
		return result;
	}
}
