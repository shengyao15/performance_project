package com.hp.hpsc.logview.po;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;


public class Link {
	private String name;
	private String uri;
	private String lastModifiedDate;
	private String size;
	private boolean folderFlag;
	private Date lastModified = null;

	private List<Link> subLinks = null;
	
	public Link(){}
	
	public Link(String n, String url, String date, String s, boolean folder){
		this.name = n;
		this.uri = url;
		this.size = s;
		this.lastModifiedDate = date;
		this.folderFlag = folder;		
	}
	
	public void showSelf(String prefix){
		System.out.println(prefix+"name="+this.name+"; size="+this.size+"; modified date="+this.lastModifiedDate+"; isFolder="+this.folderFlag+"; url="+this.uri);
	}
	
	public void showTree(String prefix){
		this.showSelf(prefix);
		if(subLinks != null && subLinks.size() > 0){
			for(Link sublink: subLinks){
				String pre = prefix + prefix;
				sublink.showTree(pre);
			}
		}
	}
	public List<Link> getSubLinks() {
		return subLinks;
	}

	public void setSubLinks(List<Link> subLinks) {
		this.subLinks = subLinks;
	}
	
	public Date getLastModified() {
		return lastModified;
	}

	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}
	
	public boolean isFolderFlag() {
		return folderFlag;
	}
	public void setFolderFlag(boolean folderFlag) {
		this.folderFlag = folderFlag;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUri() {
		return uri;
	}
	public void setUri(String uri) {
		this.uri = uri;
	}
	public String getLastModifiedDate() {
		return lastModifiedDate;
	}
	public void setLastModifiedDate(String lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}
	public String getSize() {
		return size;
	}
	public void setSize(String size) {
		this.size = size;
	}
	public String toString(){  
        return ToStringBuilder.reflectionToString(this);  
    }
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((lastModifiedDate == null) ? 0 : lastModifiedDate.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((size == null) ? 0 : size.hashCode());
		result = prime * result + ((uri == null) ? 0 : uri.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Link))
			return false;
		Link other = (Link) obj;
		if (lastModifiedDate == null) {
			if (other.lastModifiedDate != null)
				return false;
		} else if (!lastModifiedDate.equals(other.lastModifiedDate))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (size == null) {
			if (other.size != null)
				return false;
		} else if (!size.equals(other.size))
			return false;
		if (uri == null) {
			if (other.uri != null)
				return false;
		} else if (!uri.equals(other.uri))
			return false;
		return true;
	}
}
