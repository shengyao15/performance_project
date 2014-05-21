package com.hp.it.innovation.collaboration.dao.hibernate;

import java.io.Serializable;
import java.sql.Connection;

import org.hibernate.CacheMode;
import org.hibernate.Criteria;
import org.hibernate.EntityMode;
import org.hibernate.Filter;
import org.hibernate.FlushMode;
import org.hibernate.HibernateException;
import org.hibernate.LockMode;
import org.hibernate.LockOptions;
import org.hibernate.Query;
import org.hibernate.ReplicationMode;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.UnknownProfileException;
import org.hibernate.jdbc.Work;
import org.hibernate.stat.SessionStatistics;

public class FlushModeSession implements Session {
    /**
     *
     */
    private static final long serialVersionUID = 3793026694671673563L;
    protected Session session;
    protected FlushMode flushMode;

    public FlushModeSession(Session session, FlushMode flushMode) {
        this.session = session;
        this.flushMode = flushMode;
    }

    public Transaction beginTransaction() throws HibernateException {
        return session.beginTransaction();
    }

    public void cancelQuery() throws HibernateException {
        session.cancelQuery();
    }

    public void clear() {
        session.clear();
    }

    public Connection close() throws HibernateException {
        return session.close();
    }

    public Connection connection() throws HibernateException {
        return session.connection();
    }

    public boolean contains(Object arg0) {
        return session.contains(arg0);
    }

    public Criteria createCriteria(Class arg0, String arg1) {
        return session.createCriteria(arg0, arg1);
    }

    public Criteria createCriteria(Class arg0) {
        return session.createCriteria(arg0);
    }

    public Criteria createCriteria(String arg0, String arg1) {
        return session.createCriteria(arg0, arg1);
    }

    public Criteria createCriteria(String arg0) {
        return session.createCriteria(arg0);
    }

    public Query createFilter(Object arg0, String arg1) throws HibernateException {
        return getFlushModeQuery(session.createFilter(arg0, arg1));
    }

    public Query createQuery(String arg0) throws HibernateException {
        return getFlushModeQuery(session.createQuery(arg0));
    }

    public SQLQuery createSQLQuery(String arg0) throws HibernateException {
        return getFlushModeQuery(session.createSQLQuery(arg0));
    }

    public void delete(Object arg0) throws HibernateException {
        session.delete(arg0);
    }

    public void delete(String arg0, Object arg1) throws HibernateException {
        session.delete(arg0, arg1);
    }

    public void disableFilter(String arg0) {
        session.disableFilter(arg0);
    }

    public Connection disconnect() throws HibernateException {
        return session.disconnect();
    }

    public Filter enableFilter(String arg0) {
        return session.enableFilter(arg0);
    }

    public void evict(Object arg0) throws HibernateException {
        session.evict(arg0);
    }

    public void flush() throws HibernateException {
        session.flush();
    }

    public Object get(Class arg0, Serializable arg1, LockMode arg2) throws HibernateException {
        return session.get(arg0, arg1, arg2);
    }

    public Object get(Class arg0, Serializable arg1) throws HibernateException {
        return session.get(arg0, arg1);
    }

    public Object get(String arg0, Serializable arg1, LockMode arg2) throws HibernateException {
        return session.get(arg0, arg1, arg2);
    }

    public Object get(String arg0, Serializable arg1) throws HibernateException {
        return session.get(arg0, arg1);
    }

    public CacheMode getCacheMode() {
        return session.getCacheMode();
    }

    public LockMode getCurrentLockMode(Object arg0) throws HibernateException {
        return session.getCurrentLockMode(arg0);
    }

    public Filter getEnabledFilter(String arg0) {
        return session.getEnabledFilter(arg0);
    }

    public EntityMode getEntityMode() {
        return session.getEntityMode();
    }

    public String getEntityName(Object arg0) throws HibernateException {
        return session.getEntityName(arg0);
    }

    public FlushMode getFlushMode() {
        return session.getFlushMode();
    }

    public Serializable getIdentifier(Object arg0) throws HibernateException {
        return session.getIdentifier(arg0);
    }

    public Query getNamedQuery(String arg0) throws HibernateException {
        return getFlushModeQuery(session.getNamedQuery(arg0));
    }

    public Session getSession(EntityMode arg0) {
        return session.getSession(arg0);
    }

    public SessionFactory getSessionFactory() {
        return session.getSessionFactory();
    }

    public SessionStatistics getStatistics() {
        return session.getStatistics();
    }

