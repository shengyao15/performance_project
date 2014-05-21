package com.hp.it.perf.ac.common.entity;

import java.io.Serializable;

/**
 * 
 * 
 * @author lshuangy
 */
public class PaginationData implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1934519282742528167L;

    public static int DEFAULT_MAX_COUNT_PER_PAGE = 10;

    private int totalCount;

    private int maxCountPerPage;

    private int currentPage;

    private int totalPageCount;

    public PaginationData() {
        this.maxCountPerPage = DEFAULT_MAX_COUNT_PER_PAGE;
        this.totalCount = 0;
        this.currentPage = 1;
        this.totalPageCount = 1;
    }

    public PaginationData(int maxCountPerPage, int totalCount) {
        this.maxCountPerPage = maxCountPerPage;
        this.totalCount = totalCount;
        this.currentPage = 1;
        init();
    }

    public PaginationData(int maxCountPerPage, int totalCount, int currentPage) {
        if (currentPage < 1 || maxCountPerPage < 1 || totalCount < 0) {
            throw new IllegalArgumentException();
        }
        this.maxCountPerPage = maxCountPerPage;
        this.totalCount = totalCount;
        this.currentPage = currentPage;
        init();
    }

    public void init() {
        int i = totalCount % maxCountPerPage;
        if (i != 0) {
            totalPageCount = totalCount / maxCountPerPage + 1;
        } else {
            totalPageCount = totalCount / maxCountPerPage;
        }
        if (totalCount == 0) {
            totalPageCount = 1;
        }

        if (currentPage > totalPageCount) {
            currentPage = totalPageCount;
        }
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public int getMaxCountPerPage() {
        return maxCountPerPage;
    }

    public void setMaxCountPerPage(int maxCountPerPage) {
        this.maxCountPerPage = maxCountPerPage;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getTotalPageCount() {
        return totalPageCount;
    }

    public void setTotalPageCount(int totalPageCount) {
        this.totalPageCount = totalPageCount;
    }

}
