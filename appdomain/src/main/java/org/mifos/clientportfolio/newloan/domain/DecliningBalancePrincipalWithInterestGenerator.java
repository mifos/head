package org.mifos.clientportfolio.newloan.domain;

import static org.mifos.accounts.loan.util.helpers.LoanConstants.PRORATE_RULE;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.mifos.accounts.loan.util.helpers.InstallmentPrincipalAndInterest;
import org.mifos.accounts.productdefinition.util.helpers.GraceType;
import org.mifos.config.persistence.ConfigurationPersistence;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.MoneyUtils;

public class DecliningBalancePrincipalWithInterestGenerator implements PrincipalWithInterestGenerator {

    @Override
    public List<InstallmentPrincipalAndInterest> generateEqualInstallments(LoanInterestCalculationDetails loanInterestCalculationDetails) {

    	List<InstallmentPrincipalAndInterest> lstInstallmntPricplIntrst = null;
    	
        GraceType graceType = loanInterestCalculationDetails.getGraceType();
        Integer gracePeriodDuration = loanInterestCalculationDetails.getGracePeriodDuration();
        Money loanAmount = loanInterestCalculationDetails.getLoanAmount();
        Integer numberOfInstallments = loanInterestCalculationDetails.getNumberOfInstallments();
        Double interestFractionalRatePerInstallment = loanInterestCalculationDetails.getInterestFractionalRatePerInstallment();
        Double interestRate = loanInterestCalculationDetails.getInterestRate();
        
		//Madhukar HugoTechnologies
		int prorateValue = 0;
		
		LocalDate disbursalDateWithLocalDate=loanInterestCalculationDetails.getDisbursementDate();
		DateTime disbursalDate=disbursalDateWithLocalDate.toDateTimeAtStartOfDay();
		List<DateTime> dates=loanInterestCalculationDetails.getLoanScheduleDates();
		if(dates.size() > 0){ //check whether loanscheduledDates are there
			DateTime firstRepaymentDay=dates.get(0);

			long  differenceOfTwoDatesinMilliseconds=(firstRepaymentDay.toDate().getTime()-disbursalDate.toDate().getTime());
			long noOfDays=differenceOfTwoDatesinMilliseconds/(1000*60*60*24);
			int days=(int)noOfDays;


			DateTime secondRepaymentDay=dates.get(1);
			long duration=(secondRepaymentDay.toDate().getTime()-firstRepaymentDay.toDate().getTime())/(1000*60*60*24);
			int durationInDays=(int)duration;	
			
			prorateValue = new ConfigurationPersistence().getConfigurationKeyValueInteger(PRORATE_RULE).getValue();
			if (prorateValue==1)
				lstInstallmntPricplIntrst =allDecliningInstallments_v2(loanAmount, numberOfInstallments, graceType, gracePeriodDuration, interestFractionalRatePerInstallment, interestRate,days,durationInDays);		
		       }
		if (prorateValue != 1){
		  lstInstallmntPricplIntrst =allDecliningInstallments_v2(loanAmount, numberOfInstallments, graceType, gracePeriodDuration, interestFractionalRatePerInstallment, interestRate);
		}
		
		return lstInstallmntPricplIntrst;

		
	}

