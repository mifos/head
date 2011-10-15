package org.mifos.datasetup.repositories;

import java.io.Serializable;

import org.hibernate.Session;

public abstract class TestEntities<T> {
    protected Session session;
    private Class clazz;

    public TestEntities(Session session, Class clazz) {
        this.session = session;
        this.clazz = clazz;
    }

    public T any() {
        return (T) any(clazz);
    }

    protected Object any(Class aClass) {
        return session.createCriteria(aClass).list().get(0);
    }

    public T get(Serializable id) {
        return (T) session.get(clazz, id);
    }
}
