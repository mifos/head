/**
 
 * Money.java    version: 1.0
 
 
 
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

package org.mifos.framework.util.helpers;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

import org.mifos.application.master.business.MifosCurrency;
import org.mifos.framework.components.configuration.business.Configuration;

/**
 * This class represents Money objects in the system, it would be used for all
 * financial operations like addition,subtraction etc of monies. As of now it
 * deals with only one currency but later it can be extended to handle currency
 * conversions while performing operations.This is an immutable class as the
 * money object is not supposed to be modified .
 * 
 * @author ashishsm
 * 
 */
public final class Money implements Serializable {

	private final MifosCurrency currency;

	private final BigDecimal amount;

	/**
	 * In the constructor we are not creating a new MifosCurrency object because
	 * even MifosCurrency is immutable.
	 * 
	 */
	@Deprecated
	public Money(MifosCurrency currency, double amount) {
		this.currency = currency;
		this.amount = setScale(new BigDecimal(amount));

	}

	public Money(MifosCurrency currency, String amount) {
		this.currency = currency;
		if (amount == null || "".equals(amount.trim()))
			this.amount = null;
		else
			this.amount = setScale(new BigDecimal(amount));
	}

	public Money(String amount) {
		this.currency = Configuration.getInstance().getSystemConfig().getCurrency();
		if (amount == null || "".equals(amount.trim()))
			this.amount = null;

		else
			this.amount = setScale(new BigDecimal(amount));
	}

	/**
	 * In the constructor we are not creating a new MifosCurrency object because
	 * even MifosCurrency is immutable.
	 * 
	 */
	public Money(MifosCurrency currency, BigDecimal amount) {
		this.currency = currency;
		this.amount = setScale(amount);
	}
	
	public Money(BigDecimal amount) {
		this.currency = Configuration.getInstance().getSystemConfig().getCurrency();
		this.amount = amount;

	}


	/**
	 * This creates a Money object with currency set to MFICurrency and amount
	 * set to zero.
	 */
	public Money() {
		this.currency = Configuration.getInstance().getSystemConfig().getCurrency();
		this.amount = setScale(new BigDecimal(0));

	}

	public BigDecimal getAmount() {
		return this.amount;
	}

	public double getAmountDoubleValue() {
		return amount.doubleValue();
	}

	public MifosCurrency getCurrency() {
		return currency;
	}

	/**
	 * Returns true if currency is different.
	 */
	private boolean isCurrencyDifferent(Money money) {
		return !this.currency.equals(money.getCurrency());
	}

	/**
	 * If the object passed as parameter is null or if its currency or amount is
	 * null it returns this else performs the required operation and returns a
	 * new Money object corresponding to the value.
	 */
	public Money add(Money money) {
		if (null != money) {
			if (isCurrencyDifferent(money))
				throw new IllegalArgumentException(
						ExceptionConstants.ILLEGALMONEYOPERATION);
		}
		return (money == null || money.getAmount() == null || money
				.getCurrency() == null) ? this : new Money(this.currency,
				setScale(this.amount.add(money.getAmount())));

	}

	/**
	 * If the object passed as parameter is null or if its currency or amount is
	 * null it returns this else performs the required operation and returns a
	 * new Money object corresponding to the value.
	 */
	public Money subtract(Money money) {
		if (null != money) {
			if (isCurrencyDifferent(money))
				throw new IllegalArgumentException(
						ExceptionConstants.ILLEGALMONEYOPERATION);
		}
		return (money == null || money.getAmount() == null || money
				.getCurrency() == null) ? this : new Money(this.currency,
				setScale(this.amount.subtract(money.getAmount())));

	}

	/**
	 * If the object passed as parameter is null or if its currency or amount is
	 * null it returns this else performs the required operation and returns a
	 * new Money object corresponding to the value.
	 */
	public Money multiply(Money money) {
		if (null != money) {
			if (isCurrencyDifferent(money))
				throw new IllegalArgumentException(
						ExceptionConstants.ILLEGALMONEYOPERATION);
		}
		return (money == null || money.getAmount() == null || money
				.getCurrency() == null) ? this : new Money(this.currency,
				setScale(this.amount.multiply(money.getAmount())));

	}

