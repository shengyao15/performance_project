package com.hp.hpsc.logservice.utils;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LmUtils {
    
    private static Logger logger = LoggerFactory.getLogger(LmUtils.class);
    
    public static List<String> ConvertStringToList (String mails){
        
        logger.debug("String mails is: {}",mails);
        if (mails == null || "".equals(mails.trim())){
            return null;
        }
        List<String> mailList = new ArrayList<String>();
        String[] mailsArray = mails.split(",");
        for (String mail : mailsArray){
            mailList.add(mail);
        }
        logger.debug("mailList is: {}",mailList);
        return mailList;
    }

}
