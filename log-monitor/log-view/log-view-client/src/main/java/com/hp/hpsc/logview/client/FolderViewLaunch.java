package com.hp.hpsc.logview.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.hp.hpsc.logview.parsers.IParser;
import com.hp.hpsc.logview.po.*;
import com.hp.hpsc.logview.retrievers.IRetriever;
import com.hp.hpsc.logview.util.ClientUtil;
import com.hp.hpsc.logview.util.Configurations;
import com.hp.hpsc.logview.util.Configurations.ConfigurationException;

public class FolderViewLaunch {

	private IRetriever retriever = null;
	private IParser parser = null;

	private void init() throws ConfigurationException, LaunchException {
		String retrieverClass = Configurations
				.getConfigString(Configurations.ConfugrationKeys.CLASSNAME_RETREIVER_INSTANCE);
		String parserClass = Configurations
				.getConfigString(Configurations.ConfugrationKeys.CLASSNAME_PARSER_INSTANCE);

		try {
			retriever = (IRetriever) (Class.forName(retrieverClass)
					.newInstance());
			parser = (IParser) (Class.forName(parserClass).newInstance());
		} catch (InstantiationException ie) {
			throw this.new LaunchException(ie);
		} catch (IllegalAccessException iae) {
			throw this.new LaunchException(iae);
		} catch (ClassNotFoundException cne) {
			throw this.new LaunchException(cne);
		}

	}

	public FolderViewLaunch() {
		try {
			init();
		} catch (ConfigurationException e) {
			throw new RuntimeException(e);
		} catch (LaunchException e) {
			throw new RuntimeException(e);
		}
	}

	public class LaunchException extends Exception {
		public LaunchException() {
		}

		public LaunchException(Throwable r) {
			super(r);
		}

		public LaunchException(String message) {
			super(message);
		}
	}

	/**
	 * List folder nodes, and filter the result based on the last modified date
	 * of files, no impact on folders.
	 * 
	 * @param url
	 *            -- the folder url
	 * @param lastDate
	 *            -- filter condition
	 * @return List<Link> folder nodes
	 */
	public List<Link> listView(String url, long startDate, long lastDate) {
		ParserParameters params = new ParserParameters();
		params.setUrl(url);
		params.setContent(retriever.retrieve(url));
		params.setStartDate(startDate);
		params.setLastDate(lastDate);
		return parser.resolve(params);
	}

	
	/**
	 * List all view based on the url, the result is not tree-structures, it's plan. 
	 * 
	 * @param url -- the root folder url
	 * @param startDate -- start date
	 * @param lastDate -- end date
	 * @param filter -- filter the result based on the config file name
	 * @return -- list of Link
	 */
	public List<Link> listAllView(String url, long startDate, long lastDate, boolean filter) {
		String[] fileNames = null;
		try {
			fileNames = Configurations.getConfigArray(Configurations.ConfugrationKeys.ACCESSLOG_FILEFILTER);
		} catch (ConfigurationException e) {
			//TODO add a log that the file name filter config doesn't work
		}
		
		List<Link> results = new ArrayList<Link>(5);
		List<Link> links = listView(url, startDate, lastDate);
		if(links != null && links.size() > 0){
			for(Link link: links){
				if(link.isFolderFlag()){
					results.addAll(listAllView(link.getUri(), startDate, lastDate, filter));
				}else{
					if(filter && fileNames != null && fileNames.length > 0 && link.getName() != null){
						for(String s: fileNames){
							if(link.getName().startsWith(s) && !"0".equals(ClientUtil.trim(link.getSize()))){
								results.add(link);
							}
						}
					}else{
						if(!"0".equals(ClientUtil.trim(link.getSize()))){
							results.add(link);
						}
					}
				}
			}
		}
		return results;
	}
	
	/**
	 * Same method with ListView(url, date) method, the additional parameter
	 * works on the cascade with folders, it will expend all folders if the
	 * value is true.
	 * 
	 * @param url
	 *            -- the folder link
	 * @param lastDate
	 *            -- filter condition
	 * @param folderCascade
	 *            -- true, expending all folders; false, no sub folders.
	 * @return List<Link> folder nodes
	 */
	public List<Link> listView(String url, long startDate, long lastDate, boolean folderCascade) {
		if (!folderCascade) {
			return listView(url, startDate, lastDate);
		} else {
			List<Link> results = listView(url, startDate, lastDate);
			if (results != null && !results.isEmpty()) {
				for (Link link : results) {
					if (link != null && link.isFolderFlag()) {
						link.setSubLinks(listView(link.getUri(), startDate, lastDate,
								folderCascade));
					}
				}
			}
			return results;
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		FolderViewLaunch launch = new FolderViewLaunch();
		List<Link> files = launch.listAllView(args[0], 0l, Long.MAX_VALUE, true);
		for (Link n : files) {
			n.showSelf("---- ");
		}
		
		System.out.println(" ---------- --------------------");
		
		List<Link> nofiles = launch.listAllView(args[0], (new Date().getTime() - 86400000l), new Date().getTime(),
				true);
		for (Link n : nofiles) {
			n.showSelf("---- ");
		}
	}

}
