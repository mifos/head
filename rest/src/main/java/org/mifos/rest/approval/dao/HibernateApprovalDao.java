package org.mifos.rest.approval.dao;

import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.mifos.rest.approval.domain.ApprovalState;
import org.mifos.rest.approval.domain.RESTApprovalEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class HibernateApprovalDao implements ApprovalDao {

    @Autowired
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

    @SuppressWarnings("unchecked")
    @Override
    public List<RESTApprovalEntity> findByState(ApprovalState state) {
        return getSession().createCriteria(RESTApprovalEntity.class)
                           .add(Restrictions.eq("state", state))
                           .addOrder(Order.desc("createdOn"))
                           .list();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<RESTApprovalEntity> findByExcludingState(ApprovalState state) {
        return getSession().createCriteria(RESTApprovalEntity.class)
                           .add(Restrictions.ne("state", state))
                           .addOrder(Order.desc("approvedOn"))
                           .list();
    }

    private Session getSession() {
        return sessionFactory.getCurrentSession();
    }

}
