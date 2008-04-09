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
import java.math.MathContext;
import java.math.RoundingMode;

import org.mifos.application.master.business.MifosCurrency;
import org.mifos.framework.components.configuration.business.Configuration;

/**
 * This class represents Money objects in the system, it should be used for all
 * financial operations like addition,subtraction etc of money. As of now it
 * deals with only one currency but later it can be extended to handle currency
 * conversions while performing operations.This is an immutable class as the
 * money object is not supposed to be modified .
 * 
 * April, 2008 Money class refactoring
 * 
 */
public final class Money implements Serializable {

	/*
	 * March 21, 2008 refactoring in progress.
	 * usingNewMoney is an application-wide setting to switch between
	 * the original implementation of the Money class (v1) and a new
	 * implementation (v2).  This will go away once the new implementation
	 * is complete and tested, but allows the two to coexist for the time
	 * being.
	 */
	private static boolean usingNewMoney = false;

	public static boolean isUsingNewMoney() {
		return usingNewMoney;
	}

	public static void setUsingNewMoney(boolean usingNewMoney) {
		Money.usingNewMoney = usingNewMoney;
	}
	
	private static int internalPrecision = 13;
	private static RoundingMode internalRoundingMode = RoundingMode.HALF_UP;
	private static MathContext internalPrecisionAndRounding = 
		new MathContext(internalPrecision, internalRoundingMode);
	
	private static MifosCurrency defaultCurrency = null;
	
	public static MifosCurrency getDefaultCurrency() {
		return defaultCurrency;
	}

	public static void setDefaultCurrency(MifosCurrency defaultCurrency) {
		Money.defaultCurrency = defaultCurrency;
	}

	public static int getInternalPrecision() {
		return internalPrecision;
	}
	
	public static MathContext getInternalPrecisionAndRounding() {
		return internalPrecisionAndRounding;
	}
	
	private final MifosCurrency currency;

	private final BigDecimal amount;

	public Money(MifosCurrency currency, String amount) {
		if (usingNewMoney) {
			this.currency = currency;
			if (amount == null || "".equals(amount.trim())) {
				// seems like we shouldn't allow null values for money
				// should we throw an exception or set a zero value here?
				this.amount = null;
			} else {
				this.amount = new BigDecimal(amount,internalPrecisionAndRounding);
			}
		} else {
			this.currency = currency;
			if (amount == null || "".equals(amount.trim()))
				this.amount = null;
			else
				this.amount = setScale(new BigDecimal(amount));			
		}
	}

	public Money(String amount) {
		if (usingNewMoney) {
			this.currency = getDefaultCurrency();
			if (amount == null || "".equals(amount.trim())) {
				this.amount = null;
			} else {
				this.amount = new BigDecimal(amount,internalPrecisionAndRounding);
			}
		} else {
			this.currency = Configuration.getInstance().getSystemConfig()
			.getCurrency();
			if (amount == null || "".equals(amount.trim()))
				this.amount = null;
			else
				this.amount = setScale(new BigDecimal(amount));
		}
	}

	/**
	 * In the constructor we are not creating a new MifosCurrency object because
	 * even MifosCurrency is immutable.
	 * 
	 */
	public Money(MifosCurrency currency, BigDecimal amount) {
		if (usingNewMoney) {
			this.currency = currency;
			this.amount = amount.setScale(internalPrecisionAndRounding.getPrecision(), internalPrecisionAndRounding.getRoundingMode());
		} else {
			this.currency = currency;
			this.amount = setScale(amount);			
		}
	}

	public Money(BigDecimal amount) {
		if (usingNewMoney) {
			this.currency = getDefaultCurrency();
			this.amount = amount.setScale(internalPrecisionAndRounding.getPrecision(), internalPrecisionAndRounding.getRoundingMode());
		} else {
			this.currency = Configuration.getInstance().getSystemConfig()
			.getCurrency();
			this.amount = amount;
		}
	}

	/**
	 * This creates a Money object with currency set to MFICurrency and amount
	 * set to zero.
	 */
	public Money() {
		if (usingNewMoney) {
			this.currency = getDefaultCurrency();
			this.amount = new BigDecimal(0, internalPrecisionAndRounding);
		} else {
			this.currency = Configuration.getInstance().getSystemConfig()
			.getCurrency();
			this.amount = setScale(new BigDecimal(0));			
		}
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
		if (usingNewMoney) {
			return add_v2(money);
		} else {
			return add_v1(money);
		}
	}

