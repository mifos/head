/**

 * Peanlty.java    version: xxx

 

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

package org.mifos.application.penalty.util.valueobjects;

import org.mifos.application.productdefinition.util.valueobjects.ProductType;
import org.mifos.framework.util.valueobjects.ValueObject;

public class Penalty extends ValueObject {

	private static final long serialVersionUID = 43241554536556541L;

	public Penalty() {
		super();
	}
	
	public Short penaltyID;
	
	public ProductType productType;
	
	public String penaltyType;

	

	/**
	 * @return Returns the penaltyID.
	 */
	public Short getPenaltyID() {
		return penaltyID;
	}

	/**
	 * @param penaltyID The penaltyID to set.
	 */
	public void setPenaltyID(Short penaltyID) {
		this.penaltyID = penaltyID;
	}

	/**
	 * @return Returns the penaltyType.
	 */
	public String getPenaltyType() {
		return penaltyType;
	}

	/**
	 * @param penaltyType The penaltyType to set.
	 */
	public void setPenaltyType(String penaltyType) {
		this.penaltyType = penaltyType;
	}

	/**
	 * @return Returns the producType.
	 */
	public ProductType getProductType() {
		return productType;
	}

	/**
	 * @param producType The producType to set.
	 */
	public void setProductType(ProductType productType) {
		this.productType = productType;
	}
	
	

}
