package org.mifos.platform.rest.approval.dao;

import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.classic.Session;
import org.mifos.platform.rest.approval.domain.RESTApprovalEntity;
import org.springframework.stereotype.Repository;

@Repository
public class HibernateApprovalDao implements ApprovalDao {

    SessionFactory sessionFactory;

    @Override
    public void create(RESTApprovalEntity entity) {
        getSession().save(entity);
    }

    @Override
    public RESTApprovalEntity getDetails(Long id) {
        return (RESTApprovalEntity) getSession().get(RESTApprovalEntity.class, id);
    }

    @Override
    public void update(RESTApprovalEntity entity) {
        getSession().update(entity);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<RESTApprovalEntity> getDetailsAll() {
        return getSession().createCriteria(RESTApprovalEntity.class).list();
    }

    private Session getSession() {
        return sessionFactory.getCurrentSession();
    }

}
