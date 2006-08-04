
/**
 * FeeInstallmentGenerator.java version:1.0
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
package org.mifos.framework.components.repaymentschedule;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import org.mifos.application.accounts.business.AccountFeesEntity;
import org.mifos.application.accounts.util.valueobjects.AccountFees;
import org.mifos.application.meeting.util.valueobjects.Meeting;
import org.mifos.framework.components.configuration.business.Configuration;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.scheduler.SchedulerIntf;
import org.mifos.framework.util.helpers.Money;
/**
 *
 *  This class is used to generate fee installments
 */

public class FeeInstallmentGenerator implements FeeInstallmentGeneratorIfc
{

		private	Map<Integer,FeeInstallment> feeInstallments = new HashMap<Integer,FeeInstallment>();
		private FeeInputs feeInputs = null;
		private List<Date> installmentDueDates = null;
		private Map<Date,Integer> installmentDueMap = new HashMap<Date,Integer>();
		private Date installmentStartDate = null;
			// fee to be paid from disbursement date , installment date could be different
			//take each account fee
			// check if its one time/periodic
			// if one time then from intstallment dates get the first installment
					// create a fee installment for this installmentid & add the relavant object
					// get the amount based on flat or interest
			// note : all one time are added to first installment , no difference between upfront/time of disbursement/time of first repayment
			// if periodic,repayment freq to be merged with fee freq(since it will only contain n weeks/n months)
			// then check if the recur after is same , if so for each of the installment date get the amount based on flat or interest
			// if recur after is not same then call scheduler and get the dates of fee till end date of installment
			// for this fee dates check it falls under which installment date and add it to it
			//// no need of fee at the disburement date

	public List<FeeInstallment> generateFeeInstallment(FeeInputs feeInputs) throws FeeGenerationException
	{
		try
		{
		    validate(feeInputs);

		    this.feeInputs = feeInputs;

			Set<AccountFees> accountFeesSet = feeInputs.getAccountFees();

			Iterator<AccountFees> feeIterator = accountFeesSet.iterator();


			installmentDueDates = generateInstallmentDueDates();

			installmentStartDate = installmentDueDates.get(0);

			while(feeIterator.hasNext())
			{
				generateAccountFeeInstallment(feeIterator.next());

			}

			return buildFeeInstallmentList();
		}
		catch(FeeGenerationException ex)
		{
			throw ex;
		}


	}

	private void generateAccountFeeInstallment(AccountFees accountFees) throws FeeGenerationException
	{


		int accountFeeType = accountFees.getFees().getFeeFrequency().getFeeFrequencyTypeId();

		printAccountFeeInstallmentInputs(accountFees); 


		if(accountFeeType == RepaymentScheduleConstansts.FEE_TYPE_ONETIME)
		{
			handleOneTime(accountFees);
		}
		else
		if(accountFeeType == RepaymentScheduleConstansts.FEE_TYPE_PERODIC)
		{
			handlePeriodic(accountFees);

		}
		else
		throw new FeeGenerationException(RepaymentScheduleConstansts.NOT_SUPPORTED_FEE_TYPE);
	}

	private void handleOneTime(AccountFees accountFees) throws FeeGenerationException
	{

			Date feeStartDate = feeInputs.getFeeStartDate();

			Money accountFeeAmount = getAccountFeeAmount(accountFees);

			// get the current total fee amount
			Money totalFeeAmount = feeInputs.getFeeAmount();
			// add the current fee amount to the total
			feeInputs.setFeeAmount(accountFeeAmount.add(totalFeeAmount));
			
			MifosLogManager.getLogger(LoggerConstants.REPAYMENTSCHEDULAR).debug("FeeInstallmentGenerator:handleOneTime fee start date "+feeStartDate);

			int installmentId = getApplicableRepaymentInstallment(feeStartDate);

			MifosLogManager.getLogger(LoggerConstants.REPAYMENTSCHEDULAR).debug("FeeInstallmentGenerator:handleOneTime applicable installment id "+installmentId);


			buildFeeInstallment(installmentId,accountFeeAmount , accountFees);




	}

