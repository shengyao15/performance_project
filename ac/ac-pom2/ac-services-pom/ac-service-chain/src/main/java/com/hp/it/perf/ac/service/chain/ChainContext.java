package com.hp.it.perf.ac.service.chain;

import com.hp.it.perf.ac.common.model.AcContext;

public class ChainContext extends AcContext {

	private static final long serialVersionUID = -1982377888658943394L;

	// used for attach underline graph object
	private transient Object attachment;

	public Object getAttachment() {
		return attachment;
	}

	public void setAttachment(Object attachment) {
		this.attachment = attachment;
	}

	@Override
	public String toString() {
		return String.format("ChainContext [code='%s', value='%s']",
				getCode(), getValue());
	}
}
