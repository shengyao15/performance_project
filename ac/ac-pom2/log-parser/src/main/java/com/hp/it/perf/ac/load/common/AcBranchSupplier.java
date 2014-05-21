package com.hp.it.perf.ac.load.common;

public interface AcBranchSupplier<K, V> extends
		AcMapper<K, AcClosure<AcKeyValue<K, V>>> {

	public AcClosure<AcKeyValue<K, V>> apply(K name);

}
