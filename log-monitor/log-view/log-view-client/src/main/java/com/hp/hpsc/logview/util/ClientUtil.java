package com.hp.hpsc.logview.util;

public class ClientUtil {

	public static final String FOLDER_FLAG = "-";
	
	public static final boolean isFolder(String s, String flag){
		if(s == null){
			return false;
		}
		
		return (flag !=null) ? s.contains(flag) : s.contains(FOLDER_FLAG);
		
		/*if(flag != null){
			return !s.contains(flag);
		}else{
			return !s.contains(FOLDER_FLAG);
		}*/
	}
	
	public static final String trim(String s){
		return (s == null) ? s : s.trim(); 
	}

}
