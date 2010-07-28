package org.mifos.ui.core.controller;

import org.mifos.accounts.fund.servicefacade.FundDto;
import org.mifos.accounts.fund.servicefacade.FundServiceFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("fundPreview")
@SessionAttributes("formBean")
public class ViewFundsPreviewController {
    private static final String REDIRECT_TO_ADMIN_SCREEN = "redirect:/AdminAction.do?method=load";
    private static final String REDIRECT_TO_VIEW_FUNDS = "redirect:/viewFunds.ftl";
    private static final String CANCEL_PARAM = "CANCEL";
    private static final String CANCEL_PARAM_VALUE = "Cancel";
    private static final String EDIT_PARAM = "EDIT";
    private static final String EDIT_PARAM_VALUE = "Edit fund information";

    @Autowired
    private FundServiceFacade fundServiceFacade;

    protected ViewFundsPreviewController(){
        //spring auto wiring
    }

    public ViewFundsPreviewController(final FundServiceFacade fundServiceFacade){
        this.fundServiceFacade=fundServiceFacade;
    }

    @RequestMapping(method = RequestMethod.POST)
    public ModelAndView processFormSubmit(@RequestParam(value = EDIT_PARAM, required = false) String edit,
            @RequestParam(value = CANCEL_PARAM, required = false) String cancel,
            FundFormBean formBean,
            BindingResult result,
            SessionStatus status){
       String viewName=REDIRECT_TO_ADMIN_SCREEN;
       ModelAndView modelAndView=new ModelAndView();
       if (EDIT_PARAM_VALUE.equals(edit)) {
           viewName="editFunds";
           modelAndView.setViewName(viewName);
           modelAndView.addObject("formBean",formBean);
       }
       else if (CANCEL_PARAM_VALUE.equals(cancel)) {
           viewName=REDIRECT_TO_VIEW_FUNDS;
           modelAndView.setViewName(viewName);
           status.setComplete();
       } else if(result.hasErrors()){
           viewName="fundPreview";
           modelAndView.setViewName(viewName);
           modelAndView.addObject("formBean",formBean);
       }else {
           Integer fundCode=Integer.parseInt(formBean.getId());
           FundDto fundUpdate=fundServiceFacade.getFund(fundCode.shortValue());
           fundUpdate.setName(formBean.getName());
           this.fundServiceFacade.updateFund(fundUpdate);
           viewName=REDIRECT_TO_ADMIN_SCREEN ;
           modelAndView.setViewName(viewName);
           status.setComplete();
       }
       return modelAndView;
   }
}
