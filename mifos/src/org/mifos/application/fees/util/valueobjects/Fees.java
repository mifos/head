/**

 * Fees.java    version: xxx



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

package org.mifos.application.fees.util.valueobjects;

import java.util.Set;

import org.mifos.application.accounts.financial.business.GLCodeEntity;
import org.mifos.application.fees.util.helpers.FeesConstants;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.valueobjects.ValueObject;

/**
 * @author ashishsm
 *
 */
/**
 * A class that represents a row in the 'fees' table.
 * This class may be customized as it is never re-generated
 * after being created.
 */
public class Fees extends ValueObject {
	/**
	 * Simple constructor of Fees instances.
	 */
	public Fees() {
	}

	
	private Short feeId;

	
	private Short categoryId;

	
	private String globalFeeNum;

	
	private String feeName;

	
	private Short officeId;

	
	private  GLCodeEntity glCodeEntity;
	
	
	private Short status;

	
	private Double rateOrAmount;

	
	private Short rateFlatFalg;

	private Short formulaId;

	private FeeFrequency feeFrequency;

	
	private java.util.Date createdDate;

	
	private Integer createdBy;

	
	private java.util.Date updatedDate;

	
	private Integer updatedBy;

	
	private Short updatedOfficeId;

	
	private String adminCheck;

	
	private Integer versionNo;

	
	private Set<FeeLevel> feeLevelSet;
	
	private Money feeAmount;
	private Double rate;

	
	public Short getFeeId() {
		return feeId;
	}

	
	public void setFeeId(Short feeId) {
		this.feeId = feeId;
	}

	
	public String getGlobalFeeNum() {
		return this.globalFeeNum;
	}

	
	public void setGlobalFeeNum(String globalFeeNum) {
		this.globalFeeNum = globalFeeNum;
	}

	
	public String getFeeName() {
	
		return this.feeName;
	}

	public void setFeeName(String feeName) {
		this.feeName = feeName;
	}

	public FeeFrequency getFeeFrequency() {
		return this.feeFrequency;
	}

	public void setFeeFrequency(FeeFrequency feeFrequency) {
		if (feeFrequency != null)
			feeFrequency.setFee(this);
		this.feeFrequency = feeFrequency;
	}

	public Short getOfficeId() {
		return this.officeId;
	}

	public void setOfficeId(Short officeId) {
		this.officeId = officeId;
	}

	public Short getStatus() {
		return this.status;
	}

	public void setStatus(Short status) {
		this.status = status;
	}

	public Double getRateOrAmount() {
		return this.rateOrAmount;
	}

	public void setRateOrAmount(Double rateOrAmount) {
		this.rateOrAmount = rateOrAmount;
	}

	public Short getRateFlatFalg() {
		return this.rateFlatFalg;
	}

	public void setRateFlatFalg(Short rateFlatFalg) {
		this.rateFlatFalg = rateFlatFalg;
	}

	public Short getFormulaId() {
		return this.formulaId;
	}

	public void setFormulaId(Short formulaId) {
		this.formulaId = formulaId;
	}

	public java.util.Date getCreatedDate() {
		return this.createdDate;
	}

	public void setCreatedDate(java.util.Date createdDate) {
		this.createdDate = createdDate;
	}

	public Integer getCreatedBy() {
		return this.createdBy;
	}

	public void setCreatedBy(Integer createdBy) {
		this.createdBy = createdBy;
	}

	public java.util.Date getUpdatedDate() {
		return this.updatedDate;
	}

	public void setUpdatedDate(java.util.Date updatedDate) {
		this.updatedDate = updatedDate;
	}

	public Integer getUpdatedBy() {
		return this.updatedBy;
	}

	public void setUpdatedBy(Integer updatedBy) {
		this.updatedBy = updatedBy;
	}

	public Short getUpdatedOfficeId() {
		return this.updatedOfficeId;
	}

	public void setUpdatedOfficeId(Short updatedOfficeId) {
		this.updatedOfficeId = updatedOfficeId;
	}

	public Short getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Short categoryId) {
		this.categoryId = categoryId;
	}

	public boolean equals(Object obj) {
		Fees fees = (Fees) obj;
		if (this.globalFeeNum.equals(fees.getGlobalFeeNum())) {
			return true;
		} else {
			return false;
		}
	}

	public String getResultName() {
		return FeesConstants.FEES;
	}

	public Integer getVersionNo() {
		return versionNo;
	}

	public void setVersionNo(Integer versionNo) {
		this.versionNo = versionNo;
	}

	public Set<FeeLevel> getFeeLevelSet() {
		return feeLevelSet;
	}

	public void setFeeLevelSet(Set<FeeLevel> feeLevelSet) {
		if (feeLevelSet != null) {
			for (FeeLevel feeLevel : feeLevelSet) {
				feeLevel.setFeeId(this);
			}
		}
		this.feeLevelSet = feeLevelSet;
	}

	public String getAdminCheck() {
		return adminCheck;
	}

	public void setAdminCheck(String adminCheck) {
		this.adminCheck = adminCheck;
	}


	public GLCodeEntity getGlCodeEntity() {
		return glCodeEntity;
	}


	public void setGlCodeEntity(GLCodeEntity codeEntity) {
		glCodeEntity = codeEntity;
	}


	public Money getFeeAmount() {
		return feeAmount;
	}


	public void setFeeAmount(Money feeAmount) {
		this.feeAmount = feeAmount;
	}


	public Double getRate() {
		return rate;
	}


	public void setRate(Double rate) {
		this.rate = rate;
	}
	
	
	
}
