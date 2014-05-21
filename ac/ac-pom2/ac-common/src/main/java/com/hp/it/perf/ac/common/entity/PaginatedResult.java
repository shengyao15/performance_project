package com.hp.it.perf.ac.common.entity;

import java.util.Collection;

public class PaginatedResult<T> extends PaginationData {

    /**
     * 
     */
    private static final long serialVersionUID = 4391295403816191849L;

    private Collection<T> result;

    public PaginatedResult() {
        super();
    }

    public PaginatedResult(int maxCountPerPage, int totalCount, int currentPage, Collection<T> result) {
        super(maxCountPerPage, totalCount, currentPage);
        this.result = result;
    }

    public Collection<T> getResult() {
        return result;
    }

    public void setResult(Collection<T> result) {
        this.result = result;
    }

}
