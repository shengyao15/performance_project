package com.hp.it.perf.ac.core.context;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.ho.yaml.Yaml;

import com.hp.it.perf.ac.core.AcCoreException;
import com.hp.it.perf.ac.core.AcPreferences;

public class DefaultAcPreferences implements AcPreferences {

	private static final int EXPIRE_MOD_COUNT = 5; // 5 times change
	private static final long EXPIRE_MOD_TIME = 30 * 1000L; // 30 seconds change
	private Map<String, DefaultAcPreferences> servicesPreferences;
	private final File file;
	private final DefaultAcPreferences parent;
	private PreferencesStore store = new PreferencesStore();
	private int modCount = 0;
	private long modTime = -1;

	public static class PreferencesStore implements Serializable {
		private static final long serialVersionUID = 1L;

		private String name;

		private Map<String, Serializable> store = new TreeMap<String, Serializable>();

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public Map<String, Serializable> getStore() {
			return store;
		}

		public void setStore(Map<String, Serializable> store) {
			this.store = store;
		}

		public boolean hasPreferences() {
			return store != null && !store.isEmpty();
		}

	}

	private DefaultAcPreferences(String name, DefaultAcPreferences parent) {
		this.store.setName(name);
		this.parent = parent;
		this.file = null;
	}

	public DefaultAcPreferences(File file) {
		this.file = file;
		this.parent = null;
		this.store.setName(""); // global scope
		servicesPreferences = new TreeMap<String, DefaultAcPreferences>();
		loadFile();
	}

	@Override
	public synchronized void put(String key, String value)
			throws AcCoreException {
		this.store.getStore().put(key, value);
		autoFlush();
	}

	@Override
	public synchronized String get(String key, String def) {
		Serializable value = this.store.getStore().get(key);
		if (value instanceof String) {
			return (String) value;
		} else {
			return (def == null && value != null) ? value.toString() : def;
		}
	}

	@Override
	public synchronized void remove(String key) throws AcCoreException {
		this.store.getStore().remove(key);
		autoFlush();
	}

	@Override
	public synchronized void clear() throws AcCoreException {
		this.store.getStore().clear();
		autoFlush();
	}

	@Override
	public synchronized String[] keys() throws AcCoreException {
		return this.store.getStore().keySet().toArray(new String[0]);
	}

	@Override
	public synchronized void putObject(String key, Serializable value)
			throws AcCoreException {
		this.store.getStore().put(key, value);
		autoFlush();
	}

	private synchronized void autoFlush() throws AcCoreException {
		if (parent != null) {
			parent.autoFlush();
		} else {
			if (modCount >= EXPIRE_MOD_COUNT
					|| System.currentTimeMillis() > modTime + EXPIRE_MOD_TIME) {
				sync();
			} else {
				modCount++;
				modTime = System.currentTimeMillis();
			}
		}
	}

	@Override
	public synchronized Serializable getObject(String key, Serializable def) {
		Serializable value = this.store.getStore().get(key);
		return value == null ? def : value;
	}

	@Override
	public synchronized void sync() throws AcCoreException {
		if (parent == null) {
			writeFile();
		} else {
			parent.sync();
		}
	}

	private synchronized void loadFile() {
		if (file.exists() && file.length() > 0) {
			try {
				List<?> storeList = Yaml.loadType(file, ArrayList.class);
				for (Object storeObj : storeList) {
					PreferencesStore store = (PreferencesStore) storeObj;
					if (store.getName().length() == 0) {
						// global level
						this.store = store;
					} else {
						// service level
						DefaultAcPreferences servicePreferences = getServicePreferences(store
								.getName());
						servicePreferences.store = store;
					}
				}
				markUpdated();
			} catch (Exception e) {
				throw new AcCoreException("load preference error", e);
			}
		}
	}

	private void markUpdated() {
		modCount = 0;
		modTime = System.currentTimeMillis();
	}

	private synchronized void writeFile() {
		List<PreferencesStore> storeList = new ArrayList<PreferencesStore>();
		storeList.add(store);
		for (DefaultAcPreferences servicePreferences : servicesPreferences
				.values()) {
			if (servicePreferences.store.hasPreferences()) {
				storeList.add(servicePreferences.store);
			}
		}
		try {
			Yaml.dump(storeList, file);
			markUpdated();
		} catch (Exception e) {
			throw new AcCoreException("sync preferences to file error", e);
		}
	}

	public synchronized DefaultAcPreferences getServicePreferences(
			String serviceId) {
		DefaultAcPreferences preferences = servicesPreferences.get(serviceId);
		if (preferences == null) {
			preferences = new DefaultAcPreferences(serviceId, this);
			servicesPreferences.put(serviceId, preferences);
		}
		return preferences;
	}

}
