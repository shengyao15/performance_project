package com.hp.it.perf.ac.app.hpsc.storm.util;

import java.util.List;

public interface IUpdaterDelegate<T> {
	
	public void update(List<T> data);

}
