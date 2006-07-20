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
import org.mifos.application.fees.util.helpers.FeeLevel;
import org.mifos.application.fees.util.helpers.FeeStatus;
import org.mifos.application.fees.util.helpers.RateAmountFlag;
import org.mifos.application.office.business.OfficeBO;
import org.mifos.application.office.persistence.service.OfficePersistenceService;
import org.mifos.framework.business.BusinessObject;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.struts.plugin.helper.EntityMasterConstants;
import org.mifos.framework.util.helpers.Money;

public class FeesBO extends BusinessObject {

	private Short feeId;

	private String globalFeeNum;

	private OfficeBO office;

	private String feeName;

	private CategoryTypeEntity categoryType;

	private Set<FeeLevelEntity> feeLevels;

	private FeeFrequencyEntity feeFrequency;

	private Money feeAmount;

	private Double rate;

	private FeeFormulaEntity feeFormula;

	private GLCodeEntity glCodeEntity;

	private FeeStatusEntity feeStatus;

	private Short rateFlatFlag;
	
	private Short updateFlag;
	
	private FeeUpdateTypeEntity feeUpdateType;

	@Deprecated
	private Double rateOrAmount;

	protected FeesBO() {
		feeLevels = new HashSet<FeeLevelEntity>();
	}

	public FeesBO(UserContext userContext) {
		this();
		this.userContext = userContext;
	}

	public Short getFeeId() {
		return feeId;
	}

	private void setFeeId(Short feeId) {
		this.feeId = feeId;
	}

	public String getGlobalFeeNum() {
		return globalFeeNum;
	}

	public void setGlobalFeeNum(String globalFeeNum) {
		this.globalFeeNum = globalFeeNum;
	}

	public String getFeeName() {
		return feeName;
	}

	public void setFeeName(String feeName) {
		this.feeName = feeName;
	}

	public FeeFrequencyEntity getFeeFrequency() {
		return this.feeFrequency;
	}

	public void setFeeFrequency(FeeFrequencyEntity feeFrequency) {
		if (feeFrequency != null)
			feeFrequency.setFee(this);
		this.feeFrequency = feeFrequency;
	}

	public OfficeBO getOffice() {
		return office;
	}

	public void setOffice(OfficeBO office) {
		this.office = office;
	}

	@Deprecated
	private Double getRateOrAmount() {
		return rateOrAmount;
	}

	@Deprecated
	private void setRateOrAmount(Double rateOrAmount) {
		this.rateOrAmount = rateOrAmount;
	}

	private Short getRateFlatFlag() {
		return rateFlatFlag;
	}

	private void setRateFlatFlag(Short rateFlatFlag) {
		this.rateFlatFlag = rateFlatFlag;
	}

	public boolean isRateFee() {
		return this.rateFlatFlag > RateAmountFlag.AMOUNT.getValue();
	}

	public void setRateFee(boolean rateFee) {
		this.rateFlatFlag = (rateFee ? RateAmountFlag.RATE.getValue()
				: RateAmountFlag.AMOUNT.getValue());
	}

	public CategoryTypeEntity getCategoryType() {
		return categoryType;
	}

	public void setCategoryType(CategoryTypeEntity categoryType) {
		this.categoryType = categoryType;
	}

	public Set<FeeLevelEntity> getFeeLevels() {
		return feeLevels;
	}

	private void setFeeLevels(Set<FeeLevelEntity> feeLevels) {
		this.feeLevels = feeLevels;
	}

	public void addFeeLevel(FeeLevelEntity feeLevel) {
		feeLevel.setFee(this);
		feeLevels.add(feeLevel);
	}

	public GLCodeEntity getGlCodeEntity() {
		return glCodeEntity;
	}

	public void setGlCodeEntity(GLCodeEntity codeEntity) {
		glCodeEntity = codeEntity;
	}

	public FeeFormulaEntity getFeeFormula() {
		return feeFormula;
	}

