package org.mifos.framework.components.interestcalculator;

import org.mifos.framework.util.helpers.Money;

public class CompoundInterestCalculator implements  InterestCalculatorIfc {
	public Money getInterest(InterestInputs interestInputs) throws InterestCalculationException{
		double intRate = interestInputs.getInterestRate();
		if(interestInputs.getDurationType().equals(InterestCalculatorConstants.DAYS)){
			
			intRate = (intRate/(InterestCalculatorConstants.INTEREST_DAYS))* interestInputs.getDuration();
		}
		else{
			intRate = (intRate/12)*interestInputs.getDuration();
		}
		Money interestAmount = interestInputs.getPrincipal().multiply(new Double(1+(intRate/100.0))).subtract(interestInputs.getPrincipal());
		return interestAmount;
	}
}
