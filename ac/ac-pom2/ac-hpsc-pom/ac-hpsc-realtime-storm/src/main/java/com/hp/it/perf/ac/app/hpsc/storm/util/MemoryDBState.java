package com.hp.it.perf.ac.app.hpsc.storm.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import backtype.storm.tuple.Values;

import storm.trident.state.ITupleCollection;
import storm.trident.state.OpaqueValue;
import storm.trident.state.ValueUpdater;
import storm.trident.state.map.IBackingMap;
import storm.trident.state.map.MapState;
import storm.trident.state.map.OpaqueMap;
import storm.trident.state.map.SnapshottableMap;
import storm.trident.state.snapshot.Snapshottable;

public class MemoryDBState<T> implements Snapshottable<T>, ITupleCollection, MapState<T>, Serializable {
	private static final long serialVersionUID = 4582048738725343702L;
	
	@SuppressWarnings("rawtypes")
	MemoryMapStateBacking<OpaqueValue> _backing;
	SnapshottableMap<T> _delegate;
	static ConcurrentHashMap<String, Map<List<Object>, Object>> _dbs = new ConcurrentHashMap<String, Map<List<Object>, Object>>();
	private String id = "";
	static ScheduledExecutorService threadPool = Executors.newScheduledThreadPool(5);
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public MemoryDBState(String id) {
		this.id = id;
		this._backing = new MemoryMapStateBacking<OpaqueValue>(id);
		this._delegate = new SnapshottableMap(OpaqueMap.build(this._backing), new Values(new Object[] { "$MEMORY-MAP-STATE-GLOBAL$" }));
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public MemoryDBState(String id, Map<List<Object>, Object> db) {
		this.id = id;
		this._backing = new MemoryMapStateBacking<OpaqueValue>(id, db);
		this._delegate = new SnapshottableMap(OpaqueMap.build(this._backing), new Values(new Object[] { "$MEMORY-MAP-STATE-GLOBAL$" }));
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void scheduleUpdate(IUpdater<List<Object>, Object> updater, IUpdaterDelegate updaterDelegate, long initialDelaySeconds, long periodSeconds, String id) {
		Runnable command = new TimerSubmitDataThread(updater, updaterDelegate, id);
		threadPool.scheduleAtFixedRate(command, initialDelaySeconds, periodSeconds, TimeUnit.SECONDS);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void scheduleUpdate(IUpdater<List<Object>, Object> updater, IUpdaterDelegate updaterDelegate, long initialDelayMilSeconds, long periodMilSeconds, String[] ids, int [] categories) {
		Runnable command = new TimerSubmitDataThread(updater, updaterDelegate, ids, categories);
		threadPool.scheduleAtFixedRate(command, initialDelayMilSeconds, periodMilSeconds, TimeUnit.MILLISECONDS);
	}
	
	public static ScheduledExecutorService getThreadPool() {
		return threadPool;
	}
	
	public void remove() {
		_dbs.get(id).clear();
	}

	public T update(@SuppressWarnings("rawtypes") ValueUpdater updater) {
		return this._delegate.update(updater);
	}

	public void set(T o) {
		this._delegate.set(o);
	}

	public T get() {
		return this._delegate.get();
	}

	public void beginCommit(Long txid) {
		this._delegate.beginCommit(txid);
	}

	public void commit(Long txid) {
		this._delegate.commit(txid);
	}

	public Iterator<List<Object>> getTuples() {
		return this._backing.getTuples();
	}

	@SuppressWarnings("rawtypes")
	public List<T> multiUpdate(List<List<Object>> keys,
			List<ValueUpdater> updaters) {
		return this._delegate.multiUpdate(keys, updaters);
	}

	public void multiPut(List<List<Object>> keys, List<T> vals) {
		this._delegate.multiPut(keys, vals);
	}

	public List<T> multiGet(List<List<Object>> keys) {
		return this._delegate.multiGet(keys);
	}
	
	public static Map<String, Map<List<Object>, Object>> getDBs() {
		return _dbs;
	}

	static class MemoryMapStateBacking<T> implements IBackingMap<T>,
			ITupleCollection, Serializable {
		private static final long serialVersionUID = 5300668135769337901L;
		Map<List<Object>, T> db;
		Long currTx;

		public static void clearAll() {
			MemoryDBState._dbs.clear();
		}

		@SuppressWarnings("unchecked")
		public MemoryMapStateBacking(String id) {
			if (!(MemoryDBState._dbs.containsKey(id))) {
				MemoryDBState._dbs.put(id, new HashMap<List<Object>, Object>());
			}
			this.db = (Map<List<Object>, T>) MemoryDBState._dbs.get(id);
		}
		
		@SuppressWarnings("unchecked")
		public MemoryMapStateBacking(String id, Map<List<Object>, Object> db) {
			if (!(MemoryDBState._dbs.containsKey(id))) {
				MemoryDBState._dbs.put(id, db);
			}
			this.db = (Map<List<Object>, T>) MemoryDBState._dbs.get(id);
		}

		public List<T> multiGet(List<List<Object>> keys) {
			List<T> ret = new ArrayList<T>();
			for (List<Object> key : keys) {
				ret.add(this.db.get(key));
			}
			return ret;
		}

		public void multiPut(List<List<Object>> keys, List<T> vals) {
			for (int i = 0; i < keys.size(); ++i) {
				List<Object> key = keys.get(i);
				T val = vals.get(i);
				this.db.put(key, val);
			}
		}

		public Iterator<List<Object>> getTuples() {
			return new Iterator<List<Object>>() {
				private Iterator<Map.Entry<List<Object>, T>> it;

				public boolean hasNext() {
					return this.it.hasNext();
				}

				@SuppressWarnings("rawtypes")
				public List<Object> next() {
					Map.Entry<List<Object>, T> e = this.it.next();
					List<Object> ret = new ArrayList<Object>();
					ret.addAll(e.getKey());
					ret.add(((OpaqueValue) e.getValue()).getCurr());
					return ret;
				}

				public void remove() {
					throw new UnsupportedOperationException(
							"Not supported yet.");
				}
			};
		}
	}
	
	public static class TimerSubmitDataThread implements Runnable {
		Map<List<Object>, Object> db;
		IUpdater<List<Object>, Object> updater;
		IUpdaterDelegate<Object> updaterDelegate;
		String id;
		String [] ids;
		int [] categories;
		
		public TimerSubmitDataThread() {
			super();
		}
		
		public TimerSubmitDataThread(IUpdater<List<Object>, Object> updater, IUpdaterDelegate<Object> updaterDelegate, String id) {
			super();
			this.updater = updater;
			this.updaterDelegate = updaterDelegate;
			this.id = id;
		}
		
		public TimerSubmitDataThread(IUpdater<List<Object>, Object> updater, IUpdaterDelegate<Object> updaterDelegate, String [] ids, int [] categories) {
			super();
			if(ids == null || categories == null) {
				throw new NullPointerException("ids or categories is null");
			}
			if(ids.length != categories.length) {
				throw new IllegalStateException("length of does not match length of categories");
			}
			this.updater = updater;
			this.updaterDelegate = updaterDelegate;
			this.ids = ids;
			this.categories = categories;
		}

		@Override
		public void run() {
			if (updater == null || updaterDelegate == null) {
				System.out.println("updater or updaterDelegate is null");
				return;
			}
			if(ids == null || ids.length == 0) {
				if (MemoryDBState._dbs.containsKey(id) ) {
					updater.updateByDelegate(updaterDelegate,
							MemoryDBState._dbs.get(id));
				}
			} else {
				for(int i=0; i < ids.length; i++){
					String dbId = ids[i];
					int category = categories[i];
					if (MemoryDBState._dbs.containsKey(dbId)) {
						updater.updateByDelegate(updaterDelegate,
								MemoryDBState._dbs.get(dbId), category);
					}
				}
			}
		}
		
	}

}
