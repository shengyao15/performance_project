package com.hp.it.perf.ac.core;

import java.util.concurrent.Executor;

public interface QueuedExecutor extends Executor {
    public int getQueueSize();

    public int getAvaialbeSize();
    
    public int getBlockedThreadCount();
    
}
