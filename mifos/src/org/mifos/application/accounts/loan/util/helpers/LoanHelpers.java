package org.mifos.application.accounts.loan.util.helpers;

import java.util.List;

import org.mifos.application.accounts.util.valueobjects.AccountActionDate;
import org.mifos.framework.util.helpers.Money;


public class LoanHelpers {
	
	public static void roundAccountActionsDate(Boolean isInterestDeductedAtDisbursment, Boolean isPrincipalDueInLastInstallment,List<AccountActionDate> accountActionDateList){
		if(!isPrincipalDueInLastInstallment){
			AccountActionDate lastAccountActionDate =null;
			Money diffAmount=new Money();
			int count=0;
			for(AccountActionDate accountActionDate : accountActionDateList){
				if(isInterestDeductedAtDisbursment && accountActionDate.getInstallmentId().equals(Short.valueOf("1")))
					continue;
				lastAccountActionDate=accountActionDate;
				count++;
				if(count==accountActionDateList.size()){
					break;
				}
				Money totalAmount=accountActionDate.getTotalAmountWithMisc();
				Money roundedTotalAmount=Money.round(totalAmount);
				accountActionDate.setPrincipal(accountActionDate.getPrincipal().subtract(totalAmount.subtract(roundedTotalAmount)));
				diffAmount=diffAmount.add(totalAmount.subtract(roundedTotalAmount));
			}
		lastAccountActionDate.setPrincipal(lastAccountActionDate.getPrincipal().add(diffAmount));
		}
	}

}