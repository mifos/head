/**

 * FeesBusinessProcessor.java    version: xxx

 

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

package org.mifos.application.fees.business.handlers;

import java.util.Date;

import org.mifos.application.fees.dao.FeesDAO;
import org.mifos.application.fees.util.helpers.FeeConstants;
import org.mifos.application.fees.util.helpers.FeeFrequencyType;
import org.mifos.application.fees.util.helpers.FeeStatus;
import org.mifos.application.fees.util.valueobjects.Fees;
import org.mifos.framework.business.handlers.MifosBusinessProcessor;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.valueobjects.Context;

public class FeesBusinessProcessor extends MifosBusinessProcessor {

	public FeesBusinessProcessor() {
		super();
	}
	
	public void loadInitial(Context context)throws SystemException,ApplicationException{
		FeesDAO feesDao = (FeesDAO) getDAO(context.getPath());
		Short localeId=getUserLocaleId(context.getUserContext());
		feesDao.obtainMasterData(context,localeId);
		
	}

	
	private Short getUserLocaleId(UserContext userContext) {
		Short localeId=1;
		if(null != userContext) {
			localeId=userContext.getLocaleId();
		}
		if(null == localeId) {
			localeId=Short.valueOf("1");
		}
		return localeId;
	}
	
	
	public void get(Context context) throws SystemException,ApplicationException {
			FeesDAO feesDao = (FeesDAO) getDAO(context.getPath());
			Short feesId=Short.valueOf((String)context.getBusinessResults("feesId"));
			Fees fees=feesDao.getFees(feesId);
			if(fees.getFeeLevelSet()!=null && fees.getFeeLevelSet().size()>0){
				context.addBusinessResults("defaultAdminFee","Yes");
			}else{
				context.addBusinessResults("defaultAdminFee","No");
			}
			context.setValueObject(fees);
			feesDao.obtainMasterData(context,getUserLocaleId(context.getUserContext()));
	
	}

	public void update(Context context) throws SystemException,
	ApplicationException
	{
		
		FeesDAO feesDao = (FeesDAO) getDAO(context.getPath());
		Fees fees=(Fees)context.getValueObject() ; 
		context.setValueObject(feesDao.updateFees(fees));
		feesDao.obtainMasterData(context,getUserLocaleId(context.getUserContext()));
	}
	
	
	public void manage(Context context) throws SystemException,ApplicationException {
		FeesDAO feesDao = (FeesDAO) getDAO(context.getPath());
		Fees fees=(Fees)context.getValueObject() ;
		context.setValueObject(feesDao.getFees(fees.getFeeId()));
		feesDao.obtainMasterData(context,getUserLocaleId(context.getUserContext()));

	}
	
	
	public void getFees(Context context) throws SystemException,ApplicationException {
		FeesDAO feesDao = (FeesDAO) getDAO(context.getPath());
		feesDao.getFeesData(context);
		feesDao.obtainMasterData(context,getUserLocaleId(context.getUserContext()));
	}
	
	
	public void createInitial(Context context)throws SystemException,ApplicationException{
		Fees fees=(Fees)context.getValueObject();
		fees.setCreatedBy(context.getUserContext().getId().intValue());
		fees.setCreatedDate(new Date(System.currentTimeMillis()));
		fees.setUpdatedBy(null);
		fees.setUpdatedDate(null);
		//fees.setUpdatedOfficeId(null);
		fees.setGlobalFeeNum(null);
		fees.setStatus(new Short(FeeStatus.ACTIVE.getValue()));
		fees.setOfficeId(null);
		if(null!=fees.getFormulaId() && fees.getFormulaId()==0){
			fees.setFormulaId(null);
		}
	}
	
	public void previousInitial(Context context)throws SystemException,ApplicationException{
		Fees fees=(Fees)context.getValueObject();
		if(fees.getFeeFrequency().getFeeFrequencyTypeId()==FeeFrequencyType.ONETIME.getValue()){
			fees.getFeeFrequency().setFeeMeetingFrequency(null);  
		}else if(fees.getFeeFrequency().getFeeFrequencyTypeId()==FeeFrequencyType.PERIODIC.getValue()){
			fees.getFeeFrequency().setFeePaymentId(null);
		}
	}
	
	public void previewInitial(Context context)throws SystemException,ApplicationException{
		Fees fees=(Fees)context.getValueObject();
		if(fees.getRateOrAmount()==null ||fees.getRateOrAmount().equals(Double.valueOf("0.0"))){
			throw new ApplicationException(FeeConstants.AMOUNTCANNOTBEZERO);
		}
	}
	


}
