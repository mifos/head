/**

 * FeesDAO.java    version: xxx



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

package org.mifos.application.fees.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.StaleObjectStateException;
import org.hibernate.Transaction;
import org.mifos.application.accounts.financial.business.COABO;
import org.mifos.application.accounts.financial.business.FinancialActionBO;
import org.mifos.application.accounts.financial.business.GLCodeEntity;
import org.mifos.application.accounts.financial.util.helpers.FinancialActionCache;
import org.mifos.application.accounts.financial.util.helpers.FinancialActionConstants;
import org.mifos.application.fees.util.helpers.FeeFrequencyType;
import org.mifos.application.fees.util.helpers.FeesConstants;
import org.mifos.application.fees.util.helpers.RateAmountFlag;
import org.mifos.application.fees.util.valueobjects.Fees;
import org.mifos.application.fees.util.valueobjects.ViewFees;
import org.mifos.application.master.util.valueobjects.FeePaymentMaster;
import org.mifos.application.meeting.util.valueobjects.Meeting;
import org.mifos.framework.dao.DAO;
import org.mifos.framework.dao.helpers.MasterDataRetriever;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.HibernateProcessException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.valueobjects.Context;
import org.mifos.framework.util.valueobjects.SearchResults;

/**
 * @author krishankg
 * 
 */
public class FeesDAO extends DAO {

	public void obtainMasterData(Context context, Short localeId)
			throws SystemException, ApplicationException {
		MasterDataRetriever masterDataRetriever = new MasterDataRetriever();
		context.addAttribute(masterDataRetriever.retrieveMasterData(
				FeesConstants.FEECATEGORY, localeId, FeesConstants.CATAGORY,
				"org.mifos.application.fees.util.valueobjects.CategoryType",
				"categoryId"));
		context.addAttribute(masterDataRetriever.retrieveMasterData(
				FeesConstants.FEEPAYMENT, localeId, FeesConstants.PAYMENT,
				"org.mifos.application.fees.util.valueobjects.FeePayment",
				"feePaymentId"));
		context.addAttribute(masterDataRetriever.retrieveMasterData(
				FeesConstants.FEEFORMULA, localeId, FeesConstants.FORMULA,
				"org.mifos.application.fees.util.valueobjects.FeeFormula",
				"feeFormulaId"));
		context.addAttribute(masterDataRetriever.retrieveMasterData(
				FeesConstants.FEESTATUS, localeId, FeesConstants.STATUS,
				"org.mifos.application.fees.util.valueobjects.FeeStatus",
				"statusId"));
		// context.addAttribute(masterDataRetriever )
		// context.addAttribute(createSearchResultsObj("glCodeList",getGLCodes()));
		context.addAttribute(this.getSearchResults("glCodeList", getGLCodes()));
		Session session = null;
		try {
			session = HibernateUtil.getSession();
			Query query = session.getNamedQuery("masterdata.feepayment");
			query.setShort("localeId", localeId);
			query.setShort("paymentId",
					org.mifos.application.fees.util.helpers.FeePayment.UPFRONT
							.getValue());
			List<FeePaymentMaster> viewFeePaymentForLoan = query.list();
			context.addBusinessResults("feePaymentForLoan",
					viewFeePaymentForLoan);
		} catch (HibernateProcessException e) {
			throw new SystemException(e);
		} finally {
			HibernateUtil.closeSession(session);
		}
	}

	public Fees getFees(Short feesId) throws SystemException,
			ApplicationException {
		Session session = null;
		Fees fees = null;
		try {
			session = HibernateUtil.getSession();
			fees = (Fees) session.get(Fees.class, feesId);
			initializeMeetings(fees);
		} catch (HibernateProcessException e) {
			throw new SystemException(e);
		} finally {
			HibernateUtil.closeSession(session);
		}

		return fees;
	}

