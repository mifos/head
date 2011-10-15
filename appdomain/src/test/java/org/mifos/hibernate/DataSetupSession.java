package org.mifos.hibernate;

import java.io.Serializable;
import java.sql.Connection;

import org.hibernate.CacheMode;
import org.hibernate.Criteria;
import org.hibernate.EntityMode;
import org.hibernate.Filter;
import org.hibernate.FlushMode;
import org.hibernate.HibernateException;
import org.hibernate.LobHelper;
import org.hibernate.LockMode;
import org.hibernate.LockOptions;
import org.hibernate.Query;
import org.hibernate.ReplicationMode;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.TypeHelper;
import org.hibernate.UnknownProfileException;
import org.hibernate.jdbc.Work;
import org.hibernate.stat.SessionStatistics;

public class DataSetupSession implements Session {
    private Session session;

    public DataSetupSession(Session session) {
        this.session = session;
    }

    @Override
    public EntityMode getEntityMode() {
        return session.getEntityMode();
    }

    @Override
    public Session getSession(EntityMode entityMode) {
        return session.getSession(entityMode);
    }

    @Override
    public void flush() throws HibernateException {
        session.flush();
    }

    @Override
    public void setFlushMode(FlushMode flushMode) {
        session.setFlushMode(flushMode);
    }

    @Override
    public FlushMode getFlushMode() {
        return session.getFlushMode();
    }

    @Override
    public void setCacheMode(CacheMode cacheMode) {
        session.setCacheMode(cacheMode);
    }

    @Override
    public CacheMode getCacheMode() {
        return session.getCacheMode();
    }

    @Override
    public SessionFactory getSessionFactory() {
        return session.getSessionFactory();
    }

    @Override
    public Connection connection() throws HibernateException {
        return session.connection();
    }

    @Override
    public Connection close() throws HibernateException {
        return session.close();
    }

    @Override
    public void cancelQuery() throws HibernateException {
        session.cancelQuery();
    }

    @Override
    public boolean isOpen() {
        return session.isOpen();
    }

    @Override
    public boolean isConnected() {
        return session.isConnected();
    }

    @Override
    public boolean isDirty() throws HibernateException {
        return session.isDirty();
    }

    @Override
    public boolean isDefaultReadOnly() {
        return session.isDefaultReadOnly();
    }

    @Override
    public void setDefaultReadOnly(boolean b) {
        session.setDefaultReadOnly(b);
    }

    @Override
    public Serializable getIdentifier(Object o) throws HibernateException {
        return session.getIdentifier(o);
    }

    @Override
    public boolean contains(Object o) {
        return session.contains(o);
    }

    @Override
    public void evict(Object o) throws HibernateException {
        session.evict(o);
    }

    @Override
    public Object load(Class aClass, Serializable serializable, LockMode lockMode) throws HibernateException {
        return session.load(aClass, serializable, lockMode);
    }

    @Override
    public Object load(Class aClass, Serializable serializable, LockOptions lockOptions) throws HibernateException {
        return session.load(aClass, serializable, lockOptions);
    }

    @Override
    public Object load(String s, Serializable serializable, LockMode lockMode) throws HibernateException {
        return session.load(s, serializable, lockMode);
    }

    @Override
    public Object load(String s, Serializable serializable, LockOptions lockOptions) throws HibernateException {
        return session.load(s, serializable, lockOptions);
    }

    @Override
    public Object load(Class aClass, Serializable serializable) throws HibernateException {
        return session.load(aClass, serializable);
    }

    @Override
    public Object load(String s, Serializable serializable) throws HibernateException {
        return session.load(s, serializable);
    }

    @Override
    public void load(Object o, Serializable serializable) throws HibernateException {
        session.load(o, serializable);
    }

    @Override
    public void replicate(Object o, ReplicationMode replicationMode) throws HibernateException {
        session.replicate(o, replicationMode);
    }

    @Override
    public void replicate(String s, Object o, ReplicationMode replicationMode) throws HibernateException {
        session.replicate(s, o, replicationMode);
    }

    @Override
    public Serializable save(Object o) throws HibernateException {
        return session.save(o);
    }

    @Override
    public Serializable save(String s, Object o) throws HibernateException {
        return session.save(s, o);
    }

    @Override
    public void saveOrUpdate(Object o) throws HibernateException {
        session.saveOrUpdate(o);
        session.flush();
    }

    @Override
    public void saveOrUpdate(String s, Object o) throws HibernateException {
        session.saveOrUpdate(s, o);
        session.flush();
    }

    @Override
    public void update(Object o) throws HibernateException {
        session.update(o);
    }

    @Override
    public void update(String s, Object o) throws HibernateException {
        session.update(s, o);
    }

    @Override
    public Object merge(Object o) throws HibernateException {
        return session.merge(o);
    }

    @Override
    public Object merge(String s, Object o) throws HibernateException {
        return session.merge(s, o);
    }

    @Override
    public void persist(Object o) throws HibernateException {
        session.persist(o);
    }

