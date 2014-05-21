package com.hp.it.perf.ac.app.hpsc.statistics;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.hp.it.perf.ac.app.hpsc.beans.SPFWebAccessLog;
import com.hp.it.perf.ac.load.common.AcKeyValue;
import com.hp.it.perf.ac.load.process.PivotView;
import com.hp.it.perf.ac.load.process.PivotViewBuilder;
import com.hp.it.perf.ac.load.process.PivotViewCallback;
import com.hp.it.perf.ac.load.process.support.AcMathAggregators;
import com.hp.it.perf.ac.load.process.support.FieldGetter;

public class SPFWebAccessStatisticsProcessor implements
		StatisticProcessor<SPFWebAccessLog> {

	private PivotViewCallback pivotViewCallback;

	public SPFWebAccessStatisticsProcessor() {
		PivotViewBuilder builder = new PivotViewBuilder();
		builder.addGroupField("ip", new FieldGetter(SPFWebAccessLog.class,
				"remoteHost"));
		builder.addValueField("count", AcMathAggregators.count());
		pivotViewCallback = builder.pivotCallback();
	}

	@Override
	public void onProcess(SPFWebAccessLog bean) {
		pivotViewCallback.apply(bean);
	}

	@Override
	public void printTo(PrintStream out) {
		PivotView pivotView = pivotViewCallback.createView();
		Iterator<Map<String, Object>> iter = pivotView.listAll();
		List<AcKeyValue<String, Long>> list = new ArrayList<AcKeyValue<String, Long>>();
		while (iter.hasNext()) {
			Map<String, Object> map = iter.next();
			list.add(new AcKeyValue<String, Long>((String) map.get("ip"),
					(Long) map.get("count")));
		}
		Collections.sort(list, new Comparator<AcKeyValue<String, Long>>() {

			@Override
			public int compare(AcKeyValue<String, Long> o1,
					AcKeyValue<String, Long> o2) {
				return -o1.getValue().compareTo(o2.getValue());
			}
		});
		out.println("\t\tIP\t\tCount");
		for (AcKeyValue<String, Long> item : list) {
			out.println(item.getKey() + "\t\t" + item.getValue());
		}
	}

	@Override
	public String getName() {
		return "Web Access Log Statistics Report";
	}
}
