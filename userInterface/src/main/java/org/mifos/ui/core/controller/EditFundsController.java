package org.mifos.ui.core.controller;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.mifos.accounts.fund.servicefacade.FundDto;
import org.mifos.accounts.fund.servicefacade.FundServiceFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/editFunds")
@SessionAttributes("formBean")
public class EditFundsController {
    private static final String REDIRECT_TO_VIEW_FUNDS = "redirect:/viewFunds.ftl";
    //private static final String REDIRECT_TO_ADMIN_SCREEN = "redirect:/AdminAction.do?method=load";
    private static final String TO_FUND_PREVIEW = "fundPreview";
    private static final String CANCEL_PARAM = "CANCEL";
    private static final String CANCEL_PARAM_VALUE = "Cancel";

    @Autowired
    private FundServiceFacade fundServiceFacade;
       protected EditFundsController(){
        //spring autowiring
    }

    public EditFundsController(final FundServiceFacade fundServiceFacade){
        this.fundServiceFacade = fundServiceFacade;
    }
    @ModelAttribute("breadcrumbs")
    public List<BreadCrumbsLinks> showBreadCrumbs() {
        return new AdminBreadcrumbBuilder().withLink("fundEdit", "fundEdit.ftl").build();
    }

    @RequestMapping(method = RequestMethod.GET)
    @ModelAttribute("formBean")
    public FundFormBean showFund(HttpServletRequest request) {
        Integer code=Integer.parseInt(request.getParameter("fundId"));
        //System.out.println("got fund id::::::::::::"+request.getParameter("fundId"));
        FundDto fundDto=fundServiceFacade.getFund(code.shortValue());
        //System.out.println("got dtoooooooooo"+fundDto.getId());
        //System.out.println("got dtoooooooooosssssssssssssssssssssss"+fundDto.getName());
        FundFormBean ff=new FundFormBean();
        ff.setCode(fundDto.getCode());
        ff.setId(fundDto.getId());
        ff.setName(fundDto.getName());
        /*FundFormBean formBean=new FundFormBean();
        formBean.setCode(fundDto.getCode());
        formBean.setId(formBean.getId());
        formBean.setName(formBean.getName());
        System.out.println("aname saodasdbnasldbasdbasdbaslvdlashdu"+formBean.getName());*/
     return ff;
    }

    @RequestMapping(method = RequestMethod.POST)
     public ModelAndView processFormSubmit(@RequestParam(value = CANCEL_PARAM, required = false) String cancel,
             @ModelAttribute("formBean") FundFormBean formBean,
                                        BindingResult result,
                                    SessionStatus status) {
        ModelAndView modelAndView=new ModelAndView(REDIRECT_TO_VIEW_FUNDS);
        if (CANCEL_PARAM_VALUE.equals(cancel)) {
            modelAndView.setViewName(REDIRECT_TO_VIEW_FUNDS);
            status.setComplete();
        } else if (result.hasErrors()) {
            modelAndView.setViewName("editFunds");
        } else {
            /*Integer fundCode=Integer.parseInt(formBean.getId());
            System.out.println("fund code is "+formBean.getId());
            System.out.println("fund name is "+formBean.getName());
            FundDto fundUpdate=fundServiceFacade.getFund(fundCode.shortValue());
            fundUpdate.setId(formBean.getId());
            fundUpdate.setName(formBean.getName());
            fundUpdate.setCode(formBean.getCode());
            this.fundServiceFacade.updateFund(fundUpdate);
            status.setComplete();*/
            modelAndView.setViewName(TO_FUND_PREVIEW);
            modelAndView.addObject("formBean",formBean);
        }
        return modelAndView;
    }
    }


