/**

 * ProductMixBO.java    version: xxx

 

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

package org.mifos.application.productsmix.business;


import org.mifos.application.productdefinition.business.PrdOfferingBO;
import org.mifos.application.productdefinition.exceptions.ProductDefinitionException;
import org.mifos.application.productsmix.persistence.ProductMixPersistence;
import org.mifos.framework.business.BusinessObject;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.exceptions.PersistenceException;


/**
 * This class encapsulate the product mix
 *  (Allowed / Not Allowed products)
 */

public class ProductMixBO extends BusinessObject {

	private final Integer prdOfferingMixId;
	private PrdOfferingBO prdOfferingId;
	private PrdOfferingBO prdOfferingNotAllowedId;
	

	private MifosLogger prdMixLogger = MifosLogManager
			.getLogger(LoggerConstants.PRDDEFINITIONLOGGER);
	
	public ProductMixBO() {
		this.prdOfferingMixId = null;
		this.prdOfferingId = null;
		this.prdOfferingNotAllowedId = null;
		this.prdMixLogger = null;
	}
	public ProductMixBO(PrdOfferingBO prdOfferingId,
			PrdOfferingBO prdOfferingNotAllowedId) {
		this.prdOfferingMixId = null;
		this.prdOfferingId = prdOfferingId;
		this.prdOfferingNotAllowedId = prdOfferingNotAllowedId;

	}

	public MifosLogger getPrdMixLogger() {
		return prdMixLogger;
	}
	public void setPrdMixLogger(MifosLogger prdMixLogger) {
		this.prdMixLogger = prdMixLogger;
	}
	public PrdOfferingBO getPrdOfferingId() {
		return prdOfferingId;
	}
	public void setPrdOfferingId(PrdOfferingBO prdOfferingId) {
		this.prdOfferingId = prdOfferingId;
	}
	public Integer getPrdOfferingMixId() {
		return prdOfferingMixId;
	}
	public PrdOfferingBO getPrdOfferingNotAllowedId() {
		return prdOfferingNotAllowedId;
	}
	public void setPrdOfferingNotAllowedId(PrdOfferingBO prdOfferingNotAllowedId) {
		this.prdOfferingNotAllowedId = prdOfferingNotAllowedId;
	}
	public void update() throws ProductDefinitionException {
		try {
			setUpdateDetails();
			new ProductMixPersistence().createOrUpdate(this);
		} catch (PersistenceException e) {
			throw new ProductDefinitionException(e);
		}
	}
	public void delete() throws ProductDefinitionException {
		try {
			new ProductMixPersistence().delete(this);
		}
		catch (PersistenceException e) {
			throw new ProductDefinitionException(e);
		}
	}

	
	public boolean doesPrdOfferingsCanCoexist(Short idPrdOff_A, Short idPrdOff_B)
			throws PersistenceException {
		try {
			return new ProductMixPersistence().doesPrdOfferingsCanCoexist(
					idPrdOff_A, idPrdOff_B);
		}
		catch (PersistenceException e) {
			throw new PersistenceException(e);
		}
	}


}
