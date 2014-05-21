package com.hp.it.perf.ac.client.load;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.InflaterInputStream;

import javax.management.JMX;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.Notification;
import javax.management.NotificationEmitter;
import javax.management.NotificationFilterSupport;
import javax.management.NotificationListener;
import javax.management.ObjectName;
import javax.management.openmbean.CompositeData;
import javax.management.remote.JMXConnectionNotification;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.it.perf.ac.load.content.AcContentMetadata;
import com.hp.it.perf.ac.load.content.AcLoadException;
import com.hp.it.perf.ac.load.parse.AcStopParseException;
import com.hp.it.perf.ac.load.parse.AcTextPipeline;
import com.hp.it.perf.ac.load.parse.AcTextPipelineParseBuilder;

public class MonitorContentDispatcher implements NotificationListener, ContentDispatcher {

	private AcTextPipelineParseBuilder builder;

	private AcDataBeanMixAgent handler;

	private Map<Long, AcTextPipeline> pipelines = new HashMap<Long, AcTextPipeline>();

	private Map<Long, ContentInfo> contents = new HashMap<Long, ContentInfo>();

	private String lastFileName = null;

	private int lastLineNo = 0;

	private long notifyId = 0;

	private long notifyTime = 0;

	private static final Logger log = LoggerFactory
			.getLogger(MonitorContentDispatcher.class);

	private JMXConnector connector;

	private long lineCount;

	private long byteCount;

	public static interface ContentProvider {

		public String[] getFileNames();

		public List<ContentInfo> getFileContentInfos(boolean realtime,
				boolean active) throws IOException;

		public void setCompressMode(boolean mode);

		public boolean isCompressMode();

		public void setNotificationEnabled(boolean enabled);

		public boolean isNotificationEnabled();

	}

	public static class ContentInfo {

		private String fileName;

		private long providerId;

		private String currentFileName;

		private String realPath;

		private long offset;

		// real-time information
		private long lastModified;

		private long length;

		public String getFileName() {
			return fileName;
		}

		public void setFileName(String fileName) {
			this.fileName = fileName;
		}

		public long getProviderId() {
			return providerId;
		}

		public void setProviderId(long providerId) {
			this.providerId = providerId;
		}

		public String getCurrentFileName() {
			return currentFileName;
		}

		public void setCurrentFileName(String currentFileName) {
			this.currentFileName = currentFileName;
		}

		public long getOffset() {
			return offset;
		}

		public void setOffset(long offset) {
			this.offset = offset;
		}

		public long getLastModified() {
			return lastModified;
		}

		public void setLastModified(long lastModified) {
			this.lastModified = lastModified;
		}

		public long getLength() {
			return length;
		}

		public void setLength(long length) {
			this.length = length;
		}

		public String getRealPath() {
			return realPath;
		}

		public void setRealPath(String realPath) {
			this.realPath = realPath;
		}

	}

	public static class LineRecord {

		private byte[] line;

		private int lineNum;

		private long providerId;

		public byte[] getLine() {
			return line;
		}

		public void setLine(byte[] line) {
			this.line = line;
		}

		public int getLineNum() {
			return lineNum;
		}

		public void setLineNum(int lineNum) {
			this.lineNum = lineNum;
		}

		public long getProviderId() {
			return providerId;
		}

		public void setProviderId(long providerId) {
			this.providerId = providerId;
		}

		public static LineRecord from(CompositeData userData) {
			LineRecord lineRecord = new LineRecord();
			lineRecord.setLine((byte[]) userData.get("line"));
			lineRecord.setLineNum((Integer) userData.get("lineNum"));
			lineRecord.setProviderId((Long) userData.get("providerId"));
			return lineRecord;
		}

	}

	public MonitorContentDispatcher(AcTextPipelineParseBuilder pipelineBuilder,
			AcDataBeanMixAgent handler) {
		this.builder = pipelineBuilder;
		this.handler = handler;
	}

	public void monitor(JMXServiceURL monitorURL) throws IOException {
		connector = JMXConnectorFactory.connect(monitorURL);
		NotificationFilterSupport filterSupport = new NotificationFilterSupport();
		filterSupport.enableType(JMXConnectionNotification.NOTIFS_LOST);
		connector.addConnectionNotificationListener(new NotificationListener() {

			@Override
			public void handleNotification(Notification notification,
					Object handback) {
				JMXConnectionNotification connNotify = (JMXConnectionNotification) notification;
				log.warn("Notification lost on connection id {}: ",
						connNotify.getConnectionId(), connNotify.getMessage());
			}
		}, filterSupport, null);
		MBeanServerConnection mbsc = connector.getMBeanServerConnection();
		log.info("===> Connected to monitor server: {}", monitorURL);
		Set<ObjectName> names;
		try {
			names = mbsc
					.queryNames(
							ObjectName
									.getInstance("com.hp.it.perf.monitor.filemonitor:type=CompositeContentProvider,*"),
							null);
		} catch (MalformedObjectNameException e) {
			throw new RuntimeException(e);
		}
		if (names.isEmpty()) {
			throw new IOException("no monitor mbean found");
		}
		if (names.size() > 1) {
			throw new IOException("more monitor mbeans found: " + names);
		}
		ObjectName monitorObjName = names.iterator().next();
		ContentProvider contentProvider = JMX.newMXBeanProxy(mbsc,
				monitorObjName, ContentProvider.class, true);
		contentProvider.setCompressMode(true);
		contentProvider.setNotificationEnabled(true);
		((NotificationEmitter) contentProvider).addNotificationListener(this,
				null, contentProvider);
	}

