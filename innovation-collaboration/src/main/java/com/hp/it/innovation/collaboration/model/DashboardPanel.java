package com.hp.it.innovation.collaboration.model;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DashboardPanel This model is used to decide which template to use for each dashboard panel and how to render the
 * panel style and content
 * 
 * @author yhou
 */
public class DashboardPanel<T> {

    /**
     * Panel name
     */
    private String name;

    /**
     * Panel type;
     */
    private String type;

    /**
     * Panel content
     */
    private T content;

    /**
     * Panel html class list
     */
    private List<String> panelHtmlClass;

    public DashboardPanel(String name, String type, List<String> panelHtmlClass, T content) {
        this.name = name;
        this.type = type;
        this.panelHtmlClass = panelHtmlClass;
        this.content = content;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<String> getPanelHtmlClass() {
        return panelHtmlClass;
    }

    public void setPanelHtmlClass(List<String> panelHtmlClass) {
        this.panelHtmlClass = panelHtmlClass;
    }

    public T getContect() {
        return content;
    }

    public void setContect(T contect) {
        this.content = contect;
    }

}
