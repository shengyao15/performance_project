package com.hp.it.perf.ac.common.realtime;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public enum GranularityType implements Serializable {
	
	Minute(1, 1, TimeUnit.MINUTES), TenMinutes(2, 10, TimeUnit.MINUTES), FifteeniMinutes(3, 
			15, TimeUnit.MINUTES), ThirtyMinutes(4, 30, TimeUnit.MINUTES), Hour(5, 1,
			TimeUnit.HOURS), Day(6, 1, TimeUnit.DAYS);
	
	private int index;
	private long time;
	private TimeUnit timeUnit;
	
	private GranularityType(int index, long time, TimeUnit timeUnit) {
		this.index = index;
		this.time = time;
		this.timeUnit = timeUnit;
	}
	
	public int getIndex() {
		return index;
	}
	
	public static GranularityType getGranulityType(int index) {
		switch(index) {
			case 1: return Minute;
			case 2: return TenMinutes;
			case 3: return FifteeniMinutes;
			case 4: return ThirtyMinutes;
			case 5: return Hour;
			case 6: return Day;
		}
			
		return null;
	}
	
	public long getMilSecondTime() {
		return timeUnit.toMillis(time);
	}
	
	public long getTime(TimeUnit timeUnit) {
		return timeUnit.convert(time, this.timeUnit);
	}
	
	public static List<GranularityType> getGranularityTypeList() {
		return Arrays.asList(Minute, TenMinutes, FifteeniMinutes, ThirtyMinutes, Hour, Day);
	}

}
