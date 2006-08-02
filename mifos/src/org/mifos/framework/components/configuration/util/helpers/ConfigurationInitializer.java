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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.mifos.application.accounts.business.AccountStateEntity;
import org.mifos.application.accounts.persistence.service.AccountPersistanceService;
import org.mifos.application.accounts.util.helpers.AccountStates;
import org.mifos.application.accounts.util.helpers.AccountTypes;
import org.mifos.application.customer.business.CustomerStatusEntity;
import org.mifos.application.customer.client.util.helpers.ClientConstants;
import org.mifos.application.customer.group.util.helpers.GroupConstants;
import org.mifos.application.customer.persistence.service.CustomerPersistenceService;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.application.meeting.business.WeekDaysEntity;
import org.mifos.application.office.business.OfficeBO;
import org.mifos.application.office.persistence.service.OfficePersistenceService;
import org.mifos.application.productdefinition.persistence.service.LoansPrdPersistenceService;
import org.mifos.application.productdefinition.persistence.service.SavingsPrdPersistenceService;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.components.configuration.business.ConfigEntity;
import org.mifos.framework.components.configuration.cache.Cache;
import org.mifos.framework.components.configuration.cache.CacheRepository;
import org.mifos.framework.components.configuration.cache.Key;
import org.mifos.framework.components.configuration.cache.OfficeCache;
import org.mifos.framework.components.configuration.persistence.service.ConfigurationPersistenceService;
import org.mifos.framework.exceptions.ConstantsNotLoadedException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.exceptions.StartUpException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.ExceptionConstants;
import org.mifos.framework.util.helpers.PersistenceServiceName;


public class ConfigurationInitializer {
	private ConfigurationPersistenceService dbService;
	private OfficeBO headOffice;
	private Map<String,String> officeConfigConstants;
	
	private ConfigurationPersistenceService getDBService() throws ServiceException {
		if (dbService == null) 
			dbService = (ConfigurationPersistenceService) ServiceFactory.getInstance().getPersistenceService(PersistenceServiceName.Configuration);
		return dbService;
	}

	private OfficeBO getHeadOffice()throws ServiceException{
		if(headOffice==null)
			headOffice=((OfficePersistenceService) ServiceFactory.getInstance().getPersistenceService(PersistenceServiceName.Office)).getHeadOffice();
		return headOffice;	
	}
	
	protected Cache createSystemCache()throws SystemException{
		Map<String,Object> systemConfigMap = new HashMap<String,Object>();
		ConfigEntity systemConfiguration= getDBService().getSystemConfiguration();
		if(systemConfiguration.getSessionTimeOut()!=null)
			systemConfigMap.put(ConfigConstants.SESSION_TIMEOUT,systemConfiguration.getSessionTimeOut());
		else
			systemConfigMap.put(ConfigConstants.SESSION_TIMEOUT, ConfigConstants.SESSION_TIMEOUT_DEFAULT);
		systemConfigMap.put(ConfigConstants.CURRENCY, getDBService().getDefaultCurrency());
		systemConfigMap.put(ConfigConstants.MFI_LOCALE, getDBService().getSupportedLocale());
		
		//TODO: pick timezone offset from database
		systemConfigMap.put(ConfigConstants.TIMEZONE,19800000);
		return new Cache(systemConfigMap);
	}
		  
	protected OfficeCache createOfficeCache() throws SystemException{		
		Map<Key,Object> officeConfigMap = new HashMap<Key,Object>();
		List<ConfigEntity> systemConfigList= getDBService().getOfficeConfiguration();
		for(int i=0; i< systemConfigList.size(); i++){
			createOfficeCache(officeConfigMap,systemConfigList.get(i));
		}
		
		List<CustomerStatusEntity> customerOptionalStates =((CustomerPersistenceService) ServiceFactory.getInstance().getPersistenceService(PersistenceServiceName.Customer)).getCustomerStates(ConfigConstants.OPTIONAL_FLAG);
		setCustomerOptionalStates(officeConfigMap,customerOptionalStates);
		
		List<AccountStateEntity> accountOptionalStates =((AccountPersistanceService) ServiceFactory.getInstance().getPersistenceService(PersistenceServiceName.Account)).getAccountStates(ConfigConstants.OPTIONAL_FLAG);
		setAccountOptionalStates(officeConfigMap,accountOptionalStates);
		
		List<WeekDaysEntity> weekDaysList = getDBService().getWeekDaysList();
		
		setFiscalStartOfWeek(officeConfigMap,weekDaysList);
		setWeekOffList(officeConfigMap,weekDaysList);
		setLateNessAndDormancyDaysForAccount(officeConfigMap);
		
		return new OfficeCache(officeConfigMap);
	}
	
