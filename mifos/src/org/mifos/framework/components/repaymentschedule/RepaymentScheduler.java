/**
 * RepaymentScheduler.java version:1.0
 * Copyright © 2005-2006 Grameen Foundation USA

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

import org.mifos.framework.components.scheduler.SchedulerIntf;
import org.mifos.framework.components.scheduler.SchedulerException;
import org.mifos.application.accounts.loan.util.helpers.LoanHelpers;
import org.mifos.application.meeting.util.valueobjects.Meeting;
import org.mifos.framework.components.interestcalculator.InterestCalculatorConstansts;
import org.mifos.framework.components.interestcalculator.InterestCalculatorFactory;
import org.mifos.framework.components.interestcalculator.InterestCalculatorIfc;
import org.mifos.framework.components.interestcalculator.InterestInputs;
import org.mifos.framework.components.interestcalculator.InterestCalculationException;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.util.helpers.Money;


import java.util.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;

/**
 *
 *  This class handles the repayment generation
 */
public class RepaymentScheduler implements RepaymentScheduleIfc
{
	private RepaymentScheduleInputsIfc  repaymentScheduleInputs = null;

	private GracePeriodHandler gracePeriodHandler = null;
	private InstallmentGenerator installmentGenerator = null;
	

	public void setRepaymentScheduleInputs(RepaymentScheduleInputsIfc repaymentScheduleInputs) throws RepaymentScheduleException
	{
		MifosLogManager.getLogger(LoggerConstants.REPAYMENTSCHEDULAR).debug("RepamentSchedular:repaymentschedule inputs set ");

		validate(repaymentScheduleInputs);

		this.repaymentScheduleInputs = repaymentScheduleInputs;


	}
	/**
	 * This method checks if the disburesement date is valid
	 * @param disburesmentDate
	 * @return  boolean
	 * @throws RepaymentScheduleException
	 */
	public boolean isDisbursementDateValid() throws RepaymentScheduleException
	{

		isInputSet();

		try
		{
			Date disbursementDate = repaymentScheduleInputs.getDisbursementDate();

			MifosLogManager.getLogger(LoggerConstants.REPAYMENTSCHEDULAR).debug("RepamentSchedular:isDisbursementDateValid invoked ");

			SchedulerIntf scheduler = null;

			scheduler = RepaymentScheduleHelper.getSchedulerObject(repaymentScheduleInputs.getMeeting(),true);

			MifosLogManager.getLogger(LoggerConstants.REPAYMENTSCHEDULAR).debug("RepamentSchedular:isDisbursementDateValid schedular object obtained ");

		   MifosLogManager.getLogger(LoggerConstants.REPAYMENTSCHEDULAR).debug("RepamentSchedular:isDisbursementDateValid disburesement date is "+disbursementDate);

		   boolean isValid = scheduler.isValidScheduleDate(disbursementDate);

		   MifosLogManager.getLogger(LoggerConstants.REPAYMENTSCHEDULAR).debug("RepamentSchedular:isDisbursementDateValid is disburesement date valid  "+isValid);

		   return isValid;



	    }
	    catch(SchedulerException scheduleException)
	  	{
			    
	  			throw new RepaymentScheduleException(scheduleException.getKey(),scheduleException);
		}



	}

	public Date getRepaymentMeetingStartDate() throws RepaymentScheduleException
	{
		// disbursement date on meeting date
		if(isDisbursementDateValid())
			return repaymentScheduleInputs.getDisbursementDate();
		
		
		// disbursement date in between meeting dates , pick the last meeting
		SchedulerIntf scheduler = null;

		scheduler = RepaymentScheduleHelper.getSchedulerObject(repaymentScheduleInputs.getMeeting(),true);
		List<Date> allDates = null;
		try
		{
			 allDates = scheduler.getAllDates(repaymentScheduleInputs.getDisbursementDate());
		}
	    catch(SchedulerException scheduleException)
	  	{
			    
	  			throw new RepaymentScheduleException(scheduleException.getKey(),scheduleException);
		}

	    
	    //add key and uncomment
		if(allDates == null || allDates.size() == 0)
			throw new RepaymentScheduleException(RepaymentScheduleConstansts.NOT_VALID_DISBURSAL_DATE);
		
		return allDates.get(allDates.size() - 1);

	}
	
