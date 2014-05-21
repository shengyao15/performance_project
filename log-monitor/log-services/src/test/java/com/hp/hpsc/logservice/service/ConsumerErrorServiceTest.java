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

public class ConsumerErrorServiceTest {
	
	
	String reportS = "2014-03-17";  //poc
	//String reportS = "2014-03-17";  //dev1
	//String reportS = "2014-03-13";  //fut2
	
	
	@Test
	public void testConsumerLog() throws ParseException, IOException, InterruptedException, ExecutionException{
		ConsumerErrorService service = new ConsumerErrorService();
		
		List<StatisticErrorBean> beanList = service.statisticConsumerLog(reportS);

		Assert.assertTrue(beanList.size()>0);
		
		for (StatisticErrorBean statisticErrorBean : beanList) {
			Assert.assertNotNull(statisticErrorBean.getCollectDate());
			Assert.assertNotNull(statisticErrorBean.getFeatureName());
			Map<String, Integer> producerLog = statisticErrorBean.getErrorDetails();
			Assert.assertTrue(producerLog.size() > 0);
		}
		
		service.printStatisticErrorBean(beanList);
	}
	
	
}
