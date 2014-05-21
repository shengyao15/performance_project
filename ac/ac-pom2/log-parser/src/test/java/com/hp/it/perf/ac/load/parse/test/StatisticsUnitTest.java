package com.hp.it.perf.ac.load.parse.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import java.util.Random;

import org.junit.Test;

import com.hp.it.perf.ac.load.util.StatisticsUnit;
import com.hp.it.perf.ac.load.util.StatisticsUnits;

public class StatisticsUnitTest {

	@Test
	public void testIntStatistics() {
		StatisticsUnit fastIntStatisticsUnit = StatisticsUnits
				.newFastIntStatisticsUnit();
		StatisticsUnit intStatisticsUnit = StatisticsUnits
				.newIntStatisticsUnit();
		Random random = new Random();
		int loop = random.nextInt(1 << 12);
		int pos = 0;
		String label = "test";
		for (int i = 0; i < loop; i++) {
			int nextInc = random.nextInt(1 << 12);
			fastIntStatisticsUnit.add(label);
			fastIntStatisticsUnit.setInt(pos);
			intStatisticsUnit.add(label);
			intStatisticsUnit.setInt(pos);
			pos += nextInc;
			for (int j = 0; j < nextInc; j++) {
				fastIntStatisticsUnit.add();
				intStatisticsUnit.add();
			}
		}
		assertThat("same aray", fastIntStatisticsUnit.toIntArray(label),
				is(equalTo(intStatisticsUnit.toIntArray(label))));
	}
}