	private void handlePeriodic(AccountFees accountFees) throws FeeGenerationException
	{

		Money accountFeeAmount = getAccountFeeAmount(accountFees);
		
		if(feeInputs.getFeeStartDate()==null)
			feeInputs.setFeeStartDate(new Date(System.currentTimeMillis()));
		
		int installmentId = getApplicableRepaymentInstallment(feeInputs.getFeeStartDate());
		
		//buildFeeInstallment(installmentId,accountFeeAmount , accountFees);
		
		Meeting repaymentFrequency=feeInputs.getRepaymentFrequency();
		
		Calendar meetingStartDate=repaymentFrequency.getMeetingStartDate();
		
		Set<Date> installmentDatesSet=installmentDueMap.keySet();
		
		for(Date installmentDate : installmentDatesSet){
			if(installmentDueMap.get(installmentDate).equals(installmentId)){
				Calendar installment = new GregorianCalendar();
				installment.setTimeInMillis(installmentDate.getTime());
				//Calendar calendarTypeinstallmentDate=new GregorianCalendar(installmentDate.getYear()+1900,installmentDate.getMonth(),installmentDate.getDate());
				Calendar calendarTypeinstallmentDate=new GregorianCalendar(installment.get(Calendar.YEAR),installment.get(Calendar.MONTH),installment.get(Calendar.DATE));
				//Setting the meetingStartDate to next installment's date because fees periodicity should
				//start with that date and fees should first be applied to the next comming installment.
				repaymentFrequency.setMeetingStartDate(calendarTypeinstallmentDate);
				feeInputs.setFeeStartDate(calendarTypeinstallmentDate.getTime());
				//installmentDueMap.remove(installmentDate);
				break;
			}
		}
		
		Meeting feeMeetingFrequency = accountFees.getFees().getFeeFrequency().getFeeMeetingFrequency();

		List<Date> feeDates = getFeeDates(feeMeetingFrequency);

		ListIterator<Date> feeDatesIterator = feeDates.listIterator();

		Date feeDate = null;

		// in case of feeStartDate being null which is at creation time the fee date not applicable is the disburesemtn date
		// in case of feeStartDate not being null the fee date not applicable are those dates which are less than this date
		//removeFeeDatesNotApplicable(feeDatesIterator);
       			while(feeDatesIterator.hasNext())
					{

						// get the current total fee amount
						Money totalFeeAmount = feeInputs.getFeeAmount();
						// add the current fee amount to the total
						
						feeInputs.setFeeAmount(accountFeeAmount.add(totalFeeAmount));
						
						
						feeDate = feeDatesIterator.next();

						MifosLogManager.getLogger(LoggerConstants.REPAYMENTSCHEDULAR).debug("FeeInstallmentGenerator:handlePeriodic date considered after removal.."+feeDate);

						installmentId = getApplicableRepaymentInstallment(feeDate);
                        
						buildFeeInstallment(installmentId,accountFeeAmount , accountFees);
						
						if(feeInputs.getMeetingToConsider()== RepaymentScheduleConstansts.MEETING_CUSTOMER)
							break;

					}
       //Settig the meetingStartDate back to what was passed.		
       if(meetingStartDate!=null){
    	   repaymentFrequency.setMeetingStartDate(meetingStartDate);		
       }

	}

	private void removeFeeDatesNotApplicable(ListIterator<Date> feeDatesIterator)
	{
		Date feeStartDate = feeInputs.getFeeStartDate();

		Date feeDate = null;
		
		Calendar feeStartDateCalender= new GregorianCalendar();
		feeStartDateCalender.setTimeInMillis(feeStartDate.getTime());
		
		Calendar feeStartDateC=new GregorianCalendar(feeStartDateCalender.get(Calendar.YEAR),feeStartDateCalender.get(Calendar.MONTH),feeStartDateCalender.get(Calendar.DATE));
		
		MifosLogManager.getLogger(LoggerConstants.REPAYMENTSCHEDULAR).debug("FeeInstallmentGenerator:removeFeeDatesNotApplicable fee start date to consider.."+feeStartDateC.getTime());
		
		Calendar feeDateC =null;
	
		
		
		while(feeDatesIterator.hasNext())
		{
			feeDate = feeDatesIterator.next();
			feeStartDateCalender.setTimeInMillis(feeDate.getTime());
			feeDateC=new GregorianCalendar(feeStartDateCalender.get(Calendar.YEAR),feeStartDateCalender.get(Calendar.MONTH),feeStartDateCalender.get(Calendar.DATE));
						
			MifosLogManager.getLogger(LoggerConstants.REPAYMENTSCHEDULAR).debug("FeeInstallmentGenerator:removeFeeDatesNotApplicable fee date to consider.."+feeDateC.getTime());
			if(((feeDateC.getTime()).compareTo(feeStartDateC.getTime()))>=0)
			{
				feeDate=feeDatesIterator.previous();
				MifosLogManager.getLogger(LoggerConstants.REPAYMENTSCHEDULAR).debug("FeeInstallmentGenerator:removeFeeDatesNotApplicable fee date breaks");
				break;
			}
		}

		MifosLogManager.getLogger(LoggerConstants.REPAYMENTSCHEDULAR).debug("FeeInstallmentGenerator:removeFeeDatesNotApplicable fee date to consider.."+feeDate);
		//
		if(feeDatesIterator.hasNext())
			feeDatesIterator.next();


	}