    /**
     * Generate declining-interest installment variants based on the type of grace period.
     * <ul>
     * <li>If grace period is none, or applies to both principal and interest, the loan calculations are the same.
     * <li>If grace period is for principal only, don't add new installments. The first grace installments are
     * interest-only, and principal is paid off with the remaining installments.
     * </ul>
     */
    private List<InstallmentPrincipalAndInterest> allDecliningInstallments_v2(Money loanAmount, Integer numberOfInstallments,
            GraceType graceType, Integer gracePeriodDuration, Double interestFractionalRatePerInstallment, Double interestRate) {

        List<InstallmentPrincipalAndInterest> emiInstallments = new ArrayList<InstallmentPrincipalAndInterest>();

        if (graceType == GraceType.NONE || graceType == GraceType.GRACEONALLREPAYMENTS) {
            Money paymentPerPeriod = getPaymentPerPeriodForDecliningInterest_v2(numberOfInstallments, interestRate, loanAmount, interestFractionalRatePerInstallment);
            emiInstallments = generateDecliningInstallmentsNoGrace_v2(numberOfInstallments, loanAmount, interestFractionalRatePerInstallment, paymentPerPeriod);
        } else {

            // getGraceType() == GraceType.PRINCIPALONLYGRACE which is disabled.

            emiInstallments = generateDecliningInstallmentsInterestOnly_v2(loanAmount, gracePeriodDuration, interestFractionalRatePerInstallment);

            int nonGraceInstallments = (numberOfInstallments - gracePeriodDuration);
            Money paymentPerPeriod = getPaymentPerPeriodForDecliningInterest_v2(nonGraceInstallments, interestRate, loanAmount, interestFractionalRatePerInstallment);
            emiInstallments.addAll(generateDecliningInstallmentsNoGrace_v2(nonGraceInstallments, loanAmount, interestFractionalRatePerInstallment, paymentPerPeriod));
        }
        return emiInstallments;
    }
    
  //By Madhukar: Hugo Technologies
    private List<InstallmentPrincipalAndInterest> allDecliningInstallments_v2(Money loanAmount, Integer numberOfInstallments,
            GraceType graceType, Integer gracePeriodDuration, Double interestFractionalRatePerInstallment, Double interestRate,Integer days,Integer durationInDays) {

        List<InstallmentPrincipalAndInterest> emiInstallments = new ArrayList<InstallmentPrincipalAndInterest>();

        if (graceType == GraceType.NONE || graceType == GraceType.GRACEONALLREPAYMENTS) {
            Money paymentPerPeriod = getPaymentPerPeriodForDecliningInterest_v2(numberOfInstallments, interestRate, loanAmount, interestFractionalRatePerInstallment);
            emiInstallments = generateDecliningInstallmentsNoGrace_v2(numberOfInstallments, loanAmount, interestFractionalRatePerInstallment, paymentPerPeriod);
        } else {

            // getGraceType() == GraceType.PRINCIPALONLYGRACE which is disabled.

            emiInstallments = generateDecliningInstallmentsInterestOnly_v2(loanAmount, gracePeriodDuration, interestFractionalRatePerInstallment,days,durationInDays);

            int nonGraceInstallments = (numberOfInstallments - gracePeriodDuration);
            Money paymentPerPeriod = getPaymentPerPeriodForDecliningInterest_v2(nonGraceInstallments, interestRate, loanAmount, interestFractionalRatePerInstallment);
            emiInstallments.addAll(generateDecliningInstallmentsNoGrace_v2(nonGraceInstallments, loanAmount, interestFractionalRatePerInstallment, paymentPerPeriod));
        }
        return emiInstallments;
    }

    /*
     * Calculates equal payments per period for fixed payment, declining-interest loan type. Uses formula from
     * http://confluence.mifos.org :9090/display/Main/Declining+Balance+Example+Calcs The formula is copied here: EMI =
     * P * i / [1- (1+i)^-n] where p = principal (amount of loan) i = rate of interest per installment period as a
     * decimal (not percent) n = no. of installments
     *
     * Translated into program variables and method calls:
     *
     * paymentPerPeriod = interestFractionalRatePerPeriod * getLoanAmount() / ( 1 - (1 +
     * interestFractionalRatePerPeriod) ^ (-getNoOfInstallments()))
     *
     * NOTE: Use double here, not BigDecimal, to calculate the factor that getLoanAmount() is multiplied by. Since
     * calculations all involve small quantities, 64-bit precision is sufficient. It is is more accurate to use
     * floating-point, for quantities of small magnitude (say for very small interest rates)
     *
     * NOTE: These calculations do not take into account EPI or grace period adjustments.
     */
    private Money getPaymentPerPeriodForDecliningInterest_v2(final int numInstallments, final Double interestRate, final Money loanAmount, Double interestFractionalRatePerInstallment) {
        double factor = 0.0;
        if (interestRate == 0.0) {
            Money paymentPerPeriod = loanAmount.divide(numInstallments);
            return paymentPerPeriod;
        }

        factor = interestFractionalRatePerInstallment
                / (1.0 - Math.pow(1.0 + interestFractionalRatePerInstallment, -numInstallments));
        Money paymentPerPeriod = loanAmount.multiply(factor);
        return paymentPerPeriod;
    }