    @Override
    public void persist(String s, Object o) throws HibernateException {
        session.persist(s, o);
    }

    @Override
    public void delete(Object o) throws HibernateException {
        session.delete(o);
    }

    @Override
    public void delete(String s, Object o) throws HibernateException {
        session.delete(s, o);
    }

    @Override
    public void lock(Object o, LockMode lockMode) throws HibernateException {
        session.lock(o, lockMode);
    }

    @Override
    public void lock(String s, Object o, LockMode lockMode) throws HibernateException {
        session.lock(s, o, lockMode);
    }

    @Override
    public LockRequest buildLockRequest(LockOptions lockOptions) {
        return session.buildLockRequest(lockOptions);
    }

    @Override
    public void refresh(Object o) throws HibernateException {
        session.refresh(o);
    }

    @Override
    public void refresh(Object o, LockMode lockMode) throws HibernateException {
        session.refresh(o, lockMode);
    }

    @Override
    public void refresh(Object o, LockOptions lockOptions) throws HibernateException {
        session.refresh(o, lockOptions);
    }

    @Override
    public LockMode getCurrentLockMode(Object o) throws HibernateException {
        return session.getCurrentLockMode(o);
    }

    @Override
    public Transaction beginTransaction() throws HibernateException {
        return session.beginTransaction();
    }

    @Override
    public Transaction getTransaction() {
        return session.getTransaction();
    }

    @Override
    public Criteria createCriteria(Class aClass) {
        return session.createCriteria(aClass);
    }

    @Override
    public Criteria createCriteria(Class aClass, String s) {
        return session.createCriteria(aClass, s);
    }

    @Override
    public Criteria createCriteria(String s) {
        return session.createCriteria(s);
    }

    @Override
    public Criteria createCriteria(String s, String s1) {
        return session.createCriteria(s, s1);
    }

    @Override
    public Query createQuery(String s) throws HibernateException {
        return session.createQuery(s);
    }

    @Override
    public SQLQuery createSQLQuery(String s) throws HibernateException {
        return session.createSQLQuery(s);
    }

    @Override
    public Query createFilter(Object o, String s) throws HibernateException {
        return session.createFilter(o, s);
    }

    @Override
    public Query getNamedQuery(String s) throws HibernateException {
        return session.getNamedQuery(s);
    }

    @Override
    public void clear() {
        session.clear();
    }

    @Override
    public Object get(Class aClass, Serializable serializable) throws HibernateException {
        return session.get(aClass, serializable);
    }

    @Override
    public Object get(Class aClass, Serializable serializable, LockMode lockMode) throws HibernateException {
        return session.get(aClass, serializable, lockMode);
    }

    @Override
    public Object get(Class aClass, Serializable serializable, LockOptions lockOptions) throws HibernateException {
        return session.get(aClass, serializable, lockOptions);
    }

    @Override
    public Object get(String s, Serializable serializable) throws HibernateException {
        return session.get(s, serializable);
    }

    @Override
    public Object get(String s, Serializable serializable, LockMode lockMode) throws HibernateException {
        return session.get(s, serializable, lockMode);
    }

    @Override
    public Object get(String s, Serializable serializable, LockOptions lockOptions) throws HibernateException {
        return session.get(s, serializable, lockOptions);
    }

    @Override
    public String getEntityName(Object o) throws HibernateException {
        return session.getEntityName(o);
    }

    @Override
    public Filter enableFilter(String s) {
        return session.enableFilter(s);
    }

    @Override
    public Filter getEnabledFilter(String s) {
        return session.getEnabledFilter(s);
    }

    @Override
    public void disableFilter(String s) {
        session.disableFilter(s);
    }

    @Override
    public SessionStatistics getStatistics() {
        return session.getStatistics();
    }

    @Override
    public boolean isReadOnly(Object o) {
        return session.isReadOnly(o);
    }

    @Override
    public void setReadOnly(Object o, boolean b) {
        session.setReadOnly(o, b);
    }

    @Override
    public void doWork(Work work) throws HibernateException {
        session.doWork(work);
    }

    @Override
    public Connection disconnect() throws HibernateException {
        return session.disconnect();
    }

    @Override
    public void reconnect() throws HibernateException {
        session.reconnect();
    }

    @Override
    public void reconnect(Connection connection) throws HibernateException {
        session.reconnect(connection);
    }

    @Override
    public boolean isFetchProfileEnabled(String s) throws UnknownProfileException {
        return session.isFetchProfileEnabled(s);
    }

    @Override
    public void enableFetchProfile(String s) throws UnknownProfileException {
        session.enableFetchProfile(s);
    }

    @Override
    public void disableFetchProfile(String s) throws UnknownProfileException {
        session.disableFetchProfile(s);
    }

    @Override
    public TypeHelper getTypeHelper() {
        return session.getTypeHelper();
    }

    @Override
    public LobHelper getLobHelper() {
        return session.getLobHelper();
    }
}
