package org.mifos.ui.core.controller;

import javax.validation.Valid;

import org.apache.commons.lang.StringUtils;
import org.mifos.application.admin.servicefacade.CoaDto;
import org.mifos.application.admin.servicefacade.CoaServiceFacade;
import org.mifos.service.BusinessRuleException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;

@Controller
@SessionAttributes("formBean")
@RequestMapping(value = "/" + DeleteCoaController.DELETE_COA)
public class DeleteCoaController {
    
    private CoaServiceFacade coaServiceFacade;
    private static final String REDIRECT_TO_COA_ADMIN_SCREEN = "redirect:/coaAdmin.ftl";
    private static final String CANCEL_PARAM = "CANCEL";
    public static final String DELETE_COA = "deleteCoa";
    
    @ModelAttribute("formBean")
    @RequestMapping(method = RequestMethod.GET)
    public CoaFormBean showForm(@RequestParam(value = "id", required=true) Short id) {
        
        CoaDto coaDto = coaServiceFacade.getCoaDTO(id);
        
        CoaFormBean formBean = new CoaFormBean();
        formBean.setCoaName(coaDto.getAccountName());
        formBean.setGlCode(coaDto.getGlCodeString());
        formBean.setAccountId(id);
        
        return formBean;
    }
    
    
    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.setValidator(new CoaFormValidator());
    }
    
    @RequestMapping(method = RequestMethod.POST)
    public ModelAndView processFormSubmit(@RequestParam(value = CANCEL_PARAM, required = false) String cancel,
                                    @Valid @ModelAttribute("formBean") CoaFormBean formBean,
                                    BindingResult result,
                                    SessionStatus status) {

        ModelAndView mav = new ModelAndView(REDIRECT_TO_COA_ADMIN_SCREEN);

        if (StringUtils.isNotBlank(cancel)) {
            status.setComplete();
        } else {
            try {
                coaServiceFacade.delete(formBean.getAccountId());
                status.setComplete();
            } catch (BusinessRuleException ex) {
                ObjectError error = new ObjectError("formBean", new String[] { ex.getMessageKey() },
                        new Object[] {},  "default: ");
                result.addError(error);
                mav.setViewName(DELETE_COA);
                mav.addObject("formBean", formBean);
            }
        }
        return mav;
    }
    
    @Autowired
    public void setCoaServiceFacade(CoaServiceFacade coaServiceFacade) {
        this.coaServiceFacade = coaServiceFacade;
    }
}
