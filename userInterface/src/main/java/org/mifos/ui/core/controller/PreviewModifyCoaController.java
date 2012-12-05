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
@RequestMapping("/" + PreviewModifyCoaController.PREVIEW_MODIFY_COA)
@SessionAttributes("formBean")
public class PreviewModifyCoaController {

    private CoaServiceFacade coaServiceFacade;
    
    private static final String REDIRECT_TO_COA_ADMIN_SCREEN = "redirect:/coaAdmin.ftl";
    private static final String CANCEL_PARAM = "CANCEL";
    private static final String EDIT_PARAM = "EDIT";
    public static final String PREVIEW_MODIFY_COA = "previewModifyCoa";
    private static final String MODIFY_COA = "modifyCoa";
    
    @RequestMapping(method = RequestMethod.POST)
    public ModelAndView processFormSubmit(@RequestParam(value = EDIT_PARAM, required = false) String edit,
                                    @RequestParam(value = CANCEL_PARAM, required = false) String cancel,
                                    @ModelAttribute("formBean") CoaFormBean formBean,
                                    BindingResult result,
                                    SessionStatus status){

        ModelAndView mav = new ModelAndView(REDIRECT_TO_COA_ADMIN_SCREEN);
        
        if (StringUtils.isNotBlank(edit)) {
            mav = new ModelAndView(MODIFY_COA);
            mav.addObject("formBean", formBean);
            mav.addObject("COAlist", coaServiceFacade.getList(null));
        } else if (StringUtils.isNotBlank(cancel)) {
            status.setComplete();
        } else if (result.hasErrors()) {
            mav = new ModelAndView(PREVIEW_MODIFY_COA);
        } else {
            try {
                CoaDto coaDto = new CoaDto();
                coaDto.setAccountId(formBean.getAccountId());
                coaDto.setAccountName(formBean.getCoaName());
                coaDto.setGlCodeString(formBean.getGlCode());
                coaDto.setParentGlCode(formBean.getParentGlCode());
                coaServiceFacade.modify(coaDto);
                status.setComplete();
            } catch (BusinessRuleException ex) {
                ObjectError error = new ObjectError("formBean", new String[] { ex.getMessageKey() },
                        new Object[] {},  "default: ");
                result.addError(error);
                mav.setViewName(PREVIEW_MODIFY_COA);
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
