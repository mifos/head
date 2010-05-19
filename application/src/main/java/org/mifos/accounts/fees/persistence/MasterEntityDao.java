package org.mifos.accounts.fees.persistence;

import java.util.List;

import org.mifos.accounts.financial.business.GLCodeEntity;
import org.mifos.application.master.business.MasterDataEntity;
import org.mifos.framework.exceptions.PersistenceException;

/**
 * This interface is meant to be extended on need basis as replacement for MasterPersistence.
 * Do not use context reference of "masterEntityDao" when in struts context.
 * Also Dao's should not throw checked exception
 */
public interface MasterEntityDao {

    public <T extends MasterDataEntity>  T retrieveMasterEntity(Class<T> entityType, Short entityId, Short localeId) throws PersistenceException;
    public <T extends MasterDataEntity> List<T> retrieveMasterEntities(final Class<T> type, final Short localeId) throws PersistenceException;
    public GLCodeEntity retrieveGLCodeEntity(Short id);

}
