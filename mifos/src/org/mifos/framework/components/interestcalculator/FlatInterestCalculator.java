/**
 * FlatInterestCalculator.java version:1.0
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
package org.mifos.framework.components.interestcalculator;

import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.util.helpers.Money;

/**
 *  This class handles flat interest calculation
 */

public class FlatInterestCalculator implements InterestCalculatorIfc
{

	private final MifosLogger logger;

	public FlatInterestCalculator() {
		this(MifosLogManager.getLogger(LoggerConstants.REPAYMENTSCHEDULAR));
	}

	public FlatInterestCalculator(MifosLogger logger) {
		this.logger = logger;
	}

	public Money getInterest(InterestInputs interestInputs) throws InterestCalculationException
	{
			validate(interestInputs);

			Money principal = interestInputs.getPrincipal();
			
			Double interestRate= interestInputs.getInterestRate();
			Double durationInYears= getTotalDurationInYears(interestInputs);
			
			Money interestRateM=new Money(principal.getCurrency(), interestRate);
			Money durationInYearsM=new Money(principal.getCurrency(), durationInYears);


			logger.info("FlatInterestCalculator:getInterest duration in years..."+durationInYears);

			Money interest = 
				principal
					.multiply(interestRateM.multiply(durationInYearsM))
					.divide(new Money(principal.getCurrency(), 100));

			logger.info("FlatInterestCalculator:getInterest interest accumulated..."+interest);
			return interest;
	}

	// refactor this
	private double getTotalDurationInYears(InterestInputs interestInputs) throws InterestCalculationException
	{
			int interestDays = getInterestDays();
			int daysInWeek = getDaysInWeek();
			int daysInMonth = getDaysInMonth();

			if(interestDays == InterestCalculatorConstants.INTEREST_DAYS_360)
			{
				if(interestInputs.getDurationType().equals(InterestCalculatorConstants.WEEK_INSTALLMENT))
				{
						double totalWeekDays = interestInputs.getDuration() * daysInWeek;
						double durationInYears = totalWeekDays/InterestCalculatorConstants.INTEREST_DAYS_360 ;

						logger.info("FlatInterestCalculator:getTotalDurationInYears total week days.."+totalWeekDays);


						return InterestCalculatorHelper.round(durationInYears);



				}
				else
				if(interestInputs.getDurationType().equals(InterestCalculatorConstants.MONTH_INSTALLMENT))
				{

						double totalMonthDays = interestInputs.getDuration() * daysInMonth;
						double durationInYears = totalMonthDays/InterestCalculatorConstants.INTEREST_DAYS_360 ;

						logger.info("FlatInterestCalculator:getTotalDurationInYears total month days.."+totalMonthDays);



						return InterestCalculatorHelper.round(durationInYears);


				}
				throw new InterestCalculationException(InterestCalculatorConstants.NOT_SUPPORTED_DURATION_TYPE);

			}
			else
			if(interestDays == InterestCalculatorConstants.INTEREST_DAYS_365)
			{

				if(interestInputs.getDurationType().equals(InterestCalculatorConstants.WEEK_INSTALLMENT))
				{

						logger.info("FlatInterestCalculator:getTotalDurationInYears in interest week 365 days");


						double totalWeekDays = interestInputs.getDuration() * daysInWeek;
						double durationInYears = totalWeekDays/InterestCalculatorConstants.INTEREST_DAYS_365 ;

						return InterestCalculatorHelper.round(durationInYears);

				}
				else
				if(interestInputs.getDurationType().equals(InterestCalculatorConstants.MONTH_INSTALLMENT))
				{
						logger.info("FlatInterestCalculator:getTotalDurationInYears in interest month 365 days");

						// will have to consider inc/dec time in some countries
						Long installmentStartTime = interestInputs.getInstallmentStartDate().getTime();
						Long installmentEndTime = interestInputs.getInstallmentEndDate().getTime();
						Long diffTime = installmentEndTime - installmentStartTime;
						double daysDiff = diffTime/(1000*60*60*24);

						logger.info("FlatInterestCalculator:getTotalDurationInYears start date.."+interestInputs.getInstallmentStartDate());

						logger.info("FlatInterestCalculator:getTotalDurationInYears end date.."+interestInputs.getInstallmentEndDate());


						logger.info("FlatInterestCalculator:getTotalDurationInYears diff in days..."+daysDiff);


						double durationInYears = daysDiff/InterestCalculatorConstants.INTEREST_DAYS_365 ;

						return InterestCalculatorHelper.round(durationInYears);

				}
				throw new InterestCalculationException(InterestCalculatorConstants.NOT_SUPPORTED_DURATION_TYPE);


			}
			else
				throw new InterestCalculationException(InterestCalculatorConstants.NOT_SUPPORTED_INTEREST_DAYS);

	}

	// read from configuration
	private int getInterestDays()
	{
			return InterestCalculatorConstants.INTEREST_DAYS;
	}

	// read from configuration
	private int getDaysInWeek()
	{
		return InterestCalculatorConstants.DAYS_IN_WEEK;
	}

	// read from configuration
	private int getDaysInMonth()
	{
		return InterestCalculatorConstants.DAYS_IN_MONTH;
	}



	private void validate(InterestInputs interestInputs) throws InterestCalculationException
	{
		Money principal = interestInputs.getPrincipal();
		double interestRate = interestInputs.getInterestRate();
		int duration = interestInputs.getDuration();
		String durationType = interestInputs.getDurationType();

		logger.info("***********Interest Related Inputs****************");

		logger.info("principal passed.."+principal);
		logger.info("interestRate passed.."+interestRate);
		logger.info("duration.."+duration);
		logger.info("durationType passed.."+durationType);
		logger.info("***********Interest Related Inputs End****************");
	}

}
