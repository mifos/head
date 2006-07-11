package org.mifos.application.accounts.util.helpers;



import java.sql.Date;

import org.mifos.application.accounts.util.valueobjects.AccountActionDate;
import org.mifos.application.accounts.util.valueobjects.AccountFeesActionDetail;
import org.mifos.application.accounts.util.valueobjects.AccountPayment;
import org.mifos.application.accounts.util.valueobjects.AccountTrxn;
import org.mifos.application.accounts.util.valueobjects.FeesTrxnDetail;
import org.mifos.application.master.util.valueobjects.AccountAction;
import org.mifos.application.master.util.valueobjects.Currency;
import org.mifos.framework.exceptions.ApplicationException;

public class CustomerTrxnBuilder extends TrxnObjectBuilder{

	public AccountPayment buildSpecific(AccountActionDate date, short personnelId)throws ApplicationException {
		AccountPayment pmnt =  new AccountPayment();
		try{
			//TODO change this to get mifos currency from system config
			Currency currency = (Currency)session.get(org.mifos.application.master.util.valueobjects.Currency.class, new Short((short)1));
			pmnt.setAccountId(date.getAccountId());
			pmnt.setAmount(date.getTotalAmount());
			pmnt.setCurrency(currency);
			//loan trxn is always either loan repayement or a fee repayment	
			//first get the loan repayment details
			AccountAction action = (AccountAction)session.get(AccountAction.class, AccountConstants.ACTION_FEE_REPAYMENT);		
			//check if multiple transactions are required for fees set
			if(date.getAccountFeesActionDetail() != null && date.getAccountFeesActionDetail().size() != 0){
				for(AccountFeesActionDetail feeDetail :  date.getAccountFeesActionDetail()){
					AccountTrxn trxn = new AccountTrxn();
					trxn.setAccountAction(action);
					trxn.setAccountId(date.getAccountId());
					trxn.setActionDate(new Date(new java.util.Date().getTime()));
					trxn.setAmount(feeDetail.getFeeAmount());
					//TODO get this from a resource bundle
					trxn.setComments(feeDetail.getAccountFee().getFees().getFeeName());
					trxn.setCreatedDate(new Date(new java.util.Date().getTime()));
					trxn.setCurrency(currency);
					trxn.setDueDate(new Date(date.getActionDate().getTime()));
					trxn.setPersonnelId(personnelId);
					trxn.setCustomerId(date.getCustomerId());
									
					FeesTrxnDetail feesTrxn = new FeesTrxnDetail();
					feesTrxn.setFeeAmount(feeDetail.getFeeAmount());
					feesTrxn.setAccountFees(feeDetail.getAccountFee());
					
					trxn.addFeesTrxnDetails(feesTrxn);
					pmnt.addTrxn(trxn);
				}
			}
		}catch(Exception e){

				throw new ApplicationException(e);
		}
		
		return pmnt;
	}


}
