package org.mifos.platform.rest.controller;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.mifos.accounts.servicefacade.AccountServiceFacade;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.persistence.CustomerDao;
import org.mifos.customers.personnel.persistence.PersonnelDao;
import org.mifos.dto.domain.ApplicableCharge;
import org.mifos.framework.util.helpers.Money;
import org.mifos.platform.rest.controller.RESTAPIHelper.ErrorMessage;
import org.mifos.platform.rest.controller.validation.ParamValidationException;
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
                                    @RequestParam BigDecimal amount,
                                    @RequestParam Short feeId) throws Exception {

	    validateAmount(amount);

	    Map<String, String> map = new HashMap<String, String>();

		MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		CustomerBO customerBO = this.customerDao.findCustomerBySystemId(globalCustNum);
		Integer accountId = customerBO.getCustomerAccount().getAccountId();

		Money totalDueBeforeCharge = customerBO.getCustomerAccount().getTotalAmountDue();

    	this.accountServiceFacade.applyCharge(accountId, feeId, amount.doubleValue());

    	DateTime today = new DateTime();

		map.put("status", "success");
		map.put("clientName", customerBO.getDisplayName());
        map.put("clientNumber", customerBO.getGlobalCustNum());
        map.put("chargeDate", today.toLocalDate().toString());
        map.put("chargeTime", today.toLocalTime().toString());
        map.put("chargeAmount", Double.valueOf(amount.doubleValue()).toString());
        map.put("chargeMadeBy", personnelDao.findPersonnelById((short) user.getUserId()).getDisplayName());
        map.put("totalDueBeforeCharge", totalDueBeforeCharge.toString());
        map.put("totalDueAfterCharge", customerBO.getCustomerAccount().getTotalAmountDue().toString());

    	return map;
    }

    @RequestMapping(value = "/customer/fees/num-{globalCustNum}", method = RequestMethod.GET)
    public @ResponseBody
    Map<String, String> getApplicableFees(@PathVariable String globalCustNum) throws Exception {
    	CustomerBO customerBO = this.customerDao.findCustomerBySystemId(globalCustNum);
		Integer accountId = customerBO.getCustomerAccount().getAccountId();
		List<ApplicableCharge> applicableCharges = this.accountServiceFacade.getApplicableFees(accountId);

    	Map<String, String> map = new HashMap<String, String>();

    	for (ApplicableCharge applicableCharge : applicableCharges ){
    		map.put(applicableCharge.getFeeName(), applicableCharge.getFeeId());
    	}

    	return map;
    }

    private void validateAmount(BigDecimal amount) throws ParamValidationException {
        if (amount != null && amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ParamValidationException(ErrorMessage.NON_NEGATIVE_AMOUNT);
        }
    }
}