	/**
	 * This method gets the installment start date
	 * @return  Date
	 * @throws RepaymentScheduleException
	 */
	public Date getInstallmentStartDate() throws RepaymentScheduleException
	{
		try
		{

			isInputSet();

			installmentGenerator = new InstallmentGenerator();

			SchedulerIntf scheduler = null;

			int gracePeriod = getGracePeriod();

			scheduler = RepaymentScheduleHelper.getSchedulerObject(repaymentScheduleInputs.getRepaymentFrequency(),true);
			

			return installmentGenerator.getInstallmentStartDate(scheduler,repaymentScheduleInputs,gracePeriod);
		}


		catch(InstallmentException installment)
		{
			throw new RepaymentScheduleException(installment.getKey(),installment);
		}
		catch(SchedulerException scheduleException)
		{
		    
			throw new RepaymentScheduleException(scheduleException.getKey(),scheduleException);
		}

	}

	/**
	 * This method gets the grace period
	 * @return  int
	 * @throws RepaymentScheduleException
	 */
	public int getGracePeriod() throws RepaymentScheduleException
	{
		try
		{
			MifosLogManager.getLogger(LoggerConstants.REPAYMENTSCHEDULAR).debug("RepamentSchedular:getGracePeriod is invoked");

			gracePeriodHandler = new GracePeriodHandler();

			GraceInputs graceInputs = RepaymentScheduleHelper.buildGraceInputs(repaymentScheduleInputs);

			MifosLogManager.getLogger(LoggerConstants.REPAYMENTSCHEDULAR).debug("RepamentSchedular:getGracePeriod ,invoking Grace period handler");

			int gracePeriod = gracePeriodHandler.getGracePeriod(graceInputs);

			MifosLogManager.getLogger(LoggerConstants.REPAYMENTSCHEDULAR).debug("RepamentSchedular:getGracePeriod ,grace period is  "+gracePeriod);

			return gracePeriod;

		}
		catch(GraceException grace)
		{
			throw new RepaymentScheduleException(grace.getKey(),grace);
		}


	}
	/**
	 * This method gets the number of installments to skip
	 * @return  int
	 * @throws RepaymentScheduleException
	 */
	public int getInstallmentSkipToStartRepayment() throws RepaymentScheduleException
	{
		try
		{
			isInputSet();

			installmentGenerator = new InstallmentGenerator();

			int gracePeriod = getGracePeriod();

			MifosLogManager.getLogger(LoggerConstants.REPAYMENTSCHEDULAR).debug("RepamentSchedular:installmentToSkip invoked ");

			int installmentSkipToStartRepayment = installmentGenerator.getInstallmentSkipToStartRepayment(repaymentScheduleInputs,gracePeriod);

			MifosLogManager.getLogger(LoggerConstants.REPAYMENTSCHEDULAR).debug("RepamentSchedular:installmentToSkip , installmentSkipToStartRepayment ");

			return installmentSkipToStartRepayment;
		}

		catch(InstallmentException installment)
		{
			throw new RepaymentScheduleException(installment.getKey(),installment);
		}
	}
	/**
	 * This method gets the repayment schedule
	 * @return  RepaymentSchedule
	 * @throws RepaymentScheduleException
	 */
	public RepaymentSchedule getRepaymentSchedule() throws RepaymentScheduleException
	{
		try
		{
			isInputSet();


			if(repaymentScheduleInputs.getMeetingToConsider() == RepaymentScheduleConstansts.MEETING_CUSTOMER)
			{
				return handleCustomerRepayemtSchedule();

			}
			else
			if(repaymentScheduleInputs.getMeetingToConsider() == RepaymentScheduleConstansts.MEETING_LOAN)
			{
				return roundRepaymentSchedule(handleLoanRepayemtSchedule());

			}
			else
			  throw new RepaymentScheduleException(RepaymentScheduleConstansts.NOT_SUPPORTED_MEETING_TYPE);

		}
		catch(RepaymentScheduleException ic)
		{

			throw ic;
		}


	}

