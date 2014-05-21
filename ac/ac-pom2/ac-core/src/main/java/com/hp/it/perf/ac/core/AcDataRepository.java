package com.hp.it.perf.ac.core;

import com.hp.it.perf.ac.common.model.AcCommonData;

public interface AcDataRepository {

    public AcCommonData getCommonData(long acid);

    public long count();

//    public boolean isFullLoaded();

}
