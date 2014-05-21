package com.hp.hpsc.logview.po;

import java.util.List;

public class Link2HtmlHelper {

	public final static String DEFAULT_PREFIX = "---- ";
	
	public static String toHtml(List<Link> links){
		StringBuffer buffer = new StringBuffer();
		buffer.append("<html><head>");
		buffer.append("<title>List Folders</title>");
		buffer.append("</head><body>");
		buffer.append("<h1>List Folders</h1>");
		buffer.append("<table><tr><th><a href='#'>-- Name --</a></th><th><a href='#'>-- Last modified --</a></th><th><a href='#'>-- Size --</a></th></tr>");
		buffer.append("<tr><th colspan='3'><hr></th></tr>");
		
		if(links != null && !links.isEmpty()){
			for(Link l: links){
				buffer.append(toHtml(l, ""));
			}
		}
		
		buffer.append("<tr><th colspan='3'><hr></th></tr></table>");
		buffer.append("<address>HPSC LogView</address>");
		buffer.append("</body></html>");
		
		return buffer.toString();
	}
	
	public static String toHtml(Link link, String prefix){
		if(link == null){
			return "<tr><td><a href=''>"+prefix+"</a></td><td align='right'></td><td align='right'></td></tr>";
		}
		StringBuffer buffer = new StringBuffer();
		buffer.append("<tr><td><a href='");
		buffer.append(link.getUri());
		buffer.append("'>");
		buffer.append(prefix);
		buffer.append(link.getName());
		buffer.append("</a></td>");
		buffer.append("<td align='right'>");
		buffer.append(link.getLastModifiedDate());
		buffer.append("</td><td align='right'>");
		buffer.append(link.getSize());
		buffer.append("</td></tr>");
		List<Link> subFolders = link.getSubLinks();
		if(subFolders != null && !subFolders.isEmpty()){
			for(Link sub: subFolders){
				buffer.append(toHtml(sub, prefix+DEFAULT_PREFIX));
			}
		}
		return buffer.toString();
	}
}
