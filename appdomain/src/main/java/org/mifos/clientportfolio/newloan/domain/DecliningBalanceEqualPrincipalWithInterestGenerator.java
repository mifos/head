package org.mifos.clientportfolio.newloan.domain;

import static org.mifos.accounts.loan.util.helpers.LoanConstants.PRORATE_RULE;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.mifos.accounts.loan.util.helpers.InstallmentPrincipalAndInterest;
import org.mifos.accounts.productdefinition.util.helpers.GraceType;
import org.mifos.config.business.ConfigurationKeyValue;
import org.mifos.config.persistence.ConfigurationPersistence;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.MoneyUtils;

public class DecliningBalanceEqualPrincipalWithInterestGenerator implements PrincipalWithInterestGenerator {

    @Override
    public List<InstallmentPrincipalAndInterest> generateEqualInstallments(LoanInterestCalculationDetails loanInterestCalculationDetails) {

        Money loanAmount = loanInterestCalculationDetails.getLoanAmount();
        Integer numberOfInstallments = loanInterestCalculationDetails.getNumberOfInstallments();
        Double interestFractionalRatePerInstallment = loanInterestCalculationDetails.getInterestFractionalRatePerInstallment();

        GraceType graceType = loanInterestCalculationDetails.getGraceType();
        Integer gracePeriodDuration = loanInterestCalculationDetails.getGracePeriodDuration();
        
        //For M5193
        int prorateValue = 0;
		List<InstallmentPrincipalAndInterest> lstInstallmntPricplIntrst = null;
		
		LocalDate disbursalDateInLocalDate=loanInterestCalculationDetails.getDisbursementDate();
		DateTime disbursalDate=disbursalDateInLocalDate.toDateTimeAtStartOfDay();
		List<DateTime> scheduledDates=loanInterestCalculationDetails.getLoanScheduleDates();
		if(scheduledDates.size() > 2){ //chek whether loanscheduledDates are there
			DateTime firstRepaymentDay=scheduledDates.get(0);
			long  differenceOfTwoDatesinMilliseconds=(firstRepaymentDay.toDate().getTime()-disbursalDate.toDate().getTime());
			long noOfDays=differenceOfTwoDatesinMilliseconds/(1000*60*60*24);
			int noOfDaysBetweenFirstRepaymentDayAndDisbursalDate=(int)noOfDays;


			DateTime secondRepaymentDay=scheduledDates.get(1);
			long duration=(secondRepaymentDay.toDate().getTime()-firstRepaymentDay.toDate().getTime())/(1000*60*60*24);
			int noOfDaysInOneSchedule=(int)duration;	
			
			prorateValue = new ConfigurationPersistence().getConfigurationValueInteger(PRORATE_RULE);
			if (prorateValue==1)
				lstInstallmntPricplIntrst = allDecliningEPIInstallments_v2(loanAmount, numberOfInstallments, interestFractionalRatePerInstallment, graceType, gracePeriodDuration,noOfDaysBetweenFirstRepaymentDayAndDisbursalDate,noOfDaysInOneSchedule);		
		       }
		 if (prorateValue != 1){
		  lstInstallmntPricplIntrst = allDecliningEPIInstallments_v2(loanAmount, numberOfInstallments, interestFractionalRatePerInstallment, graceType, gracePeriodDuration);
		 }
		
		return lstInstallmntPricplIntrst;}
    
    
    //for M5193
	private List<InstallmentPrincipalAndInterest> allDecliningEPIInstallments_v2(Money loanAmount, Integer numberOfInstallments, Double interestFractionalRatePerInstallment, GraceType graceType, Integer gracePeriodDuration,Integer days,Integer durationInDays) {

		List<InstallmentPrincipalAndInterest> emiInstallments = new ArrayList<InstallmentPrincipalAndInterest>();

		if (graceType == GraceType.NONE || graceType == GraceType.GRACEONALLREPAYMENTS) {
			emiInstallments = generateDecliningEPIInstallmentsNoGrace_v2(numberOfInstallments, loanAmount, interestFractionalRatePerInstallment);
		} else {
			emiInstallments = generateDecliningInstallmentsInterestOnly_v2(loanAmount, gracePeriodDuration, interestFractionalRatePerInstallment,days,durationInDays);
			emiInstallments.addAll(generateDecliningEPIInstallmentsNoGrace_v2(numberOfInstallments - gracePeriodDuration, loanAmount, interestFractionalRatePerInstallment));
		}
		return emiInstallments;
	}

    
	private List<InstallmentPrincipalAndInterest> generateDecliningInstallmentsInterestOnly_v2(Money loanAmount, Integer gracePeriodDuration, Double interestFractionalRatePerInstallment,Integer days,Integer durationInDays) {

		List<InstallmentPrincipalAndInterest> emiInstallments = new ArrayList<InstallmentPrincipalAndInterest>();

		Money zeroPrincipal = MoneyUtils.zero(loanAmount.getCurrency());


		for (int i = 0; i < gracePeriodDuration; i++) {
			if (i<1) {
				int noOfDaysBetweenDisbursaldateAndNextMeetingDate=days;

				Money interestPerInstallment = loanAmount.multiply(interestFractionalRatePerInstallment,noOfDaysBetweenDisbursaldateAndNextMeetingDate,durationInDays);
				InstallmentPrincipalAndInterest installment = new InstallmentPrincipalAndInterest(zeroPrincipal, interestPerInstallment);
				emiInstallments.add(installment);
			}
			else{
				InstallmentPrincipalAndInterest installment = new InstallmentPrincipalAndInterest(zeroPrincipal, loanAmount.multiply(interestFractionalRatePerInstallment));
				emiInstallments.add(installment);
			}
		}
		return emiInstallments;
	}
    
    
    

