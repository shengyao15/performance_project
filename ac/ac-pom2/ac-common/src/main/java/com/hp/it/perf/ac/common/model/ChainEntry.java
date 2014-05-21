package com.hp.it.perf.ac.common.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChainEntry<T, V> implements Serializable {

	private static final long serialVersionUID = 1L;

	public static class EntryData<D> implements Serializable {
		private static final long serialVersionUID = 1L;
		private D data;
        private List<Object> contexts;

        public D getData() {
            return data;
        }

        public void setData(D data) {
            this.data = data;
        }

        public List<?> getContexts() {
            return contexts == null ? Collections.emptyList() : contexts;
        }

        public void addContext(Object context) {
            if (contexts == null) {
                contexts = new ArrayList<Object>();
            }
            contexts.add(context);
        }

        @Override
        public String toString() {
            return "EntryData [data=" + data + ", contexts=" + contexts + "]";
        }

    }

    private final List<EntryData<T>> dataNodes = new ArrayList<EntryData<T>>();

    private final List<ChainEntry<T, V>> childEntryNodes = new ArrayList<ChainEntry<T, V>>();

    private V value;

    private boolean current;

    private ChainEntry<T, V> parent;

    public List<EntryData<T>> getDataNodes() {
        return dataNodes;
    }

    public List<ChainEntry<T, V>> getChildEntryNodes() {
        return childEntryNodes;
    }

    public ChainEntry<T, V> getParent() {
        return parent;
    }

    public void setParent(ChainEntry<T, V> parent) {
        this.parent = parent;
    }

    public V getValue() {
        return value;
    }

    public void setValue(V value) {
        this.value = value;
    }

    public boolean isCurrent() {
        return current;
    }

    public void setCurrent(boolean current) {
        this.current = current;
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        toString(buffer, "");
        return buffer.toString();
    }

    private void toString(StringBuffer buffer, String indent) {
        buffer.append("ChainEntry [value=" + value + ", dataNodes=" + dataNodes + (current ? "] <-- current" : "]"));
        indent += " ";
        for (ChainEntry<T, V> child : childEntryNodes) {
            buffer.append('\n');
            buffer.append(indent);
            child.toString(buffer, indent);
        }
    }

}
