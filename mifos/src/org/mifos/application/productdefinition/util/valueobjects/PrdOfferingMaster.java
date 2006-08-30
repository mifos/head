/**

 * PrdOfferingMaster.java    version: xxx

 

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

package org.mifos.application.productdefinition.util.valueobjects;

import org.mifos.framework.util.valueobjects.ValueObject;

/**
 * This is a helper class which would be used when we need only certain details of a product offering
 * hence instead of loading the entire product offering object we can load only this object using a query. 
 */
public class PrdOfferingMaster extends ValueObject {

	public PrdOfferingMaster() {
		super();
		
	}
	
	/**
	 * This constructor would be used to create the object from the query accounts.applicableproductofferings
	 * 
	 * @param prdOfferingId
	 * @param prdOfferingShortName
	 * @param globalPrdOfferingNum
	 */
	public PrdOfferingMaster(Short prdOfferingId,String prdOfferingName,String globalPrdOfferingNum,Short recurAfter) {
		this.globalPrdOfferingNum = globalPrdOfferingNum;
		this.prdOfferingName = prdOfferingName;
		this.prdOfferingId = prdOfferingId;
		this.recurAfter = recurAfter;
		
	}
	
	private Short prdOfferingId;
	private String prdOfferingName;
	private String globalPrdOfferingNum;
	private Short recurAfter;
	/**
	 * @return Returns the globalPrdOfferingNum}.
	 */
	public String getGlobalPrdOfferingNum() {
		return globalPrdOfferingNum;
	}
	/**
	 * @param globalPrdOfferingNum The globalPrdOfferingNum to set.
	 */
	public void setGlobalPrdOfferingNum(String globalPrdOfferingNum) {
		this.globalPrdOfferingNum = globalPrdOfferingNum;
	}
	/**
	 * @return Returns the prdOfferingId}.
	 */
	public Short getPrdOfferingId() {
		return prdOfferingId;
	}
	/**
	 * @param prdOfferingId The prdOfferingId to set.
	 */
	public void setPrdOfferingId(Short prdOfferingId) {
		this.prdOfferingId = prdOfferingId;
	}
	

	/**
	 * @return Returns the prdOfferingName}.
	 */
	public String getPrdOfferingName() {
		return prdOfferingName;
	}

	/**
	 * @param prdOfferingName The prdOfferingName to set.
	 */
	public void setPrdOfferingName(String prdOfferingName) {
		this.prdOfferingName = prdOfferingName;
	}

	public Short getRecurAfter() {
		return recurAfter;
	}

	public void setRecurAfter(Short recurAfter) {
		this.recurAfter = recurAfter;
	}

}
