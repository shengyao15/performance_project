package com.hp.it.perf.ac.app.hpsc.statistics;

import java.io.PrintStream;
import java.util.Iterator;
import java.util.Map;

import com.hp.it.perf.ac.app.hpsc.beans.SPFPortalLog;
import com.hp.it.perf.ac.load.common.AcMapper;
import com.hp.it.perf.ac.load.common.AcPredicate;
import com.hp.it.perf.ac.load.common.AcReduceCallback;
import com.hp.it.perf.ac.load.content.AcContentLine;
import com.hp.it.perf.ac.load.content.AcContentMetadata;
import com.hp.it.perf.ac.load.process.PivotView;
import com.hp.it.perf.ac.load.process.PivotViewBuilder;
import com.hp.it.perf.ac.load.process.PivotViewCallback;
import com.hp.it.perf.ac.load.process.support.AcMathAggregators;
import com.hp.it.perf.ac.load.util.ZipStringAppender;

public class PortalErrorStatisticProcessor implements
		StatisticProcessor2<SPFPortalLog> {

	private PivotViewCallback pivotViewCallback;
	private PivotViewCallback errorPivotViewCallback;

	public PortalErrorStatisticProcessor() {
		PivotViewBuilder builder = new PivotViewBuilder();
		AcPredicate<Object> filter = new AcPredicate<Object>() {

			@Override
			public boolean apply(Object data) {
				SPFPortalLog log = (SPFPortalLog) ((Object[]) data)[0];
				return log.getRenderError() != null;
			}

		};
		builder.addFilter("renderError", filter);
		AcMapper<Object, Object> titleMapper = new AcMapper<Object, Object>() {

			@Override
			public Object apply(Object object) {
				SPFPortalLog log = (SPFPortalLog) ((Object[]) object)[0];
				return log.getRenderError().getPortletTitle();
			}

		};
		builder.addGroupField("portletTitle", titleMapper);
		AcMapper<Object, Object> errorCategoryMapper = new AcMapper<Object, Object>() {

			@Override
			public Object apply(Object object) {
				SPFPortalLog log = (SPFPortalLog) ((Object[]) object)[0];
				String errorMsg = log.getRenderError().getErrorMessage()
						.split("\\n")[0];
				if (errorMsg
						.startsWith("com.vignette.portal.portlet.website.PortletTimedOutException")) {
					errorMsg = errorMsg.split(":")[0];
				}
				return errorMsg;
			}

		};
		builder.addGroupField("errorCategory", errorCategoryMapper);
		builder.addValueField("count", AcMathAggregators.count());
		builder.addValueField("errorDetails",
				new AcReduceCallback<Object, String>() {

					@Override
					public Object createReduceContext() {
						return new ZipStringAppender();
					}

					@Override
					public void reduce(Object item, Object context) {
						AcContentLine content = (AcContentLine) ((Object[]) item)[1];
						AcContentMetadata metadata = (AcContentMetadata) ((Object[]) item)[2];
						ZipStringAppender builder = (ZipStringAppender) context;
						builder.append("\n==== From ").append(
								metadata.getBasename());
						builder.append("\n");
						builder.append(content.getLineInfo());
						builder.append("\n----\n");
						builder.append(content.getCurrentLines());
					}

					@Override
					public String getResult(Object context) {
						return ((ZipStringAppender) context).toString();
					}

				});
		pivotViewCallback = builder.pivotCallback();
		builder = new PivotViewBuilder();
		builder.addFilter("renderError", filter);
		builder.addGroupField("errorCategory", errorCategoryMapper);
		builder.addValueField("count", AcMathAggregators.count());
		errorPivotViewCallback = builder.pivotCallback();
	}

	public void onProcess(SPFPortalLog portalLog, AcContentLine content,
			AcContentMetadata metadata) {
		pivotViewCallback.apply(new Object[] { portalLog, content, metadata });
		errorPivotViewCallback.apply(new Object[] { portalLog });
	}

	@Override
	public void printTo(PrintStream out) {
		PivotView pivotView = pivotViewCallback.createView();
		Iterator<Map<String, Object>> iter = pivotView.listAll();
		out.println();
		out.println("PORTLET_TITLE, ERROR_COUNT, ERROR_INFO");
		while (iter.hasNext()) {
			Map<String, Object> item = iter.next();
			String portlet = (String) item.get("portletTitle");
			Number errorCount = (Number) item.get("count");
			String errorCategory = (String) item.get("errorCategory");
			StringBuilder builder = new StringBuilder();
			builder.append(portlet).append(", ");
			builder.append(errorCount).append(", ");
			builder.append(errorCategory);
			out.println(builder.toString());
		}
		PivotView errorPivotView = errorPivotViewCallback.createView();
		Iterator<Map<String, Object>> errorIter = errorPivotView.listAll();
		out.println();
		out.println("ERROR_INFO, ERROR_COUNT");
		while (errorIter.hasNext()) {
			Map<String, Object> item = errorIter.next();
			Number errorCount = (Number) item.get("count");
			String errorCategory = (String) item.get("errorCategory");
			StringBuilder builder = new StringBuilder();
			builder.append(errorCategory).append(",");
			builder.append(errorCount);
			out.println(builder.toString());
		}
		out.println();
		Iterator<Map<String, Object>> iter2 = pivotView.listAll();
		while (iter2.hasNext()) {
			Map<String, Object> item = iter2.next();
			String portlet = (String) item.get("portletTitle");
			Number errorCount = (Number) item.get("count");
			String errorCategory = (String) item.get("errorCategory");
			String errorDetails = (String) item.get("errorDetails");
			out.print(portlet + " - " + errorCategory + "(" + errorCount + ")");
			out.println(errorDetails);
		}
	}

	@Override
	public void onProcess(SPFPortalLog bean) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getName() {
		return "Vignette Portal Error Statistics Report";
	}

}