	public Money multiply(Double factor) {
		if (factor != null)
			return new Money(this.currency, setScale(this.amount
					.multiply(new BigDecimal(factor))));
		else
			throw new IllegalArgumentException(
					ExceptionConstants.ILLEGALMONEYOPERATION);
	}

	/**
	 * If the object passed as parameter is null or if its currency or amount is
	 * null it returns this else performs the required operation and returns a
	 * new Money object corresponding to the value.
	 */
	public Money divide(Money money) {
		if (null != money) {
			if (isCurrencyDifferent(money))
				throw new IllegalArgumentException(
						ExceptionConstants.ILLEGALMONEYOPERATION);
		}
		return (money == null || money.getAmount() == null || money
				.getCurrency() == null) ? this : new Money(this.currency,
				setScale(this.amount.divide(money.getAmount())));
	}

	public Money negate() {
		BigDecimal tempAmnt = this.amount.negate();
		return new Money(this.currency, setScale(tempAmnt));
	}

	/**
	 * This method returns a new Money object with currency same as current
	 * currency and amount calculated after rounding based on rounding mode and
	 * roundingAmount where in both are obtained from MifosCurrency object.
	 * 
	 * The rounding calculation is as follows:- Lets say we want to round 142.34
	 * to nearest 50 cents and and rounding mode is ceil (i.e. to greater
	 * number) we will divide 142.34 by .5 which will result in 284.68 now we
	 * will round this to a whole number using ceil mode which will result in
	 * 285 and then multiply 285 by 0.5 resulting in 142.5.
	 * 
	 * @return
	 */
	public static Money round(Money money) {
		if (null != money) {
			BigDecimal roundingAmount = new BigDecimal(money.getCurrency()
					.getRoundingAmount().doubleValue());
			BigDecimal nearestFactor = money.getAmount().divide(roundingAmount);

			if (money.getCurrency().getRoundingMode().equals(
					MifosCurrency.CIEL_MODE))
				nearestFactor = nearestFactor.setScale(0, RoundingMode.CEILING);
			else if (money.getCurrency().getRoundingMode().equals(
					MifosCurrency.FLOOR_MODE))
				nearestFactor = nearestFactor.setScale(0, RoundingMode.FLOOR);

			BigDecimal roundedAmount = nearestFactor.multiply(roundingAmount);
			return new Money(money.getCurrency(), roundedAmount);
		}
		return money;
	}

	/**
	 * This method return true if the currecy associated with the two money
	 * objects is equal and also the compareTo method of BigDecimal return 0 for
	 * the amount of the two money objects. It is not advisable to use equals
	 * method of BigDecimal because it would return false for numbers like 10.0
	 * and 10.00 instead we should use compareTo.
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if (!(obj instanceof Money))
			return false;
		if (obj == this)
			return true;
		Money money = (Money) obj;
		return this.currency.equals(money.getCurrency())
				&& (this.amount.compareTo(money.getAmount()) == 0);
	}

	public int hashCode() {
		if (amount == null || currency == null)
			return System.identityHashCode(null);
		return this.currency.getCurrencyId() * 100 + this.amount.intValue();
	}

	private BigDecimal setScale(BigDecimal amount) {
		if (null != amount && null != currency) {
			/*
			 * if(this.currency.getRoundingMode().equals(MifosCurrency.CIEL_MODE) )
			 * amount = amount.setScale(currency.getDefaultDigitsAfterDecimal(),
			 * RoundingMode.CEILING); else
			 * if(this.currency.getRoundingMode().equals(MifosCurrency.FLOOR_MODE) )
			 * amount = amount.setScale(currency.getDefaultDigitsAfterDecimal(),
			 * RoundingMode.FLOOR); else amount = amount;
			 */
			amount = amount.setScale(currency.getDefaultDigitsAfterDecimal(),
					RoundingMode.HALF_UP);
		}

		return amount;
	}
	

	public String toString() {
		if(amount != null && currency != null)
			return amount.toString();
		return "0";
	}

}
