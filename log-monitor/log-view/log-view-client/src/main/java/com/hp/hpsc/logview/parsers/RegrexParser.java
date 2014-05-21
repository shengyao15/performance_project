package com.hp.hpsc.logview.parsers;

import com.hp.hpsc.logview.po.*;
import com.hp.hpsc.logview.util.ClientUtil;
import com.hp.hpsc.logview.util.Configurations;
import com.hp.hpsc.logview.util.Configurations.ConfigurationException;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;

public class RegrexParser implements IParser {

	private String pattern;
	private String dateformat;
	private String enabledDate = "true";
	private String enableRootpath = "true";
	private String enableDateSort = "true";
	private String enableDateFilter = "true";
	
	/* (non-Javadoc)
	 * @see com.hp.hpsc.logview.parsers.IParser#resolve(java.lang.String, java.lang.String)
	 */
	@Override
	public List<Link> resolve(ParserParameters params) {
		
		pattern = Configurations.getConfigString(Configurations.ConfugrationKeys.REGREX_PATTEN, defaultPattern);
		dateformat = Configurations.getConfigString(Configurations.ConfugrationKeys.DATE_FORMAT_STRING, defaultDateFormat);	
		enabledDate = Configurations.getConfigString(Configurations.ConfugrationKeys.ENALBE_DATE_IN_PARSER, enabledDate);
		enableRootpath = Configurations.getConfigString(Configurations.ConfugrationKeys.ENALBE_ROOTPATH_IN_PARSER, enableRootpath);
		enableDateSort = Configurations.getConfigString(Configurations.ConfugrationKeys.ENABLE_DATE_SORT_DESC, enableDateSort);
		enableDateFilter = Configurations.getConfigString(Configurations.ConfugrationKeys.ENALBE_DATEFILTER_IN_PARSER, enableDateFilter);
		boolean setDate = Boolean.valueOf(enabledDate);
		boolean setRootPath = Boolean.valueOf(enableRootpath);
		boolean setDateSort = Boolean.valueOf(enableDateSort);
		boolean setDateFilter = Boolean.valueOf(enableDateFilter);
		
		//log.trace("get content>> {}", content);
		Matcher matcher = UtilInstanceCollection.getInstance().getPattern(pattern).matcher(params.getContent());
		DateFormat formatter = UtilInstanceCollection.getInstance().getFormatter(dateformat);
		List<Link> results = new ArrayList<Link>();
		while (matcher.find()) {
			String path = matcher.group(1);
			String name = matcher.group(2);
			String date = matcher.group(3);
			String size = matcher.group(4);
			boolean isFile = ClientUtil.isFolder(size, null);
			Link node = null;
			node = new Link(name, path, ClientUtil.trim(date), size, isFile);
			if(setRootPath){
				node.setUri(params.getUrl()+path);
			}
			if(setDate){
				try {
					node.setLastModified(formatter.parse(date));
				} catch (ParseException e) {
					//added a log
					throw new RuntimeException(e);
				}
			}
			if(setDate && setDateFilter && (!node.isFolderFlag()) && node.getLastModified() != null){
				if(params.checkValidDate(node.getLastModified().getTime())){
					results.add(node);
				}
			}else{
				results.add(node);
			}
		}
		if(setDateSort){
			Collections.sort(results, new LinkDateComparator());
		}
		
		return results;
	}
	
	private String defaultPattern = "<td><a href=\"(.+?)\">(.+?)</a></td><td align=\"right\">(.+?)</td><td align=\"right\">(.+?)</td>";
	private String defaultDateFormat = "dd-MMM-yyyy HH:mm";
}
		// default old first
/*		Collections.sort(files, new Comparator<FileEntry>() {

			@Override
			public int compare(FileEntry f1, FileEntry f2) {
				return f1.modifiedDate().compareTo(
						f2.modifiedDate());
			}
		});
		return files;
	}*/
