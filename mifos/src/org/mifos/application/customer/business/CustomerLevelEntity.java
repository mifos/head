/**

 * CustomerLevel.java    version: xxx



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
package org.mifos.application.customer.business;

import org.mifos.application.accounts.util.helpers.AccountConstants;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.application.master.util.valueobjects.LookUpEntity;
import org.mifos.application.productdefinition.util.helpers.ProductDefinitionConstants;
import org.mifos.framework.business.PersistentObject;

/**
 * This class  represents  customer level e.g client,center etc
 */
public class CustomerLevelEntity extends PersistentObject {

	private Short levelId;

	private CustomerLevelEntity parentCustomerLevel;

	private LookUpEntity lookUpEntity;

	private Short interactionFlag;

	private Short maxChildCount;

	private Short maxInstanceCount;

	public CustomerLevelEntity() {
	}

	public Short getLevelId() {
		return levelId;
	}
	public CustomerLevelEntity getParentCustomerLevel() {
		return parentCustomerLevel;
	}
	public void setParentCustomerLevel(CustomerLevelEntity parentCustomerLevel) {
		this.parentCustomerLevel = parentCustomerLevel;
	}

	public LookUpEntity getLookupEntity() {
		return this.lookUpEntity;
	}
	public void setLookUpEntity(LookUpEntity lookUpEntity) {
		this.lookUpEntity = lookUpEntity;
	}
	private Short getInteractionFlag() {
		return this.interactionFlag;
	}
	private void setInteractionFlag(Short interactionFlag) {
		this.interactionFlag = interactionFlag;
	}
	public boolean isInteractionAllowed(){
		return this.interactionFlag>0;
	}
	public Short getMaxChildCount() {
		return this.maxChildCount;
	}

	public void setMaxChildCount(Short maxChildCount) {
		this.maxChildCount = maxChildCount;
	}
	public Short getMaxInstanceCount() {
		return this.maxInstanceCount;
	}
	public void setMaxInstanceCount(Short maxInstanceCount) {
		this.maxInstanceCount = maxInstanceCount;
	}
	public LookUpEntity getLookUpEntity() {
		return lookUpEntity;
	}
	
	public void setLevelId(Short levelId) {
		this.levelId = levelId;
	}
	
	/** Based on the customer level , it returns the product applicable type. This is being used, 
	 *  when savings/loan products are to find as per customer level.
	 */
	public Short getProductApplicableType(){
		if(levelId!=null){
			if(levelId.shortValue() == CustomerConstants.CLIENT_LEVEL_ID ){
				return Short.valueOf(ProductDefinitionConstants.OFFERINGAPPLICABLETOCLIENTS);
			}else if(levelId == CustomerConstants.GROUP_LEVEL_ID){
				return Short.valueOf(ProductDefinitionConstants.OFFERINGAPPLICABLETOGROUPS);	
			}else
				return Short.valueOf(ProductDefinitionConstants.OFFERINGAPPLICABLETOCENTERS);
		}
		return null;
	}
}
