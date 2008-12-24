/**

 * FundBO.java    version: 1.0

 

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
package org.mifos.application.fund.business;

import org.mifos.application.fund.exception.FundException;
import org.mifos.application.fund.persistence.FundPersistence;
import org.mifos.application.fund.util.helpers.FundConstants;
import org.mifos.application.master.business.FundCodeEntity;
import org.mifos.framework.business.BusinessObject;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.util.helpers.StringUtils;

public class FundBO extends BusinessObject {
	private final Short fundId;

	private final FundCodeEntity fundCode;

	private String fundName;

	private MifosLogger logger = MifosLogManager
			.getLogger(LoggerConstants.FUNDLOGGER);

	public FundBO(FundCodeEntity fundCode, String fundName)
			throws FundException {
		logger.debug("building fund");
		this.fundId = null;
		validate(fundCode, fundName);
		validateDuplicateFundName(fundName);
		this.fundCode = fundCode;
		this.fundName = fundName;
		logger.debug("Fund build :" + getFundName());
	}

	public FundBO() {
		this.fundId = null;
		this.fundCode = null;
	}

	public Short getFundId() {
		return fundId;
	}

	public String getFundName() {
		return fundName;
	}

	public void setFundName(String fundName) {
		this.fundName = fundName;
	}

	public FundCodeEntity getFundCode() {
		return fundCode;
	}

	public void save() throws FundException {
		logger.debug("creating the fund ");
		try {
			new FundPersistence().createOrUpdate(this);
		} catch (PersistenceException e) {
			throw new FundException(e);
		}
		logger.debug("creating the fund Done : " + getFundName());
	}
	
	public void update(String fundName) throws FundException {
		logger.debug("updating the fund ");
		validateFundName(fundName);
		if(!this.fundName.equals(fundName))
			validateDuplicateFundName(fundName);
		this.fundName = fundName;
		try {
			new FundPersistence().createOrUpdate(this);
		} catch (PersistenceException e) {
			throw new FundException(e);
		}
		logger.debug("updation of the sfund Done : " + getFundName());
	}

	private void validate(FundCodeEntity fundCode, String fundName)
			throws FundException {
		logger.debug("Validating the fields in Fund");
		validateFundName(fundName);
		validateFundCode(fundCode);
		logger.debug("Validating the fields in Fund done");
	}

	private void validateDuplicateFundName(String fundName)
			throws FundException {
		logger.debug("Checking for duplicate Fund name");
		try {
			if (!new FundPersistence().getFundNameCount(fundName.trim()).equals(
					Long.valueOf("0")))
				throw new FundException(
						FundConstants.DUPLICATE_FUNDNAME_EXCEPTION);
		} catch (PersistenceException e) {
			throw new FundException(e);
		}
	}

	private void validateFundName(String fundName) throws FundException {
		logger.debug("Checking for empty Fund name");
		if (StringUtils.isNullOrEmpty(fundName))
			throw new FundException(FundConstants.INVALID_FUND_NAME);
	}

	private void validateFundCode(FundCodeEntity fundCode) throws FundException {
		logger.debug("Checking for empty Fund Code");
		if (fundCode == null)
			throw new FundException(FundConstants.INVALID_FUND_CODE);
	}
}