	private void setFiscalStartOfWeek(Map<Key,Object> officeConfigMap,List<WeekDaysEntity> weekDaysList)throws SystemException{
		for(WeekDaysEntity weekDaysEntity : weekDaysList){
			if(weekDaysEntity.isStartOfFiscalWeek()){
				officeConfigMap.put(new Key(getHeadOffice().getOfficeId(),ConfigConstants.FISCAL_START_OF_WEEK),weekDaysEntity.getWeekDayId());
				break;
			}
		}
	}
		
	private void setWeekOffList(Map<Key,Object> officeConfigMap,List<WeekDaysEntity> weekDaysList)throws SystemException{
		List<Short> weekOffList = null;
		for(WeekDaysEntity weekDaysEntity : weekDaysList){
			if(!weekDaysEntity.isWorkingDay()){
				if(weekOffList==null)
					weekOffList = new ArrayList<Short>();
				weekOffList.add(weekDaysEntity.getWeekDayId());
			}
		}
		if(weekOffList!=null)
			officeConfigMap.put(new Key(getHeadOffice().getOfficeId(),ConfigConstants.WEEK_OFF_LIST),weekOffList);
	}
	
	private void setLateNessAndDormancyDaysForAccount(Map<Key,Object> officeConfigMap)throws SystemException{
		Short latenessDays=((LoansPrdPersistenceService) ServiceFactory.getInstance().
				getPersistenceService(PersistenceServiceName.LoansProduct)).retrieveLatenessForPrd();
		Short dormancyDays=((SavingsPrdPersistenceService) ServiceFactory.getInstance().
				getPersistenceService(PersistenceServiceName.SavingsProduct)).retrieveDormancyDays();
		officeConfigMap.put(new Key(getHeadOffice().getOfficeId(),ConfigConstants.LATENESS_DAYS),latenessDays);
		officeConfigMap.put(new Key(getHeadOffice().getOfficeId(),ConfigConstants.DORMANCY_DAYS),dormancyDays);
	}
	
	private void setCustomerOptionalStates(Map<Key,Object> officeConfigMap,List<CustomerStatusEntity> customerOptionalStates)throws SystemException{
		if(customerOptionalStates!=null && customerOptionalStates.size()>0){
			for(CustomerStatusEntity customerStateEntity: customerOptionalStates){
				if(customerStateEntity.getCustomerLevel().getId().equals(CustomerConstants.CLIENT_LEVEL_ID))
					setClientOptionalState(officeConfigMap,customerStateEntity);
				else if(customerStateEntity.getCustomerLevel().getId().equals(CustomerConstants.GROUP_LEVEL_ID))
					setGroupOptionalState(officeConfigMap,customerStateEntity);
			}
		}
	}
	
	private void setAccountOptionalStates(Map<Key,Object> officeConfigMap,List<AccountStateEntity> accountOptionalStates)throws SystemException{
		if(accountOptionalStates!=null && accountOptionalStates.size()>0){
			for(AccountStateEntity accountStateEntity: accountOptionalStates){
				if(accountStateEntity.getPrdType().getProductTypeID().equals(Short.valueOf(AccountTypes.SAVINGSACCOUNT)))
					setSavingsOptionalState(officeConfigMap,accountStateEntity);
				else if(accountStateEntity.getPrdType().getProductTypeID().equals(Short.valueOf(AccountTypes.LOANACCOUNT)))
					setLoanOptionalStates(officeConfigMap,accountStateEntity);
			}
		}
	}

	private void setClientOptionalState(Map<Key,Object> officeConfigMap,CustomerStatusEntity customerStateEntity)throws SystemException{
		if(customerStateEntity.getId().equals(ClientConstants.STATUS_PENDING))
			officeConfigMap.put(new Key(getHeadOffice().getOfficeId(),ConfigConstants.PENDING_APPROVAL_DEFINED_FOR_CLIENT),Constants.NO);
	}
	
	private void setGroupOptionalState(Map<Key,Object> officeConfigMap,CustomerStatusEntity customerStateEntity)throws SystemException{
		if(customerStateEntity.getId().equals(GroupConstants.PENDING_APPROVAL))
			officeConfigMap.put(new Key(getHeadOffice().getOfficeId(),ConfigConstants.PENDING_APPROVAL_DEFINED_FOR_GROUP),Constants.NO);
	}
	