	private List<Date> getFeeDates(Meeting feeMeetingFrequency) throws FeeGenerationException
	{

		try
		{
			SchedulerIntf scheduler = null;

			Meeting repaymentFrequency = feeInputs.getRepaymentFrequency();
			feeMeetingFrequency = RepaymentScheduleHelper.mergeFrequency(repaymentFrequency,feeMeetingFrequency);

			Date repaymentEndDate = installmentDueDates.get(installmentDueDates.size() - 1);

			scheduler = RepaymentScheduleHelper.getSchedulerObject(feeMeetingFrequency,false);

			List<Date> feeDueDates = scheduler.getAllDates(repaymentEndDate);

			printFeeDueDates(feeDueDates);

			return feeDueDates;


		}
		catch(Exception e)
		{
			throw new FeeGenerationException(RepaymentScheduleConstansts.FEE_SCHEDULE,e);
		}
	}

	private List<Date> generateInstallmentDueDates()
	{
		List<InstallmentDate> installmentDates = feeInputs.getInstallmentDate();
		List<Date> installmentDueDates = new ArrayList();

		Iterator<InstallmentDate> installmentDatesIterator = installmentDates.iterator();
		while(installmentDatesIterator.hasNext())
		{
			InstallmentDate instDate = installmentDatesIterator.next();
			
			Date date = instDate.getInstallmentDueDate();
			Calendar dateCalender= new GregorianCalendar();
			dateCalender.setTimeInMillis(date.getTime());

			Calendar calendarDate=new GregorianCalendar(dateCalender.get(Calendar.YEAR),dateCalender.get(Calendar.MONTH),dateCalender.get(Calendar.DATE));
			
			installmentDueDates.add(calendarDate.getTime());
			MifosLogManager.getLogger(LoggerConstants.REPAYMENTSCHEDULAR).debug("FeeInstallmentGenerator:generateInstallmentDueDates date being put in map.."+instDate.getInstallmentDueDate()+"   "+instDate.getInstallmentId());

			
			installmentDueMap.put(calendarDate.getTime(),instDate.getInstallmentId());
		}

		printFeeInstallmentDates(installmentDueDates); // remove this later , used for testing
		return installmentDueDates;

	}


	private int getApplicableRepaymentInstallment(Date feeDate)
	{
		// match with the installment dates and see which date this fee will get applicable to be paid   
		Calendar dateCalender= new GregorianCalendar();
		dateCalender.setTimeInMillis(feeDate.getTime());

		Calendar calendarDate=new GregorianCalendar(dateCalender.get(Calendar.YEAR),dateCalender.get(Calendar.MONTH),dateCalender.get(Calendar.DATE));
		
		Integer installmentId = installmentDueMap.get(calendarDate.getTime());
				
		MifosLogManager.getLogger(LoggerConstants.REPAYMENTSCHEDULAR).debug("FeeInstallmentGenerator:getApplicableRepaymentInstallment in getApplicable..."+feeDate);


		if(installmentId != null)
		{
			MifosLogManager.getLogger(LoggerConstants.REPAYMENTSCHEDULAR).debug("FeeInstallmentGenerator:getApplicableRepaymentInstallment in getApplicable matches directly..."+installmentId);

			return installmentId;

		}


		Date matchingInstallmentDate = matchInstallmentDate(calendarDate.getTime());

		MifosLogManager.getLogger(LoggerConstants.REPAYMENTSCHEDULAR).debug("FeeInstallmentGenerator:getApplicableRepaymentInstallment matchingInstallmentDate..."+matchingInstallmentDate);

		installmentId = installmentDueMap.get(matchingInstallmentDate);

		MifosLogManager.getLogger(LoggerConstants.REPAYMENTSCHEDULAR).debug("FeeInstallmentGenerator:getApplicableRepaymentInstallment matchingInstallmentId..."+installmentId);

		return installmentId;
	}

