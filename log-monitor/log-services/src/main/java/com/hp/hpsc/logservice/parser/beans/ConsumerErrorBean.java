package com.hp.hpsc.logservice.parser.beans;

import java.util.ArrayList;
import java.util.List;

public class ConsumerErrorBean {
	private String featureName ="";
	private List<String> errorMsg = new ArrayList<String>();
	public String getFeatureName() {
		return featureName;
	}
	public void setFeatureName(String featureName) {
		this.featureName = featureName;
	}
	public List<String> getErrorMsg() {
		return errorMsg;
	}
	public void setErrorMsg(List<String> errorMsg) {
		this.errorMsg = errorMsg;
	}
	
	
	
}
