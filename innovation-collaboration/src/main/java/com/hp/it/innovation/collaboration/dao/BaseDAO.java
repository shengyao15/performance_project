package com.hp.it.innovation.collaboration.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.hibernate.SessionFactory;

import com.hp.it.innovation.collaboration.model.Component;

public interface BaseDAO<T extends Component, ID extends Serializable> {
    T findById(ID id);

    T findById(ID id, boolean lock);

    T fetchById(ID id);

    T fetchById(ID id, boolean lock);

    List<T> findAll();

    List<T> findPageByPage(int firstResult, int maxResults);

    List<T> findByExample(T exampleInstance, String... excludeProperty);

    T save(T entity);

    void remove(ID id);

    void remove(T entity);

    T merge(T entity);

    T persist(T entity);

    void setSessionFactory(SessionFactory s);

    void flush();

    void evict(T entity);

    @SuppressWarnings("rawtypes")
    public List queryByHQL(String hql, int start, int max);

    public Object queryByHQL(String hql);

    public Object queryByHQL(String hql, Object... params);

    public Object queryByHQL(String hql, int start, int size, Object... params);

    public Object queryByHQL(String hql, Map<String, Object> params);
    
    public Object queryBySQL(String sql);
    
    public T queryByName(T entity);
}
