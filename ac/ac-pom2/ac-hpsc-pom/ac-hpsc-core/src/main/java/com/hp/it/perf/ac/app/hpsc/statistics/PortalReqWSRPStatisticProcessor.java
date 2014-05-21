package com.hp.it.perf.ac.app.hpsc.statistics;

import java.io.PrintStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import com.hp.it.perf.ac.app.hpsc.beans.SPFPerformanceDBLog;
import com.hp.it.perf.ac.app.hpsc.beans.SPFPerformanceLog;
import com.hp.it.perf.ac.app.hpsc.beans.SPFPerformanceLog.Detail;
import com.hp.it.perf.ac.load.common.AcPredicate;
import com.hp.it.perf.ac.load.common.AcReduceCallback;
import com.hp.it.perf.ac.load.content.AcContentLine;
import com.hp.it.perf.ac.load.content.AcContentMetadata;
import com.hp.it.perf.ac.load.process.PivotView;
import com.hp.it.perf.ac.load.process.PivotViewBuilder;
import com.hp.it.perf.ac.load.process.PivotViewCallback;
import com.hp.it.perf.ac.load.process.support.AcMathAggregators;
import com.hp.it.perf.ac.load.process.support.FieldGetter;
import com.hp.it.perf.ac.load.util.Calculator;
import com.hp.it.perf.ac.load.util.StatisticsUnit;
import com.hp.it.perf.ac.load.util.StatisticsUnits;

