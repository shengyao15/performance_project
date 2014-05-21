package com.hp.it.perf.ac.app.hpsc.storm.spout;

import java.util.Map;


import org.apache.log4j.Logger;

import com.hp.it.perf.ac.app.hpsc.storm.util.JMXDataReceiver;
import com.hp.it.perf.ac.common.model.AcCommonData;

import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Values;

public class AcCommonDataReceiverSpout extends BaseRichSpout {
	
	private static final long serialVersionUID = -5201789919853288844L;
	
	private static final Logger logger = Logger.getLogger(AcCommonDataReceiverSpout.class);
	
	SpoutOutputCollector collector;
	
	TupleProducer<AcCommonData> tupleProducer;

	private String jmxURL;
	
	private JMXDataReceiver jmxDataReceiver;

	public AcCommonDataReceiverSpout(String jmxURL) {
		this.jmxURL = jmxURL;
		tupleProducer = new AcCommonDataTupleProducer();
	}

	@Override
	public void open(@SuppressWarnings("rawtypes") Map conf, TopologyContext context,
			SpoutOutputCollector collector) {
		
		if(jmxURL == null) {
			throw new IllegalStateException("JMX URL is null");
		}
		if(tupleProducer == null) {
			throw new IllegalStateException("TupleProducer is null");
		}
		this.collector = collector;
		
		jmxDataReceiver = new JMXDataReceiver(jmxURL);
	}

	@Override
	public void nextTuple() {
		AcCommonData[] contentBean = null;
		if(jmxDataReceiver != null) {
			contentBean = jmxDataReceiver.poll();
		}
		if (contentBean != null) {
			for (AcCommonData acData : contentBean) {
				Values values = tupleProducer.toEmitValues(acData);
				if(values != null) {
					collector.emit(values);
				}
			}
		}
	}

	@Override
	public void ack(Object msgId) {
		// TODO Auto-generated method stub
		logger.debug("Success to proccess message: " + msgId);

	}

	@Override
	public void fail(Object msgId) {
		// TODO Auto-generated method stub
		logger.error("Fail to process message: " + msgId);
	}

	@Override
	public void close() {
		jmxDataReceiver.closeJMX();
		super.close();
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		tupleProducer.declareOutputFields(declarer);
	}

	public String getJmxURL() {
		return jmxURL;
	}

}