	private Money add_v1(Money money) {
		if (null != money) {
			if (isCurrencyDifferent(money))
				throw new IllegalArgumentException(
						ExceptionConstants.ILLEGALMONEYOPERATION);
		}
		return (money == null || money.getAmount() == null || money
				.getCurrency() == null) ? this : new Money(this.currency,
				setScale(this.amount.add(money.getAmount())));
	}

	private Money add_v2(Money money) {
		if (null != money) {
			if (isCurrencyDifferent(money))
				throw new IllegalArgumentException(
						ExceptionConstants.ILLEGALMONEYOPERATION);
		}
		// why not disallow null amounts and currencies?
		if (money == null || money.getAmount() == null || 
			money.getCurrency() == null) {
			return this;
		} else { 
			return new Money(currency, amount.add(money.getAmount()));
		}
	}

	
	/**
	 * If the object passed as parameter is null or if its currency or amount is
	 * null it returns this else performs the required operation and returns a
	 * new Money object corresponding to the value.
	 */
	public Money subtract(Money money) {
		if (usingNewMoney) {
			return subtract_v2(money);
		} else {
			return subtract_v1(money);
		}
	}

	private Money subtract_v1(Money money) {
		if (null != money) {
			if (isCurrencyDifferent(money))
				throw new IllegalArgumentException(
						ExceptionConstants.ILLEGALMONEYOPERATION);
		}
		return (money == null || money.getAmount() == null || money
				.getCurrency() == null) ? this : new Money(this.currency,
				setScale(this.amount.subtract(money.getAmount())));
	}

	private Money subtract_v2(Money money) {
		if (null != money) {
			if (isCurrencyDifferent(money))
				throw new IllegalArgumentException(
						ExceptionConstants.ILLEGALMONEYOPERATION);
		}
		// why not disallow null amounts and currencies?
		if (money == null || money.getAmount() == null || 
			money.getCurrency() == null) {
			return this;
		} else { 
			return new Money(currency, amount.subtract(money.getAmount()));
		}
	}

	
	/**
	 * If the object passed as parameter is null or if its currency or amount is
	 * null it returns this else performs the required operation and returns a
	 * new Money object corresponding to the value.
	 */
	public Money multiply(Money money) {
		if (usingNewMoney) {
			return multiply_v2(money);
		} else {
			return multiply_v1(money);
		}
	}

	private Money multiply_v1(Money money) {
		if (null != money) {
			if (isCurrencyDifferent(money))
				throw new IllegalArgumentException(
						ExceptionConstants.ILLEGALMONEYOPERATION);
		}
		return (money == null || money.getAmount() == null || money
				.getCurrency() == null) ? this : new Money(this.currency,
				setScale(this.amount.multiply(money.getAmount())));
	}

	private Money multiply_v2(Money money) {
		if (null != money) {
			if (isCurrencyDifferent(money))
				throw new IllegalArgumentException(
						ExceptionConstants.ILLEGALMONEYOPERATION);
		}
		// why not disallow null amounts and currencies?
		if (money == null || money.getAmount() == null || 
			money.getCurrency() == null) {
			return this;
		} else { 
			return new Money(currency, amount.multiply(money.getAmount())
					.setScale(internalPrecisionAndRounding.getPrecision(), 
							  internalPrecisionAndRounding.getRoundingMode()));
		}
	}

	
	public Money multiply(Double factor) {
		if (usingNewMoney) {
			return multiply_v2(factor);
		} else {
			return multiply_v1(factor);
		}
	}

	private Money multiply_v1(Double factor) {
		if (factor != null)
			return new Money(this.currency, setScale(this.amount
					.multiply(new BigDecimal(factor))));
		else
			throw new IllegalArgumentException(
					ExceptionConstants.ILLEGALMONEYOPERATION);
	}

	private Money multiply_v2(Double factor) {
		if (factor == null) {
			throw new IllegalArgumentException(
					ExceptionConstants.ILLEGALMONEYOPERATION);			
		}
		return new Money(currency, amount.multiply(new BigDecimal(factor))
			.setScale(internalPrecisionAndRounding.getPrecision(), 
					  internalPrecisionAndRounding.getRoundingMode()));
	}

	public Money multiply(BigDecimal factor) {
		if (factor == null) {
			throw new IllegalArgumentException(
					ExceptionConstants.ILLEGALMONEYOPERATION);			
		}
		return new Money(currency, amount.multiply(factor)
			.setScale(internalPrecisionAndRounding.getPrecision(), 
					  internalPrecisionAndRounding.getRoundingMode()));
	}

	
	/**
	 * Dividing by Money doesn't seem to make sense.  It should be
	 * dividing by a BigDecimal.  This method should be eliminated.
	 */
	public Money divide(Money money) {
		if (usingNewMoney) {
			return divide_v2(money);
		} else {
			return divide_v1(money);
		}
	}

