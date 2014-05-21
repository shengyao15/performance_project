package com.hp.it.innovation.collaboration.dao.hibernate;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.FlushMode;
import org.hibernate.LockOptions;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Example;
import org.springframework.dao.DataAccessResourceFailureException;

import com.hp.it.innovation.collaboration.model.Component;
import com.hp.it.innovation.collaboration.utilities.Constants;

public abstract class AbstractHibernateDao<T extends Component, ID extends Serializable> {

    protected final Log log = LogFactory.getLog(getClass());

    // TODO need to make this configurable
    protected final static FlushMode queryFlushMode = FlushMode.COMMIT;

    // protected StopWatch s = new StopWatch();

    private SessionFactory sessionFactory;
    private Class<T> persistentClass;

    // private Session session;

    @SuppressWarnings("unchecked")
    public AbstractHibernateDao() {
        Type type = ((ParameterizedType)getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        this.persistentClass = (Class<T>)type;
    }

    //
    // @SuppressWarnings("unchecked")
    // public void setSession(Session s) {
    // this.session = s;
    // // return (DaoImpl) this;
    // }
    //
    // protected Session getSession() {
    // if (session == null)
    // throw new IllegalStateException("Session has not been set on DAO before
    // usage");
    // return session;
    // }

    public Class<T> getPersistentClass() {
        return persistentClass;
    }

    public T findById(ID id) {
        return findById(id, false);
    }

    @SuppressWarnings("unchecked")
    public T findById(ID id, boolean lock) {
        T entity;
        if (lock)
            entity = (T)sessionFactory.getCurrentSession().load(getPersistentClass(), id, LockOptions.UPGRADE);
        else
            entity = (T)sessionFactory.getCurrentSession().load(getPersistentClass(), id);

        return entity;
    }

    public T fetchById(ID id) {
        return fetchById(id, false);
    }

    @SuppressWarnings("unchecked")
    public T fetchById(ID id, boolean lock) {
        T entity;
        if (lock)
            entity = (T)sessionFactory.getCurrentSession().get(getPersistentClass(), id, LockOptions.UPGRADE);
        else
            entity = (T)sessionFactory.getCurrentSession().get(getPersistentClass(), id);

        return entity;
    }

    @SuppressWarnings("unchecked")
    public List<T> findAll() {
        Criteria crit = getSession().createCriteria(getPersistentClass());
        return crit.list();
    }

    public List<T> findPageByPage(int firstResult, int maxResults) {
        return findByCriteriaPageByPage(firstResult, maxResults);
    }

    @SuppressWarnings("unchecked")
    public List<T> findByExample(T exampleInstance, String... excludeProperty) {
        Criteria crit = getSession().createCriteria(getPersistentClass());
        Example example = Example.create(exampleInstance);
        for (String exclude : excludeProperty) {
            example.excludeProperty(exclude);
        }
        crit.add(example);
        return crit.list();
    }

    public T save(T entity) {
        sessionFactory.getCurrentSession().saveOrUpdate(entity);
        return entity;
    }

    public void remove(ID id) {
        T entity = findById(id, false);

        remove(entity);
    }

    public void remove(T entity) {
        sessionFactory.getCurrentSession().delete(entity);
    }

    @SuppressWarnings("unchecked")
    public T merge(T entity) {
        return (T)sessionFactory.getCurrentSession().merge(entity);
    }

    public T persist(T entity) {
        sessionFactory.getCurrentSession().persist(entity);

        return entity;
    }

    protected Session getSession() throws DataAccessResourceFailureException, IllegalStateException {
        return new FlushModeSession(sessionFactory.getCurrentSession(), queryFlushMode);
    }

    /**
     * Use this inside subclasses as a convenience method.
     */
    @SuppressWarnings("unchecked")
    protected List<T> findByCriteria(Criterion... criterion) {
        Criteria crit = getSession().createCriteria(getPersistentClass());
        for (Criterion c : criterion) {
            crit.add(c);
        }
        return crit.list();
    }

    /**
     * Use this inside subclasses as a convenience method.
     */
    @SuppressWarnings("unchecked")
    protected List<T> findByCriteriaPageByPage(int firstResult, int maxResults, Criterion... criterion) {
        Criteria criteria = getSession().createCriteria(getPersistentClass());
        for (Criterion c : criterion) {
            criteria.add(c);
        }
        criteria.setFirstResult(firstResult);
        criteria.setMaxResults(maxResults);
        return criteria.list();
    }

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void flush() {
        sessionFactory.getCurrentSession().flush();
    }

    public void evict(T entity) {
        sessionFactory.getCurrentSession().evict(entity);
    }

    public Object queryByHQL(String hql) {
    		return getSession().createQuery(hql).list();
    }

    public Object queryByHQL(String hql, Map<String, Object> params) {
        Query q = getSession().createQuery(hql);
        for (String key : params.keySet()) {
            q.setParameter(key, params.get(key));
        }
        return q.list();
    }

    public Object queryByHQL(String hql, int start, int max, Object... params) {
        Query q = getSession().createQuery(hql).setFirstResult(start).setMaxResults(max);
        if (null != params) {
            for (int i = 0; i < params.length; i++) {
                q.setParameter(i, params[i]);
            }
        }
        return q.list();
    }

    @SuppressWarnings("rawtypes")
    public List queryByHQL(String hql, int start, int max) {
        return getSession().createQuery(hql).setFirstResult(start).setMaxResults(max).list();
    }

    public Object queryByHQL(String hql, Object... params) {
        Query q = getSession().createQuery(hql);
        if (null != params) {
            for (int i = 0; i < params.length; i++) {
                q.setParameter(i, params[i]);
            }
        }
        return q.list();
    }
    
    public Object queryBySQL(String sql){
        return getSession().createSQLQuery(sql).list();
    }
    
    public T queryByName(T entity){
        StringBuilder hql = new StringBuilder("FROM");
        String className = entity.getClass().getSimpleName();
        hql.append(Constants.SPACE_PLACEHOLDER);
        hql.append(className);
        hql.append(Constants.SPACE_PLACEHOLDER);
        hql.append("e");
        hql.append(Constants.SPACE_PLACEHOLDER);
        hql.append("WHERE");
        hql.append(Constants.SPACE_PLACEHOLDER);
        hql.append("e.name");
        hql.append(Constants.EQUALS_SYMBOL);
        hql.append("'");
        hql.append(entity.getName());
        hql.append("'");
        List<T> list = (List<T>)queryByHQL(hql.toString());
        if (list != null && list.size() > 0){
        	return list.get(0);
        }
        return null;
    }
}
