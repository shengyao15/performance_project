package com.hp.it.perf.ac.service.transfer.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AcMixDataBeanBlock implements Serializable {

	private static final long serialVersionUID = 1L;

	private int count;

	private Map<Integer, DataBeanSegment> segaments = new LinkedHashMap<Integer, DataBeanSegment>();

	public static class DataBeanSegment implements Serializable {

		private static final long serialVersionUID = 1L;

		private Class<?> beanClass;

		private String transformName;

		private int count = 0;

		private List<Object> beans = new ArrayList<Object>();

		private List<AcDataContentLine> lines = new ArrayList<AcDataContentLine>();

		public int getCount() {
			return count;
		}

		public void setCount(int count) {
			this.count = count;
		}

		public Class<?> getBeanClass() {
			return beanClass;
		}

		public void setBeanClass(Class<?> beanClass) {
			this.beanClass = beanClass;
		}

		public String getTransformName() {
			return transformName;
		}

		public void setTransformName(String transformName) {
			this.transformName = transformName;
		}

		public List<Object> getBeans() {
			return beans;
		}

		public void setBeans(List<Object> beans) {
			this.beans = beans;
		}

		public List<AcDataContentLine> getLines() {
			return lines;
		}

		public void setLines(List<AcDataContentLine> lines) {
			this.lines = lines;
		}

	}

	public void add(int dataSessionId, Class<?> beanClass,
			String transformName, Object bean, AcDataContentLine line) {
		DataBeanSegment segment;
		synchronized (this) {
			segment = segaments.get(dataSessionId);
			if (segment == null) {
				segment = new DataBeanSegment();
				segment.setBeanClass(beanClass);
				segment.setTransformName(transformName);
				segaments.put(dataSessionId, segment);
			}
			count++;
		}
		segment.getLines().add(line);
		segment.getBeans().add(bean);
		segment.setCount(segment.getCount() + 1);
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public Map<Integer, DataBeanSegment> getSegaments() {
		return segaments;
	}

	public void setSegaments(Map<Integer, DataBeanSegment> segaments) {
		this.segaments = segaments;
	}

	public boolean contains(int dataSessionId) {
		return segaments.containsKey(dataSessionId);
	}

}
