package com.hp.hpsc.logservice.service;

import static org.junit.Assert.*;

import java.util.Date;
import java.util.List;


import org.junit.Assert;
import org.junit.Test;

import com.hp.hpsc.logservice.parser.beans.TopIPAggregateBean;
import com.hp.hpsc.logservice.parser.beans.TopIPAggregateBean.StatisticsGranularities;
import com.hp.hpsc.logservice.service.WebAccessStatisticsService.StatisticsServiceException;

public class WebAccessStatisticsServiceTest {

	@Test
	public void testService() throws Exception {
		Date date = new Date(new Date().getTime() - 86400000l);
		
		WebAccessStatisticsService service = new WebAccessStatisticsService();
		List<TopIPAggregateBean> beans = service.service(date, StatisticsGranularities.DAY, 50);
		//Assert.assertEquals(50, beans.size());
		System.out.println("beans size = "+beans.size());
		int i=0;
		for(TopIPAggregateBean bean: beans){
			i++;
			System.out.println(" -- "+bean.getIp()+" | "+bean.getCount()+" | "+bean.getUserAgent()+" | "+bean.getDate());
		}
	}

}
