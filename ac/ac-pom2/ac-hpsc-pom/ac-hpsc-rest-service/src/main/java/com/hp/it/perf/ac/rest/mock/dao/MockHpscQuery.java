package com.hp.it.perf.ac.rest.mock.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.stereotype.Service;

import com.hp.it.perf.ac.app.hpsc.search.bean.ConsumerDetailReport;
import com.hp.it.perf.ac.app.hpsc.search.bean.ConsumerHomeInfo;
import com.hp.it.perf.ac.app.hpsc.search.bean.ConsumerRequestReport;
import com.hp.it.perf.ac.app.hpsc.search.bean.ProducerHomeInfo;
import com.hp.it.perf.ac.app.hpsc.search.bean.ProducerReport;
import com.hp.it.perf.ac.app.hpsc.search.bean.QueryCondition;
import com.hp.it.perf.ac.app.hpsc.search.service.HpscQueryService;
import com.hp.it.perf.ac.common.model.AcCommonDataWithPayLoad;

@Service
public class MockHpscQuery implements HpscQueryService {

	@Override
	public ConsumerHomeInfo getConsumerHomeInfo(QueryCondition queryCondition) {
		// TODO Auto-generated method stub
		System.out.println("getConsumerHomeInfo enter.");
		ConsumerHomeInfo consumerHome = new ConsumerHomeInfo();
		consumerHome.setTotalRequestURLs(100);
		consumerHome.setTotal(1000);
		consumerHome.setMaxTime(10);
		consumerHome.setMinTime(3);
		consumerHome.setFail(10);
		consumerHome.setFailRate(0.1);
		return consumerHome;
	}

	@Override
	public List<ProducerHomeInfo> getProducerHomeInfo(
			QueryCondition queryCondition) {
		// TODO Auto-generated method stub
		System.out.println("getProducerHomeInfo enter.");
		List<ProducerHomeInfo> pList = new ArrayList<ProducerHomeInfo>();
		ProducerHomeInfo p1 = new ProducerHomeInfo();
		p1.setTotal(100);
		p1.setFeature("AAE");
		p1.setFail(10);
		p1.setFailRate(0.1);

		ProducerHomeInfo p2 = new ProducerHomeInfo();
		p2.setTotal(200);
		p2.setFeature("PSI");
		p2.setFail(10);
		p2.setFailRate(0.05);

		pList.add(p1);
		pList.add(p2);
		return pList;
	}

	@Override
	public List<ConsumerDetailReport> getConsumerDetailReport(QueryCondition queryCondition) {
		// TODO Auto-generated method stub
		System.out.println("getProducerReport enter.");
		List<ConsumerDetailReport> pList = new ArrayList<ConsumerDetailReport>();
		ConsumerDetailReport pr1 = new ConsumerDetailReport();
		pr1.setCount(100);
		pr1.setMax(24);
		pr1.setMin(3);
		pr1.setAvg(6);
		pr1.setNinetyPercent(8);
		pr1.setPortletName("PSI");
		pr1.setError(5);
		pr1.setStd(0.05);

		ConsumerDetailReport pr2 = new ConsumerDetailReport();
		pr2.setCount(200);
		pr2.setMax(24);
		pr2.setMin(3);
		pr2.setAvg(6);
		pr2.setNinetyPercent(8);
		pr2.setPortletName("AAE");
		pr2.setError(10);
		pr2.setStd(0.05);

		pList.add(pr1);
		pList.add(pr2);
		return pList;
	}

	@Override
	public List<ConsumerRequestReport> getConsumerRequestReport(QueryCondition queryCondition) {
		// TODO Auto-generated method stub
		System.out.println("getConsumerReport enter.");
		List<ConsumerRequestReport> pList = new ArrayList<ConsumerRequestReport>();
		ConsumerRequestReport pr1 = new ConsumerRequestReport();
		pr1.setCount(100);
		pr1.setRequest("/portal/site/hpsc/public/home");
		pr1.setDurMax(10);
		pr1.setDurMin(2);
		pr1.setDurAvg(3);
		pr1.setDur90(4);
		pr1.setDurStd(0.1);
		pr1.setError(1);
		pr1.setPart("Home");

		ConsumerRequestReport pr2 = new ConsumerRequestReport();
		pr2.setCount(100);
		pr2.setRequest("/portal/site/hpsc/public/kb/search");
		pr2.setDurMax(10);
		pr2.setDurMin(2);
		pr2.setDurAvg(3);
		pr2.setDur90(4);
		pr2.setDurStd(0.1);
		pr2.setError(1);
		pr2.setPart("Search");

		pList.add(pr1);
		pList.add(pr2);
		return pList;
	}

	@Override
	public List<ProducerReport> getProducerReport(QueryCondition queryCondition) {
		// TODO Auto-generated method stub
		System.out.println("getWsrpReport enter.");
		List<ProducerReport> pList = new ArrayList<ProducerReport>();
		ProducerReport pr1 = new ProducerReport();
		pr1.setCount(100);
		pr1.setMax(10);
		pr1.setMin(3);
		pr1.setAvg(5);
		pr1.setError(10);
		pr1.setName("psiSupportOptions");
		pr1.setNinetyPercent(8);

		ProducerReport pr2 = new ProducerReport();
		pr2.setCount(200);
		pr2.setMax(10);
		pr2.setMin(3);
		pr2.setAvg(5);
		pr2.setError(20);
		pr2.setName("psiProductSelector");
		pr2.setNinetyPercent(8);

		pList.add(pr1);
		pList.add(pr2);
		return pList;
	}

	@Override
	public List<AcCommonDataWithPayLoad> getLogDetail(
			QueryCondition queryCondition) {
		// TODO Auto-generated method stub
		List<AcCommonDataWithPayLoad> result = new LinkedList<AcCommonDataWithPayLoad>();
		for (Long acid : queryCondition.getAcids()) {
			AcCommonDataWithPayLoad acCommonDataWithPayLoad = new AcCommonDataWithPayLoad();
			acCommonDataWithPayLoad.setAcid(acid);
			acCommonDataWithPayLoad.setCreated(new Date().getTime());
			acCommonDataWithPayLoad.setDuration(10);
			acCommonDataWithPayLoad.setName(null);
			result.add(acCommonDataWithPayLoad);
		}
		return result;
	}

	@Override
	public List<ConsumerRequestReport> getConsumerRequestDetailReport(
			QueryCondition queryCondition) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ProducerReport> getProducerDetailReoprt(
			QueryCondition queryCondition) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteDataInDB(QueryCondition queryCondition, boolean deleteAll) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handledBySql(String sql, Object[] args, RowCallbackHandler rch) {
		// TODO Auto-generated method stub
		
	}
}