	private RepaymentSchedule handleCustomerRepayemtSchedule() throws RepaymentScheduleException
	{


				List<InstallmentDate> installmentDates = getInstallmentDates();

				MifosLogManager.getLogger(LoggerConstants.REPAYMENTSCHEDULAR).debug("RepamentSchedular:getRepaymentSchedule , installment dates obtained ");

				repaymentScheduleInputs.setInstallmentDate(installmentDates);

				List<FeeInstallment> feeInstallment = getFeeInstallment();

				MifosLogManager.getLogger(LoggerConstants.REPAYMENTSCHEDULAR).debug("RepamentSchedular:getRepaymentSchedule , fee installment obtained ");

				RepaymentSchedule repaymentSchedule =  generateRepaymentSchedule(installmentDates,feeInstallment);

				MifosLogManager.getLogger(LoggerConstants.REPAYMENTSCHEDULAR).debug("RepamentSchedular:getRepaymentSchedule , repayment schedule generated  ");

				return repaymentSchedule;
	}

	private RepaymentSchedule handleLoanRepayemtSchedule() throws RepaymentScheduleException
	{

				MifosLogManager.getLogger(LoggerConstants.REPAYMENTSCHEDULAR).debug("RepamentSchedular:getRepaymentSchedule invoked ");

				List<InstallmentDate> installmentDates = getInstallmentDates();

				MifosLogManager.getLogger(LoggerConstants.REPAYMENTSCHEDULAR).debug("RepamentSchedular:getRepaymentSchedule , installment dates obtained ");

				repaymentScheduleInputs.setInstallmentDate(installmentDates);

				Money loanInterest = getLoanInterest();

				MifosLogManager.getLogger(LoggerConstants.REPAYMENTSCHEDULAR).debug("RepamentSchedular:getRepaymentSchedule , loan interest obtained "+loanInterest);

				repaymentScheduleInputs.setLoanInterest(loanInterest);

				List<EMIInstallment> EMIInstallments = getEMIInstallment();

				MifosLogManager.getLogger(LoggerConstants.REPAYMENTSCHEDULAR).debug("RepamentSchedular:getRepaymentSchedule , emi installment  obtained ");

				validateSize(installmentDates,EMIInstallments);

				List<FeeInstallment> feeInstallment  = new ArrayList();

				if(repaymentScheduleInputs.getAccountFee().size() != 0)
					feeInstallment = getFeeInstallment();

				MifosLogManager.getLogger(LoggerConstants.REPAYMENTSCHEDULAR).debug("RepamentSchedular:getRepaymentSchedule , fee installment obtained ");

				RepaymentSchedule repaymentSchedule =  generateRepaymentSchedule(installmentDates,EMIInstallments,feeInstallment);

				MifosLogManager.getLogger(LoggerConstants.REPAYMENTSCHEDULAR).debug("RepamentSchedular:getRepaymentSchedule , repayment schedule generated  ");

				repaymentSchedule.setPrincipal(repaymentScheduleInputs.getPrincipal());
				repaymentSchedule.setInterest(repaymentScheduleInputs.getLoanInterest());
				repaymentSchedule.setFees(repaymentScheduleInputs.getTotalFees().add(repaymentScheduleInputs.getMiscFees()));
				repaymentSchedule.setPenalty(repaymentScheduleInputs.getMiscPenalty());
				
				MifosLogManager.getLogger(LoggerConstants.REPAYMENTSCHEDULAR).debug("RepamentSchedular:total fees   "+repaymentScheduleInputs.getTotalFees());

				return repaymentSchedule;
		}



