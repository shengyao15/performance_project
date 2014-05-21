package com.hp.it.perf.monitor.files.nio;

import java.io.File;

public interface FileRenameProposal {

	public boolean isRenamed(File fromFile, long fromModified, long fromLength,
			File toFile, long toModified, long toLength);

}
