package com.hp.hpsc.logview.util;

import static org.junit.Assert.*;

import org.junit.Assert;
import org.junit.Test;

import com.hp.hpsc.logview.util.Configurations;
import com.hp.hpsc.logview.util.Configurations.ConfigurationException;

public class ConfigurationsTest {

	@Test
	public void testGetConfigString() throws ConfigurationException {
		String value = Configurations.getConfigString(Configurations.ConfugrationKeys.MAX_HOST_CONNECTIONS);
		System.out.println("the result is -- "+value);
		Assert.assertNotNull(value);
	}

	@Test
	public void testGetConfigInt() throws ConfigurationException {
		int value = Configurations.getConfigInt(Configurations.ConfugrationKeys.MAX_TOTAL_CONNECTIONS);
		System.out.println("the result is -- "+value);
		Assert.assertTrue(value > 0);
	}

	@Test
	public void testGetConfigArray() throws ConfigurationException {
		String[] array = Configurations.getConfigArray(Configurations.ConfugrationKeys.ACCESSLOG_FOLDER_URL);
		for(String url: array){
			System.out.println("the folder is -- " + url);
		}
		Assert.assertTrue(array.length > 0);
	}
}