	/**
	 * This method gets the installment dates
	 * @return  List
	 * @throws RepaymentScheduleException
	 */

	public List<InstallmentDate> getInstallmentDates() throws RepaymentScheduleException
	{
		try
		{
			SchedulerIntf scheduler = null;

			scheduler = RepaymentScheduleHelper.getSchedulerObject(repaymentScheduleInputs.getRepaymentFrequency(),true);
			
			installmentGenerator = new InstallmentGenerator();

			int installmentSkipToStartRepayment = getInstallmentSkipToStartRepayment();

			MifosLogManager.getLogger(LoggerConstants.REPAYMENTSCHEDULAR).debug("RepamentSchedular:getInstallmentDates , installments input  "+repaymentScheduleInputs.getNoOfInstallments());

			List<InstallmentDate> installmentDates =  installmentGenerator.getInstallmentDates(scheduler,repaymentScheduleInputs.getNoOfInstallments()+installmentSkipToStartRepayment,repaymentScheduleInputs.getMeetingToConsider());

			removeInstallmentsNeedNotPay(installmentSkipToStartRepayment,installmentDates);

			printInstallmentDates(installmentDates); 

			return installmentDates;
		}
		catch(InstallmentException installment)
		{
					throw new RepaymentScheduleException(installment.getKey(),installment);
		}
		catch(SchedulerException scheduleException)
		{
		
					throw new RepaymentScheduleException(scheduleException.getKey(),scheduleException);
		}



	}
	/**
	 * This method gets the loan interest
	 * @return  double
	 * @throws RepaymentScheduleException
	 */

	public Money getLoanInterest() throws RepaymentScheduleException
	{

		try
		{
			InterestCalculatorIfc interestCalculator = InterestCalculatorFactory.getInterestCalculator(InterestCalculatorConstansts.FLAT_INTEREST);
			InterestInputs interestInputs = RepaymentScheduleHelper.buildInterestInputs(repaymentScheduleInputs);

			Money loanInterest = interestCalculator.getInterest(interestInputs);

			return loanInterest;
		}
	    catch(InterestCalculationException ic)
		{

			throw new RepaymentScheduleException(ic.getKey(),ic);
		}


	}
	/**
	 * This method gets the emi installment
	 * @return  List
	 * @throws RepaymentScheduleException
	 */
	public List<EMIInstallment> getEMIInstallment() throws RepaymentScheduleException
	{
		try
		{
			EMIGenerator emiGenerator = new EMIGenerator();

			EMIInputs emiInputs = RepaymentScheduleHelper.buildEMIInputs(repaymentScheduleInputs);

			List<EMIInstallment> emiInstallments = emiGenerator.generateEMI(emiInputs);
			return emiInstallments;
		}
    	catch(EMIGenerationException emiex)
		{
			    
				throw new RepaymentScheduleException(emiex.getKey(),emiex);
		}
	}
	/**
	 * This method gets fee installment
	 * @return  List
	 * @throws RepaymentScheduleException
	 */
	public List<FeeInstallment> getFeeInstallment() throws RepaymentScheduleException
	{
		try
		{
			FeeInstallmentGeneratorIfc feeInstallmentGenerator = RepaymentScheduleFactory.getFeeInstallmentGenerator();


			if(repaymentScheduleInputs.getFeeStartDate() == null)
				repaymentScheduleInputs.setFeeStartDate(repaymentScheduleInputs.getRepaymentFrequency().getMeetingStartDate().getTime());

			FeeInputs feeInputs = RepaymentScheduleHelper.buildFeeInputs(repaymentScheduleInputs);

			List<FeeInstallment> feeInstallment = feeInstallmentGenerator.generateFeeInstallment(feeInputs);

			repaymentScheduleInputs.setTotalFees(feeInputs.getFeeAmount());

			return feeInstallment;

		}
		
		catch(FeeGenerationException feeGenerationEx)
		{

				throw new RepaymentScheduleException(feeGenerationEx.getKey(),feeGenerationEx);
		}

	}

