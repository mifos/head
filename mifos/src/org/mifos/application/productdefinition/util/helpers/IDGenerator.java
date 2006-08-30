/**
 
 * IDGenerator.java    version: xxx
 
 
 
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

package org.mifos.application.productdefinition.util.helpers;

import org.mifos.application.productdefinition.exceptions.IDGenerationException;

public class IDGenerator {
	static short prdCategoryId=0;
	
	/**
	 * 
	 */
	public IDGenerator() {
		super();
		
	}
	
	/**
	 * Generates an id for the Product category.
	 * @return
	 * @throws IDGenerationException - IF it is not able to generate the ID
	 */
	public static Short generateIdForProductCategory()throws IDGenerationException{
		
		return ++prdCategoryId;
	}
	
	/**
	 * Generates an id for the Loan Product .
	 * @return
	 * @throws IDGenerationException - IF it is not able to generate the ID
	 */
	public static String generateIdForLoanProduct()throws IDGenerationException{
		return null;
	}
	
	/**
	 * Generates an id for the Savings Product .
	 * @return
	 * @throws IDGenerationException - IF it is not able to generate the ID
	 */
	public static String generateIdForSavingsProduct()throws IDGenerationException{
		return null;
	}
	
	/**
	 * Generates an id for the Insurance Product .
	 * @return
	 * @throws IDGenerationException - IF it is not able to generate the ID
	 */
	public static String generateIdForInsuranceProduct()throws IDGenerationException{
		return null;
	}
	
	
}
