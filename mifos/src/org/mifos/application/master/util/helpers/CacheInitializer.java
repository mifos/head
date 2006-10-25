package org.mifos.application.master.util.helpers;

import java.util.List;

import org.mifos.application.accounts.business.AccountActionEntity;
import org.mifos.application.master.business.MasterDataEntity;
import org.mifos.application.master.persistence.MasterPersistence;
import org.mifos.framework.exceptions.PersistenceException;

public class CacheInitializer {
	
	public static void initialize() throws PersistenceException {
		for(MasterDataEntity masterDataEntity : getAccountActionMasterData()) {
			AccountActionEntity accountActionEntity = (AccountActionEntity)masterDataEntity;
			Cache.addToCache(accountActionEntity.getId(),accountActionEntity,null);
		}
	}
	
	private static List<MasterDataEntity> getAccountActionMasterData() throws PersistenceException {
		return new MasterPersistence().retrieveMasterDataEntity(MasterConstants.PATH_ACCOUNTACTIONENTITY);
	}

}
