package org.mifos.accounts.fees.servicefacade;

import java.util.List;

import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.mifos.accounts.fees.servicefacade.MasterEntityDao;
import org.mifos.accounts.financial.business.GLCodeEntity;

import org.mifos.application.NamedQueryConstants;
import org.mifos.application.master.business.MasterDataEntity;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class MasterEntityDaoHibernateImpl extends HibernateDaoSupport implements MasterEntityDao {

    @Override
    public <T extends MasterDataEntity> T retrieveMasterEntity(Class<T> entityType, Short entityId, Short localeId)
            throws PersistenceException {

        List<T> masterEntityList =
           getHibernateTemplate().find(String.format("from %s masterEntity where masterEntity.id=%d",entityType.getName(),entityId));

        if (masterEntityList != null && !masterEntityList.isEmpty()) {
            T masterEntity = masterEntityList.get(0);
            masterEntity.setLocaleId(localeId);
            /** forces loading of lazy initialized objects.
             * Why should the session be closed before accessing this?
             */
            //getHibernateTemplate().initialize(masterEntity.getNames());

            return masterEntity;
        }
        throw new PersistenceException("errors.entityNotFound");
    }

    public <T extends MasterDataEntity> List<T> retrieveMasterEntities(final Class<T> type, final Short localeId) throws PersistenceException {
        try {

            List<T> masterEntityList =
                getHibernateTemplate().find("from " + type.getName());

            for (MasterDataEntity masterEntity : masterEntityList) {
                masterEntity.setLocaleId(localeId);
                //getHibernateTemplate().initialize(masterEntity.getNames());
            }
            return masterEntityList;
        } catch (Exception e) {
            throw new PersistenceException(e);
        }
    }

    public GLCodeEntity retrieveGLCodeEntity(Short id) {
        //TODO: change namedquery from GlCode.FindBy.. to GlCodeEntity.FindBy.. in JPA mapping
        //getHibernateTemplate().findByNamedQuery(NamedQueryConstants.GL_CODE_BY_ID, id).;
        Query query = getSession().getNamedQuery(NamedQueryConstants.GL_CODE_BY_ID);
        query.setParameter("glcodeId", id);
        return (GLCodeEntity)query.uniqueResult();
    }

}
