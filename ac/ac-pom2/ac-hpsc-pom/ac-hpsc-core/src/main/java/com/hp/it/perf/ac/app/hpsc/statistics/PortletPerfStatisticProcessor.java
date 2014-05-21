package com.hp.it.perf.ac.app.hpsc.statistics;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.hp.it.perf.ac.app.hpsc.beans.PortletPerformanceLog;
import com.hp.it.perf.ac.app.hpsc.beans.PortletPerformanceLog.Detail;
import com.hp.it.perf.ac.load.common.AcReduceCallback;
import com.hp.it.perf.ac.load.process.PivotView;
import com.hp.it.perf.ac.load.process.PivotViewBuilder;
import com.hp.it.perf.ac.load.process.PivotViewCallback;
import com.hp.it.perf.ac.load.process.support.FieldGetter;
import com.hp.it.perf.ac.load.util.Calculator;
import com.hp.it.perf.ac.load.util.StatisticsUnit;
import com.hp.it.perf.ac.load.util.StatisticsUnits;

public class PortletPerfStatisticProcessor implements
		StatisticProcessor<PortletPerformanceLog> {

	private StatisticsUnit backendUnit = StatisticsUnits.newIntStatisticsUnit();

	private PivotViewCallback pivotView;

	private int NAME_SIZE_INIT = 25;
	private int nameSize = NAME_SIZE_INIT;

	public PortletPerfStatisticProcessor() {
		PivotViewBuilder builder = new PivotViewBuilder();
		builder.addGroupField("portletName", new FieldGetter(
				PortletPerformanceLog.class, "portletName"));
		builder.addGroupField("portletMethod", new FieldGetter(
				PortletPerformanceLog.class, "portletMethod"));
		builder.addSortField("portletName", String.class);
		builder.addSortField("portletMethod", String.class);
		builder.addValueField("duration",
				new AcReduceCallback<Object, StatisticsUnit>() {

					@Override
					public Object createReduceContext() {
						return StatisticsUnits.newIntStatisticsUnit();
					}

					@Override
					public void reduce(Object item, Object context) {
						PortletPerformanceLog unit = (PortletPerformanceLog) item;
						StatisticsUnit stat = (StatisticsUnit) context;
						for (PortletPerformanceLog.Detail detail : unit
								.getDetails()) {
							stat.add(detail.getName());
							stat.setInt(detail.getDuration());
						}
					}

					@Override
					public StatisticsUnit getResult(Object context) {
						return (StatisticsUnit) context;
					}
				});
		pivotView = builder.pivotCallback();
	}

	public void onProcess(PortletPerformanceLog performanceLog) {
		for (Detail detail : performanceLog.getDetails()) {
			backendUnit.add(detail.getName());
			backendUnit.setInt(detail.getDuration());
		}
		pivotView.apply(performanceLog);
	}

	public Map<String, Calculator> getBackendCallStatistics(StatisticsUnit unit) {
		Map<String, Calculator> map = new HashMap<String, Calculator>();
		for (String backendCall : unit.getLabels()) {
			nameSize = Math.max(nameSize, backendCall.length());
			map.put(backendCall,
					Calculator.build(unit.toLongArray(backendCall)));
		}
		return map;
	}

	@Override
	public void printTo(PrintStream out) {
		out.println("[All Backend]");
		printBackend(backendUnit, out);
		// print each portlet
		PivotView view = pivotView.createView();
		Iterator<Map<String, Object>> iter = view.listAll();
		while (iter.hasNext()) {
			Map<String, Object> map = iter.next();
			out.println();
			out.println("[" + map.get("portletName") + "("
					+ map.get("portletMethod") + ")]");
			printBackend((StatisticsUnit) map.get("duration"), out);
		}
	}

	private void printBackend(StatisticsUnit stat, PrintStream out) {
		nameSize = NAME_SIZE_INIT + 1;
		Map<String, Calculator> backendCallStat = getBackendCallStatistics(stat);
		out.printf("%" + nameSize + "s%10s%10s%10s%10s%10s%10s%n", "NAME",
				"COUNT", "AVE", "  MIN", "  MAX", "STV", "90%");
		String[] backendCalls = backendCallStat.keySet().toArray(new String[0]);
		Arrays.sort(backendCalls);
		for (String portlet : backendCalls) {
			Calculator cal = backendCallStat.get(portlet);
			printItem(out, portlet, cal);
		}
	}

	private void printItem(PrintStream out, String name, Calculator cal) {
		out.printf(
				"%" + nameSize + "s%10s%10.3f%10.3f%10.3f%10.3f%10.3f%n",
				name,
				cal.getCount(),
				cal.getMean() / 1000.0,
				Math.max(0, cal.getMin()) / 1000.0,
				(cal.getMax() == Long.MAX_VALUE ? 0 : cal.getMax()) / 1000.0,
				Double.isNaN(cal.getStandardDeviation()) ? 0 : cal
						.getStandardDeviation() / 1000.0, cal
						.getPercentPoint(0.9) / 1000.0);
	}

	@Override
	public String getName() {
		return "Portlet Performance Log Statistics Report";
	}
}