	private void setSavingsOptionalState(Map<Key,Object> officeConfigMap,AccountStateEntity accountStateEntity)throws SystemException{
		if(accountStateEntity.getId().equals(AccountStates.SAVINGS_ACC_PENDINGAPPROVAL))
			officeConfigMap.put(new Key(getHeadOffice().getOfficeId(),ConfigConstants.PENDING_APPROVAL_DEFINED_FOR_SAVINGS),Constants.NO);
	}

	private void setLoanOptionalStates(Map<Key,Object> officeConfigMap,AccountStateEntity accountStateEntity)throws SystemException{
		if(accountStateEntity.getId().equals(AccountStates.LOANACC_PENDINGAPPROVAL))
			officeConfigMap.put(new Key(getHeadOffice().getOfficeId(),ConfigConstants.PENDING_APPROVAL_DEFINED_FOR_LOAN),Constants.NO);
		else if (accountStateEntity.getId().equals(AccountStates.LOANACC_DBTOLOANOFFICER))
			officeConfigMap.put(new Key(getHeadOffice().getOfficeId(),ConfigConstants.DISBURSED_TO_LO_DEFINED_FOR_LOAN),Constants.NO);
	}
	
	private void createOfficeCache(Map<Key,Object> officeConfigMap,ConfigEntity config)throws SystemException{
		Iterator keyIterator = officeConfigConstants.keySet().iterator();
		while(keyIterator.hasNext()){
			String key = (String)keyIterator.next();
			String methodName = "get" + officeConfigConstants.get(key);
			try{
				Method method = config.getClass().getMethod(methodName,null);
				Object invocationResult=method.invoke(config,null);
				if(invocationResult!=null)
					officeConfigMap.put(new Key(config.getOffice().getOfficeId(),officeConfigConstants.get(key)),invocationResult);
			}catch(NoSuchMethodException nsme){
				throw new SystemException(nsme);
			}
			catch(InvocationTargetException ite){
				throw new SystemException(ite);
			}
			catch(IllegalAccessException iae){
				throw new SystemException(iae);
			}
		}
	}
	
	public void initialize(){
		try{
			CacheRepository cacheRepository = CacheRepository.getInstance();
			cacheRepository.setSystemCache(createSystemCache());
			loadOfficeConfigConstants();
			cacheRepository.setOfficeCache(createOfficeCache());
		}catch(SystemException se){
			throw new StartUpException(se);
		}
	}	
	
	private void loadOfficeConfigConstants()throws SystemException{
		try{
			officeConfigConstants = ConstantMapBuilder.getInstance().buildMap(Class.forName(ConfigConstants.OFFICE_CONFIG_CONSTANTS));			
		}
		catch(ClassNotFoundException cnfe){			
			Object[] values = new Object[]{ConfigConstants.OFFICE_CONFIG_CONSTANTS};
			throw new ConstantsNotLoadedException(ExceptionConstants.CONSTANTSNOTLOADEDEXCEPTION,cnfe,values);
		}
	}
	

	private static class ConstantMapBuilder{		
		private  static ConstantMapBuilder instance = new ConstantMapBuilder();
		private  ConstantMapBuilder(){			
		}
		public static ConstantMapBuilder getInstance(){
			return instance;
		}
		
		public Map<String,String> buildMap(Class constantClass) throws ConstantsNotLoadedException{			
			Map<String, String> constantsMap = new HashMap<String, String>();
			Field[] fields = constantClass.getDeclaredFields();
			try{
				for (int i = 0; i < fields.length; i++) {
					 checkModifiers(fields[i]);
			         String fieldName = fields[i].getName();			        
			         String fieldValue =(String) fields[i].get(null);			         
			         constantsMap.put(fieldName,fieldValue);			         
			    }
			}catch(IllegalAccessException iae){
				throw new ConstantsNotLoadedException(iae);
			}
			return constantsMap;
		}
	 }
	private static void checkModifiers(Field field) throws ConstantsNotLoadedException{
		if(!Modifier.isFinal(field.getModifiers()))
			throw new ConstantsNotLoadedException("field: "+ field.getName()+" is not declared as final");
		if(!Modifier.isStatic(field.getModifiers()))
			throw new ConstantsNotLoadedException("field: "+ field.getName()+" is not declared as static");
		if(!Modifier.isPublic(field.getModifiers()))
			throw new ConstantsNotLoadedException("field: "+ field.getName()+" is not declared as public");
	}
}
