/*
 * Copyright (c) 2005-2010 Grameen Foundation USA
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 * explanation of the license and how it is applied.
 */

package org.mifos.framework.persistence;

import org.hibernate.Session;
import org.mifos.accounts.fees.business.FeeBO;
import org.mifos.accounts.fees.business.FeeFrequencyTypeEntity;
import org.mifos.accounts.loan.business.LoanBO;
import org.mifos.accounts.productdefinition.business.PrdStatusEntity;
import org.mifos.accounts.productdefinition.business.ProductCategoryBO;
import org.mifos.accounts.productdefinition.util.helpers.PrdStatus;
import org.mifos.application.holiday.business.HolidayPK;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.center.business.CenterBO;
import org.mifos.customers.center.persistence.CenterPersistence;
import org.mifos.customers.client.business.ClientBO;
import org.mifos.customers.client.persistence.ClientPersistence;
import org.mifos.customers.group.business.GroupBO;
import org.mifos.customers.group.persistence.GroupPersistence;
import org.mifos.customers.office.business.OfficeBO;
import org.mifos.customers.persistence.CustomerPersistence;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.framework.business.PersistentObject;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;

/**
 * This class is used to persist objects created by TestObjectFactory.
 */
public class TestObjectPersistence {

    private CustomerPersistence customerPersistence = new CustomerPersistence();
    private GroupPersistence groupPersistence = new GroupPersistence();
    private ClientPersistence clientPersistence = new ClientPersistence();
    private CenterPersistence centerPersistence = new CenterPersistence();

    /**
     * @return - Returns the personnel created by master data scripts. This
     *         record does not have any custom fields or roles associated with
     *         it. If the row does not already exist in the database it returns
     *         null.
     */
    public PersonnelBO getPersonnel(Session session, Short personnelId) {
        return (PersonnelBO) session.get(PersonnelBO.class, personnelId);
    }

    public PersonnelBO getPersonnel(Short personnelId) {
        return getPersonnel(StaticHibernateUtil.getSessionTL(), personnelId);
    }

    /**
     * @return - Returns the Non loan officer created by test data scripts. This
     *         record does not have any custom fields or roles associated with
     *         it. If the row does not already exist in the database it returns
     *         null.
     */
    public PersonnelBO getNonLoanOfficer() {
        Session session = StaticHibernateUtil.getSessionTL();
        return (PersonnelBO) session.get(PersonnelBO.class, new Short("2"));
    }

    public FeeBO createFees(FeeBO fees) {
        Session session = StaticHibernateUtil.getSessionTL();
        session.save(fees);
        return fees;
    }

    public FeeBO createFee(FeeBO fee) {
        Session session = StaticHibernateUtil.getSessionTL();
        session.save(fee);
        return fee;
    }

    /**
     * This persists any object passed as parameter. It starts a new transaction
     * and commits it if the insertion was successful.
     */
    public PersistentObject persist(PersistentObject obj) {
        Session session = StaticHibernateUtil.getSessionTL();
        StaticHibernateUtil.startTransaction();
        session.save(obj);
        StaticHibernateUtil.getTransaction().commit();
        return obj;

    }

    public ProductCategoryBO getLoanPrdCategory() {
        Session session = StaticHibernateUtil.getSessionTL();
        return (ProductCategoryBO) session.get(ProductCategoryBO.class, new Short("1"));
    }

    /**
     * @return - Returns the office created by test data scripts. If the row
     *         does not already exist in the database it returns null. defaults
     *         created are 1- Head Office , 2 - Area Office , 3 - BranchOffice.
     */

    public OfficeBO getOffice(Short officeId) {
        Session session = StaticHibernateUtil.getSessionTL();
        return (OfficeBO) session.get(OfficeBO.class, officeId);
    }

    public PrdStatusEntity retrievePrdStatus(PrdStatus status) {
        Session session = StaticHibernateUtil.getSessionTL();
        return (PrdStatusEntity) session.get(PrdStatusEntity.class, status.getValue());
    }

    public MifosCurrency getCurrency() {
        Session session = StaticHibernateUtil.getSessionTL();
        return (MifosCurrency) session.get(MifosCurrency.class, Short.valueOf((short) 2));
    }

    public MifosCurrency getCurrency(Short currencyId) {
        Session session = StaticHibernateUtil.getSessionTL();
        return (MifosCurrency) session.get(MifosCurrency.class, currencyId);
    }

    /**
     * TODO: This should just call {@link #update(PersistentObject)}, right? Or
     * is the difference between save and saveOrUpdate relevant here?
     */
    public LoanBO getLoanAccount(LoanBO loan) {
        Session session = StaticHibernateUtil.getSessionTL();
        session.save(loan);
        StaticHibernateUtil.getTransaction().commit();
        return loan;
    }

    public FeeFrequencyTypeEntity getFeeFrequencyType() {
        Session session = StaticHibernateUtil.getSessionTL();
        return (FeeFrequencyTypeEntity) session.get(FeeFrequencyTypeEntity.class, Short.valueOf("1"));
    }

    public void removeObject(PersistentObject obj) {
        Session session = StaticHibernateUtil.getSessionTL();
        StaticHibernateUtil.startTransaction();
        session.delete(obj);
        StaticHibernateUtil.getTransaction().commit();
    }

    public void update(PersistentObject obj) {
        Session session = StaticHibernateUtil.getSessionTL();
        StaticHibernateUtil.startTransaction();
        session.saveOrUpdate(obj);
        StaticHibernateUtil.commitTransaction();
    }

    public void flushandCloseSession() {
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
    }

    public <T> T getObject(Class<T> clazz, Integer pk) {
        return (T) StaticHibernateUtil.getSessionTL().get(clazz, pk);
    }

    public Object getObject(Class clazz, Short pk) {
        return StaticHibernateUtil.getSessionTL().get(clazz, pk);
    }

    public Object getObject(Class clazz, HolidayPK pk) {
        return StaticHibernateUtil.getSessionTL().get(clazz, pk);
    }

    public CustomerBO getCustomer(Integer customerId) {
        CustomerBO customer = null;
        try {
            customer = customerPersistence.getCustomer(customerId);
        } catch (PersistenceException e) {
            throw new RuntimeException(e);
        }
        return customer;
    }

    public GroupBO getGroup(Integer groupId) {
        GroupBO group = null;
        try {
            group = groupPersistence.getGroupByCustomerId(groupId);
        } catch (PersistenceException e) {
            throw new RuntimeException(e);
        }
        return group;
    }

    public ClientBO getClient(Integer clientId) {
        ClientBO client = null;
        try {
            client = clientPersistence.getClient(clientId);
        } catch (PersistenceException e) {
            throw new RuntimeException(e);
        }
        return client;
    }

    public CenterBO getCenter(Integer centerId) {
        CenterBO center = null;
        try {
            center = centerPersistence.getCenter(centerId);
        } catch (PersistenceException e) {
            throw new RuntimeException(e);
        }
        return center;
    }

}