	public void setFeeFormula(FeeFormulaEntity feeFormula) {
		this.feeFormula = feeFormula;
	}

	public FeeStatusEntity getFeeStatus() {
		return feeStatus;
	}

	private void setFeeStatus(FeeStatusEntity status) {
		this.feeStatus = status;
	}

	public void modifyStatus(FeeStatus status) {
		setFeeStatus(new FeeStatusEntity(status.getValue()));
	}

	public Money getFeeAmount() {
		return feeAmount;
	}

	public void setFeeAmount(Money feeAmount) {
		this.feeAmount = feeAmount;
	}

	public void setAmount(String amount) {
		feeAmount = new Money(amount);
	}

	public Double getRate() {
		return rate;
	}

	public void setRate(Double rate) {
		this.rate = rate;
	}

	private void buildFeeLevels(boolean adminCheck) throws FeeException {
		if (adminCheck) {
			if (categoryType.getCategoryId() == null)
				throw new FeeException("errors.invalidcategory");
			Short categoryId = categoryType.getCategoryId();
			if (categoryId.equals(FeeCategory.CLIENT.getValue()))
				addFeeLevel(createFeeLevel(FeeLevel.CLIENTLEVEL));
			else if (categoryId.equals(FeeCategory.GROUP.getValue()))
				addFeeLevel(createFeeLevel(FeeLevel.GROUPLEVEL));
			else if (categoryId.equals(FeeCategory.CENTER.getValue()))
				addFeeLevel(createFeeLevel(FeeLevel.CENTERLEVEL));
			else if (categoryId.equals(FeeCategory.ALLCUSTOMERS.getValue())) {
				addFeeLevel(createFeeLevel(FeeLevel.CLIENTLEVEL));
				addFeeLevel(createFeeLevel(FeeLevel.GROUPLEVEL));
				addFeeLevel(createFeeLevel(FeeLevel.CENTERLEVEL));
			}
		}
	}

	private FeeLevelEntity createFeeLevel(FeeLevel feeLevel) {
		FeeLevelEntity level = new FeeLevelEntity();
		level.setLevelId(feeLevel.getValue());
		return level;
	}

	private void setRateOrAmount() {
		if (rate != null)
			setRateOrAmount(rate);
		else
			setRateOrAmount(feeAmount.getAmountDoubleValue());
	}

	public void save(boolean adminCheck) throws ServiceException, FeeException {
		if (feeFrequency == null)
			throw new FeeException("errors.invalidfeefrequency");
		feeFrequency.buildFeeFrequency();
		setCreateDetails();
		modifyStatus(FeeStatus.ACTIVE);
		setOffice(new OfficePersistenceService().getHeadOffice());
		buildFeeLevels(adminCheck);
		setRateOrAmount();
		setRateFee(rate != null);
		try {
			new FeePersistence().createOrUpdate(this);
		} catch (HibernateException he) {
			throw new FeeException("errors.feecreate", he);
		}
	}

	public void update() throws ServiceException, FeeException {
		setUpdateDetails();
		setRateOrAmount();
		try {
			new FeePersistence().createOrUpdate(this);
		} catch (HibernateException he) {
			throw new FeeException("errors.feecreate", he);
		}
	}

	@Override
	public Short getEntityID() {
		return EntityMasterConstants.Fees;
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

	public boolean isAdminFee() {
		return getFeeLevels() != null && getFeeLevels().size() > 0;
	}

	public boolean isActive() {
		return getFeeStatus().isActive();
	}

	public Short getUpdateFlag() {
		return updateFlag;
	}

	public void setUpdateFlag(Short updateFlag) {
		this.updateFlag = updateFlag;
	}

	public FeeUpdateTypeEntity getFeeUpdateType() {
		return feeUpdateType;
	}

	public void setFeeUpdateType(FeeUpdateTypeEntity feeUpdateType) {
		this.feeUpdateType = feeUpdateType;
	}

}
