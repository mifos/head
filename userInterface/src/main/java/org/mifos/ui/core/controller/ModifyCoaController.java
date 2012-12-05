package org.mifos.ui.core.controller;

import javax.validation.Valid;

import org.apache.commons.lang.StringUtils;
import org.mifos.application.admin.servicefacade.CoaDto;
import org.mifos.application.admin.servicefacade.CoaServiceFacade;
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
@RequestMapping(value = "/" + ModifyCoaController.MODIFY_COA)
@SessionAttributes("formBean")
public class ModifyCoaController {

    private CoaServiceFacade coaServiceFacade;
    private static final String REDIRECT_TO_COA_ADMIN_SCREEN = "redirect:/coaAdmin.ftl";
    private static final String CANCEL_PARAM = "CANCEL";
    public static final String MODIFY_COA = "modifyCoa";
    private static final String PREVIEW_MODIFY_COA = "previewModifyCoa";
    
    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView showForm(@RequestParam(value = "id", required=true) Short id) {
        
        ModelAndView modelAndView = new ModelAndView(MODIFY_COA);
        
        CoaDto coaDto = coaServiceFacade.getCoaDTO(id);
        
        CoaFormBean formBean = new CoaFormBean();
        formBean.setCoaName(coaDto.getAccountName());
        formBean.setGlCode(coaDto.getGlCodeString());
        formBean.setAccountId(id);
        formBean.setParentGlCode(coaDto.getParentGlCode());
        
        modelAndView.addObject("formBean", formBean);
        modelAndView.addObject("COAlist", coaServiceFacade.getList(null));
        
        return modelAndView;
    }
    
    @RequestMapping(method = RequestMethod.POST)
    public ModelAndView processFormSubmit(@RequestParam(value = CANCEL_PARAM, required = false) String cancel,
                                    @Valid @ModelAttribute("formBean") CoaFormBean formBean,
                                    BindingResult result,
                                    SessionStatus status) {

        ModelAndView mav = new ModelAndView(REDIRECT_TO_COA_ADMIN_SCREEN);

        if (StringUtils.isNotBlank(cancel)) {
            status.setComplete();
        } else if (result.hasErrors()) {
            mav = new ModelAndView(MODIFY_COA);
        } else {
            mav = new ModelAndView(PREVIEW_MODIFY_COA);
            mav.addObject("formBean", formBean);
        }

        return mav;
    }
    
    @Autowired
    public void setCoaServiceFacade(CoaServiceFacade coaServiceFacade) {
        this.coaServiceFacade = coaServiceFacade;
    }
}
