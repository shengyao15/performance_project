package com.hp.it.perf.ac.app.hpsc.statistics;

import java.io.PrintStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import com.hp.it.perf.ac.app.hpsc.beans.OpenPortalLog;
import com.hp.it.perf.ac.load.content.AcContentMetadata;
import com.hp.it.perf.ac.load.util.StatisticsUnit;
import com.hp.it.perf.ac.load.util.StatisticsUnits;

public class OpenPortalLogProcessor implements
		StatisticProcessor<OpenPortalLog>, StatisticProcessorLifecycle {

	private StatisticsUnit unit = StatisticsUnits.newLongStatisticsUnit();

	private AcContentMetadata current;

	@Override
	public void onProcess(OpenPortalLog bean) {
		unit.add(bean.getHpscDiagnosticId(), bean.toString());
		unit.setLong(bean.getDateTime().getTime());
		int[] past = unit.indexesFor(bean.getHpscDiagnosticId());
		if ("SEVERE".equals(bean.getLogLevel())) {
			System.out
					.printf("startOn: %tT, duration: %d ms, old-msg: %s (%d)%n=>%s%n",
							new Date(unit.getLong(past[0])),
							bean.getDateTime().getTime()
									- unit.getLong(past[0]),
							Arrays.toString(unit.getLabels(past[0])),
							past.length, bean);
			for(int i:past){
				System.out.println(Arrays.toString(unit.getLabels(i)));
			}
		}
	}

	public void printTo(PrintStream out) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStart(AcContentMetadata metadata) {
		current = metadata;
	}

	@Override
	public void onEnd(AcContentMetadata metadata) {
	}

	@Override
	public String getName() {
		return "Open Portal Log Reports";
	}

}
