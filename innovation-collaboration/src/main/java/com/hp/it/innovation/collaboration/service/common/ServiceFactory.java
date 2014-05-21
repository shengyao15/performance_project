package com.hp.it.innovation.collaboration.service.common;

public class ServiceFactory {
    @SuppressWarnings("unchecked")
    public static <T> T getService(Class<T> t) {
        return (T)ServiceLocator.getBean(t.getName());
    }
}
