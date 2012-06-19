/*
 * Copyright (c) 2005-2011 Grameen Foundation USA
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 * explanation of the license and how it is applied.
 */

package org.mifos.accounting.struts.actionform;

import java.util.Calendar;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.mifos.application.accounting.util.helpers.SimpleAccountingConstants;
import org.mifos.application.admin.servicefacade.InvalidDateException;
import org.mifos.framework.struts.actionforms.BaseActionForm;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.FilePaths;
import org.mifos.security.util.UserContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ViewGlTransactionsActionForm extends BaseActionForm {
    private static final Logger logger = LoggerFactory.getLogger(GeneralLedgerActionForm.class);

    private String trxnDate;

	public void setTrxnDate(String trxnDate){
		this.trxnDate=trxnDate;
	}
	
	public void setTrxnDate(java.util.Date date){
	    this.trxnDate=DateUtils.format(date);
	}

	public String getTrxnDate(){
	  return trxnDate;
	}

	public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
	        logger.debug("GeneralLedgerActionForm.validate");
//	        request.setAttribute(Constants.CURRENTFLOWKEY, request.getParameter(Constants.CURRENTFLOWKEY));
	        ActionErrors errors = new ActionErrors();

	        if (request.getParameter(SimpleAccountingConstants.METHOD).equalsIgnoreCase(
				SimpleAccountingConstants.SUBMIT)) {
	                return mandatoryCheck(getUserContext(request));

	        }
	        return errors;
	    }

	   private ActionErrors trxnDateValidate(ActionErrors errors, Locale locale) {
	        if (StringUtils.isNotBlank(getTrxnDate()) && !DateUtils.isValidDate(getTrxnDate())) {
	            ResourceBundle resources = ResourceBundle.getBundle(FilePaths.SIMPLE_ACCOUNTING_RESOURCE, locale);
	            String trxnDate = resources.getString(SimpleAccountingConstants.TRXNDATE);
	            errors.add(SimpleAccountingConstants.INVALID_TRXN_DATE, new ActionMessage(
				SimpleAccountingConstants.INVALID_TRXN_DATE, trxnDate));
	        }
	        return errors;
	    }


	   private ActionErrors mandatoryCheck(UserContext userContext) {
	        Locale locale = userContext.getPreferredLocale();
	        ResourceBundle resources = ResourceBundle.getBundle(FilePaths.SIMPLE_ACCOUNTING_RESOURCE, locale);

	        String trxn_Date = resources.getString(SimpleAccountingConstants.TRXNDATE);

	        ActionErrors errors = new ActionErrors();
	        java.sql.Date currentDate = null;
	        try {
	            currentDate = DateUtils.getLocaleDate(userContext.getPreferredLocale(), DateUtils
	                    .getCurrentDate(userContext.getPreferredLocale()));
	        } catch (InvalidDateException ide) {
	            errors.add(SimpleAccountingConstants.INVALIDDATE, new ActionMessage(
				SimpleAccountingConstants.INVALIDDATE));
	        }

	        java.sql.Date trxnDate = null;

	        if (getTrxnDate() ==null || "".equals(getTrxnDate())) {
			 errors.add(SimpleAccountingConstants.MANDATORYENTER, new ActionMessage(
					 SimpleAccountingConstants.MANDATORYENTER, trxn_Date));
	        }
	        else if(getTrxnDate() != null && !getTrxnDate().equals("") && !DateUtils.isValidDate(getTrxnDate())){
			 errors=trxnDateValidate(errors, locale);
	        }
	        else if(DateUtils.isValidDate(getTrxnDate())){
			 try {
		                trxnDate = DateUtils.getDateAsSentFromBrowser(getTrxnDate());
		            } catch (InvalidDateException ide) {
		                errors.add(SimpleAccountingConstants.MANDATORYFIELDS, new ActionMessage(SimpleAccountingConstants.INVALID_TRXN_DATE,trxn_Date));
		            }
			 if(trxnDate.compareTo(currentDate)>0){
				 errors.add(SimpleAccountingConstants.INVALID_FUTURE, new ActionMessage(
						 SimpleAccountingConstants.INVALID_FUTURE, trxn_Date));
			 }

	        }

	        return errors;
	    }






}
