package com.hp.hpsc.logservice.service;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.junit.Assert;
import org.junit.Test;

import com.hp.hpsc.logservice.parser.beans.StatisticErrorBean;
import com.hp.hpsc.logservice.parser.beans.UrlFolderBean;
import com.hp.hpsc.logservice.utils.PropUtils;
import com.hp.km.util.PropertyUtil;

public class ProducerErrorServiceTest {
	
	
	String reportS = "2014-03-17";   //poc
	//String reportS = "2014-04-14";  //FUT2
	//String reportS = "2014-02-26";  //dev1
	
	@Test
	public void testAllProducerLog() throws Exception{
		
		long begin = System.currentTimeMillis();
		ProducerErrorService service = new ProducerErrorService();
		
		
		List<StatisticErrorBean> beanList = service.statisticAllProducerLog(reportS);
		
		Assert.assertTrue(beanList.size()>0);
		
		for (StatisticErrorBean statisticErrorBean : beanList) {
			Assert.assertNotNull(statisticErrorBean.getCollectDate());
			Assert.assertNotNull(statisticErrorBean.getFeatureName());
			Map<String, Integer> producerLog = statisticErrorBean.getErrorDetails();
			Assert.assertTrue(producerLog.size() > 0);
		}
		
		service.printStatisticErrorBean(beanList);
		
		long end = System.currentTimeMillis(); 
        System.out.println("cost " + (end - begin) + " ms");
	}
	
}
