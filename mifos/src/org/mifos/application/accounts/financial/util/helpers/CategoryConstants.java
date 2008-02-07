/**

 * CategoryConstants.java    version: 1.0

 

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

package org.mifos.application.accounts.financial.util.helpers;

/**
 * Hard-coded constants for general ledger categories. Categories are top-level
 * entities in the chart of accounts (see "COA" table in latest-data.sql).
 * <p>
 * TODO: don't use IDs from COA table. Replace this class with methods to fetch
 * top-level categories in {@link DynamicFinancialRules}.
 * <p>
 * 
 * @see <a
 *      href="http://mifos.org/knowledge/functional-specs/accounting-in-mifos">Accounting
 *      in Mifos</a>
 */
public interface CategoryConstants {
	public static final short ASSETS = 1;

	public static final short CASHBANKBALANCE = 2;

	public static final short LIABILITIES = 14;

	public static final short INCOME = 18;
	
	public static final short DIRECTINCOME = 19;
	
	public static final short INTERESTONLOANS = 21;

	public static final short EXPENDITURE = 53;

}
