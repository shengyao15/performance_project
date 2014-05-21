package com.hp.it.perf.ac.rest.service;

import com.hp.it.perf.ac.rest.model.Category;
import com.hp.it.perf.ac.rest.model.ChainEntry;
import com.hp.it.perf.ac.service.chain.ChainContext;

public interface IService {

	public ChainEntry getFullChain(long acid);

	public ChainEntry getFullChain(ChainContext context);

	public Category[] getCategories(boolean includeall);
}
