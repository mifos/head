/**
 
 * Currency.java    version: xxx
 
 
 
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

package org.mifos.application.master.util.valueobjects;

import org.mifos.application.master.business.MifosCurrency;
import org.mifos.framework.util.valueobjects.ValueObject;

/**
 * Replaced by {@link MifosCurrency}.
 */
public class Currency extends ValueObject {
	
	public Currency() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	/** The composite primary key value. */
	private java.lang.Short currencyId;
	
	/** The value of the simple currencyName property. */
	private java.lang.String currencyName;
	
	/** The value of the simple displaySymbol property. */
	private java.lang.String displaySymbol;
	
	/** The value of the simple roundUpDown property. */
	private Short roundingMode;
	
	/** The value of the simple roundingDigits property. */
	private Float roundingAmount;
	
	/** The value of the simple defaultCurrency property. */
	private java.lang.Short defaultCurrency;

	public java.lang.Short getCurrencyId() {
		return currencyId;
	}

	public void setCurrencyId(java.lang.Short currencyId) {
		this.currencyId = currencyId;
	}

	public java.lang.String getCurrencyName() {
		return currencyName;
	}

	public void setCurrencyName(java.lang.String currencyName) {
		this.currencyName = currencyName;
	}

	public java.lang.Short getDefaultCurrency() {
		return defaultCurrency;
	}

	public void setDefaultCurrency(java.lang.Short defaultCurrency) {
		this.defaultCurrency = defaultCurrency;
	}

	public java.lang.String getDisplaySymbol() {
		return displaySymbol;
	}

	public void setDisplaySymbol(java.lang.String displaySymbol) {
		this.displaySymbol = displaySymbol;
	}

	public Float getRoundingAmount() {
		return roundingAmount;
	}

	public void setRoundingAmount(Float roundingAmount) {
		this.roundingAmount = roundingAmount;
	}

	public java.lang.Short getRoundingMode() {
		return roundingMode;
	}

	public void setRoundingMode(java.lang.Short roundingMode) {
		this.roundingMode = roundingMode;
	}
	
}
