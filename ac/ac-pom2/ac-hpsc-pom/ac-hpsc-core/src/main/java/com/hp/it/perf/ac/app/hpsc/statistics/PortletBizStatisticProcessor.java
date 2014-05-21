package com.hp.it.perf.ac.app.hpsc.statistics;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.hp.it.perf.ac.app.hpsc.beans.PortletBusinessLog;
import com.hp.it.perf.ac.load.util.Calculator;
import com.hp.it.perf.ac.load.util.StatisticsUnit;
import com.hp.it.perf.ac.load.util.StatisticsUnits;

public class PortletBizStatisticProcessor implements
		StatisticProcessor<PortletBusinessLog> {

	private StatisticsUnit portletUnit = StatisticsUnits.newIntStatisticsUnit();

	private Set<String> portlets = new HashSet<String>();

	private int nameSize = 25;

	public void onProcess(PortletBusinessLog log) {
		String label = log.getPortletName() + "(" + log.getPortletMethod()
				+ ")";
		portlets.add(label);
		portletUnit.add(label, log.getStatus());
		portletUnit.setInt(log.getDuration());
	}

	public Map<String, Calculator> getPortletStatistics() {
		Map<String, Calculator> map = new HashMap<String, Calculator>();
		for (String portlet : portletUnit.getLabels()) {
			nameSize = Math.max(nameSize, portlet.length());
			map.put(portlet, Calculator.build(portletUnit.toLongArray(portlet)));
		}
		return map;
	}
	
	public Map<String, Integer> getPortletErrorStatistics() {
		Map<String, Integer> map = new HashMap<String, Integer>();
		for (String portlet : portletUnit.getLabels()) {
			map.put(portlet, portletUnit.count(portlet, "FATAL"));
		}
		return map;
	}

	@Override
	public void printTo(PrintStream out) {
		nameSize++;
		Map<String, Calculator> portletStat = getPortletStatistics();
		out.printf("%" + nameSize + "s%10s%10s%10s%10s%10s%10s%10s%n", "NAME",
				"COUNT", "AVE", "  MIN", "  MAX", "STV", "90%", "ERROR");
		Map<String, Integer> portletErrorStat = getPortletErrorStatistics();
		String[] backendCalls = portlets.toArray(new String[0]);
		Arrays.sort(backendCalls);
		for (String portlet : backendCalls) {
			Calculator cal = portletStat.get(portlet);
			int error = portletErrorStat.get(portlet);
			printItem(out, portlet, cal, error);
		}
	}

	private void printItem(PrintStream out, String name, Calculator cal,
			int error) {
		out.printf(
				"%" + nameSize + "s%10s%10.3f%10.3f%10.3f%10.3f%10.3f%10s%n",
				name,
				cal.getCount(),
				cal.getMean() / 1000.0,
				Math.max(0, cal.getMin()) / 1000.0,
				(cal.getMax() == Long.MAX_VALUE ? 0 : cal.getMax()) / 1000.0,
				Double.isNaN(cal.getStandardDeviation()) ? 0 : cal
						.getStandardDeviation() / 1000.0, cal
						.getPercentPoint(0.9) / 1000.0, error);
	}

	@Override
	public String getName() {
		return "Portlet Business Log Statistics Report";
	}
}
