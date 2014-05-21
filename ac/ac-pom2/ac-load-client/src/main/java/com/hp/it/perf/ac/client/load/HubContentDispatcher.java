package com.hp.it.perf.ac.client.load;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.management.remote.JMXServiceURL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.it.perf.ac.load.content.AcContentMetadata;
import com.hp.it.perf.ac.load.content.AcLoadException;
import com.hp.it.perf.ac.load.parse.AcStopParseException;
import com.hp.it.perf.ac.load.parse.AcTextPipeline;
import com.hp.it.perf.ac.load.parse.AcTextPipelineParseBuilder;
import com.hp.it.perf.monitor.hub.GatewayStatus;
import com.hp.it.perf.monitor.hub.HubEvent;
import com.hp.it.perf.monitor.hub.HubEvent.HubStatus;
import com.hp.it.perf.monitor.hub.HubSubscribeOption;
import com.hp.it.perf.monitor.hub.HubSubscriber;
import com.hp.it.perf.monitor.hub.MonitorEndpoint;
import com.hp.it.perf.monitor.hub.MonitorEvent;
import com.hp.it.perf.monitor.hub.MonitorFilter;
import com.hp.it.perf.monitor.hub.MonitorHub;
import com.hp.it.perf.monitor.hub.jmx.HubJMX;
import com.hp.it.perf.monitor.hub.jmx.MonitorHubJmxFactory;

class HubContentDispatcher implements ContentDispatcher, HubSubscriber {

	private AcTextPipelineParseBuilder builder;
	private AcDataBeanMixAgent handler;
	private long byteCount;
	private long lineCount;
	private MonitorHub jmxClient;
	private Map<String, AcTextPipeline> pipelines = new HashMap<String, AcTextPipeline>();
	private String lastFileName = null;
	private int lastLineNo = 0;

	private static final Logger log = LoggerFactory
			.getLogger(HubContentDispatcher.class);

	public HubContentDispatcher(AcTextPipelineParseBuilder pipelineBuilder,
			AcDataBeanMixAgent handler) {
		this.builder = pipelineBuilder;
		this.handler = handler;
	}

	@Override
	public long getLineCount() {
		return lineCount;
	}

	@Override
	public long getByteCount() {
		return byteCount;
	}

	@Override
	public void close() throws IOException {
		if (jmxClient != null) {
			jmxClient.unsubscribe(this);
			// TODO how to close jmx client
			jmxClient = null;
		}
	}

	@Override
	public void monitor(JMXServiceURL jmxURL) throws IOException {
		if (jmxClient == null) {
			jmxClient = MonitorHubJmxFactory.createHubJmxClient(jmxURL,
					new HashMap<String, Object>(), HubJMX.getHubObjectName());
		}
		jmxClient.subscribe(this, new HubSubscribeOption() {

			@Override
			public boolean isSubscribeEnabled(MonitorEndpoint endpoint) {
				return true;
			}

			@Override
			public MonitorEndpoint[] getPreferedEndpoints() {
				return new MonitorEndpoint[0];
			}

			@Override
			public MonitorFilter getMonitorFilter() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public int getBatchSize() {
				return 0;
			}
		});
		log.info("===> Connected to hub server: {}", jmxURL);
	}

	@Override
	public void onData(MonitorEvent ... events) {
		for (MonitorEvent event : events) {
			String fileName = event.getContentSource();
			byte[] content = (byte[]) event.getContent();
			AcTextPipeline pipeline = pipelines.get(fileName);
			if (pipeline == null) {
				// simulate new file created
				pipeline = createFile(fileName);
			}
			if (!fileName.equals(lastFileName)) {
				if (lastFileName != null) {
					// end last file
					log.debug("{}: {}", lastFileName, lastLineNo);
				}
				lastLineNo = 0;
				lastFileName = fileName;
			}
			lastLineNo++;
			String newline = null;
			try {
				newline = new String(content, "UTF-8");
				lineCount++;
				byteCount += content.length;
				// trim last '\n'
				// TODO, what about '\n\r'?
				if (newline.endsWith("\n")) {
					newline = newline.substring(0, newline.length() - 1);
				}
				pipeline.putLine(newline);
			} catch (UnsupportedEncodingException e) {
				log.error(
						"Unexpected error on line: " + Arrays.toString(content),
						e);
			} catch (AcStopParseException e) {
				log.warn("stop parse on file {} due to {}", fileName, e);
				// stop this pipeline
				closePipeline(fileName, pipeline);
			} catch (AcLoadException e) {
				log.error("parse error on file " + fileName, e);
			} catch (Exception e) {
				log.error("Unexpected error on parsing line: " + newline, e);
			}
		}
	}

	private void closePipeline(String fileName, AcTextPipeline pipeline) {
		pipelines.remove(fileName);
		if (pipeline != null) {
			pipeline.close();
		}
	}

	@Override
	public void onHubEvent(HubEvent event) {
		log.info("got hub event status-{}, endpoint-{}, data-{}",
				event.getStatus(), event.getEndpoint(), event.getData());
		if (event.getStatus() == HubStatus.EndpointBroadcast) {
			// TODO suppose this is for this endpoint
			GatewayStatus status = (GatewayStatus) event.getData();
			// TODO hardcode value
			if (status.getStatus() == 0) {
				// new file
				createFile((String) status.getContext());
			} else if (status.getStatus() == 1) {
				// old file
				removeFile((String) status.getContext());
			}
		}
	}

	private void removeFile(String fileName) {
		closePipeline(fileName, pipelines.get(fileName));
	}

	private AcTextPipeline createFile(String fileName) {
		AcTextPipeline pipeline = builder.createPipeline(null,
				handler.createContentHandler());
		AcContentMetadata metadata = new AcContentMetadata();
		metadata.setBasename(fileName);
		metadata.setLastModified(0);
		try {
			metadata.setLocation(new URI(fileName));
		} catch (URISyntaxException e) {
			log.debug("convert location error", e);
		}
		metadata.setReloadable(false);
		metadata.setSize(0);
		try {
			pipeline.prepare(metadata);
		} catch (AcStopParseException e) {
			// stop this pipeline
			log.warn("Got parse stop error on prepare phase for {}: {}",
					metadata, e);
		} catch (AcLoadException e) {
			log.error("Got load error on prepare phase for " + metadata, e);
		}
		pipelines.put(fileName, pipeline);
		return pipeline;
	}
}
