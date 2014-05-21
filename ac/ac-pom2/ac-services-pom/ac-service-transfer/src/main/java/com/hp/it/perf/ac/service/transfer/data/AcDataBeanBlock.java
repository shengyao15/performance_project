package com.hp.it.perf.ac.service.transfer.data;

import java.io.Serializable;
import java.lang.reflect.Array;

public class AcDataBeanBlock implements Serializable {

	private static final long serialVersionUID = 1L;

	private Class<?> beanClass;

	private String transformName;

	private int count;

	private Object[] beans;

	private AcDataContentLine[] lines;

	private int dataSessionId;

	public AcDataBeanBlock() {
	}

	public AcDataBeanBlock(Class<?> beanClass, int blockSize) {
		this.beanClass = beanClass;
		beans = (Object[]) Array.newInstance(beanClass, blockSize);
		lines = new AcDataContentLine[blockSize];
	}

	public Class<?> getBeanClass() {
		return beanClass;
	}

	public void setBeanClass(Class<?> beanClass) {
		this.beanClass = beanClass;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public Object[] getBeans() {
		return beans;
	}

	public void setBeans(Object[] beans) {
		this.beans = beans;
	}

	public AcDataContentLine[] getLines() {
		return lines;
	}

	public void setLines(AcDataContentLine[] lines) {
		this.lines = lines;
	}

	public String getTransformName() {
		return transformName;
	}

	public void setTransformName(String transformName) {
		this.transformName = transformName;
	}
	
	public int getDataSessionId() {
		return dataSessionId;
	}

	public void setDataSessionId(int dataSessionId) {
		this.dataSessionId = dataSessionId;
	}

}
