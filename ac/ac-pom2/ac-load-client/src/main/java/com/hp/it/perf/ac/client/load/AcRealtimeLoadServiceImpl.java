package com.hp.it.perf.ac.client.load;

import java.io.Closeable;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.ServiceLoader;

import javax.inject.Inject;
import javax.management.remote.JMXServiceURL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.hp.it.perf.ac.core.AcDataStatusEvent;
import com.hp.it.perf.ac.core.AcStatusSubscriber;
import com.hp.it.perf.ac.core.AcStatusSubscriber.Status;
import com.hp.it.perf.ac.core.service.AcServiceConfig;
import com.hp.it.perf.ac.load.parse.AcTextPipelineParseBuilder;
import com.hp.it.perf.ac.load.parse.impl.TextPatternScanner;
import com.hp.it.perf.ac.load.parse.plugins.AcProcessErrorCheckPlugin;
import com.hp.it.perf.ac.load.parse.plugins.AcTextProcessLoggingPlugin;
import com.hp.it.perf.ac.load.parse.plugins.AcTextProcessPluginManager;
import com.hp.it.perf.ac.service.transfer.AcSender;
import com.hp.it.perf.ac.service.transfer.AcTransferSender;
import com.hp.it.perf.ac.service.transfer.AcTransferService;

@Service
public class AcRealtimeLoadServiceImpl implements AcRealtimeLoadService {

	private static final Logger log = LoggerFactory
			.getLogger(AcRealtimeLoadServiceImpl.class);

	private Map<Class<?>, AcClientLoadFactory> beanClasses = new LinkedHashMap<Class<?>, AcClientLoadFactory>();

	@Inject
	private AcTransferService transferService;

	@Inject
	private AcServiceConfig serviceConfig;

	private String monitorURL;

	private static final int BLOCK_SIZE = Integer
			.getInteger("sendBlockSize", 1);

	private static final int ERROR_DETECT_SIZE = Integer.getInteger(
			"errorDetectSize", 1000);

	private ContentDispatcher dispatcher;

	private boolean startOnActive;

	private boolean useHub = false;

	public AcRealtimeLoadServiceImpl() {
		initClient();
	}

	private void initClient() {
		// load supported bean class for parser
		ServiceLoader<AcClientLoadFactory> loader = ServiceLoader
				.load(AcClientLoadFactory.class);
		for (Iterator<AcClientLoadFactory> it = loader.iterator(); it.hasNext();) {
			AcClientLoadFactory loadFactory = it.next();
			for (Class<?> beanClass : loadFactory.getSupportedBeanClassList()) {
				beanClasses.put(beanClass, loadFactory);
			}
		}
		if (beanClasses.isEmpty()) {
			throw new IllegalArgumentException("no parser bean class loaded");
		}
	}

	public void monitor(String monitorURL) throws IOException {
		JMXServiceURL jmxURL = new JMXServiceURL(monitorURL);
		AcTextPipelineParseBuilder pipelineBuilder = TextPatternScanner
				.createAutodetectPipelineParseBuilder(beanClasses.keySet()
						.toArray(new Class[0]));
		AcSender sender = new AcTransferSender(transferService);
		AcTextProcessPluginManager
				.addDefaultPlugin(new AcTextProcessLoggingPlugin());
		AcTextProcessPluginManager
				.addDefaultPlugin(new AcProcessErrorCheckPlugin(
						ERROR_DETECT_SIZE));

		AcDataBeanMixAgent handler = new AcDataBeanMixAgent(sender, beanClasses);
		handler.setBlockSize(BLOCK_SIZE);
		ContentDispatcher disp = useHub ? new HubContentDispatcher(
				pipelineBuilder, handler) : new MonitorContentDispatcher(
				pipelineBuilder, handler);
		disp.monitor(jmxURL);
		dispatcher = disp;
	}

	@Override
	public void setMonitorURL(String monitorURL) {
		if (isRunning()) {
			throw new IllegalStateException("cannot change in monitoring");
		}
		try {
			this.monitorURL = new JMXServiceURL(monitorURL).toString();
		} catch (MalformedURLException e) {
			throw new IllegalArgumentException(e);
		}
		serviceConfig.getServicePreferences()
				.put("monitorURL", this.monitorURL);
		serviceConfig.getServicePreferences().sync();
	}

	@Override
	public String getMonitorURL() {
		return monitorURL;
	}

	@Override
	public void start() throws IOException {
		monitor(getMonitorURL());
		log.info("monitor content dispatcher is started at: {}",
				getMonitorURL());
	}

	@Override
	public void stop() {
		Closeable disp = dispatcher;
		dispatcher = null;
		if (disp != null) {
			log.info("stop monitor content dispatcher");
			try {
				disp.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public boolean isRunning() {
		return dispatcher != null;
	}

	@Override
	public long getLineCount() {
		return dispatcher != null ? dispatcher.getLineCount() : -1;
	}

	@Override
	public long getByteCount() {
		return dispatcher != null ? dispatcher.getByteCount() : -1;
	}

	@Override
	public void setStartOnActive(boolean startOnActive) {
		this.startOnActive = startOnActive;
		serviceConfig.getServicePreferences().putObject("startOnActive",
				startOnActive);
		serviceConfig.getServicePreferences().sync();
	}

	@Override
	public boolean isStartOnActive() {
		return startOnActive;
	}

	@AcStatusSubscriber(Status.ACTIVE)
	public void onActive(AcDataStatusEvent dataEvent) {
		// load preference
		monitorURL = serviceConfig.getServicePreferences().get("monitorURL",
				null);
		startOnActive = Boolean.TRUE.equals(serviceConfig
				.getServicePreferences().getObject("startOnActive",
						Boolean.FALSE));
		useHub = Boolean.TRUE.equals(serviceConfig.getServicePreferences()
				.getObject("useHub", Boolean.FALSE));
		if (monitorURL != null && monitorURL.trim().length() > 0
				&& startOnActive) {
			log.info("realtime load client service is starting on active: {}",
					monitorURL);
			try {
				start();
			} catch (IOException e) {
				log.warn("init start monitor loading fail", e);
			}
		} else {
			log.info("realtime load client service is not started");
		}
	}

	@AcStatusSubscriber(Status.DEACTIVE)
	public void onDeactive(AcDataStatusEvent dataEvent) {
		if (isRunning()) {
			stop();
		}
	}

	@Override
	public void setUseHub(boolean useHub) {
		this.useHub = useHub;
		serviceConfig.getServicePreferences().putObject("useHub",
				useHub);
		serviceConfig.getServicePreferences().sync();
	}

	@Override
	public boolean isUseHub() {
		return useHub;
	}
}
