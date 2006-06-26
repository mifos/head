package org.mifos.application.accounts.util.helpers;

import java.lang.String;
import java.util.List;

import org.mifos.application.NamedQueryConstants;
import org.mifos.application.accounts.exceptions.AccountExceptionConstants;
import org.mifos.application.accounts.exceptions.IDGenerationException;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.dao.DAO;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.util.helpers.StringUtils;

/** 
 *  This class is used to generate ids for accounts.
 *  @author amit
 *  
 */
public class IDGenerator {
	 /**
	  * This method returns the generated accountId for the loan account
	  * @param  officeGlobalNum office system number
	  * @return generated account id for loan account
	  * @throws ApplicationException
	  * @throws SystemException
	  */
	 public static  String generateIdForLoan(String officeGlobalNum)throws ApplicationException ,SystemException{
		 return generateId(officeGlobalNum);
	}

	 /**
	  * This method returns the generated accountId for the customerAccount
	  * @param  officeGlobalNum office system number
	  * @return generated account id for customer account
	  * @throws ApplicationException
	  * @throws SystemException
	  */
	 public static String generateIdForCustomerAccount(String officeGlobalNum)throws ApplicationException,SystemException{
		 return generateId(officeGlobalNum);
	 }
	 
	 /** 
	  *  Generates an id for the Loan Product .
	  *  The algorithm followed for generating the system ids for loan account is :-
	  *  Account ID - office id followed by 11 digits of running number. This ensures that 100 accounts could be held by any customer and still suffice for 100 million customers. 
	  *	a) split - the old account number is retained to ensure uniqueness. The office id is never inferred from this number. 
	  *	b) merger - the old account number is retained to ensure uniqueness. The office id is never inferred from this number. 
	  *	c) transfer to another branch - the old account number is retained to ensure uniqueness. The office id is never inferred from this number. The other details like customer pk etc get created int he new office as a result of new record creation with the customer movement. The customer global number is retained.
	  *  @return
	  *  
	  */
	 private static String generateId(String officeGlobalNum)throws ApplicationException,SystemException{
		 StringBuilder loanAccountSysID = new StringBuilder();
			
			// setting the customer id
			loanAccountSysID.append(officeGlobalNum);
			MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).debug("After appending the officeGlobalNum to loanAccountSysID  it becomes"  +loanAccountSysID.toString());
			
			
			// setting the 11 digits of account running number.
			
			try {
				loanAccountSysID.append(StringUtils.lpad(getAccountRunningNumber().toString(),'0',11) );
				MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).debug("After appending the running number to loanAccountSysID  it becomes"  +loanAccountSysID.toString());
			} catch (SystemException se) {
				MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).error("There was some error retieving the running number",true,null,se  );
				throw new IDGenerationException(AccountExceptionConstants.IDGenerationException,se);
			}
			
			return loanAccountSysID.toString();
	 }
	 /**
	 *This method returns the running number which is indicated by the max account id in the accounts table  
	 * @return
	 * @throws SystemException
	 */
	private static Integer getAccountRunningNumber()throws SystemException{
		 List queryResult = DAO.executeNamedQuery(NamedQueryConstants.MAXACCOUNTID, null);
		 Integer accountRunningNumber = new Integer(0);
		 if(null != queryResult && null != queryResult.get(0)){
			
				 // it breaks after the first iteration because the query should return only one row.
				 accountRunningNumber = new Integer(queryResult.get(0).toString());
		 }
		 return accountRunningNumber + 1;
	 }
	
	
}