    public Transaction getTransaction() {
        return session.getTransaction();
    }

    public boolean isConnected() {
        return session.isConnected();
    }

    public boolean isDirty() throws HibernateException {
        return session.isDirty();
    }

    public boolean isOpen() {
        return session.isOpen();
    }

    public Object load(Class arg0, Serializable arg1, LockMode arg2) throws HibernateException {
        return session.load(arg0, arg1, arg2);
    }

    public Object load(Class arg0, Serializable arg1) throws HibernateException {
        return session.load(arg0, arg1);
    }

    public void load(Object arg0, Serializable arg1) throws HibernateException {
        session.load(arg0, arg1);
    }

    public Object load(String arg0, Serializable arg1, LockMode arg2) throws HibernateException {
        return session.load(arg0, arg1, arg2);
    }

    public Object load(String arg0, Serializable arg1) throws HibernateException {
        return session.load(arg0, arg1);
    }

    public void lock(Object arg0, LockMode arg1) throws HibernateException {
        session.lock(arg0, arg1);
    }

    public void lock(String arg0, Object arg1, LockMode arg2) throws HibernateException {
        session.lock(arg0, arg1, arg2);
    }

    public Object merge(Object arg0) throws HibernateException {
        return session.merge(arg0);
    }

    public Object merge(String arg0, Object arg1) throws HibernateException {
        return session.merge(arg0, arg1);
    }

    public void persist(Object arg0) throws HibernateException {
        session.persist(arg0);
    }

    public void persist(String arg0, Object arg1) throws HibernateException {
        session.persist(arg0, arg1);
    }

    public void reconnect() throws HibernateException {
        session.reconnect();
    }

    public void reconnect(Connection arg0) throws HibernateException {
        session.reconnect(arg0);
    }

    public void refresh(Object arg0, LockMode arg1) throws HibernateException {
        session.refresh(arg0, arg1);
    }

    public void refresh(Object arg0) throws HibernateException {
        session.refresh(arg0);
    }

    public void replicate(Object arg0, ReplicationMode arg1) throws HibernateException {
        session.replicate(arg0, arg1);
    }

    public void replicate(String arg0, Object arg1, ReplicationMode arg2) throws HibernateException {
        session.replicate(arg0, arg1, arg2);
    }

    public Serializable save(Object arg0) throws HibernateException {
        return session.save(arg0);
    }

    public Serializable save(String arg0, Object arg1) throws HibernateException {
        return session.save(arg0, arg1);
    }

    public void saveOrUpdate(Object arg0) throws HibernateException {
        session.saveOrUpdate(arg0);
    }

    public void saveOrUpdate(String arg0, Object arg1) throws HibernateException {
        session.saveOrUpdate(arg0, arg1);
    }

    public void setCacheMode(CacheMode arg0) {
        session.setCacheMode(arg0);
    }

    public void setFlushMode(FlushMode arg0) {
        session.setFlushMode(arg0);
    }

    public void setReadOnly(Object arg0, boolean arg1) {
        session.setReadOnly(arg0, arg1);
    }

    public void update(Object arg0) throws HibernateException {
        session.update(arg0);
    }

    public void update(String arg0, Object arg1) throws HibernateException {
        session.update(arg0, arg1);
    }

    protected Query getFlushModeQuery(Query query) {
        query.setFlushMode(flushMode);

        return query;
    }

    protected SQLQuery getFlushModeQuery(SQLQuery query) {
        query.setFlushMode(flushMode);

        return query;
    }

    @Override
    public boolean isDefaultReadOnly() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void setDefaultReadOnly(boolean readOnly) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public Object load(Class theClass, Serializable id, LockOptions lockOptions) throws HibernateException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Object load(String entityName, Serializable id, LockOptions lockOptions) throws HibernateException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public LockRequest buildLockRequest(LockOptions lockOptions) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void refresh(Object object, LockOptions lockOptions) throws HibernateException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public Object get(Class clazz, Serializable id, LockOptions lockOptions) throws HibernateException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Object get(String entityName, Serializable id, LockOptions lockOptions) throws HibernateException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean isReadOnly(Object entityOrProxy) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void doWork(Work work) throws HibernateException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public boolean isFetchProfileEnabled(String name) throws UnknownProfileException {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void enableFetchProfile(String name) throws UnknownProfileException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void disableFetchProfile(String name) throws UnknownProfileException {
        // TODO Auto-generated method stub
        
    }
}
