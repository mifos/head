package org.mifos.platform.rest.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.joda.time.DateTime;
import org.mifos.accounts.servicefacade.AccountServiceFacade;
import org.mifos.core.MifosRuntimeException;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.persistence.CustomerDao;
import org.mifos.customers.personnel.persistence.PersonnelDao;
import org.mifos.framework.util.helpers.Money;
import org.mifos.security.MifosUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class CustomerRESTController {

	@Autowired
    private AccountServiceFacade accountServiceFacade;
	
    @Autowired
    private CustomerDao customerDao;
    
    @Autowired
    private PersonnelDao personnelDao;
	
	@RequestMapping(value = "/customer/charge/num-{globalCustNum}", method = RequestMethod.POST)
    public @ResponseBody
    Map<String, String> applyCharge(@PathVariable String globalCustNum, HttpServletRequest request) throws Exception {
    	String amountString = request.getParameter("amount");
    	String feeIdString = request.getParameter("feeId");
    	
    	Double chargeAmount = Double.parseDouble(amountString);
    	Short feeId = Short.parseShort(feeIdString);
    	
		if ( chargeAmount <= 0 ){
    		throw new MifosRuntimeException("Amount must be greater than 0");
    	}
    	
		MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		CustomerBO customerBO = this.customerDao.findCustomerBySystemId(globalCustNum);
		Integer accountId = customerBO.getCustomerId();
		
		Money totalDueBeforeCharge = customerBO.getCustomerAccount().getTotalAmountDue();
		
    	this.accountServiceFacade.applyCharge(accountId, feeId, chargeAmount);
    	
    	DateTime today = new DateTime();
    	
    	Map<String, String> map = new HashMap<String, String>();
    	
		map.put("status", "success");
		map.put("clientName", customerBO.getDisplayName());
        map.put("clientNumber", customerBO.getGlobalCustNum());
        map.put("chargeDate", today.toLocalDate().toString());
        map.put("chargeTime", today.toLocalTime().toString());
        map.put("chargeAmount", Double.toString(chargeAmount));
        map.put("chargeMadeBy", personnelDao.findPersonnelById((short) user.getUserId()).getDisplayName());
        map.put("totalDueBeforeCharge", totalDueBeforeCharge.toString());
        map.put("totalDueAfterCharge", customerBO.getCustomerAccount().getTotalAmountDue().toString());
        
    	return map;
    }
}
