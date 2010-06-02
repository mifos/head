package org.mifos.accounts.fees.servicefacade;

import javax.servlet.http.HttpServletRequest;

import org.mifos.config.AccountingRules;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.security.util.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class FeeController {

    @Autowired
    private FeeServiceFacade feeServiceFacade;

    public FeeController() {
    }

    public FeeController(FeeServiceFacade feeServiceFacade) {
        this.feeServiceFacade = feeServiceFacade;
    }

    @RequestMapping("/Fees.ftl")
    public String viewFees(HttpServletRequest request) throws Exception {
        feeServiceFacade.getFeeParameters(getUserContext(request).getLocaleId());
        return "Fees";
    }

    //@RequestMapping(value="/defineFee.ftl", method = RequestMethod.GET)
    public String defineFee(ModelMap model, HttpServletRequest request) throws Exception {
        model.addAttribute("isMultiCurrencyEnabled", AccountingRules.isMultiCurrencyEnabled());
        model.addAttribute("currencies", AccountingRules.getCurrencies());
        model.addAttribute("FeeParameters", feeServiceFacade.getFeeParameters(getUserContext(request).getLocaleId()));
        //org.springframework.webflow.mvc.servlet.FlowController
        return "defineFee";
    }

    //@RequestMapping("/previewFee.ftl")
    public String previewFee() {
        return "previewFee";
    }

    //@RequestMapping(value="/createFee.ftl",method = RequestMethod.POST)
    public String createFee(@ModelAttribute("FeeDto") FeeCreateRequest request,
            BindingResult result) {
        return "defineFeeSuccess";
    }

    private UserContext getUserContext(HttpServletRequest request) {
        return  (UserContext) SessionUtils.getAttribute(Constants.USER_CONTEXT_KEY, request.getSession());
    }


}
