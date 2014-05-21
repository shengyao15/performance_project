package com.hp.it.perf.ac.app.hpsc.realtime;

import java.util.Collection;
import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.hp.it.perf.ac.common.realtime.RealTimeBean;

public class DBTest {

	public static void main(String[] args) {

		ApplicationContext ctx = new GenericXmlApplicationContext(
				"spring/service-config.xml");

		System.out.println("ctx: " + ctx);

		MongoOperations mongoOperation = (MongoOperations) ctx
				.getBean("mongoTemplate");

		List<RealTimeBean> datas = mongoOperation.find(new Query(Criteria
				.where("value").is(12345)), RealTimeBean.class);
		for (RealTimeBean d : datas)
			System.out.println(d.toString());

		System.out.println("********** service test ***********");
		RealtimeService service = (RealtimeService) ctx
				.getBean("realtimeService");
		Collection<RealTimeBean> res = service.getAllRealtimeData();
		for (RealTimeBean r : res)
			System.out.println(r.toString());
	}
}
