package com.hp.hpsc.logview.util;

import java.io.File;
import java.util.Comparator;

public class FileComparator implements Comparator<File>{

	@Override
	public int compare(File f1, File f2) {
		if(f1.lastModified()<f2.lastModified()){
			return 1;
		}else{
			return -1;
		}
	}

}
