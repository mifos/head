package org.mifos.accounts.fees.servicefacade;

import javax.servlet.http.HttpServletRequest;

import org.mifos.accounts.fees.struts.actionforms.FeeActionForm;
import org.mifos.accounts.fees.util.helpers.FeeConstants;
import org.mifos.config.AccountingRules;
import org.mifos.framework.exceptions.PropertyNotFoundException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.security.util.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.binding.message.MessageBuilder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.webflow.core.collection.SharedAttributeMap;
import org.springframework.webflow.engine.Transition;
import org.springframework.webflow.execution.RequestContext;

@Controller
public class FeeController {

    @Autowired
    private FeeServiceFacade feeServiceFacade;

    public FeeController() {
    }

    public FeeController(FeeServiceFacade feeServiceFacade) {
        this.feeServiceFacade = feeServiceFacade;
    }

    @RequestMapping("/viewFees.ftl")
    public String viewFees(ModelMap model, HttpServletRequest request) throws Exception {
        model.addAttribute(FeeConstants.CUSTOMER_FEES, feeServiceFacade.getCustomerFees());
        model.addAttribute(FeeConstants.PRODUCT_FEES, feeServiceFacade.getProductFees());
        return "viewFees";
    }

    /**
     * to be used from a webflow context
     * @param requestForm
     * @param requestContext
     * @return
     */
    public String createFee(FeeActionForm requestForm, RequestContext requestContext) {
        UserContext userCtx = (UserContext) requestContext.getExternalContext().getSessionMap().get(Constants.USER_CONTEXT_KEY);
        FeeCreateRequest feeCreateRequest;
        try {
            feeCreateRequest = new FeeCreateRequest(
                    requestForm.getCategoryTypeValue(),
                    requestForm.getFeeFrequencyTypeValue(),
                    requestForm.getGlCodeValue(),
                    requestForm.getFeePaymentTypeValue(),
                    requestForm.getFeeFormulaValue(),
                    requestForm.getFeeName(),
                    requestForm.isRateFee(),
                    requestForm.isCustomerDefaultFee(),
                    requestForm.getRateValue(),
                    requestForm.getCurrencyId(),
                    requestForm.getAmount(),
                    requestForm.getFeeRecurrenceTypeValue(),
                    requestForm.getMonthRecurAfterValue(),
                    requestForm.getWeekRecurAfterValue());
            FeeDto feeDto = feeServiceFacade.createFee(feeCreateRequest, userCtx);
            requestForm.setFeeId(feeDto.getId().toString());
        } catch (PropertyNotFoundException e) {
            requestContext.getMessageContext().addMessage(
                   new MessageBuilder().error().source("feeDefinition").code("fees.error.feeDefinitionFailure").
                       defaultText("Error occurred while defining new fee. Property Not found Exception!").build());
            return "feeDefinitionFailure";
        } catch (ServiceException e) {
            requestContext.getMessageContext().addMessage(
                    new MessageBuilder().error().source("feeDefinition").code("fees.error.feeDefinitionFailure").
                        defaultText("Error occurred while defining new fee. Service Exception key:!" + e.getKey()).build());
             return "feeDefinitionFailure";
        }
        return "feeDefinitionSuccess";
    }

    private UserContext getUserContext(HttpServletRequest request) {
        return  (UserContext) SessionUtils.getAttribute(Constants.USER_CONTEXT_KEY, request.getSession());
    }




}
