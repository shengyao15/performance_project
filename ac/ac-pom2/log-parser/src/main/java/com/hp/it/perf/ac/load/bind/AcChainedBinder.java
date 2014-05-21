package com.hp.it.perf.ac.load.bind;

import java.util.HashMap;
import java.util.Map;

import com.hp.it.perf.ac.load.parse.AcParseSyntaxException;

public abstract class AcChainedBinder implements AcBinder {

	protected String binderName;

	protected String binderAlias;

	protected Map<String, AcChainedBinder> children = new HashMap<String, AcChainedBinder>();

	protected AcChainedBinder parent;

	public void setName(String name, String alias) {
		this.binderName = name;
		this.binderAlias = alias;
	}

	public void addChildBinder(AcChainedBinder child) {
		for (AcChainedBinder childBinder : children.values()) {
			if (childBinder.binderAlias.equals(child.binderAlias)) {
				throw new AcParseSyntaxException("duplicate name/alias: "
						+ child.binderAlias);
			}
		}
		children.put(child.binderName, child);
		child.parent = this;
	}

	protected AcChainedBinder findBinder(String name) {
		AcChainedBinder child = children.get(name);
		if (child == null) {
			if (parent != null) {
				return parent.findBinder(name);
			} else {
				return this;
			}
		}
		return child;
	}

}