	private void initializeMeetings(Fees fees) {

		if (fees.getFeeFrequency().getFeeFrequencyTypeId().equals(
				FeeFrequencyType.PERIODIC.getValue())) {
			Meeting meeting = fees.getFeeFrequency().getFeeMeetingFrequency();
			meeting.getMeetingType().getMeetingPurpose();
		}

	}

	public Fees updateFees(Fees fees) throws SystemException,
			ApplicationException {
		Session session = null;
		Fees retrivedFees = null;
		Transaction tx = null;
		try {
			session = HibernateUtil.getSession();
			retrivedFees = getFeeObject(fees.getFeeId());
			retrivedFees.setStatus(fees.getStatus()); // if fee becomes
			// inactive ,
			// validations
			retrivedFees.setRateOrAmount(fees.getRateOrAmount());
			if (retrivedFees.getRateFlatFalg().equals(
					RateAmountFlag.RATE.getValue()))
				retrivedFees.setRate(fees.getRateOrAmount());
			else
				retrivedFees.setFeeAmount(new Money(fees.getRateOrAmount()
						.toString()));
			if (null != fees.getFormulaId() && fees.getFormulaId() == 0) {
				fees.setFormulaId(null);
			}
			retrivedFees.setFormulaId(fees.getFormulaId());
			retrivedFees.setFeeFrequency(null);

			tx = session.beginTransaction();
			session.update(retrivedFees);
			tx.commit();
		} catch (StaleObjectStateException sOSE) {
			tx.rollback();
			throw new ApplicationException(
					FeesConstants.VERSIONNOMATCHINGPROBLEM);
		} catch (HibernateProcessException e) {
			tx.rollback();
			throw new SystemException(e);
		} finally {
			HibernateUtil.closeSession(session);
		}

		return fees;
	}

	public Fees getFeeObject(Short feeId) throws SystemException {
		Session session = null;
		try {
			session = HibernateUtil.getSession();
			return (Fees) session.get(Fees.class, feeId);

		} catch (HibernateProcessException e) {
			throw new SystemException(e);
		} finally {
			HibernateUtil.closeSession(session);
		}

	}

	public void getFeesData(Context context) throws SystemException {
		Session session = null;
		try {
			session = HibernateUtil.getSession();
			Query query = session.getNamedQuery("viewproductfees");
			query.setShort("localeId", context.getUserContext().getLocaleId());

			List<ViewFees> viewProductFees = query.list();

			context.addBusinessResults("productfeesData", viewProductFees);

			query = session.getNamedQuery("viewclientfees");
			query.setShort("localeId", context.getUserContext().getLocaleId());

			List<ViewFees> viewClientFees = query.list();

			context.addBusinessResults("clientfeesData", viewClientFees);

		} catch (HibernateProcessException e) {
			throw new SystemException(e);
		} finally {
			HibernateUtil.closeSession(session);
		}

	}

	/**
	 * This method creates a new SearchResults object with values as passed in
	 * parameters
	 * 
	 * @param resultName
	 *            the name with which framework will put resultvalue in request
	 * @param resultValue
	 *            that need to be put in request
	 * @return SearchResults instance
	 */
	private SearchResults getSearchResults(String resultName, Object value) {
		SearchResults searchResults = new SearchResults();
		searchResults.setResultName(resultName);
		searchResults.setValue(value);
		return searchResults;
	}

	private List getGLCodes() throws SystemException, ApplicationException {
		List<GLCodeEntity> glCodeList = new ArrayList<GLCodeEntity>();
		GLCodeEntity glCode;
		FinancialActionBO finActionFees = FinancialActionCache
				.getFinancialAction(FinancialActionConstants.FEEPOSTING);
		Set<COABO> applicableCreditCategory = finActionFees
				.getApplicableCreditCOA();
		for (COABO coabo : applicableCreditCategory) {
			glCodeList.add(coabo.getAssociatedGlcode());
		}
		return glCodeList;
	}

}
