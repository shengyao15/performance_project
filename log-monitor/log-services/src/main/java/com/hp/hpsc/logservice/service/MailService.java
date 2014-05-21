package com.hp.hpsc.logservice.service;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpsc.logservice.parser.beans.StatisticErrorBean;
import com.hp.hpsc.logservice.parser.beans.TopIPAggregateBean;
import com.hp.hpsc.logservice.utils.LmConsts;
import com.hp.hpsc.logservice.utils.LmUtils;
import com.hp.it.sp4ts.xa.mail.MailSenderException;
import com.hp.it.spf.xa.properties.PropertyResourceBundleManager;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class MailService {
    
    private static Logger logger = LoggerFactory.getLogger(MailService.class);

    /**
     * Send the report which user indicated date, the date format is: yyyy-mm-dd.
     *
     * @param collectDate
     */
    public void sendReportByEmailForErrorLog(String collectDate) {
        String subject = PropertyResourceBundleManager.getString(LmConsts.CONFIG_FILE_NAME, LmConsts.MAIL_SUBJECT);
        logger.debug("ErrorLog subject is: {}", subject);
        List<String> fromAddrList = getMailAddressList(LmConsts.MAIL_FROM_ADDRESS_LIST);
        logger.debug("ErrorLog from address is: {}", fromAddrList);
        List<String> toAddrList = getMailAddressList(LmConsts.MAIL_TO_ADDRESS_LIST);
        logger.debug("ErrorLog To address is: {}", toAddrList);
        MailSender mSender = new MailSender(fromAddrList, toAddrList, subject, prepareMailBodyForErrorLog(collectDate));
        try {
            mSender.sendMail();
        } catch (MailSenderException e) {
            logger.error("exception occurred when sending errorlog report");
        }
    }
    
    public void sendReportByEmailForTopIp(Date collectDate){
        String subject = PropertyResourceBundleManager.getString(LmConsts.CONFIG_FILE_NAME, LmConsts.MAIL_SUBJECT_TOPIP);
        logger.debug("TopIP subject is: {}", subject);
        List<String> fromAddrList = getMailAddressList(LmConsts.MAIL_FROM_ADDRESS_LIST_TOPIP);
        logger.debug("TopIP From address is: {}", fromAddrList);
        List<String> toAddrList = getMailAddressList(LmConsts.MAIL_TO_ADDRESS_LIST_TOPIP);
        logger.debug("TopIP To address is: {}", toAddrList);
        MailSender mSender = new MailSender(fromAddrList, toAddrList, subject, prepareMailBodyForTopIp(collectDate));
        try {
            mSender.sendMail();
        } catch (MailSenderException e) {
            logger.error("exception occurred when sending TopIP report");
        }
    }


    private String prepareMailBodyForErrorLog(String collectDate) {

        Configuration cfg = new Configuration();
        DAOService dao = new DAOService();
        List<StatisticErrorBean> list = dao.retrieveErrorLogListByCollectDate(collectDate);

        String mailContent = null;
        Template tem = null;
        String templateName = PropertyResourceBundleManager.getString(LmConsts.CONFIG_FILE_NAME, LmConsts.ERROR_LOG_TEMPLATE_NAME);
        logger.debug("errorlog templateName is: {}",templateName);
        URL url = this.getClass().getResource(LmConsts.TEMPLATES_DIR);
        logger.debug("template dir is: {}",url);
        try {
            cfg.setDirectoryForTemplateLoading(new File(url.toURI()));
            tem = cfg.getTemplate(templateName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        StringWriter writer = new StringWriter();

        Map<String, Object> rootMap = new HashMap<String, Object>();
        rootMap.put("StatisticErrorBeanList", list);
        try {
            tem.process(rootMap, writer);
        } catch (TemplateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mailContent = writer.toString();
        logger.debug("errorlog mailContent is: {}", mailContent);
        return mailContent;
    }
    
    private String prepareMailBodyForTopIp(Date collectDate){
        Configuration cfg = new Configuration();
        DAOService dao = new DAOService();
        List<TopIPAggregateBean> list = dao.getTopIpListByCollectDate(collectDate);
        String mailContent = null;
        Template tem = null;
        String templateName = PropertyResourceBundleManager.getString(LmConsts.CONFIG_FILE_NAME, LmConsts.TOP_IP_TEMPLATE_NAME);
        logger.debug("topip templateName is: {}",templateName);
        URL url = this.getClass().getResource(LmConsts.TEMPLATES_DIR);
        logger.debug("template url is: {}",url);
        try {
            cfg.setDirectoryForTemplateLoading(new File(url.toURI()));
            tem = cfg.getTemplate(templateName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        StringWriter writer = new StringWriter();

        Map<String, Object> rootMap = new HashMap<String, Object>();
        rootMap.put("TopIpList", list);
        try {
            tem.process(rootMap, writer);
        } catch (TemplateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mailContent = writer.toString();
        logger.debug("topip mailContent is: {}", mailContent);
        return mailContent;
    } 
    
    private List<String> getMailAddressList(String addressType){
        String fromAddrStr = PropertyResourceBundleManager.getString(LmConsts.CONFIG_FILE_NAME, addressType);
        logger.debug("fromAddrStr is:", fromAddrStr);
        List<String> fromMailList = LmUtils.ConvertStringToList(fromAddrStr);
        return fromMailList;
    }
}
