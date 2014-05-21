package com.hp.it.perf.ac.core;

import java.io.Serializable;

public interface AcPreferences {

	public void put(String key, String value);

	public String get(String key, String def);

	public void remove(String key);

	public void clear() throws AcCoreException;

	public String[] keys() throws AcCoreException;

	public void putObject(String key, Serializable value);

	public Serializable getObject(String key, Serializable def);

	public void sync() throws AcCoreException;

}
