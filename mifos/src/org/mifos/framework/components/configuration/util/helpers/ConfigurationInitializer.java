/**

 * ConfigurationInitializer.java    version: 1.0



 * Copyright (c) 2005-2006 Grameen Foundation USA

 * 1029 Vermont Avenue, NW, Suite 400, Washington DC 20005

 * All rights reserved.



 * Apache License
 * Copyright (c) 2005-2006 Grameen Foundation USA
 *

 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the

 * License.
 *
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an explanation of the license

 * and how it is applied.

 *

 */
package org.mifos.framework.components.configuration.util.helpers;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mifos.application.accounts.business.AccountStateEntity;
import org.mifos.application.accounts.persistence.AccountPersistence;
import org.mifos.application.accounts.util.helpers.AccountStates;
import org.mifos.application.accounts.util.helpers.AccountTypes;
import org.mifos.application.customer.business.CustomerStatusEntity;
import org.mifos.application.customer.group.util.helpers.GroupConstants;
import org.mifos.application.customer.persistence.CustomerPersistence;
import org.mifos.application.customer.util.helpers.CustomerLevel;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.application.office.business.OfficeBO;
import org.mifos.application.office.persistence.OfficePersistence;
import org.mifos.application.productdefinition.persistence.LoanPrdPersistence;
import org.mifos.application.productdefinition.persistence.SavingsPrdPersistence;
import org.mifos.config.AccountingRules;
import org.mifos.config.FiscalCalendarRules;
import org.mifos.framework.components.configuration.business.SystemConfiguration;
import org.mifos.framework.components.configuration.cache.CacheRepository;
import org.mifos.framework.components.configuration.cache.Key;
import org.mifos.framework.components.configuration.cache.OfficeCache;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.ConstantsNotLoadedException;
import org.mifos.framework.exceptions.StartUpException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.util.helpers.Constants;

/**
 * This class is a remnant of per-office configuration, which <a
 * href="http://article.gmane.org/gmane.comp.finance.mifos.devel/3498">is
 * deprecated and may be removed</a> (-Adam 22-JAN-2008).
 */
public class ConfigurationInitializer {
	private OfficeBO headOffice;

	private OfficeBO getHeadOffice() throws ApplicationException {
		if (headOffice == null)
			headOffice = new OfficePersistence().getHeadOffice();
		return headOffice;
	}

	protected SystemConfiguration createSystemConfiguration()
		throws SystemException {

		MifosCurrency defaultCurrency = null;
		try {
			defaultCurrency = AccountingRules.getMifosCurrency();
		} catch (RuntimeException re) {
			throw new SystemException("cannot fetch default currency", re);
		}

		// TODO: pick timezone offset from database
		int timeZone = 19800000;

		return new SystemConfiguration(defaultCurrency, timeZone);
	}

	protected OfficeCache createOfficeCache() throws SystemException,
			ApplicationException {
		Map<Key, Object> officeConfigMap = new HashMap<Key, Object>();

		// TODO: don't set optional states here... this is handled in ProcessFlowRules
//		List<CustomerStatusEntity> customerOptionalStates;
//		customerOptionalStates = new CustomerPersistence()
//				.getCustomerStates(ConfigConstants.OPTIONAL_FLAG);
//		setCustomerOptionalStates(officeConfigMap, customerOptionalStates);
//
//		List<AccountStateEntity> accountOptionalStates = new AccountPersistence()
//				.getAccountStates(ConfigConstants.OPTIONAL_FLAG);
//		setAccountOptionalStates(officeConfigMap, accountOptionalStates);

		setFiscalStartOfWeek(officeConfigMap);
		setWeekOffList(officeConfigMap);
		setLateNessAndDormancyDaysForAccount(officeConfigMap);

		return new OfficeCache(officeConfigMap);
	}

	private void setFiscalStartOfWeek(Map<Key,Object> officeConfigMap)throws SystemException,ApplicationException{
		
		Short id = FiscalCalendarRules.getStartOfWeek();
		officeConfigMap.put(new Key(getHeadOffice().getOfficeId(),ConfigConstants.FISCAL_START_OF_WEEK),id);
	}

	private void setWeekOffList(Map<Key, Object> officeConfigMap) throws SystemException,
			ApplicationException {
		
		// get weekday off (not working day)
		List<Short> weekOffList = FiscalCalendarRules.getWeekDayOffList();
		if (weekOffList != null)
			officeConfigMap.put(new Key(getHeadOffice().getOfficeId(),
					ConfigConstants.WEEK_OFF_LIST), weekOffList);
	}

	private void setLateNessAndDormancyDaysForAccount(
			Map<Key, Object> officeConfigMap) throws SystemException,
			ApplicationException {
		Short latenessDays = new LoanPrdPersistence().retrieveLatenessForPrd();
		Short dormancyDays = new SavingsPrdPersistence().retrieveDormancyDays();
		officeConfigMap.put(new Key(getHeadOffice().getOfficeId(),
				ConfigConstants.LATENESS_DAYS), latenessDays);
		officeConfigMap.put(new Key(getHeadOffice().getOfficeId(),
				ConfigConstants.DORMANCY_DAYS), dormancyDays);
	}
	