	private boolean refreshFiles(ContentProvider providers, long provId)
			throws IOException {
		List<ContentInfo> contentInfos;
		ContentInfo contInfo = contents.get(provId);
		if (contInfo == null) {
			long start = System.currentTimeMillis();
			contentInfos = providers.getFileContentInfos(false, true);
			log.info("Get {} file content infos within {} ms",
					contentInfos.size(), System.currentTimeMillis() - start);
			Set<Long> currentIds = new HashSet<Long>(pipelines.keySet());
			for (ContentInfo content : contentInfos) {
				long providerId = content.getProviderId();
				contents.put(providerId, content);
				AcTextPipeline pipeline = pipelines.get(providerId);
				if (pipeline != null) {
					currentIds.remove(providerId);
				}
			}
			if (!currentIds.isEmpty()) {
				for (Long notexist : currentIds) {
					closePipeline(notexist, pipelines.get(notexist));
				}
			}
			contInfo = contents.get(provId);
		}
		if (contInfo != null) {
			AcTextPipeline pipeline = pipelines.get(provId);
			if (pipeline == null) {
				pipeline = builder.createPipeline(null,
						handler.createContentHandler());
				AcContentMetadata metadata = toMetadata(contInfo);
				try {
					pipeline.prepare(metadata);
				} catch (AcStopParseException e) {
					// stop this pipeline
					log.warn(
							"Got parse stop error on prepare phase for {}: {}",
							metadata, e);
				} catch (AcLoadException e) {
					log.error(
							"Got load error on prepare phase for " + metadata,
							e);
				}
				pipelines.put(provId, pipeline);
			}
		}
		return contInfo != null;

	}

	private AcContentMetadata toMetadata(ContentInfo content) {
		AcContentMetadata metadata = new AcContentMetadata();
		metadata.setBasename(content.getCurrentFileName());
		metadata.setLastModified(content.getLastModified());
		try {
			String location = content.getRealPath();
			if (location == null) {
				location = content.getCurrentFileName();
			}
			metadata.setLocation(new URI(location));
		} catch (URISyntaxException e) {
			log.debug("convert location error", e);
		}
		metadata.setReloadable(false);
		metadata.setSize(content.getLength());
		return metadata;
	}

	public List<LineRecord> decompressLines(byte[] bytes) {
		try {
			DataInputStream input = new DataInputStream(
					new BufferedInputStream(new InflaterInputStream(
							new ByteArrayInputStream(bytes))));
			int size = input.readInt();
			List<LineRecord> lines = new ArrayList<LineRecord>(size);
			for (int i = 0; i < size; i++) {
				LineRecord line = new LineRecord();
				line.setProviderId(input.readLong());
				line.setLineNum(input.readInt());
				int lineLen = input.readInt();
				byte[] lineBytes = new byte[lineLen];
				input.readFully(lineBytes);
				line.setLine(lineBytes);
				lines.add(line);
			}
			input.close();
			return lines;
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	@Override
	public void handleNotification(Notification notification, Object handback) {
		if (notifyId != 0 && notifyId + 1 != notification.getSequenceNumber()) {
			log.error("may lost {} notification in {}ms",
					(notification.getSequenceNumber() - notifyId - 1),
					(notification.getTimeStamp() - notifyTime));
		}
		notifyId = notification.getSequenceNumber();
		notifyTime = notification.getTimeStamp();
		ContentProvider provider = (ContentProvider) handback;
		LineRecord lineRecord = LineRecord.from((CompositeData) notification
				.getUserData());
		List<LineRecord> lines;
		if (lineRecord.getProviderId() == -1) {
			// compress mode
			lines = decompressLines(lineRecord.getLine());
		} else {
			lines = Collections.singletonList(lineRecord);
		}
		HashSet<Long> bitSet = new HashSet<Long>();
		for (LineRecord line : lines) {
			long providerId = line.getProviderId();
			AcTextPipeline pipeline = pipelines.get(providerId);
			if (pipeline == null) {
				try {
					if (bitSet.contains(providerId)) {
						continue;
					}
					if (!refreshFiles(provider, providerId)) {
						bitSet.add(providerId);
					}
				} catch (IOException e) {
					log.warn("refresh file list error for provider id: "
							+ providerId, e);
				}
			}
			pipeline = pipelines.get(providerId);
			ContentInfo contentInfo = contents.get(providerId);
			if (pipeline != null) {
				String fileName = contentInfo.getCurrentFileName();
				if (!fileName.equals(lastFileName)) {
					if (lastFileName != null) {
						// end last file
						log.debug("{}: {}", lastFileName, lastLineNo);
					}
					lastLineNo = 0;
					lastFileName = fileName;
				}
				lastLineNo++;
				String newline;
				try {
					newline = new String(line.getLine(), "UTF-8");
				} catch (UnsupportedEncodingException ignored) {
					continue;
				}
				lineCount++;
				byteCount += line.getLine().length;
				try {
					// trim last '\n'
					// TODO, what about '\n\r'?
					if (newline.endsWith("\n")) {
						newline = newline.substring(0, newline.length() - 1);
					}
					pipeline.putLine(newline);
				} catch (AcStopParseException e) {
					log.warn("stop parse on file {} due to {}", fileName, e);
					// stop this pipeline
					closePipeline(providerId, pipeline);
				} catch (AcLoadException e) {
					log.error("parse error on file " + fileName, e);
				} catch (Exception e) {
					log.error("Unexpected error on parsing line: " + newline, e);
				}
			} else {
				log.warn("pipeline is null for provider id: {}", providerId);
			}
		}
	}

	private void closePipeline(long providerId, AcTextPipeline pipeline) {
		pipelines.remove(providerId);
		if (pipeline != null) {
			pipeline.close();
		}
	}

	public void close() throws IOException {
		if (connector != null) {
			connector.close();
		}
	}

	public long getByteCount() {
		return byteCount;
	}

	public long getLineCount() {
		return lineCount;
	}

}
