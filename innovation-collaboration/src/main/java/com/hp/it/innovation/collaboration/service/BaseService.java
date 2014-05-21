package com.hp.it.innovation.collaboration.service;

import java.io.Serializable;
import java.util.List;

import com.hp.it.innovation.collaboration.dao.BaseDAO;
import com.hp.it.innovation.collaboration.model.Component;

public interface BaseService<T extends Component, D extends BaseDAO<T, ID>, ID extends Serializable> {

    T findById(ID id);

    T fetchById(ID id);

    List<T> findAll();

    List<T> findPageByPage(int firstResult, int maxResults);

    List<T> findByExample(T exampleInstance, String... excludeProperty);

    T save(T dto);

    T merge(T dto);

    T persist(T dto);

    void delete(T dto);

    void delete(ID id);

    void flush();

    void evict(T dto);

}
