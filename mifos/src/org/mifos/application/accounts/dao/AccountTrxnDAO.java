/**

 * AccountTrxnDAO.java    version: xxx



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
package org.mifos.application.accounts.dao;

import java.sql.Date;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.mifos.application.NamedQueryConstants;
import org.mifos.application.accounts.loan.util.valueobjects.LoanSummary;
import org.mifos.application.accounts.util.helpers.AccountConstants;
import org.mifos.application.accounts.util.helpers.AccountTypes;
import org.mifos.application.accounts.util.valueobjects.AccountActionDate;
import org.mifos.application.accounts.util.valueobjects.AccountFeesActionDetail;
import org.mifos.application.accounts.util.valueobjects.AccountPayment;
import org.mifos.framework.dao.DAO;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.HibernateProcessException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.valueobjects.Context;
//import test.HibernateUtil;

public class AccountTrxnDAO extends DAO {
	public AccountTrxnDAO() {
		super();
	}

	public AccountActionDate getPaymentDetail(Integer accountId) throws ApplicationException, SystemException{
		AccountActionDate acct = null;

		Session session=null;
		Transaction transaction=null;
		try{
			session = HibernateUtil.getSession();
			transaction = session.beginTransaction();
			Query query = session.getNamedQuery(NamedQueryConstants.RETRIEVE_INSTALLMENT_NUM);
			query.setInteger("accountId",accountId);
			Integer id = (Integer)query.uniqueResult();

			if(id!=null && id!=0){
				acct = (AccountActionDate)session.get(AccountActionDate.class, id);
				//get the associated fees details as well
				acct.getAccountFeesActionDetail();
				for(AccountFeesActionDetail feeDetail : acct.getAccountFeesActionDetail())
					feeDetail.getAccountFee().getFees().getFeeName();
			}
			transaction.commit();
		}catch(HibernateProcessException hbe){
			hbe.printStackTrace();
			transaction.rollback();
			throw new ApplicationException(hbe);
		}finally{
			HibernateUtil.closeSession(session);
		}

		return acct;
	}

	public List<AccountActionDate> getInstallmentHistory(Integer accountId) throws ApplicationException, SystemException{

		List<AccountActionDate> list = null;

		Session session=null;
		Transaction transaction=null;
		try{
			session = HibernateUtil.getSession();
			transaction = session.beginTransaction();
			Query query = session.getNamedQuery(NamedQueryConstants.RETRIEVE_INSTALLMENTS);
			query.setInteger("accountId",accountId);
			list = query.list();

			/*for(AccountActionDate acctRecord:list){
				// System.out.println(acctRecord.getActionDate());
				// System.out.println(acctRecord.getAccountFeesActionDetail().size());
			}*/

			transaction.commit();

		}catch(HibernateProcessException hpe){
			//TODO comment the following printStackTrace
			hpe.printStackTrace();
			transaction.rollback();
			throw new ApplicationException(hpe);

		}finally{
			HibernateUtil.closeSession(session);
			session=null;
		}

		return list;

	}

	public void save(AccountPayment payment, Date paymentDate, Short paymentType) throws ApplicationException, SystemException{

	}

	public void create(Context context) throws ApplicationException, SystemException {
		Session session=null;
		Transaction transaction=null;
		AccountPayment payment = (AccountPayment)context.getValueObject();
		AccountActionDate actDate = (AccountActionDate)context.getBusinessResults(AccountConstants.ACCOUNT_ACTION_DATE_KEY);
		try{

			session = HibernateUtil.getSession();
			transaction = session.beginTransaction();


			//check the account type
			if(payment.getAccountType() == Short.parseShort(AccountTypes.LOANACCOUNT)){

				//TODO - current version supports full payment only. Refactoring needed here
				actDate.setPrincipalPaid(actDate.getPrincipal());
				actDate.setInterestPaid(actDate.getInterest());
				actDate.setPenaltyPaid(actDate.getPenalty());
				actDate.setPaymentStatus(Constants.YES);
				actDate.setPaymentDate(payment.getPaymentDate());
				for(AccountFeesActionDetail feeDetail : actDate.getAccountFeesActionDetail()){
					feeDetail.setAmountPaid(feeDetail.getFeeAmount());
				}
                LoanSummary summary = getLoanSummary(payment.getAccountId());
				//update the loan summary table

				//LoanSummary summary = (LoanSummary)session.get(LoanSummary.class, payment.getAccountId());
				
				if(summary != null){
					summary.setPrincipalPaid(summary.getPrincipalPaid().add(actDate.getPrincipal()));
					summary.setInterestPaid(summary.getInterestPaid().add(actDate.getInterest()));
					summary.setPenaltyPaid(summary.getPenaltyPaid().add(actDate.getPenalty()));
					Money feesPaid = summary.getFeesPaid().add(actDate.getTotalFeesAmount());
					summary.setFeesPaid(feesPaid);
					session.update(summary);
				}

			}else if(payment.getAccountType() == Short.parseShort(AccountTypes.SAVINGSACCOUNT)){
				//TODO Savings code goes here
			}else if(payment.getAccountType() == Short.parseShort(AccountTypes.CUSTOMERACCOUNT)){
				actDate.setPaymentStatus(Constants.YES);
				actDate.setPaymentDate(payment.getPaymentDate());
				for(AccountFeesActionDetail feeDetail : actDate.getAccountFeesActionDetail()){
					feeDetail.setAmountPaid(feeDetail.getFeeAmount());
				}
			}
			
			
			session.save(payment);
			session.update(actDate);



			transaction.commit();

		}catch(HibernateProcessException hpe){
			//TODO comment the following printStackTrace
			hpe.printStackTrace();
			transaction.rollback();
			throw new ApplicationException(hpe);

		}finally{
			HibernateUtil.closeSession(session);
			session=null;
		}
	}


  public LoanSummary getLoanSummary(Integer accountId) throws ApplicationException, SystemException 
  {
	  Session session=null;
	  LoanSummary summary = null; 
	  try
	  {
		  session = HibernateUtil.getSession();
			System.out.println("before fetching loan summary");
		  summary = (LoanSummary)session.get(LoanSummary.class, accountId);
		  System.out.println("after fetching loan summary");
	  }
	catch(HibernateProcessException hpe){
		//TODO comment the following printStackTrace
		hpe.printStackTrace();
		
		throw new ApplicationException(hpe);

	}
	finally
	{
		HibernateUtil.closeSession(session);
		session=null;
	}
	return summary;


  }

}