    /**
     * Generate interest-only payments for the duration of the grace period. Interest paid is on the outstanding
     * balance, which during the grace period is the entire principal amount.
     */
    private List<InstallmentPrincipalAndInterest> generateDecliningInstallmentsInterestOnly_v2(Money loanAmount, Integer gracePeriodDuration, Double interestFractionalRatePerInstallment) {

        List<InstallmentPrincipalAndInterest> emiInstallments = new ArrayList<InstallmentPrincipalAndInterest>();
        Money zero = MoneyUtils.zero(loanAmount.getCurrency());
        for (int i = 0; i < gracePeriodDuration; i++) {

            InstallmentPrincipalAndInterest installment = new InstallmentPrincipalAndInterest(zero, loanAmount.multiply(interestFractionalRatePerInstallment));
            emiInstallments.add(installment);
        }

        return emiInstallments;
    }

  //By Madhukar:HugoTechnologies
    private List<InstallmentPrincipalAndInterest> generateDecliningInstallmentsInterestOnly_v2(Money loanAmount, Integer gracePeriodDuration, Double interestFractionalRatePerInstallment,Integer days,Integer durationInDays) {

        List<InstallmentPrincipalAndInterest> emiInstallments = new ArrayList<InstallmentPrincipalAndInterest>();
        Money zero = MoneyUtils.zero(loanAmount.getCurrency());

        for (int i = 0; i < gracePeriodDuration; i++) {
        	 if (i<1) {
             	int noOfDaysBetweenDisbursaldateAndNextMeetingDate=days;
                 InstallmentPrincipalAndInterest installment = new InstallmentPrincipalAndInterest(zero, loanAmount.multiply(interestFractionalRatePerInstallment,noOfDaysBetweenDisbursaldateAndNextMeetingDate,durationInDays));
                 emiInstallments.add(installment);
     		}
        	 else{
        InstallmentPrincipalAndInterest installment = new InstallmentPrincipalAndInterest(zero, loanAmount.multiply(interestFractionalRatePerInstallment));
        emiInstallments.add(installment);
        }
        	 }
    return emiInstallments;
}
    
    
    /**
     * Return the list if payment installments for declining interest method, for the number of installments specified.
     */
    private List<InstallmentPrincipalAndInterest> generateDecliningInstallmentsNoGrace_v2(final int numberOfInstallments, Money loanAmount, Double interestFractionalRatePerInstallment, Money paymentPerPeriod) {

        List<InstallmentPrincipalAndInterest> emiInstallments = new ArrayList<InstallmentPrincipalAndInterest>();

        Money decliningPrincipalBalance = loanAmount;
        for (int i = 0; i < numberOfInstallments; i++) {

            Money interestThisPeriod = decliningPrincipalBalance.multiply(interestFractionalRatePerInstallment);
            Money principalThisPeriod = paymentPerPeriod.subtract(interestThisPeriod);

            InstallmentPrincipalAndInterest installment = new InstallmentPrincipalAndInterest(principalThisPeriod, interestThisPeriod);
            emiInstallments.add(installment);

            decliningPrincipalBalance = decliningPrincipalBalance.subtract(principalThisPeriod);
        }

        return emiInstallments;
    }
}