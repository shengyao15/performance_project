package com.hp.it.perf.ac.launch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sampullara.cli.Args;
import com.sampullara.cli.Argument;

public class AcLauncherMain implements AcLaunchable {

	private static final Logger log = LoggerFactory
			.getLogger(AcLauncherMain.class);

	@Argument(value = "type", alias = "t", description = "Naming Type")
	private String type = "jmx";

	@Argument(value = "host", alias = "h", description = "Naming Host (default is localhost)")
	private String host = null;

	@Argument(value = "port", alias = "p", description = "Naming Port")
	private int port = 1099;

	@Argument(value = "location", alias = "l", description = "Naming Location")
	private String location;

	@Argument(value = "server", description = "Set whether to startup as server (create the naming service/registry in-process)")
	private boolean server = true;

	private Map<String, AcNamingLauncher> namingLaunches = new HashMap<String, AcNamingLauncher>();

	private Map<AcLaunchable, Boolean> launchers = new LinkedHashMap<AcLaunchable, Boolean>();

	public AcLauncherMain() {
		setupNamingLauncher();
	}

	private void setupNamingLauncher() {
		namingLaunches.put("jmx", new AcJmxNamingLauncher(this));
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public void setServer(boolean server) {
		this.server = server;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		AcLauncherMain main = new AcLauncherMain();
		try {
			List<String> launchables = Args.parse(main, args);
			List<Class<? extends AcLaunchable>> launchableClassList = new ArrayList<Class<? extends AcLaunchable>>();
			for (String h : launchables) {
				Class<?> clz;
				try {
					clz = Class.forName(h);
				} catch (ClassNotFoundException e) {
					throw new IllegalArgumentException("Class name " + h
							+ " is not found", e);
				}
				if (AcLaunchable.class.isAssignableFrom(clz)) {
					launchableClassList.add(clz.asSubclass(AcLaunchable.class));
				} else {
					throw new IllegalArgumentException("Class name " + h
							+ " is not subclass of " + AcLaunchable.class);
				}
			}
			main.prepareNamingService();
			main.prepareLaunchabls(launchableClassList);
			main.launch();
		} catch (IllegalArgumentException e) {
			System.err.println("Error: " + e.getMessage());
			Args.usage(main);
			System.err.println("  <Launchable Class> [<Launchable Class>] ...");
			System.exit(1);
		}
	}

	private void prepareLaunchabls(
			List<? extends Class<? extends AcLaunchable>> launchableClassList) {
		if (launchableClassList.isEmpty()) {
			throw new IllegalArgumentException(
					"No <Launchable Class> to launch");
		}
		for (Class<? extends AcLaunchable> clz : launchableClassList) {
			try {
				launchers.put(clz.newInstance(), Boolean.FALSE);
				log.info("launcher '{}' is ready.", clz.getName());
			} catch (Exception e) {
				throw new IllegalArgumentException("Cannot instantiate " + clz
						+ " due to error: " + e.getMessage(), e);
			}
		}
	}

	public void setLaunchables(
			List<? extends Class<? extends AcLaunchable>> launchableClassList) {
		prepareNamingService();
		prepareLaunchabls(launchableClassList);
	}

	public <T extends AcLaunchable> T getLaunchable(Class<T> clazz) {
		for (AcLaunchable l : launchers.keySet()) {
			if (clazz.isInstance(l) && Boolean.TRUE.equals(launchers.get(l))) {
				return clazz.cast(l);
			}
		}
		return null;
	}

	@Override
	public void launch() {
		for (AcLaunchable l : new ArrayList<AcLaunchable>(launchers.keySet())) {
			if (l instanceof AcNamingLauncher) {
				AcNamingLauncher namingLauncher = (AcNamingLauncher) l;
				namingLauncher.setHost(host);
				namingLauncher.setPort(port);
				namingLauncher.setLocation(location);
				namingLauncher.setServer(server);
			}
			l.launch();
			launchers.put(l, Boolean.TRUE);
		}
	}

	private void prepareNamingService() {
		AcNamingLauncher namingLauncher = namingLaunches.get(type);
		if (namingLauncher == null) {
			throw new IllegalArgumentException(
					"no naming launcher found for type " + type);
		}
		launchers.put(namingLauncher, Boolean.FALSE);
	}

	@Override
	public void close() {
		List<AcLaunchable> launchers = new ArrayList<AcLaunchable>(
				this.launchers.keySet());
		Collections.reverse(launchers);
		for (AcLaunchable l : launchers) {
			l.close();
		}
		launchers.clear();
	}

}