	private Money getTotalFeeAmount(List<FeeInstallment> feeInstallment)
	{
		int count = feeInstallment.size();
		FeeInstallment feeInst = null;
		double totalFee = 0.0;
		if(feeInstallment.size() > 0)
		{
			 return ((FeeInstallment)feeInstallment.get(0)).getAccountFee();
	    }
	    return new Money();
	}

	private void isInputSet() throws RepaymentScheduleException
	{
		if(repaymentScheduleInputs == null)
		 	throw new RepaymentScheduleException(RepaymentScheduleConstansts.REPAYMENTINPUTS_NOTSPECIFIED);
	}

	private void validate(RepaymentScheduleInputsIfc repaymentScheduleInputs) throws RepaymentScheduleException
	{

	}



		private void printInstallmentDates(List<InstallmentDate> installmentDates)
		{



							java.util.Iterator<InstallmentDate> itList = installmentDates.iterator();

							while(itList.hasNext())
							{
								InstallmentDate installmentDate = itList.next();
								MifosLogManager.getLogger(LoggerConstants.REPAYMENTSCHEDULAR).debug("RepamentSchedular:printInstallmentDates : id  "+installmentDate.getInstallmentId());
								MifosLogManager.getLogger(LoggerConstants.REPAYMENTSCHEDULAR).debug("RepamentSchedular:printInstallmentDates : date  "+installmentDate.getInstallmentDueDate().toString());

							}

		}

	
		public void printRepaymentSchedule(RepaymentSchedule repaymentSchedule)
		{

			List<RepaymentScheduleInstallment> scheduleInstallment = repaymentSchedule.getRepaymentScheduleInstallment();
			for(int i = 0; i < scheduleInstallment.size(); i++)
			{
				RepaymentScheduleInstallment rs = scheduleInstallment.get(i);
				MifosLogManager.getLogger(LoggerConstants.REPAYMENTSCHEDULAR).debug("RepamentSchedular:printRepaymentSchedule : installment id  "+rs.getInstallment());
				MifosLogManager.getLogger(LoggerConstants.REPAYMENTSCHEDULAR).debug("RepamentSchedular:printRepaymentSchedule : installment date  "+rs.getDueDate());
				MifosLogManager.getLogger(LoggerConstants.REPAYMENTSCHEDULAR).debug("RepamentSchedular:printRepaymentSchedule : principal  "+rs.getPrincipal());
				MifosLogManager.getLogger(LoggerConstants.REPAYMENTSCHEDULAR).debug("RepamentSchedular:printRepaymentSchedule : interest  "+rs.getInterest());
				MifosLogManager.getLogger(LoggerConstants.REPAYMENTSCHEDULAR).debug("RepamentSchedular:printRepaymentSchedule : fees  "+rs.getFees());
				MifosLogManager.getLogger(LoggerConstants.REPAYMENTSCHEDULAR).debug("RepamentSchedular:printRepaymentSchedule : total  "+rs.getTotal());


			}
		}


		private void validateSize(List installmentDates,List EMIInstallments) throws RepaymentScheduleException
		{
			MifosLogManager.getLogger(LoggerConstants.REPAYMENTSCHEDULAR).debug("RepamentSchedular:validateSize : installment size  "+installmentDates.size());
			MifosLogManager.getLogger(LoggerConstants.REPAYMENTSCHEDULAR).debug("RepamentSchedular:validateSize : emi installment size  "+EMIInstallments.size());

			if(installmentDates.size() != EMIInstallments.size())
				throw new RepaymentScheduleException(RepaymentScheduleConstansts.DATES_MISMATCH);
		}

