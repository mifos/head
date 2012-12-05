package org.mifos.ui.core.controller;

import javax.validation.Valid;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
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
@RequestMapping("/" + DefineNewCoaController.DEFINE_NEW_COA)
@SessionAttributes("formBean")
public class DefineNewCoaController {

    private static final String REDIRECT_TO_COA_ADMIN_SCREEN = "redirect:/coaAdmin.ftl";
    private static final String CANCEL_PARAM = "CANCEL";
    private static final String PREVIEW_COA = "previewCoa";
    public static final String DEFINE_NEW_COA = "defineNewCoa";
    
    @ModelAttribute("formBean")
    @RequestMapping(method = RequestMethod.GET)
    public CoaFormBean showForm(@RequestParam(value = "parentId", required=true) Short parentId) {
        CoaFormBean coaFormBean = new CoaFormBean();
        coaFormBean.setParentId(parentId);
        return coaFormBean;
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
        } else if (result.hasErrors()) {
            mav = new ModelAndView(DEFINE_NEW_COA);
        } else {
            mav = new ModelAndView(PREVIEW_COA);
            mav.addObject("formBean", formBean);
        }

        return mav;
    }
    
}
