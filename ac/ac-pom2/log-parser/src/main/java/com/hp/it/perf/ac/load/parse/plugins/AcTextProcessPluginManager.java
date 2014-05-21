package com.hp.it.perf.ac.load.parse.plugins;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class AcTextProcessPluginManager {

	// first come, last serve
	private LinkedList<AcTextProcessPlugin> pluginList = new LinkedList<AcTextProcessPlugin>();

	private static List<AcTextProcessPlugin> defaultPluginList = new ArrayList<AcTextProcessPlugin>();

	private static final AcTextProcessPluginManager defaultManager;

	static {
		addDefaultPlugin(new AcThreadInterruptedPlugin());
		addDefaultPlugin(new AcBeanParseHookPlugin());
		// addDefaultPlugin(new AcTextProcessLoggingPlugin());
		// make sure all default are added
		defaultManager = new AcTextProcessPluginManager();
	}

	public AcTextProcessPluginManager() {
		pluginList.addAll(defaultPluginList);
		Collections.reverse(pluginList);
	}

	public static AcTextProcessPluginManager getDefaultManager() {
		return defaultManager;
	}

	public static void addDefaultPlugin(AcTextProcessPlugin plugin) {
		if (plugin != null && defaultPluginList.indexOf(plugin) == -1) {
			defaultPluginList.add(plugin);
			// update defaut manager
			if (defaultManager != null) {
				defaultManager.addPlugin(plugin);
			}
		}
	}

	public void addPlugin(AcTextProcessPlugin plugin) {
		if (plugin != null && pluginList.indexOf(plugin) == -1) {
			pluginList.addFirst(plugin);
		}
	}

	public AcTextProcessPlugin[] getPlugins() {
		return pluginList.toArray(new AcTextProcessPlugin[0]);
	}
}