	private Money divide_v1(Money money) {
		if (null != money) {
			if (isCurrencyDifferent(money))
				throw new IllegalArgumentException(
						ExceptionConstants.ILLEGALMONEYOPERATION);
		}
		return (money == null || money.getAmount() == null || money
				.getCurrency() == null) ? this : new Money(this.currency,
				setScale(this.amount.divide(money.getAmount())));
	}

	private Money divide_v2(Money money) {
		if (null != money) {
			if (isCurrencyDifferent(money))
				throw new IllegalArgumentException(
						ExceptionConstants.ILLEGALMONEYOPERATION);
		}
		// why not disallow null amounts and currencies?
		if (money == null || money.getAmount() == null || 
			money.getCurrency() == null) {
			return this;
		} else { 
			return new Money(currency, amount.divide(money.getAmount(), 
				internalPrecisionAndRounding));					
		}
	}

	public Money divide(BigDecimal factor) {
		return new Money(currency, amount.divide(factor, 
				internalPrecisionAndRounding));					
	}
	
	public Money negate() {
		if (usingNewMoney) {
			return negate_v2();
		} else {
			return negate_v1();
		}
	}

	private Money negate_v1() {
		BigDecimal tempAmnt = this.amount.negate();
		return new Money(this.currency, setScale(tempAmnt));
	}

	// no need to set scale since negation preserves scale
	private Money negate_v2() {
		return new Money(currency, amount.negate());
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
	 */
	public static Money round(Money money) {
		if (usingNewMoney) {
			return round_v2(money);
		} else {
			return round_v1(money);
		}
	}

	private static Money round_v1(Money money) {
		if (null != money) {
			BigDecimal roundingAmount = new BigDecimal(money.getCurrency()
					.getRoundingAmount().doubleValue());
			BigDecimal nearestFactor = money.getAmount().divide(roundingAmount);

			nearestFactor = nearestFactor.setScale(
				0, money.getCurrency().getRoundingModeEnum());

			BigDecimal roundedAmount = nearestFactor.multiply(roundingAmount);
			return new Money(money.getCurrency(), roundedAmount);
		}
		return money;
	}

	private static Money round_v2(Money money) {
		if (null != money) {
			BigDecimal roundingAmount = new BigDecimal(money.getCurrency()
					.getRoundingAmount().doubleValue(),internalPrecisionAndRounding);
			BigDecimal nearestFactor = money.getAmount().divide(roundingAmount,internalPrecisionAndRounding);

			nearestFactor = nearestFactor.setScale(
				0, money.getCurrency().getRoundingModeEnum());

			BigDecimal roundedAmount = nearestFactor.multiply(roundingAmount);
			return new Money(money.getCurrency(), roundedAmount);
		}
		return money;
	}

	public static Money round(Money money,BigDecimal roundOffMultiple, RoundingMode roundingMode) {
		// should we allow a null money or throw and exception instead?
		if (null != money) {
			// insure that we are using the correct internal precision
			BigDecimal roundingAmount = roundOffMultiple.round(internalPrecisionAndRounding);
			BigDecimal nearestFactor = money.getAmount().divide(roundingAmount,internalPrecisionAndRounding);

			nearestFactor = nearestFactor.setScale(0, roundingMode);

			BigDecimal roundedAmount = nearestFactor.multiply(roundingAmount);
			return new Money(money.getCurrency(), roundedAmount);
		}
		return money;
	}
	
	/**
	 * This method return true if the currency associated with the two money
	 * objects is equal and also the compareTo method of BigDecimal return 0 for
	 * the amount of the two money objects. It is not advisable to use equals
	 * method of BigDecimal because it would return false for numbers like 10.0
	 * and 10.00 instead we should use compareTo.
	 * 
	 */
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Money))
			return false;
		if (obj == this)
			return true;
		Money money = (Money) obj;
		return this.currency.equals(money.getCurrency())
				&& (this.amount.compareTo(money.getAmount()) == 0);
	}

	@Override
	public int hashCode() {
		if (amount == null || currency == null)
			return System.identityHashCode(null);
		return this.currency.getCurrencyId() * 100 + this.amount.intValue();
	}

	private BigDecimal setScale(BigDecimal amount) {
		if (null != amount && null != currency) {
			amount = amount.setScale(currency.getDefaultDigitsAfterDecimal(),
					RoundingMode.HALF_UP);
		}

		return amount;
	}

	@Override
	public String toString() {
		if (amount != null && currency != null)
			return amount.toString();
		return "0";
	}


}
