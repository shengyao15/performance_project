package com.hp.it.perf.ac.app.hpsc.statistics;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.hp.it.perf.ac.app.hpsc.beans.SPFPerformanceDBLog;
import com.hp.it.perf.ac.app.hpsc.beans.SPFPerformanceLog;
import com.hp.it.perf.ac.app.hpsc.beans.SPFPerformanceLog.Detail;
import com.hp.it.perf.ac.load.util.Calculator;
import com.hp.it.perf.ac.load.util.StatisticsUnit;
import com.hp.it.perf.ac.load.util.StatisticsUnits;
import com.hp.it.perf.ac.load.util.StatisticsUnits.LongStatisticsFilter;

public class PortalPerfStatisticProcessor implements
		StatisticProcessor<SPFPerformanceLog> {

	private StatisticsUnit wsrpStatisticsUnit = StatisticsUnits
			.newIntStatisticsUnit();

	private StatisticsUnit requestStatisticsUnit = StatisticsUnits
			.newIntStatisticsUnit();

	private StatisticsUnit otherStatisticsUnit = StatisticsUnits
			.newIntStatisticsUnit();

	private Set<String> wsrpNames = new HashSet<String>();

	private int nameSize = 25;

	public void onProcess(SPFPerformanceLog performanceLog) {
		for (Detail detail : performanceLog.getDetailList()) {
			switch (detail.getType()) {
			case WSRP_CALL:
				wsrpNames.add(detail.getName());
				nameSize = Math.max(nameSize, detail.getName().length());
				wsrpStatisticsUnit.add(detail.getName(), detail.getStatus(),
						detail.getStatusDetail());
				wsrpStatisticsUnit.setInt(detail.getDuration());
				break;
			case REQUEST:
				requestStatisticsUnit.add(detail.getName());
				requestStatisticsUnit.setInt(detail.getDuration());
			case PROFILE_CALL:
			case GROUPS_CALL:
				otherStatisticsUnit.add(detail.getType().name(),
						detail.getStatus());
				otherStatisticsUnit.setInt(detail.getDuration());
				break;
			default:
				break;
			}
		}
	}

	public Map<String, Calculator> getWsrpCallStatistics() {
		Map<String, Calculator> map = new HashMap<String, Calculator>();
		for (String wsrpCall : wsrpNames) {
			map.put(wsrpCall, Calculator.build(StatisticsUnits.filter(
					wsrpStatisticsUnit.toLongArray(wsrpCall),
					new LongStatisticsFilter() {

						@Override
						public boolean accept(long testValue) {
							return testValue >= 0;
						}
					})));
		}
		return map;
	}

	public Map<String, Integer> getWsrpCallErrorCount() {
		Map<String, Integer> map = new HashMap<String, Integer>();
		for (String wsrpCall : wsrpNames) {
			map.put(wsrpCall, wsrpStatisticsUnit.count(wsrpCall, "ERROR"));
		}
		return map;
	}

	public Calculator getProfileCallStatistics() {
		return Calculator.build(otherStatisticsUnit.toLongArray(
				Detail.Type.PROFILE_CALL.name(), "OK"));
	}

	public Calculator getGroupCallStatistics() {
		return Calculator.build(otherStatisticsUnit.toLongArray(
				Detail.Type.GROUPS_CALL.name(), "OK"));
	}

	public Map<String, Calculator> getRequestStatistics() {
		Map<String, Calculator> map = new HashMap<String, Calculator>();
		for (String request : requestStatisticsUnit.getLabels()) {
			map.put(request, Calculator.build(requestStatisticsUnit
					.toLongArray(request)));
		}
		return map;
	}

	public void onProcess2(SPFPerformanceDBLog performanceLog) {
		for (com.hp.it.perf.ac.app.hpsc.beans.SPFPerformanceDBLog.Detail detail : performanceLog.getDetails()) {
			if(detail.getType().equals("WSRP_CALL")){
				wsrpNames.add(detail.getName());
				nameSize = Math.max(nameSize, detail.getName().length());
				wsrpStatisticsUnit.add(detail.getName(), detail.getStatus(),
						detail.getStatusDetail());
				wsrpStatisticsUnit.setInt(detail.getDuration());
			} else if (detail.getType().equals("PROFILE_CALL")|| detail.getType().equals("GROUPS_CALL")){
				otherStatisticsUnit.add(detail.getType(),
						detail.getStatus());
				otherStatisticsUnit.setInt(detail.getDuration());
			}
		}
	}
	
	@Override
	public void printTo(PrintStream out) {
		nameSize++;
		out.printf(
				"%"
						+ nameSize
						+ "s  COUNT        MIN    AVERAGE        MAX        90%%        STD    ERROR%n",
				"PORTLET_NAME");
		Map<String, Calculator> wsrpCallStat = getWsrpCallStatistics();
		Map<String, Integer> wsrpCallErrorCount = getWsrpCallErrorCount();
		String[] portlets = wsrpCallStat.keySet().toArray(new String[0]);
		Arrays.sort(portlets);
		for (String portlet : portlets) {
			Calculator cal = wsrpCallStat.get(portlet);
			int error = wsrpCallErrorCount.get(portlet);
			printItem(out, portlet, cal, error);
		}
		printItem(out, Detail.Type.PROFILE_CALL.name(),
				getProfileCallStatistics(), otherStatisticsUnit.count(
						Detail.Type.PROFILE_CALL.name(), "ERROR"));
		printItem(out, Detail.Type.GROUPS_CALL.name(),
				getGroupCallStatistics(), otherStatisticsUnit.count(
						Detail.Type.GROUPS_CALL.name(), "ERROR"));
	}

	private void printItem(PrintStream out, String name, Calculator cal,
			int error) {
		out.printf(
				"%" + nameSize + "s %6d %10d %10d %10d %10d %10.2f %8d\n",
				name,
				cal.getCount(),
				Math.max(0, cal.getMin()),
				(int) cal.getMean(),
				cal.getMax() == Long.MAX_VALUE? 0:cal.getMax(),
				cal.getPercentPoint(0.9),
				Double.isNaN(cal.getStandardDeviation()) ? 0 : cal
						.getStandardDeviation(), error);
	}

	@Override
	public String getName() {
		return "WSRP/Portlet Performance Statistics Report";
	}
}