	private Date matchInstallmentDate(Date feeDate)
	{
		//iterate to check where this date falls
		int count = installmentDueDates.size();
		Date instDate = null;

		for(int i =0; i < count; i++)
		{
			instDate = installmentDueDates.get(i);
						
			if(instDate.compareTo(feeDate) > 0)
				break;

		}
		return instDate;
	}

	private Money getAccountFeeAmount(AccountFees accountFees) throws FeeGenerationException
	{
		MifosLogManager.getLogger(LoggerConstants.REPAYMENTSCHEDULAR).debug("FeeInstallmentGenerator:getAccountFeeAmount rate flat flag.."+accountFees.getFees().getRateFlatFalg());

		int isRateFlat = accountFees.getFees().getRateFlatFalg().intValue();
		Money feeAmount;
		Double accountFeeAmountRate = accountFees.getFeeAmount();

		MifosLogManager.getLogger(LoggerConstants.REPAYMENTSCHEDULAR).debug("FeeInstallmentGenerator:getAccountFeeAmount feeAmount.."+accountFeeAmountRate);
		
		if(isRateFlat == RepaymentScheduleConstansts.FEE_PAYMENT_FLAT)
		{
				feeAmount = new Money (Configuration.getInstance().getSystemConfig().getCurrency(),accountFeeAmountRate);
				MifosLogManager.getLogger(LoggerConstants.REPAYMENTSCHEDULAR).debug("FeeInstallmentGenerator:getAccountFeeAmount feeAmount Flat.."+feeAmount);
		}
		else
		if(isRateFlat == RepaymentScheduleConstansts.FEE_PAYMENT_RATE)
		{

				feeAmount = new Money (Configuration.getInstance().getSystemConfig().getCurrency(),getRateBasedOnFormula(accountFeeAmountRate,accountFees.getFees().getFormulaId()));
				MifosLogManager.getLogger(LoggerConstants.REPAYMENTSCHEDULAR).debug("FeeInstallmentGenerator:getAccountFeeAmount feeAmount Formula.."+feeAmount);
		}
		else
			throw new FeeGenerationException(RepaymentScheduleConstansts.NOT_SUPPORTED_FEE_PAYMENT);

		accountFees.setAccountFeeAmount(feeAmount);
		return feeAmount;


	}

	private void buildFeeInstallment(int installmentId, Money accountFeeAmount , AccountFees accountFee) throws FeeGenerationException
	{
		FeeInstallment feeInstallment = getFeeInstallment(installmentId);
		AccountFeeInstallment accountFeeInstallment = new AccountFeeInstallment();

		accountFeeInstallment.setFeeId(accountFee.getFees().getFeeId());
		accountFeeInstallment.setAccountFeeAmount(accountFeeAmount);
		accountFeeInstallment.setAccountFee(accountFee);
		accountFeeInstallment.setAccountFeeEntity(getAccountFeeEntity(accountFee));
		feeInstallment.setAccountFee(accountFeeAmount);
		feeInstallment.addAccountFeeInstallment(accountFeeInstallment);



	}
	
	private AccountFeesEntity getAccountFeeEntity(AccountFees accountFee)
	{
		Set<AccountFeesEntity> accountFeesEntity = feeInputs.getAccountFeesEntity();
		if(accountFeesEntity!=null){
			for(AccountFeesEntity accountFeeEntity: accountFeesEntity)
				if(accountFeeEntity.getFees().getFeeId().equals(accountFee.getFees().getFeeId()))
					return accountFeeEntity;			
		}		
		return null;
	}

	private FeeInstallment getFeeInstallment(int installmentId)
	{
		FeeInstallment feeInstallment = feeInstallments.get(new Integer(installmentId));
		if(feeInstallment == null)
		{
			feeInstallment = new FeeInstallment();
			feeInstallment.setInstallmentId(installmentId);
			feeInstallments.put(new Integer(installmentId),feeInstallment);

		}

		return feeInstallment;
	}

	private Double getRateBasedOnFormula(Double accountFeeAmountRate,Short formulaId) throws FeeGenerationException
	{
		Money loanAmount = feeInputs.getLoanAmount();
		Money loanInterest = feeInputs.getLoanInterest();
		Money amountToCalculateOn;

		if(formulaId.intValue() == RepaymentScheduleConstansts.FEE_FORMULA_LOAN)
		{
			amountToCalculateOn = loanAmount;

		}
		else
		if(formulaId.intValue() == RepaymentScheduleConstansts.FEE_FORMULA_LOAN_INTEREST)
		{
			amountToCalculateOn = loanAmount.add(loanInterest);

		}
		else
		if(formulaId.intValue() == RepaymentScheduleConstansts.FEE_FORMULA_INTEREST)
		{
			amountToCalculateOn = loanInterest;

		}
		else
			throw new FeeGenerationException(RepaymentScheduleConstansts.NOT_SUPPORTED_FEE_FORMULA);


		double rateAmount = (accountFeeAmountRate* amountToCalculateOn.getAmountDoubleValue())/100;
		return rateAmount;
	}

