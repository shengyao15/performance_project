package com.hp.it.perf.monitor.files.nio;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.hp.it.perf.monitor.files.CompositeInstanceContentLineStream;
import com.hp.it.perf.monitor.files.ContentLineStream;
import com.hp.it.perf.monitor.files.ContentLineStreamProvider;
import com.hp.it.perf.monitor.files.ContentLineStreamProviderDelegator;
import com.hp.it.perf.monitor.files.FileCluster;
import com.hp.it.perf.monitor.files.FileContentChangeAwareProxy;
import com.hp.it.perf.monitor.files.FileContentChangeListener;
import com.hp.it.perf.monitor.files.FileInstance;
import com.hp.it.perf.monitor.files.FileInstanceChangeAwareProxy;
import com.hp.it.perf.monitor.files.FileInstanceChangeListener;
import com.hp.it.perf.monitor.files.FileOpenOption;

class MonitorFileCluster implements FileCluster, ContentLineStreamProvider,
		FileInstanceChangeListener, FileContentChangeListener {

	private final FileInstanceChangeAwareProxy instanceChangeProxy = new FileInstanceChangeAwareProxy();

	private final FileContentChangeAwareProxy contentChangeProxy = new FileContentChangeAwareProxy();

	private final String name;

	private final List<FileInstance> instanceList = new CopyOnWriteArrayList<FileInstance>();

	private final File folder;

	private ContentLineStreamProviderDelegator delegator;

	MonitorFileCluster(String clusterName, File folder,
			ContentLineStreamProviderDelegator delegator) {
		this.name = clusterName;
		this.folder = folder;
		this.delegator = delegator;
	}

	@Override
	public void addFileInstanceChangeListener(
			FileInstanceChangeListener listener) {
		instanceChangeProxy.addFileInstanceChangeListener(listener);
	}

	@Override
	public void removeFileInstanceChangeListener(
			FileInstanceChangeListener listener) {
		instanceChangeProxy.removeFileInstanceChangeListener(listener);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public List<FileInstance> listInstances() {
		return instanceList;
	}

	void addFileInstance(MonitorFileInstance fileInstance) {
		instanceList.add(fileInstance);
	}

	void removeFileInstance(MonitorFileInstance fileInstance) {
		instanceList.add(fileInstance);
	}

	@Override
	public ContentLineStream open(FileOpenOption option) throws IOException {
		final CompositeInstanceContentLineStream contentStream = new CompositeInstanceContentLineStream(
				"cluster " + name + "@" + folder, option, delegator, this);
		for (FileInstance instance : instanceList) {
			contentStream.addFileInstance(instance);
		}
		return contentStream;
	}

	boolean isEmpty() {
		return instanceList.isEmpty();
	}

	private boolean isInclude(FileInstance instance) {
		return (instance instanceof MonitorFileInstance)
				&& name.equals(((MonitorFileInstance) instance)
						.getClusterName());
	}

	@Override
	public void onFileInstanceCreated(FileInstance instance,
			FileChangeOption changeOption) {
		FileInstance checkInstance = instance;
		if (changeOption.isRenameOption()) {
			// old file instance
			checkInstance = changeOption.getRenameFile();
		}
		if (!isInclude(checkInstance)) {
			return;
		}
		instanceChangeProxy.onFileInstanceCreated(instance, changeOption);
	}

	@Override
	public void onFileInstanceDeleted(FileInstance instance,
			FileChangeOption changeOption) {
		if (!isInclude(instance)) {
			return;
		}
		instanceChangeProxy.onFileInstanceDeleted(instance, changeOption);
	}

	@Override
	public void addFileContentChangeListener(FileContentChangeListener listener) {
		contentChangeProxy.addFileContentChangeListener(listener);
	}

	@Override
	public void removeFileContentChangeListener(
			FileContentChangeListener listener) {
		contentChangeProxy.removeFileContentChangeListener(listener);
	}

	@Override
	public void onContentChanged(FileInstance instance) {
		if (!isInclude(instance)) {
			return;
		}
		contentChangeProxy.onContentChanged(instance);
	}

}
