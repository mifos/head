package org.mifos.platform.rest.controller;

import java.util.HashMap;
import java.util.Map;

import org.joda.time.DateTime;
import org.mifos.accounts.servicefacade.AccountServiceFacade;
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
import org.springframework.web.bind.annotation.RequestParam;
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
    Map<String, String> applyCharge(@PathVariable String globalCustNum,
                                    @RequestParam(value="amount", required = false) String amountString,
                                    @RequestParam(value="feeId", required = false) String feeIdString) throws Exception {

    	Map<String, String> map = new HashMap<String, String>();
    	Double chargeAmount = null;
    	Short feeId = null;
    	boolean validationPassed = true;

    	//validation
    	try {
    		chargeAmount = Double.parseDouble(amountString);
    	} catch (Exception e){
    		map.put("amount", "please specify correct");
    		validationPassed = false;
    	}
    	try {
    		feeId = Short.parseShort(feeIdString);
    	} catch (Exception e){
    		map.put("feeId", "please specify correct");
    		validationPassed = false;
    	}
		if ( chargeAmount != null && chargeAmount <= 0 ){
    		map.put("amount", "must be greater than 0");
    		validationPassed = false;
    	}
        if (!validationPassed){
        	map.put("status", "error");
        	return map;
        }

		MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		CustomerBO customerBO = this.customerDao.findCustomerBySystemId(globalCustNum);
		Integer accountId = customerBO.getCustomerId();

		Money totalDueBeforeCharge = customerBO.getCustomerAccount().getTotalAmountDue();

    	this.accountServiceFacade.applyCharge(accountId, feeId, chargeAmount);

    	DateTime today = new DateTime();

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