	private List <FeeInstallment> buildFeeInstallmentList()
	{

		// convert feeInstallments map to list
		Collection<FeeInstallment> feeCollection = feeInstallments.values();
		Iterator<FeeInstallment> iter  = feeCollection.iterator();
		List<FeeInstallment> feeInstallmentList = new ArrayList<FeeInstallment>();
		FeeInstallment feeInst = null;
		while(iter.hasNext())
		{
			feeInst = iter.next();
			feeInstallmentList.add(feeInst);
			printFeeInst(feeInst); // remove this later , used for testing

		}




		return feeInstallmentList;
	}

	private void printFeeInst(FeeInstallment feeInst)
	{
		MifosLogManager.getLogger(LoggerConstants.REPAYMENTSCHEDULAR).debug("FeeInstallmentGenerator:printFeeInst");
		MifosLogManager.getLogger(LoggerConstants.REPAYMENTSCHEDULAR).debug("FeeInstallmentGenerator:printFeeInst fee installment id..."+feeInst.getInstallmentId());
		MifosLogManager.getLogger(LoggerConstants.REPAYMENTSCHEDULAR).debug("FeeInstallmentGenerator:printFeeInst fee amount..."+feeInst.getTotalAccountFee());
	}

	private void printFeeInstallmentDates(List<Date> installmentDueDates)
	{


		int count = installmentDueDates.size();
		Date feeDate = null;

				for(int i =0; i < count; i++)
				{
					feeDate = installmentDueDates.get(i);
					MifosLogManager.getLogger(LoggerConstants.REPAYMENTSCHEDULAR).debug("FeeInstallmentGenerator:printFeeInstallmentDates installment dates in fees..."+feeDate);


				}
	}

	private void printFeeDueDates(List<Date> feeDates)
	{



		int count = feeDates.size();
		Date feeDate = null;

				for(int i =0; i < count; i++)
				{
					feeDate = feeDates.get(i);
                    

				}


	}

	private void printAccountFeeInstallmentInputs(AccountFees accountFees)
	{

			MifosLogManager.getLogger(LoggerConstants.REPAYMENTSCHEDULAR).debug("FeeInstallmentGenerator:Fee Inputs ");
			MifosLogManager.getLogger(LoggerConstants.REPAYMENTSCHEDULAR).debug("FeeInstallmentGenerator:Fee Inputs fee type..."+accountFees.getFees().getFeeFrequency().getFeeFrequencyTypeId());
			MifosLogManager.getLogger(LoggerConstants.REPAYMENTSCHEDULAR).debug("FeeInstallmentGenerator:Fee Inputs fee payment..."+accountFees.getFees().getRateFlatFalg().intValue());
			MifosLogManager.getLogger(LoggerConstants.REPAYMENTSCHEDULAR).debug("FeeInstallmentGenerator:Fee Inputs fee amount/rate..."+accountFees.getAccountFeeAmount() );
			MifosLogManager.getLogger(LoggerConstants.REPAYMENTSCHEDULAR).debug("FeeInstallmentGenerator:Fee Inputs fee amount/rate..."+accountFees.getAccountFeeAmount() );
			MifosLogManager.getLogger(LoggerConstants.REPAYMENTSCHEDULAR).debug("FeeInstallmentGenerator:Fee Inputs fee formula..."+accountFees.getFees().getFormulaId() );
			MifosLogManager.getLogger(LoggerConstants.REPAYMENTSCHEDULAR).debug("FeeInstallmentGenerator:Fee Inputs loan amount..."+feeInputs.getLoanAmount() );
			MifosLogManager.getLogger(LoggerConstants.REPAYMENTSCHEDULAR).debug("FeeInstallmentGenerator:Fee Inputs loan interest..."+feeInputs.getLoanInterest() );

	}

	private void validate(FeeInputs feeInputs) throws FeeGenerationException
	{

	}
	// need to call mifos rounder
	private double round(double valueToRound)
	{
		return valueToRound;

	}
	


}