		private RepaymentSchedule generateRepaymentSchedule(List<InstallmentDate> installmentDates,List<EMIInstallment> EMIInstallments,List<FeeInstallment> feeInstallmentList) throws RepaymentScheduleException
		{

			RepaymentSchedule repaymentSchedule = new RepaymentSchedule();
			List<RepaymentScheduleInstallment> repaymentScheduleInstallment = new ArrayList();
			Map<Integer,FeeInstallment> feeInstallmentMap = convertToFeeInstallmentMap(feeInstallmentList);
			
			Calendar date = new GregorianCalendar();
			date.setTimeInMillis(repaymentScheduleInputs.getDisbursementDate().getTime());
			Calendar disbursementDate=new GregorianCalendar(date.get(Calendar.YEAR),date.get(Calendar.MONTH),date.get(Calendar.DATE));
			date = new GregorianCalendar();
			date.setTimeInMillis(System.currentTimeMillis());
			Calendar currentDate=new GregorianCalendar(date.get(Calendar.YEAR),date.get(Calendar.MONTH),date.get(Calendar.DATE));
			
			int count = installmentDates.size();
			for(int i = 0; i < count ; i++)
			{
				InstallmentDate id = installmentDates.get(i);
				EMIInstallment em = EMIInstallments.get(i);

				RepaymentScheduleInstallment rs = new RepaymentScheduleInstallment();
				rs.setInstallment(id.getInstallmentId());
				rs.setDueDate(id.getInstallmentDueDate());
				rs.setPrincipal(em.getPrincipal());
				rs.setInterest(em.getInterest());
				
				if(disbursementDate.compareTo(currentDate)==0 && repaymentScheduleInputs.getIsInterestDedecutedAtDisburesement()==true){
					handleIDADisbursementAccountForMiscFeeAndPenalty(id,rs);
				}else{
					handleNormalAccountForMiscFeeAndPenalty(id,rs);
				}
					

				FeeInstallment feeInstallment = feeInstallmentMap.get(new Integer(id.getInstallmentId()));
				if(feeInstallment != null)
				{
					rs.setFees(feeInstallment.getTotalAccountFee());
					rs.setFeeInstallment(feeInstallment);

				}

				repaymentScheduleInstallment.add(rs);

			}

			repaymentSchedule.setRepaymentScheduleInstallment(repaymentScheduleInstallment);

			printRepaymentSchedule(repaymentSchedule); 

			return repaymentSchedule;

		}

		private RepaymentSchedule generateRepaymentSchedule(List<InstallmentDate> installmentDates,List<FeeInstallment> feeInstallmentList) throws RepaymentScheduleException
		{

			RepaymentSchedule repaymentSchedule = new RepaymentSchedule();
			List<RepaymentScheduleInstallment> repaymentScheduleInstallment = new ArrayList();
			Map<Integer,FeeInstallment> feeInstallmentMap = convertToFeeInstallmentMap(feeInstallmentList);

			int count = installmentDates.size();
			for(int i = 0; i < count ; i++)
			{
				InstallmentDate id = installmentDates.get(i);


				RepaymentScheduleInstallment rs = new RepaymentScheduleInstallment();
				rs.setInstallment(id.getInstallmentId());
				rs.setDueDate(id.getInstallmentDueDate());

				FeeInstallment feeInstallment = feeInstallmentMap.get(new Integer(id.getInstallmentId()));
				if(feeInstallment != null)
				{
					rs.setFees(feeInstallment.getTotalAccountFee());
					rs.setFeeInstallment(feeInstallment);

				}

				repaymentScheduleInstallment.add(rs);

			}

			repaymentSchedule.setRepaymentScheduleInstallment(repaymentScheduleInstallment);

			printRepaymentSchedule(repaymentSchedule); 

			return repaymentSchedule;

		}

