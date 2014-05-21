package com.hp.hpsc.logview.service;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.hp.hpsc.logview.po.Link;
import com.hp.hpsc.logview.util.FileComparator;

public class DirectoryService {

	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	
	public List<Link> readDirectory4Client(String dirPath)
			throws IOException {
		File file = new File(dirPath);

		if(file.isFile() || !file.exists()){
			return null;
		}
		
		File[] list = file.listFiles();
		List<Link> linkList2 = new ArrayList<Link>();
		
		if(list == null){
			return null;
		}
		
		//SortUtils.sort(list);
		for (int i = 0; i < list.length; i++) {

			Link link = new Link();
			if (list[i].isDirectory()) {
				String fileName = list[i].getName();
				link.setName(fileName);
				link.setFolderFlag(true);
				link.setUri(list[i].getAbsolutePath());
				link.setLastModifiedDate(sdf.format(list[i].lastModified()));
				linkList2.add(link);
			} else {
				String fileName = list[i].getName();
				link.setName(fileName);
				link.setFolderFlag(false);
				link.setUri(list[i].getAbsolutePath());
				link.setLastModifiedDate(sdf.format(list[i].lastModified()));
				link.setSize(String.valueOf(list[i].length() / 1024) + "K");
				linkList2.add(link);
			}
		}
		
		return linkList2;
	}

	public List<Link> readDirectory(String dirPath, String ContextPath)
			throws IOException {
		File file = new File(dirPath);

		File[] files = file.listFiles();
		List<Link> linkList = new ArrayList<Link>();
		//SortUtils.sort(list);
		FileComparator comparator = new FileComparator();
		Arrays.sort(files, comparator);
		
		for (int i = 0; i < files.length; i++) {

			Link link = new Link();
			if (files[i].isDirectory()) {
				String fileName = files[i].getName();
				link.setName(fileName);
				link.setFolderFlag(true);
				link.setUri(ContextPath + "/DirectoryServlet?path="
						+ files[i].getAbsolutePath());
				link.setLastModifiedDate(sdf.format(files[i].lastModified()));
				linkList.add(link);
			} else {
				String fileName = files[i].getName();
				link.setName(fileName);
				link.setFolderFlag(false);
				link.setUri(ContextPath + "/DownloadServlet?path="
						+ files[i].getAbsolutePath());
				link.setLastModifiedDate(sdf.format(files[i].lastModified()));
				link.setSize(String.valueOf(files[i].length() / 1024) + "K");
				linkList.add(link);
			}
		}
		return linkList;
	}


}
