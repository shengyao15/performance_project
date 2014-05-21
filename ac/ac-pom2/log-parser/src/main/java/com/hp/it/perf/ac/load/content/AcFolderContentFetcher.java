package com.hp.it.perf.ac.load.content;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

import com.hp.it.perf.ac.load.common.AcPredicate;

public class AcFolderContentFetcher implements AcContentFetcher {

	private static class Filefilter implements FilenameFilter {
		private final AcPredicate<String> filter;
		private final File file;

		private Filefilter(AcPredicate<String> filter, File file) {
			this.filter = filter;
			this.file = file;
		}

		@Override
		public boolean accept(File dir, String name) {
			return filter.apply(getRelativeName(file, dir, name));
		}

		private String getRelativeName(File rootFolder, File childFolder,
				String name) {
			// the child folder is created by File.list()
			// so we can use this quick equal to check
			LinkedList<String> relativeNames = new LinkedList<String>();
			relativeNames.addFirst(name);
			if (childFolder != rootFolder) {
				relativeNames.addFirst(childFolder.getName());
			}
			char pathSep = File.separatorChar;
			StringBuilder sb = new StringBuilder();
			for (String pathPart : relativeNames) {
				sb.append(pathPart).append(pathSep);
			}
			sb.deleteCharAt(sb.length() - 1);
			return sb.toString();
		}
	}

	private File dir;
	private FilenameFilter fileFilter;
	private Queue<File> pending;

	public AcFolderContentFetcher(File file) {
		this(file, (FilenameFilter) null);
	}

	public AcFolderContentFetcher(File file, FilenameFilter filter) {
		this.dir = file;
		this.fileFilter = filter;
		this.pending = new LinkedList<File>();
		pending.offer(dir);
	}

	public AcFolderContentFetcher(final File file,
			final AcPredicate<String> filter) {
		this(file, filter == null ? null : new Filefilter(filter, file));
	}

	@Override
	public void close() throws IOException {
		pending = null;
	}

	@Override
	public AcReaderContent next() throws IOException {
		if (pending == null) {
			throw new IOException("closed fetcher");
		}
		File file;
		while ((file = pending.poll()) != null) {
			if (file.isDirectory()) {
				File[] files = fileFilter == null ? file.listFiles() : file
						.listFiles(fileFilter);
				for (int i = files.length - 1; i >= 0; i--) {
					pending.offer(files[i]);
				}
			} else {
				return new AcFileContent(file);
			}
		}
		return null;
	}
}