		private Map<Integer,FeeInstallment> convertToFeeInstallmentMap(List<FeeInstallment> feeInstallmentList)
		{
			Map<Integer,FeeInstallment> feeInstallmentMap = new HashMap();

			int count = feeInstallmentList.size();
			FeeInstallment feeInstallment = null;

			for(int i = 0; i < count ; i++)
			{
					feeInstallment =   feeInstallmentList.get(i);
					feeInstallmentMap.put(feeInstallment.getInstallmentId(),feeInstallment);

			}

			return feeInstallmentMap;


		}

		private void removeInstallmentsNeedNotPay(int installmentSkipToStartRepayment,List<InstallmentDate> installmentDates)
		{

			int removeCounter = 0;
			for(int i =0; i < installmentSkipToStartRepayment; i++)
				installmentDates.remove(removeCounter);

			// re-adjust the installment ids
			if(installmentSkipToStartRepayment > 0)
			{
				int count = installmentDates.size();
				for(int i = 0; i < count ; i++)
				{
					InstallmentDate instDate = installmentDates.get(i);

					instDate.setInstallmentId(i+1);


				}
			}
		}
		
		private RepaymentSchedule roundRepaymentSchedule(RepaymentSchedule repaymentSchedule){
			if(!repaymentScheduleInputs.getIsPrincipalInLastPayment()){
				List<RepaymentScheduleInstallment> repaymentScheuduleInstallments=repaymentSchedule.getRepaymentScheduleInstallment();
				RepaymentScheduleInstallment lastRepaymentScheuduleInstallment=null;
				Money diffAmount=new Money();	
				for(RepaymentScheduleInstallment repaymentScheduleInstallment : repaymentScheuduleInstallments){
					if(repaymentScheduleInputs.getIsInterestDedecutedAtDisburesement() && repaymentScheduleInstallment.getInstallment()==1)
						continue;
					lastRepaymentScheuduleInstallment=repaymentScheduleInstallment;
					if(repaymentScheduleInstallment.getInstallment()==repaymentScheuduleInstallments.size())
						break;
					Money totalAmount=repaymentScheduleInstallment.getTotal();
					Money roundedTotalAmount=Money.round(totalAmount);
					repaymentScheduleInstallment.setPrincipal(repaymentScheduleInstallment.getPrincipal().subtract(totalAmount.subtract(roundedTotalAmount)));
					diffAmount=diffAmount.add(totalAmount.subtract(roundedTotalAmount));
				}
				lastRepaymentScheuduleInstallment.setPrincipal(lastRepaymentScheuduleInstallment.getPrincipal().add(diffAmount));
			}
			return repaymentSchedule; 
		}
		
		private void handleIDADisbursementAccountForMiscFeeAndPenalty(InstallmentDate id,RepaymentScheduleInstallment rs){
			//This code has been added for adding miscFee and miscPenalty
			Money miscFee= repaymentScheduleInputs.getMiscFees();
			Money miscPenalty= repaymentScheduleInputs.getMiscPenalty();
			if(miscFee!=null){	
				if(id.getInstallmentId()==2)
					rs.setMiscFees(miscFee);
			}

			if(miscPenalty!=null){
				if(id.getInstallmentId()==2)
					rs.setMiscPenalty(miscPenalty);
			}
		}
		
		private void handleNormalAccountForMiscFeeAndPenalty(InstallmentDate id,RepaymentScheduleInstallment rs){
			//This code has been added for adding miscFee and miscPenalty
			Money miscFee= repaymentScheduleInputs.getMiscFees();
			Money miscPenalty= repaymentScheduleInputs.getMiscPenalty();
			if(miscFee!=null){	
				if(id.getInstallmentId()==1)
					rs.setMiscFees(miscFee);
			}
			if(miscPenalty!=null){
				if(id.getInstallmentId()==1)
					rs.setMiscPenalty(miscPenalty);
			}
		}

		
}