    private List<InstallmentPrincipalAndInterest> allDecliningEPIInstallments_v2(Money loanAmount, Integer numberOfInstallments, Double interestFractionalRatePerInstallment, GraceType graceType, Integer gracePeriodDuration) {

        List<InstallmentPrincipalAndInterest> emiInstallments = new ArrayList<InstallmentPrincipalAndInterest>();

        if (graceType == GraceType.NONE || graceType == GraceType.GRACEONALLREPAYMENTS) {
            emiInstallments = generateDecliningEPIInstallmentsNoGrace_v2(numberOfInstallments, loanAmount, interestFractionalRatePerInstallment);
        } else {
            emiInstallments = generateDecliningInstallmentsInterestOnly_v2(loanAmount, gracePeriodDuration, interestFractionalRatePerInstallment);
            emiInstallments.addAll(generateDecliningEPIInstallmentsNoGrace_v2(numberOfInstallments - gracePeriodDuration, loanAmount, interestFractionalRatePerInstallment));
        }
        return emiInstallments;
    }

    /**
     * Generate interest-only payments for the duration of the grace period. Interest paid is on the outstanding
     * balance, which during the grace period is the entire principal amount.
     */
    private List<InstallmentPrincipalAndInterest> generateDecliningInstallmentsInterestOnly_v2(Money loanAmount, Integer gracePeriodDuration, Double interestFractionalRatePerInstallment) {

        List<InstallmentPrincipalAndInterest> emiInstallments = new ArrayList<InstallmentPrincipalAndInterest>();

        Money zeroPrincipal = MoneyUtils.zero(loanAmount.getCurrency());
        Money interestPerInstallment = loanAmount.multiply(interestFractionalRatePerInstallment);

        for (int i = 0; i < gracePeriodDuration; i++) {
            InstallmentPrincipalAndInterest installment = new InstallmentPrincipalAndInterest(zeroPrincipal, interestPerInstallment);
            emiInstallments.add(installment);
        }

        return emiInstallments;
    }


    private List<InstallmentPrincipalAndInterest> generateDecliningEPIInstallmentsNoGrace_v2(final int numInstallments, Money loanAmount, Double interestFractionalRatePerInstallment) {

        List<InstallmentPrincipalAndInterest> emiInstallments = new ArrayList<InstallmentPrincipalAndInterest>();
        Money principalBalance = loanAmount;
        Money principalPerPeriod = principalBalance.divide(new BigDecimal(numInstallments));
        double interestRate = interestFractionalRatePerInstallment;

        for (int i = 0; i < numInstallments; i++) {
            Money interestThisPeriod = principalBalance.multiply(interestRate);

            InstallmentPrincipalAndInterest installment = new InstallmentPrincipalAndInterest(principalPerPeriod, interestThisPeriod);
            emiInstallments.add(installment);

            principalBalance = principalBalance.subtract(principalPerPeriod);
        }

        return emiInstallments;
    }
}