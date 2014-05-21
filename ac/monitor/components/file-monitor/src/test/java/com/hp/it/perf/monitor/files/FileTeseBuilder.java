package com.hp.it.perf.monitor.files;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class FileTeseBuilder implements Closeable {

	private static final Logger log = LoggerFactory
			.getLogger(FileTeseBuilder.class);

	private static final String NEW_LINE = System.getProperty("line.separator");

	private File targetRootFolder;

	private File targetDeleteFolder;

	private Timer timer;

	private boolean moveOnDelete = true;

	private List<Closeable> closeableList = new ArrayList<Closeable>();

	public FileTeseBuilder(String root) {
		String prefix = "/tmp/filemonitor/";
		targetRootFolder = new File(prefix + "test/data/" + root);
		targetDeleteFolder = new File(prefix + "test/for-removed/" + root);
		deleteFiles(targetRootFolder);
		deleteFiles(targetDeleteFolder);
		targetRootFolder.deleteOnExit();
		targetRootFolder.mkdirs();
		targetDeleteFolder.deleteOnExit();
		targetDeleteFolder.mkdirs();
	}

	public File copy(File sourceFile, String name) throws IOException {
		File targetFile = new File(targetRootFolder, name);
		copyFile(sourceFile, targetFile);
		return targetFile;
	}

	public void copyFile(File sourceFile, File targetFile) throws IOException {
		targetFile.deleteOnExit();
		FileChannel sourceChannel = null;
		FileChannel targetChannel = null;
		FileInputStream sourceStream = null;
		FileOutputStream targetStream = null;
		try {
			sourceStream = new FileInputStream(sourceFile);
			sourceChannel = sourceStream.getChannel();
			targetStream = new FileOutputStream(targetFile);
			targetChannel = targetStream.getChannel();
			sourceChannel.transferTo(0, sourceChannel.size(), targetChannel);
			log.trace("create file {}", targetFile);
		} finally {
			close(sourceChannel);
			close(targetChannel);
			close(sourceStream);
			close(targetStream);
		}
		targetFile.setLastModified(sourceFile.lastModified());
	}

	private void close(Closeable closeable) {
		if (closeable != null) {
			try {
				closeable.close();
			} catch (IOException ignored) {
			}
		}
	}

	public File copy(File sourceFile) throws IOException {
		return copy(sourceFile, sourceFile.getName());
	}

	@Override
	public void close() throws IOException {
		for (Closeable closeable : closeableList) {
			close(closeable);
		}
		closeableList.clear();
		if (!targetRootFolder.exists()) {
			return;
		}
		deleteFiles(targetRootFolder);
		moveOnDelete = false;
		deleteFiles(targetDeleteFolder);
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
	}

	private void deleteFiles(File dir) {
		File[] files = dir.listFiles();
		if (files == null) {
			return;
		}
		for (File file : files) {
			if (file.isFile()) {
				delete(file);
			} else {
				deleteFiles(file);
			}
		}
		delete(dir);
	}

	public void echo(String line, File targetFile) throws IOException {
		RandomAccessFile access = null;
		try {
			access = new RandomAccessFile(targetFile, "rw");
			access.seek(access.length());
			byte[] bs = line(line);
			log.trace("echo line size of {} to file {}", bs.length, targetFile);
			access.write(bs);
			log.trace("echo line done.");
		} finally {
			close(access);
		}
	}

	public void print(String txt, File targetFile) throws IOException {
		RandomAccessFile access = null;
		try {
			access = new RandomAccessFile(targetFile, "rw");
			access.seek(access.length());
			byte[] bs = txt.getBytes();
			log.trace("print line size of {} to file {}", bs.length, targetFile);
			access.write(bs);
			log.trace("print line done.");
		} finally {
			close(access);
		}
	}

	public byte[] line(String data) {
		return (data + NEW_LINE).getBytes();
	}

	public void echoSync(final String line, final File targetFile, long time,
			TimeUnit unit) {
		Timer timer = getTimer();
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				try {
					echo(line, targetFile);
				} catch (IOException ignored) {
				}
			}
		}, unit.toMillis(time));
	}

	private Timer getTimer() {
		if (timer == null) {
			timer = new Timer("FileTestBuilderTimer", true);
		}
		return timer;
	}

	public void printThreads() {
		Map<Thread, StackTraceElement[]> allStackTraces = Thread
				.getAllStackTraces();
		for (Thread t : allStackTraces.keySet()) {
			if (t.getThreadGroup() != null) {
				if ((t.getThreadGroup().getName().equals("system"))
						|| t == Thread.currentThread()) {
					continue;
				}
				// ignore other junit threads
				if (t.getThreadGroup().getName().equals("main")
						&& t.getName().equals("ReaderThread")) {
					continue;
				}
				// ignore keep alive threads
				if (t.getThreadGroup().getName().equals("main")
						&& t.getName().equals("Reader keepalive thread")) {
					continue;
				}
				// ignore jacoco threads
				if (t.getThreadGroup().getName().equals("main")
						&& t.getName().startsWith("org.jacoco.agent")) {
					continue;
				}
			}
			if (t.getName().equals("FileTestBuilderTimer")) {
				continue;
			}
			System.out.println(t + (t.isDaemon() ? " daemon" : ""));
			for (StackTraceElement trace : allStackTraces.get(t)) {
				System.out.println("\tat " + trace.toString());
			}
			System.out.println();
		}
	}

	public File rename(File file, String newFileName) {
		File newFile = new File(file.getParentFile(), newFileName);
		if (newFile.exists()) {
			delete(newFile);
		}
		if (file.renameTo(newFile)) {
			log.trace("{} rename to {}", file.getName(), newFile.getName());
		} else {
			log.warn("{} cannot rename to {}", file.getName(),
					newFile.getName());
		}
		newFile.deleteOnExit();
		return newFile;
	}

	public void delete(File file) {
		if (file.delete()) {
			log.trace("Delete {} success", file);
		} else {
			log.debug("Cannot delete {}", file);
			if (moveOnDelete) {
				File removedFile;
				removedFile = new File(targetDeleteFolder, file.getName() + "."
						+ System.nanoTime());
				removedFile.deleteOnExit();
				boolean result = file.renameTo(removedFile);
				log.debug("Move file {} to delete folder as name {}: {}",
						new Object[] { file, removedFile, result });
			}
		}
	}

	public void registerClosable(Closeable closeable) {
		closeableList.add(closeable);
	}

	public File simulateRename(File file, String newFileName, boolean trancate)
			throws IOException {
		File newFile = new File(file.getParentFile(), newFileName);
		if (newFile.exists()) {
			trancate(newFile, 0);
		}
		copyFile(file, newFile);
		if (trancate) {
			trancate(file, 0);
		} else {
			delete(file);
		}
		log.trace("{} (simulate) rename to {}", file.getName(),
				newFile.getName());
		newFile.deleteOnExit();
		return newFile;
	}

	public void trancate(File file, long offset) {
		RandomAccessFile access = null;
		try {
			access = new RandomAccessFile(file, "rw");
			access.setLength(offset);
		} catch (IOException e) {
			log.trace("trancate file fail: {}", e.toString());
		} finally {
			close(access);
		}
	}

	public void setModifiedBefore(File file, int before, TimeUnit unit) {
		long now = System.currentTimeMillis();
		file.setLastModified(now - unit.toMillis(before));
	}

	public File create(String name) {
		File targetFile = new File(targetRootFolder, name);
		delete(targetFile);
		try {
			targetFile.createNewFile();
		} catch (IOException e) {
		}
		return targetFile;
	}
}
