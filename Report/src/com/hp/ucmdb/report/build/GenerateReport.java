/**
 * Copyright : HP
 * project : Report
 * Create by : Steven Zhang(Ling Kai)
 * Create on : 2011-7-8
 */
package com.hp.ucmdb.report.build;

import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hp.ucmdb.report.bean.AdapterDetailBean;
import com.hp.ucmdb.report.bean.AdapterSummaryBean;
import com.hp.ucmdb.report.bean.EmailMapingBean;
import com.hp.ucmdb.report.bean.EmailMapingMap;
import com.hp.ucmdb.report.bean.PRDetailBean;
import com.hp.ucmdb.report.bean.PRSummaryBean;
import com.hp.ucmdb.report.dao.AdapterDao;
import com.hp.ucmdb.report.dao.PRDao;
import com.hp.ucmdb.report.util.AllConstants;
import com.hp.ucmdb.report.util.ReportUtil;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class GenerateReport {
	private Template mainTemplate;
	
	public GenerateReport(){
		Configuration cfg = new Configuration();
        cfg.setClassForTemplateLoading(this.getClass(), "/");
        cfg.setObjectWrapper(new DefaultObjectWrapper());
        try {
            mainTemplate = cfg.getTemplate("html-report-main.ftl");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
	}
	
	public void generate(){
		 List<PRSummaryBean> allPRList = generatePR();
		 List<AdapterSummaryBean> allAdapterList = generateAdapter();
		 ReportUtil.getLogger().info("Begin process template ... ");
		 generateAllInOneReport(allPRList, allAdapterList);
		 generateOneByOneReport(allPRList, allAdapterList);
		 
	}
	
	private void generateAllInOneReport(List<PRSummaryBean> allPRList, List<AdapterSummaryBean> allAdapterList){
		ReportUtil.getLogger().info("Begin generate all in one report ... ");
		Map<String, Object> root = new HashMap<String, Object>();
		root.put("reportDate", new Date().toString());
		root.put("prList",allPRList);
		root.put("adapterList",allAdapterList);
		Writer out = new StringWriter();
		try {
			mainTemplate.process(root, out);
			out.flush();
			writeToFile(out.toString(),AllConstants.HTML_TMP_FILE);
		} catch (TemplateException e) {
			ReportUtil.getLogger().error("TemplateException occured !");
			throw new RuntimeException(e);
		} catch (IOException e) {
			ReportUtil.getLogger().error("IOException occured !");
			throw new RuntimeException(e);
		}
	}
	
	private void generateOneByOneReport(List<PRSummaryBean> allPRList, List<AdapterSummaryBean> allAdapterList){
		ReportUtil.getLogger().info("Begin generate one by one report ... ");
		mappingEmail(allPRList,allAdapterList);
		for(String dsContactEmail : EmailMapingMap.emailMapingMap.keySet()){
			Map<String, Object> root = new HashMap<String, Object>();
			root.put("reportDate", new Date().toString());
			root.put("prList",EmailMapingMap.emailMapingMap.get(dsContactEmail).prMapList);
			root.put("adapterList",EmailMapingMap.emailMapingMap.get(dsContactEmail).adapterMapList);
			Writer out = new StringWriter();
			try {
				mainTemplate.process(root, out);
				out.flush();
				writeToFile(out.toString(),dsContactEmail+".html");
			} catch (TemplateException e) {
				ReportUtil.getLogger().error("TemplateException occured !");
				throw new RuntimeException(e);
			} catch (IOException e) {
				ReportUtil.getLogger().error("IOException occured !");
				throw new RuntimeException(e);
			}
			
		}
		
	}
	
	private void mappingEmail(List<PRSummaryBean> allPRList, List<AdapterSummaryBean> allAdapterList){
		ReportUtil.getLogger().info("Begin mapping the pr list to Email map... ");
		for(PRSummaryBean pRSummaryBean: allPRList){
			String dsContactEmail = pRSummaryBean.getDsContactEmail();
			if(EmailMapingMap.emailMapingMap.containsKey(dsContactEmail)){
				EmailMapingMap.emailMapingMap.get(dsContactEmail).prMapList.add(pRSummaryBean);
			}else{
				EmailMapingBean emailMapingBean = new EmailMapingBean();
				emailMapingBean.prMapList.add(pRSummaryBean);
				EmailMapingMap.emailMapingMap.put(dsContactEmail, emailMapingBean);
			}
		}
		ReportUtil.getLogger().info("Begin mapping the adapter list to Email map... ");
		for(AdapterSummaryBean adapterSummaryBean: allAdapterList){
			String dsContactEmail = adapterSummaryBean.getDsContactEmail();
			if(EmailMapingMap.emailMapingMap.containsKey(dsContactEmail)){
				EmailMapingMap.emailMapingMap.get(dsContactEmail).adapterMapList.add(adapterSummaryBean);
			}else{
				EmailMapingBean emailMapingBean = new EmailMapingBean();
				emailMapingBean.adapterMapList.add(adapterSummaryBean);
				EmailMapingMap.emailMapingMap.put(dsContactEmail, emailMapingBean);
			}
		}
	}

	private List<PRSummaryBean> generatePR(){
		ReportUtil.getLogger().info("Begin generatePR ... ");
		PRDao prDao = new PRDao();
		List<PRSummaryBean> prSummaryList = prDao.getSummary();
		String temp = prDao.getErrorFileIdList().toString();
		String fileErrorIds = temp.substring(1, temp.length()-1);
		List<PRDetailBean> prDetailList = prDao.getDetail(fileErrorIds);
		buildPRSummaryAndDetail(prSummaryList,prDetailList);
		return prSummaryList;
	}
	
	private List<AdapterSummaryBean> generateAdapter(){
		ReportUtil.getLogger().info("Begin generateAdapter ... ");
		AdapterDao adapterDao = new AdapterDao();
		List<AdapterSummaryBean> adapterSummaryList = adapterDao.getSummary();
		String temp = adapterDao.getErrorFileIdList().toString();
		String fileErrorIds = temp.substring(1, temp.length()-1);
		List<AdapterDetailBean> adapterDetailList = adapterDao.getDetail(fileErrorIds);
		buildAdapterSummaryAndDetail(adapterSummaryList,adapterDetailList);
		return adapterSummaryList;
	}
	
	private void writeToFile(String content, String fileName) {
		ReportUtil.getLogger().info("Begin write to file: " + fileName);
        writeStringToFile(content, BuildMail.getReportFileAbsolutePath(fileName));
    }
    private void writeStringToFile(String content, String fileName) {
        try {
            FileWriter fw = new FileWriter(fileName);
            fw.write(content);
            fw.close();
        } catch (Exception e) {
        	ReportUtil.getLogger().error("We got an issue while trying to dump to file:" + fileName);
            throw new RuntimeException(e);
        }
    }
    
    private void buildPRSummaryAndDetail(List<PRSummaryBean> prSummaryList, List<PRDetailBean> prDetailList){
    	for(PRSummaryBean pRSummaryBean : prSummaryList){
    		if(pRSummaryBean.isHaveErrorRecords()){
    			for(PRDetailBean pRDetailBean : prDetailList){
    				if(pRSummaryBean.getFileId() == pRDetailBean.getFileId()){
    					pRSummaryBean.prDetailList.add(pRDetailBean);
    				}
    			}
    		}
    	}
    }
    
    private void buildAdapterSummaryAndDetail(List<AdapterSummaryBean> adapterSummaryList, List<AdapterDetailBean> adapterDetailList){
    	for(AdapterSummaryBean adapterSummaryBean : adapterSummaryList){
    		if(adapterSummaryBean.isHaveErrorRecords()){
    			for(AdapterDetailBean adapterDetailBean : adapterDetailList){
    				if(adapterSummaryBean.getFileId() == adapterDetailBean.getFileId()){
    					adapterSummaryBean.adapterDetailList.add(adapterDetailBean);
    				}
    			}
    		}
    	}
    }
}