	// TODO: Will be removed shortly along with other per-office configuration
	// detritus. (-Adam 24-JAN-2008)

//	private void setCustomerOptionalStates(Map<Key, Object> officeConfigMap,
//			List<CustomerStatusEntity> customerOptionalStates)
//			throws SystemException, ApplicationException {
//		if (customerOptionalStates != null && customerOptionalStates.size() > 0) {
//			for (CustomerStatusEntity customerStateEntity : customerOptionalStates) {
//				if (customerStateEntity.getCustomerLevel().getId().equals(
//						CustomerLevel.CLIENT.getValue()))
//					setClientOptionalState(officeConfigMap, customerStateEntity);
//				else if (customerStateEntity.getCustomerLevel().getId().equals(
//						CustomerLevel.GROUP.getValue()))
//					setGroupOptionalState(officeConfigMap, customerStateEntity);
//			}
//		}
//	}
//
//	private void setAccountOptionalStates(Map<Key, Object> officeConfigMap,
//			List<AccountStateEntity> accountOptionalStates)
//			throws SystemException, ApplicationException {
//		if (accountOptionalStates != null && accountOptionalStates.size() > 0) {
//			for (AccountStateEntity accountStateEntity : accountOptionalStates) {
//				if (accountStateEntity.getPrdType().getProductTypeID().equals(
//						AccountTypes.SAVINGS_ACCOUNT.getValue()))
//					setSavingsOptionalState(officeConfigMap, accountStateEntity);
//				else if (accountStateEntity.getPrdType().getProductTypeID()
//						.equals(AccountTypes.LOAN_ACCOUNT.getValue()))
//					setLoanOptionalStates(officeConfigMap, accountStateEntity);
//			}
//		}
//	}

//	private void setClientOptionalState(Map<Key, Object> officeConfigMap,
//			CustomerStatusEntity customerStateEntity) throws SystemException,
//			ApplicationException {
//		if (customerStateEntity.getId().equals(
//				CustomerStatus.CLIENT_PENDING.getValue()))
//			officeConfigMap.put(new Key(getHeadOffice().getOfficeId(),
//					ConfigConstants.PENDING_APPROVAL_DEFINED_FOR_CLIENT),
//					Constants.NO);
//	}
//
//	private void setGroupOptionalState(Map<Key, Object> officeConfigMap,
//			CustomerStatusEntity customerStateEntity) throws SystemException,
//			ApplicationException {
//		if (customerStateEntity.getId().equals(GroupConstants.PENDING_APPROVAL))
//			officeConfigMap.put(new Key(getHeadOffice().getOfficeId(),
//					ConfigConstants.PENDING_APPROVAL_DEFINED_FOR_GROUP),
//					Constants.NO);
//	}
//
//	private void setSavingsOptionalState(Map<Key, Object> officeConfigMap,
//			AccountStateEntity accountStateEntity) throws SystemException,
//			ApplicationException {
//		if (accountStateEntity.getId().equals(
//				AccountStates.SAVINGS_ACC_PENDINGAPPROVAL))
//			officeConfigMap.put(new Key(getHeadOffice().getOfficeId(),
//					ConfigConstants.PENDING_APPROVAL_DEFINED_FOR_SAVINGS),
//					Constants.NO);
//	}
//
//	private void setLoanOptionalStates(Map<Key, Object> officeConfigMap,
//			AccountStateEntity accountStateEntity) throws SystemException,
//			ApplicationException {
//		if (accountStateEntity.getId().equals(
//				AccountStates.LOANACC_PENDINGAPPROVAL))
//			officeConfigMap.put(new Key(getHeadOffice().getOfficeId(),
//					ConfigConstants.PENDING_APPROVAL_DEFINED_FOR_LOAN),
//					Constants.NO);
//		else if (accountStateEntity.getId().equals(
//				AccountStates.LOANACC_DBTOLOANOFFICER))
//			officeConfigMap.put(new Key(getHeadOffice().getOfficeId(),
//					ConfigConstants.DISBURSED_TO_LO_DEFINED_FOR_LOAN),
//					Constants.NO);
//	}

	public void initialize() {
		try {
			CacheRepository cacheRepository = CacheRepository.getInstance();
			cacheRepository.setSystemConfiguration(createSystemConfiguration());
			cacheRepository.setOfficeCache(createOfficeCache());
		} catch (SystemException se) {
			throw new StartUpException(se);
		} catch (ApplicationException e) {
			throw new StartUpException(e);
		}
	}

	static void checkModifiers(Field field)
			throws ConstantsNotLoadedException {
		if (!Modifier.isFinal(field.getModifiers()))
			throw new ConstantsNotLoadedException("field: " + field.getName()
					+ " is not declared as final");
		if (!Modifier.isStatic(field.getModifiers()))
			throw new ConstantsNotLoadedException("field: " + field.getName()
					+ " is not declared as static");
		if (!Modifier.isPublic(field.getModifiers()))
			throw new ConstantsNotLoadedException("field: " + field.getName()
					+ " is not declared as public");
	}
}
