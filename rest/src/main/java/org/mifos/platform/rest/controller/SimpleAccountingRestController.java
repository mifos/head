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
package org.mifos.platform.rest.controller;


import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.joda.time.DateTime;
import org.mifos.application.accounting.business.GlDetailBO;
import org.mifos.application.accounting.business.GlMasterBO;
import org.mifos.application.servicefacade.AccountingServiceFacade;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.platform.rest.controller.RESTAPIHelper.ErrorMessage;
import org.mifos.platform.rest.controller.validation.ParamValidationException;
import org.mifos.security.util.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import freemarker.core.ParseException;

@Controller
public class SimpleAccountingRestController {
	
	
    
    @Autowired 
    private AccountingServiceFacade accountingservicefacade;
     
   @RequestMapping(value = "/accounting/savingeneralledger", method = RequestMethod.GET)
   public @ResponseBody
    boolean getSavingAccountingTranctions(@RequestParam String transactionDate,
											    		  @RequestParam String transactionType,
											              @RequestParam String officeLevel,
											              @RequestParam String officeId,
											              @RequestParam String mainAccount,
											              @RequestParam String transactionAmount,
											              @RequestParam String subAccount,
											              @RequestParam(required=false) String chequeNo,
											              @RequestParam(required=false) String chequeDate,
											              @RequestParam(required=false) String bankName,
											              @RequestParam(required=false) String bankBranch,
											              @RequestParam String transactionNarration,
											              HttpServletRequest request) throws Exception {
	   List<String> amountActionList = getAmountAction(transactionType);

	   List<GlDetailBO> glDetailBOList = getGlDetailBOList(subAccount,transactionAmount,chequeNo,chequeDate,bankName,bankBranch,amountActionList);
	   GlMasterBO glMasterBO=new GlMasterBO();
		glMasterBO.setTransactionDate(DateUtils.getDate(transactionDate));
		glMasterBO.setTransactionType(transactionType);//
		glMasterBO.setAmountAction(amountActionList.get(0));//
		glMasterBO.setFromOfficeLevel(new Integer(officeLevel));
		glMasterBO.setFromOfficeId(officeId);
		glMasterBO.setToOfficeLevel(new Integer(officeLevel));
		glMasterBO.setToOfficeId(officeId);
		glMasterBO.setMainAccount(mainAccount);
		glMasterBO.setTransactionAmount(new BigDecimal(transactionAmount));
		glMasterBO.setTransactionNarration(transactionNarration);
		glMasterBO.setGlDetailBOList(glDetailBOList);
		glMasterBO.setStatus("");// default value
		glMasterBO.setTransactionBy(0); // default value
		glMasterBO.setCreatedBy(((UserContext) SessionUtils.getAttribute(Constants.USER_CONTEXT_KEY, request.getSession())).getId());
		glMasterBO.setCreatedDate(DateUtils.getCurrentDateWithoutTimeStamp());
	    
        return accountingservicefacade.savingAccountingTransactions(glMasterBO);
    }   
   
   public List<String> getAmountAction(String transactionType) {
		List<String> amountActionList = new ArrayList<String>();

		if (transactionType.equals("CR")
				|| transactionType.equals("BR")|| transactionType.equals("JV")) {
			amountActionList.add("debit");// for MainAccount amountAction
			amountActionList.add("credit");// for SubAccount amountAction
		} else if (transactionType.equals("CP")
				|| transactionType.equals("BP")) {
			amountActionList.add("credit");// for MainAccount amountAction
			amountActionList.add("debit");// for SubAccount amountAction
		}

		return amountActionList;
	}
   
	List<GlDetailBO> getGlDetailBOList(String subAccount,String transactionAmount,String chequeNo,String chequeDate,String bankName,String bankBranch,
			List<String> amountActionList) {
		List<GlDetailBO> glDetailBOList = new ArrayList<GlDetailBO>();
		glDetailBOList.add(new GlDetailBO(subAccount,
				new BigDecimal(transactionAmount),
				amountActionList.get(1), chequeNo, DateUtils
						.getDate(chequeDate), bankName, bankBranch));
		return glDetailBOList;
	}
	
	
   
	 private void validateTransactionAmount(BigDecimal transactionAmount) throws ParamValidationException {
	        if (transactionAmount != null && transactionAmount.compareTo(BigDecimal.ZERO) <= 0) {
	            throw new ParamValidationException(ErrorMessage.NON_NEGATIVE_AMOUNT);
	        }
	    }
   
    public void validateTransactionsDate(DateTime date) throws ParamValidationException {
    	DateTime today = new DateTime();
    	if (date.isAfter(today)){
    		throw new ParamValidationException(ErrorMessage.FUTURE_DATE);
    	}
    }

   
}



