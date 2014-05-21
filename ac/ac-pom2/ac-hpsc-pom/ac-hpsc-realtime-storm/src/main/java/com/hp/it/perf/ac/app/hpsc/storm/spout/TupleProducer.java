package com.hp.it.perf.ac.app.hpsc.storm.spout;

import java.io.Serializable;

import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.tuple.Values;

public interface TupleProducer<T> extends Serializable {
	
	Values toEmitValues(T object);
	
	void declareOutputFields(OutputFieldsDeclarer declarer);

}
