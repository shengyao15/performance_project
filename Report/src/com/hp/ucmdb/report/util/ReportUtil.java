/**
 * Copyright : HP
 * project : Report
 * Create by : Steven Zhang(Ling Kai)
 * Create on : 2011-7-7
 */
package com.hp.ucmdb.report.util;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ReportUtil {
	public static PropertiesConfiguration config = null;
	private static Log logger = null;
	
	public static PropertiesConfiguration getConfig() {
		if (config == null) {
			try {
				config = new PropertiesConfiguration(
						AllConstants.PROPERTIES_FILE);
			} catch (ConfigurationException e) {

				ReportUtil.getLogger().error(e);
			}
			// TODO: change the refreshDelay for this reload strategy
			config.setReloadingStrategy(new FileChangedReloadingStrategy());
		}
		return config;
	}
	
	public static Log getLogger() {
		if (logger == null) {
			logger = LogFactory.getLog(ReportUtil.class);
		}
		return logger;
	}
	
}