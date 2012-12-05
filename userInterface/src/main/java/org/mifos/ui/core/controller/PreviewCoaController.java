package org.mifos.ui.core.controller;

import org.apache.commons.lang.StringUtils;
import org.mifos.application.admin.servicefacade.CoaDto;
import org.mifos.application.admin.servicefacade.CoaServiceFacade;
import org.mifos.service.BusinessRuleException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/" + PreviewCoaController.PREVIEW_COA)
@SessionAttributes("formBean")
public class PreviewCoaController {
    private static final String REDIRECT_TO_COA_ADMIN_SCREEN = "redirect:/coaAdmin.ftl";
    private static final String CANCEL_PARAM = "CANCEL";
    private static final String EDIT_PARAM = "EDIT";
    public static final String PREVIEW_COA = "previewCoa";
    private static final String DEFINE_NEW_COA = "defineNewCoa";
    
    private CoaServiceFacade coaServiceFacade;

    @RequestMapping(method = RequestMethod.POST)
    public ModelAndView processFormSubmit(@RequestParam(value = EDIT_PARAM, required = false) String edit,
                                    @RequestParam(value = CANCEL_PARAM, required = false) String cancel,
                                    @ModelAttribute("formBean") CoaFormBean formBean,
                                    BindingResult result,
                                    SessionStatus status) {

        String viewName = REDIRECT_TO_COA_ADMIN_SCREEN;

        ModelAndView modelAndView = new ModelAndView();
        if (StringUtils.isNotBlank(edit)) {
            viewName = DEFINE_NEW_COA;
            modelAndView.setViewName(viewName);
            modelAndView.addObject("formBean", formBean);
        } else if (StringUtils.isNotBlank(cancel)) {
            modelAndView.setViewName(REDIRECT_TO_COA_ADMIN_SCREEN);
            status.setComplete();
        } else if (result.hasErrors()) {
            modelAndView.setViewName(PREVIEW_COA);
        } else {
            try {
                CoaDto coaDto = new CoaDto();
                coaDto.setAccountName(formBean.getCoaName());
                coaDto.setGlCodeString(formBean.getGlCode());
                coaDto.setParentId(formBean.getParentId());
                coaServiceFacade.create(coaDto);
                viewName = REDIRECT_TO_COA_ADMIN_SCREEN;
                modelAndView.setViewName(viewName);
                status.setComplete();
            } catch (BusinessRuleException e) {
                ObjectError error = new ObjectError("formBean", new String[] { e.getMessageKey() }, new Object[] {},  "default: ");
                result.addError(error);
                modelAndView.setViewName(PREVIEW_COA);
                modelAndView.addObject("formBean", formBean);
            } 
        }
        return modelAndView;
    }

    @Autowired
    public void setCoaServiceFacade(CoaServiceFacade coaServiceFacade) {
        this.coaServiceFacade = coaServiceFacade;
    }

}
