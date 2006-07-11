package org.mifos.application.accounts.util.helpers;

import java.sql.Date;

import org.mifos.application.accounts.util.valueobjects.AccountActionDate;
import org.mifos.application.accounts.util.valueobjects.AccountFees;
import org.mifos.application.accounts.util.valueobjects.AccountFeesActionDetail;
import org.mifos.application.accounts.util.valueobjects.AccountPayment;
import org.mifos.application.accounts.util.valueobjects.AccountTrxn;
import org.mifos.application.accounts.util.valueobjects.FeesTrxnDetail;
import org.mifos.application.accounts.util.valueobjects.LoanTrxnDetail;
import org.mifos.application.master.util.valueobjects.AccountAction;
import org.mifos.application.master.util.valueobjects.Currency;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.util.helpers.Money;


public class LoanTrxnBuilder extends TrxnObjectBuilder{


	
	public AccountPayment buildSpecific(AccountActionDate date, short personnelId)throws ApplicationException{
		AccountPayment pmnt =  new AccountPayment();
		try{
		//TODO change this to get mifos currency from system config
		Currency currency = (Currency)session.get(org.mifos.application.master.util.valueobjects.Currency.class, new Short((short)1));
		pmnt.setAccountId(date.getAccountId());
		pmnt.setAmount(date.getTotalAmount());
		pmnt.setCurrency(currency);
		//loan trxn is always either loan repayement or a fee repayment	
		//first get the loan repayment details
		AccountAction action = (AccountAction)session.get(AccountAction.class, AccountConstants.ACTION_LOAN_REPAYMENT);		
		AccountTrxn trxn = new AccountTrxn();
		trxn.setAccountAction(action);
		trxn.setAccountId(date.getAccountId());
		trxn.setActionDate(new Date(new java.util.Date().getTime()));
		trxn.setAmount(date.getPrincipal().add(date.getInterest()));
		//TODO get this from a resource bundle
		trxn.setComments("Loan Repayment for installment # " + date.getInstallmentId());
		trxn.setCreatedDate(new Date(new java.util.Date().getTime()));
		trxn.setCurrency(currency);
		trxn.setDueDate(new Date(date.getActionDate().getTime()));
		trxn.setPersonnelId(personnelId);
		trxn.setCustomerId(date.getCustomerId());
		
		LoanTrxnDetail loanTrxn = new LoanTrxnDetail();
		loanTrxn.setInstallmentId(date.getInstallmentId());
		loanTrxn.setInterestAmount(date.getInterest());
		loanTrxn.setPenaltyAmount(new Money());
		loanTrxn.setPrincipalAmount(date.getPrincipal());
		
		trxn.addLoanTrxnDetails(loanTrxn);
		pmnt.addTrxn(trxn);
		
		//check if the loan transaction has a penalty associated
		if(date.getPenalty() != null && date.getPenalty().getAmountDoubleValue() != 0){
			//create a new penalty transaction
			AccountAction penaltyAction = (AccountAction)session.get(AccountAction.class, AccountConstants.ACTION_LOAN_PENALTY);		
			AccountTrxn penaltyTrxn = new AccountTrxn();
			penaltyTrxn.setAccountAction(penaltyAction);
			penaltyTrxn.setAccountId(date.getAccountId());
			penaltyTrxn.setActionDate(new Date(new java.util.Date().getTime()));
			penaltyTrxn.setAmount(date.getPenalty());
			//TODO get this from a resource bundle
			penaltyTrxn.setComments("Penalty for installment # " + date.getInstallmentId());
			penaltyTrxn.setCreatedDate(new Date(new java.util.Date().getTime()));
			penaltyTrxn.setCurrency(currency);
			penaltyTrxn.setDueDate(new Date(date.getActionDate().getTime()));
			penaltyTrxn.setPersonnelId(personnelId);
			penaltyTrxn.setCustomerId(date.getCustomerId());
			
			LoanTrxnDetail loanPenaltyTrxn = new LoanTrxnDetail();
			loanPenaltyTrxn.setInstallmentId(date.getInstallmentId());
			loanPenaltyTrxn.setInterestAmount(new Money());
			loanPenaltyTrxn.setPenaltyAmount(date.getPenalty());
			loanPenaltyTrxn.setPrincipalAmount(new Money());	
			
			penaltyTrxn.addLoanTrxnDetails(loanPenaltyTrxn);	
			pmnt.addTrxn(penaltyTrxn);
		}
		
		//check if multiple transactions are required for fees set
		if(date.getAccountFeesActionDetail() != null && date.getAccountFeesActionDetail().size() != 0){
			AccountAction feeAction = (AccountAction)session.get(AccountAction.class, AccountConstants.ACTION_FEE_REPAYMENT);
			for(AccountFeesActionDetail feeDetail :  date.getAccountFeesActionDetail()){
				//create a new fees transaction		
				AccountFees fees = feeDetail.getAccountFee();
				//try to retrieve the fees object's name as well
				feeDetail.getAccountFee().getFees().getFeeName();
				AccountTrxn feesAccountTrxn = new AccountTrxn();
				feesAccountTrxn.setAccountAction(feeAction);
				feesAccountTrxn.setAccountId(date.getAccountId());
				feesAccountTrxn.setActionDate(new Date(new java.util.Date().getTime()));
				feesAccountTrxn.setAmount(feeDetail.getFeeAmount());
				feesAccountTrxn.setComments(fees.getFees().getFeeName());
				feesAccountTrxn.setCreatedDate(new Date(new java.util.Date().getTime()));
				feesAccountTrxn.setCurrency(currency);
				feesAccountTrxn.setDueDate(new Date(date.getActionDate().getTime()));
				feesAccountTrxn.setPersonnelId(personnelId);
				
				FeesTrxnDetail feesTrxn = new FeesTrxnDetail();
				feesTrxn.setFeeAmount(feeDetail.getFeeAmount());
				feesTrxn.setAccountFees(fees);
				
				feesAccountTrxn.addFeesTrxnDetails(feesTrxn);
				pmnt.addTrxn(feesAccountTrxn);
			}
		}
		}catch(Exception e){
			throw new ApplicationException(e);
		}
		
		return pmnt;
	}
	
}
