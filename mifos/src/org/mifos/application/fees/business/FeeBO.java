/**

 * FeesBO.java    version: xxx



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

package org.mifos.application.fees.business;

import java.util.HashSet;
import java.util.Set;

import org.hibernate.HibernateException;
import org.mifos.application.accounts.financial.business.GLCodeEntity;
import org.mifos.application.fees.exceptions.FeeException;
import org.mifos.application.fees.persistence.FeePersistence;
import org.mifos.application.fees.util.helpers.FeeCategory;
import org.mifos.application.fees.util.helpers.FeeChangeType;
import org.mifos.application.fees.util.helpers.FeeLevel;
import org.mifos.application.fees.util.helpers.FeeStatus;
import org.mifos.application.fees.util.helpers.FeesConstants;
import org.mifos.application.fees.util.helpers.RateAmountFlag;
import org.mifos.application.master.persistence.MasterPersistence;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.office.business.OfficeBO;
import org.mifos.application.office.persistence.service.OfficePersistenceService;
import org.mifos.framework.business.BusinessObject;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.PropertyNotFoundException;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.struts.plugin.helper.EntityMasterConstants;
import org.mifos.framework.util.helpers.StringUtils;

public abstract class FeeBO extends BusinessObject {

	private final Short feeId;

	private final OfficeBO office;

	private final String feeName;

	private final CategoryTypeEntity categoryType;

	private final FeeFrequencyEntity feeFrequency;

	private final GLCodeEntity glCode;

	private final Set<FeeLevelEntity> feeLevels;

	private FeeStatusEntity feeStatus;

	private Short changeType;
	
	@Deprecated
	protected Double rateOrAmount;
	
	@Deprecated
	protected Short rateAmountFlag;
	
	/**
	 * Addding a default constructor is hibernate's requirement and should not be used to create a valid Fee object.   
	 */
	protected FeeBO() {
		this.feeId = null;
		this.office = null;
		this.feeName = null;
		this.categoryType = null;
		this.feeFrequency = null;
		this.glCode = null;
		this.feeLevels = null;
	}
		
	/**
	 * Constructor to create a valid Fee Object 
	 */
	protected FeeBO(UserContext userContext, String feeName, CategoryTypeEntity categoryType,
			FeeFrequencyTypeEntity feeFrequencyType, GLCodeEntity glCodeEntity,boolean isCustomerDefaultFee,
			FeePaymentEntity feePayment, MeetingBO feeMeeting) throws FeeException{
		
		validateFields(feeName, categoryType, glCodeEntity);
		this.feeFrequency = new FeeFrequencyEntity(feeFrequencyType, this, feePayment, feeMeeting);
		
		setUserContext(userContext);
		setCreateDetails();
		
		this.feeName = feeName;
		this.categoryType = categoryType;		
		this.glCode = glCodeEntity;
		
		this.feeId = null;
		this.feeLevels = new HashSet<FeeLevelEntity>();
		this.office = new OfficePersistenceService().getHeadOffice();
		this.changeType = (FeeChangeType.NOT_UPDATED.getValue());
		this.setFeeStatus(retrieveFeeStatusEntity(FeeStatus.ACTIVE));	
		if(isCustomerDefaultFee)
			makeFeeDefaultToCustomer();
	}
	
		
	public Short getFeeId() {
		return feeId;
	}

	public OfficeBO getOffice() {
		return office;
	}

	public String getFeeName() {
		return feeName;
	}

	public CategoryTypeEntity getCategoryType() {
		return categoryType;
	}

	public FeeFrequencyEntity getFeeFrequency() {
		return feeFrequency;
	}

	public Set<FeeLevelEntity> getFeeLevels() {
		return feeLevels;
	}

	public FeeStatusEntity getFeeStatus() {
		return feeStatus;
	}

	public GLCodeEntity getGlCode() {
		return glCode;
	}

	public FeeChangeType getFeeChangeType() throws PropertyNotFoundException{
		return FeeChangeType.getFeeChangeType(this.changeType);
	}

	public void updateFeeChangeType(FeeChangeType updateFlag){
		this.changeType = updateFlag.getValue();
	}
	
	private void setFeeStatus(FeeStatusEntity feeStatus) {
		this.feeStatus = feeStatus;
	}	

	@Override
	public Short getEntityID() {
		return EntityMasterConstants.Fees;
	}
	
	public void updateStatus(FeeStatus status) throws FeeException{
		if(!this.feeStatus.getId().equals(status.getValue()))
			setFeeStatus(retrieveFeeStatusEntity(status));
	}
	
	public void update() throws FeeException{
		try {
			setUpdateDetails();
			new FeePersistence().createOrUpdate(this);
		} catch (HibernateException he) {
			throw new FeeException(FeesConstants.FEE_UPDATE_ERROR, he);
		}
	}
		
	public abstract RateAmountFlag getFeeType();
	
	public void save() throws ApplicationException, FeeException {
		try {
			new FeePersistence().createOrUpdate(this);
		} catch (HibernateException he) {
			throw new FeeException(FeesConstants.FEE_CREATE_ERROR, he);
		}
	}	

	public boolean isActive() {
		return getFeeStatus().isActive();
	}
	
	public boolean isPeriodic() {
		return getFeeFrequency().isPeriodic();
	}

	public boolean isOneTime() {
		return getFeeFrequency().isOneTime();
	}

	public boolean isTimeOfDisbursement() {
		return getFeeFrequency().isTimeOfDisbursement();
	}
	
	public boolean isCustomerDefaultFee() {
		return  getFeeLevels().size() > 0;
	}
		
	
	protected void validateFields(String feeName, CategoryTypeEntity categoryType, GLCodeEntity glCode)throws FeeException{
		validateFeeName(feeName);
		validateFeeCateogry(categoryType);
		validateGLCode(glCode);
	}
	

	private void makeFeeDefaultToCustomer() throws FeeException {
		try{
			
			if (getCategoryType().getFeeCategory().equals(FeeCategory.CLIENT))
				addFeeLevel(createFeeLevel(FeeLevel.CLIENTLEVEL));
			else if (getCategoryType().getFeeCategory().equals(FeeCategory.GROUP))
				addFeeLevel(createFeeLevel(FeeLevel.GROUPLEVEL));
			else if (getCategoryType().getFeeCategory().equals(FeeCategory.CENTER))
				addFeeLevel(createFeeLevel(FeeLevel.CENTERLEVEL));
			else if (getCategoryType().getFeeCategory().equals(FeeCategory.ALLCUSTOMERS)) {
				addFeeLevel(createFeeLevel(FeeLevel.CLIENTLEVEL));
				addFeeLevel(createFeeLevel(FeeLevel.GROUPLEVEL));
				addFeeLevel(createFeeLevel(FeeLevel.CENTERLEVEL));
			}
		}catch(PropertyNotFoundException pnfe){
			throw new FeeException(pnfe);
		}
	}
	
	private FeeLevelEntity createFeeLevel(FeeLevel feeLevel) {
		return new FeeLevelEntity(this,feeLevel);
	}
	
	private void addFeeLevel(FeeLevelEntity feeLevel) {
		feeLevels.add(feeLevel);
	}	
	
	private void validateFeeName(String feeName)throws FeeException{
		if (StringUtils.isNullOrEmpty(feeName))
			throw new FeeException(FeesConstants.INVALID_FEE_NAME);
	}
	
	private void validateGLCode(GLCodeEntity glCode)throws FeeException{
		if(glCode==null)
			throw new FeeException(FeesConstants.INVALID_GLCODE);
	}
	
	private void validateFeeCateogry(CategoryTypeEntity categoryType)throws FeeException{
		if(categoryType==null)
			throw new FeeException(FeesConstants.INVALID_FEE_CATEGORY);
	}
	
	private FeeStatusEntity retrieveFeeStatusEntity(FeeStatus status)throws FeeException{
		try{
			return (FeeStatusEntity)new MasterPersistence().retrieveMasterEntity(status.getValue(), FeeStatusEntity.class, userContext.getLocaleId());
		}catch(PersistenceException pe){
			throw new FeeException(pe);
		}
	}

	
 
}
