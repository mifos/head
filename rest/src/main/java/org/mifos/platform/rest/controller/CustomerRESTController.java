package org.mifos.platform.rest.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
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

	@RequestMapping(value = "/customer/num-{globalCustNum}/charge", method = RequestMethod.POST)
    public @ResponseBody
    Map<String, String> applyCharge(@PathVariable String globalCustNum,
                                    @RequestParam BigDecimal amount,
                                    @RequestParam Short feeId) throws Exception {

	    validateAmount(amount);
	    
        List<String> applicableFees = new ArrayList<String>();
        for (Map<String, String> feeMap : this.getApplicableFees(globalCustNum).values()) {
            applicableFees.add(feeMap.get("feeId"));
        }
        validateFeeId(feeId, applicableFees);
        
	    Map<String, String> map = new HashMap<String, String>();

		MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		CustomerBO customerBO = this.customerDao.findCustomerBySystemId(globalCustNum);
		Integer accountId = customerBO.getCustomerAccount().getAccountId();

		Money totalDueBeforeCharge = customerBO.getCustomerAccount().getTotalAmountDue();

    	this.accountServiceFacade.applyCharge(accountId, feeId, amount.doubleValue(), false);

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

    @RequestMapping(value = "/customer/num-{globalCustNum}/fees", method = RequestMethod.GET)
    public @ResponseBody
    Map<String, Map<String, String>> getApplicableFees(@PathVariable String globalCustNum) throws Exception {
    	CustomerBO customerBO = this.customerDao.findCustomerBySystemId(globalCustNum);
		Integer accountId = customerBO.getCustomerAccount().getAccountId();
		List<ApplicableCharge> applicableCharges = this.accountServiceFacade.getApplicableFees(accountId);

    	Map<String, Map<String, String>> map = new HashMap<String, Map<String, String>>();

    	for (ApplicableCharge applicableCharge : applicableCharges ){
            Map<String, String> feeMap = new HashMap<String, String>();
            feeMap.put("feeId", applicableCharge.getFeeId());
            feeMap.put("amountOrRate", applicableCharge.getAmountOrRate());
            feeMap.put("formula", applicableCharge.getFormula());
            feeMap.put("periodicity", applicableCharge.getPeriodicity());
            feeMap.put("paymentType", applicableCharge.getPaymentType());
            feeMap.put("isRateType", applicableCharge.getIsRateType());
    		map.put(applicableCharge.getFeeName(), feeMap);
    	}

    	return map;
    }

    private void validateAmount(BigDecimal amount) throws ParamValidationException {
        if (amount != null && amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ParamValidationException(ErrorMessage.NON_NEGATIVE_AMOUNT);
        }
    }
    
    public static void validateFeeId(Short feeId, List<String> applicableFees) throws ParamValidationException{
        if (!applicableFees.contains(Short.toString(feeId))){
                throw new ParamValidationException(ErrorMessage.INVALID_FEE_ID);
        }
     }
}