public class PortalReqWSRPStatisticProcessor implements
		StatisticProcessor2<SPFPerformanceLog> {

	private static final int WORST_COUNT = Integer.getInteger("WorstCount", 5);
	private static final Integer WORST_LEVEL = Integer.getInteger("WorstLevel",
			5000);
	private PivotViewCallback pivotViewCallback;
	private DecimalFormat numberFormat = new DecimalFormat("#0.00");

	static class RequestPartPerfUnit {
		String requestURL;
		String partName;// WSRP[PORTLET], GROUP, CONSUMER OTHER, REQUEST
		boolean success;
		int duration;
		SPFPerformanceLog log;
		AcContentLine line;
		AcContentMetadata metadata;
	}
	
	static class DBRequestPartPerfUnit {
		String requestURL;
		String partName;// WSRP[PORTLET], GROUP, CONSUMER OTHER, REQUEST
		boolean success;
		int duration;
		SPFPerformanceDBLog log;
		AcContentLine line;
		AcContentMetadata metadata;
	}

	public PortalReqWSRPStatisticProcessor() {
		PivotViewBuilder builder = new PivotViewBuilder();
		builder.addGroupField("request", new FieldGetter(
				RequestPartPerfUnit.class, "requestURL"));
		builder.addGroupField("part", new FieldGetter(
				RequestPartPerfUnit.class, "partName"));
		builder.addValueField("duration",
				new AcReduceCallback<Object, Calculator>() {

					@Override
					public Object createReduceContext() {
						return StatisticsUnits.newIntStatisticsUnit();
					}

					@Override
					public void reduce(Object item, Object context) {
						RequestPartPerfUnit unit = (RequestPartPerfUnit) item;
						StatisticsUnit stat = (StatisticsUnit) context;
						stat.add();
						stat.setInt(unit.duration);
					}

					@Override
					public Calculator getResult(Object context) {
						return Calculator.build(((StatisticsUnit) context)
								.toLongArray());
					}
				});
		builder.addValueField("error",
				AcMathAggregators.count(new AcPredicate<Object>() {

					@Override
					public boolean apply(Object data) {
						return !((RequestPartPerfUnit) data).success;
					}
				}));
		builder.addValueField("worstRequest",
				new AcReduceCallback<Object, RequestPartPerfUnit[]>() {

					@Override
					public Object createReduceContext() {
						return new PriorityQueue<RequestPartPerfUnit>(5,
								new Comparator<RequestPartPerfUnit>() {

									@Override
									public int compare(RequestPartPerfUnit o1,
											RequestPartPerfUnit o2) {
										return o1.duration < o2.duration ? -1
												: (o1.duration == o2.duration ? 0
														: 1);
									}
								});
					}

					@Override
					@SuppressWarnings("unchecked")
					public void reduce(Object item, Object context) {
						RequestPartPerfUnit unit = (RequestPartPerfUnit) item;
						if (!unit.success) {
							return;
						}
						if (unit.duration < WORST_LEVEL) {
							return;
						}
						PriorityQueue<RequestPartPerfUnit> queue = (PriorityQueue<RequestPartPerfUnit>) context;
						queue.add(unit);
						if (queue.size() > WORST_COUNT) {
							queue.remove();
						}
					}

					@Override
					public RequestPartPerfUnit[] getResult(Object context) {
						PriorityQueue<?> queue = (PriorityQueue<?>) context;
						RequestPartPerfUnit[] ret = new RequestPartPerfUnit[queue
								.size()];
						for (int i = 0; i < ret.length; i++) {
							ret[i] = (RequestPartPerfUnit) queue.poll();
						}
						return ret;
					}

				});
		builder.addSortField("request", String.class);
		builder.addSortField("part", String.class);
		pivotViewCallback = builder.pivotCallback();
	}

	public void onProcess(SPFPerformanceLog performanceLog) {
		throw new UnsupportedOperationException();
	}

	public void onProcess(SPFPerformanceLog performanceLog, AcContentLine line,
			AcContentMetadata metadata) {
		String requestURL = null;
		int requestDuration = 0;
		int maxWSRPDuration = 0;
		int otherDuration = 0;
		List<RequestPartPerfUnit> partUnits = new ArrayList<RequestPartPerfUnit>();
		for (Detail detail : performanceLog.getDetailList()) {
			RequestPartPerfUnit unit;
			switch (detail.getType()) {
			case WSRP_CALL:
				unit = new RequestPartPerfUnit();
				unit.partName = detail.getType().name() + "["
						+ detail.getName() + "]";
				unit.duration = detail.getDuration();
				unit.success = "OK".equals(detail.getStatus());
				partUnits.add(unit);
				maxWSRPDuration = Math.max(detail.getDuration(),
						maxWSRPDuration);
				break;
			case REQUEST:
				requestURL = detail.getName();
				requestDuration = detail.getDuration();
				unit = new RequestPartPerfUnit();
				unit.duration = detail.getDuration();
				unit.partName = detail.getType().name();
				unit.success = "OK".equals(detail.getStatus());
				partUnits.add(unit);
				break;
			case PROFILE_CALL:
			case GROUPS_CALL:
				unit = new RequestPartPerfUnit();
				unit.duration = detail.getDuration();
				unit.partName = detail.getType().name();
				unit.success = "OK".equals(detail.getStatus());
				otherDuration += detail.getDuration();
				partUnits.add(unit);
				break;
			default:
				break;
			}
		}
		for (RequestPartPerfUnit partUnit : partUnits) {
			partUnit.requestURL = requestURL;
		}
		if (partUnits.size() == 1) {
			// only request, maybe redirect, or local portlets only
			RequestPartPerfUnit unit = partUnits.get(0);
			unit.partName = "REQUEST[NO REMOTE]";
		} else {
			RequestPartPerfUnit unit = new RequestPartPerfUnit();
			unit.requestURL = requestURL;
			unit.partName = "PORTAL_OTHERS";
			unit.duration = requestDuration - maxWSRPDuration - otherDuration;
			unit.success = true;
			partUnits.add(unit);
		}
		for (RequestPartPerfUnit partUnit : partUnits) {
			partUnit.log = performanceLog;
			partUnit.line = line;
			partUnit.metadata = metadata;
			pivotViewCallback.apply(partUnit);
		}
	}

	@Override
	public void printTo(PrintStream out) {
		PivotView pivotView = pivotViewCallback.createView();
		Iterator<Map<String, Object>> iter1 = pivotView.listAll();
		out.println();
		out.println("REQUEST, PART, COUNT, DUR-AVE, DUR-MIN, DUR-MAX, DUR-STD, DUR-90%, ERROR");
		while (iter1.hasNext()) {
			printLine(out, iter1.next());
		}
		out.println();
		out.println();
		out.println("=== WORST REQUESTS ===");
		Iterator<Map<String, Object>> iter2 = pivotView.listAll();
		while (iter2.hasNext()) {
			Map<String, Object> item = iter2.next();
			String request = (String) item.get("request");
			String part = (String) item.get("part");
			RequestPartPerfUnit[] worstRequests = (RequestPartPerfUnit[]) item
					.get("worstRequest");
			if (worstRequests.length == 0)
				continue;
			out.println();
			out.println(request + ", " + part);
			for (int i = worstRequests.length - 1; i >= 0; i--) {
				RequestPartPerfUnit unit = worstRequests[i];
				out.println("\t" + unit.duration + "\t@"
						+ unit.metadata.getBasename() + " (Line:"
						+ unit.line.getLineInfo().getLineNum() + ")");
				out.println("\t\t" + unit.line.getCurrentLines());
			}
		}
	}

	private void printLine(PrintStream out, Map<String, Object> item) {
		String request = (String) item.get("request");
		String part = (String) item.get("part");
		Calculator cal = (Calculator) item.get("duration");
		Number errorCount = (Number) item.get("error");
		StringBuilder builder = new StringBuilder();
		builder.append(request).append(", ");
		builder.append(part).append(", ");
		builder.append(cal.getCount()).append(", ");
		builder.append(numberFormat.format(cal.getMean())).append(", ");
		builder.append(Math.max(0, cal.getMin())).append(", ");
		builder.append(cal.getMax() == Long.MAX_VALUE ? 0 : cal.getMax())
				.append(", ");
		builder.append(
				Double.isNaN(cal.getStandardDeviation()) ? 0 : numberFormat
						.format(cal.getStandardDeviation())).append(", ");
		builder.append(cal.getPercentPoint(0.9)).append(", ");
		builder.append(errorCount.intValue());
		out.println(builder.toString());
	}

	@Override
	public String getName() {
		return "Portal Request/WSRP Breakdown Reports";
	}

	public void onProcess2(SPFPerformanceDBLog performanceDBLog, AcContentLine line,
			AcContentMetadata metadata) {
		String requestURL = null;
		int requestDuration = 0;
		int maxWSRPDuration = 0;
		int otherDuration = 0;
		List<DBRequestPartPerfUnit> partUnits = new ArrayList<DBRequestPartPerfUnit>();
		for (com.hp.it.perf.ac.app.hpsc.beans.SPFPerformanceDBLog.Detail detail : performanceDBLog.getDetails()) {
			DBRequestPartPerfUnit unit;
			if(detail.getType().equals("WSRP_CALL")){
				unit = new DBRequestPartPerfUnit();
				unit.partName = detail.getType() + "["
						+ detail.getName() + "]";
				unit.duration = detail.getDuration();
				unit.success = "OK".equals(detail.getStatus());
				partUnits.add(unit);
				maxWSRPDuration = Math.max(detail.getDuration(),
						maxWSRPDuration);
			}else if (detail.getType().equals("REQUEST")){
				requestURL = detail.getName();
				requestDuration = detail.getDuration();
				unit = new DBRequestPartPerfUnit();
				unit.duration = detail.getDuration();
				unit.partName = detail.getType();
				unit.success = "OK".equals(detail.getStatus());
				partUnits.add(unit);
			}else if (detail.getType().equals("PROFILE_CALL")|| detail.getType().equals("GROUPS_CALL")){
				unit = new DBRequestPartPerfUnit();
				unit.duration = detail.getDuration();
				unit.partName = detail.getType();
				unit.success = "OK".equals(detail.getStatus());
				otherDuration += detail.getDuration();
				partUnits.add(unit);
			}
		}
		for (DBRequestPartPerfUnit partUnit : partUnits) {
			partUnit.requestURL = requestURL;
		}
		if (partUnits.size() == 1) {
			// only request, maybe redirect, or local portlets only
			DBRequestPartPerfUnit unit = partUnits.get(0);
			unit.partName = "REQUEST[NO REMOTE]";
		} else {
			DBRequestPartPerfUnit unit = new DBRequestPartPerfUnit();
			unit.requestURL = requestURL;
			unit.partName = "PORTAL_OTHERS";
			unit.duration = requestDuration - maxWSRPDuration - otherDuration;
			unit.success = true;
			partUnits.add(unit);
		}
		for (DBRequestPartPerfUnit partUnit : partUnits) {
			partUnit.log = performanceDBLog;
			partUnit.line = line;
			partUnit.metadata = metadata;
			pivotViewCallback.apply(partUnit);
		}
	}
	
}
