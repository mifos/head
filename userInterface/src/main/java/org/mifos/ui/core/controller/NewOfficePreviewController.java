package org.mifos.ui.core.controller;

import org.apache.commons.lang.StringUtils;
import org.mifos.application.admin.servicefacade.OfficeServiceFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/previewOfficeDetails")
public class NewOfficePreviewController {

    @Autowired
    OfficeServiceFacade officeServiceFacade;

    protected NewOfficePreviewController() {
        // TODO Auto-generated constructor stub
    }

    public NewOfficePreviewController(final OfficeServiceFacade officeServiceFacade){
        this.officeServiceFacade=officeServiceFacade;
    }

    private static final String REDIRECT_TO_ADMIN_SCREEN = "redirect:/AdminAction.do?method=load";
    private static final String EDIT_PARAM = "EDIT";
    private static final String CANCEL_PARAM = "CANCEL";


    @RequestMapping(method=RequestMethod.GET)
    public ModelAndView populateForm(){
        return  new ModelAndView(REDIRECT_TO_ADMIN_SCREEN);
    }

    @RequestMapping(method = RequestMethod.POST)
    public ModelAndView processSubmit(@RequestParam(value = CANCEL_PARAM, required = false) String cancel,@RequestParam(value = EDIT_PARAM, required = false) String edit,
                                    @ModelAttribute("officeFormBean") OfficeFormBean formBean,
                                    BindingResult result,
                                    SessionStatus status) {

        ModelAndView modelAndView = new ModelAndView(REDIRECT_TO_ADMIN_SCREEN);

        if (StringUtils.isNotBlank(cancel)) {
            status.setComplete();
        } else if (result.hasErrors()) {
            modelAndView = new ModelAndView("previewOfficeDetails");
            modelAndView.addObject("officeFormBean", formBean);
            modelAndView.addObject("showError", "true");
        }
        else if (StringUtils.isNotBlank(edit)) {
            EditOfficeInformationController editOfficeInformationController = new EditOfficeInformationController(officeServiceFacade);
            modelAndView = new ModelAndView("defineNewOffice");
            modelAndView.addObject("showError", "false");
            if(!formBean.getLevelId().equals("1")){
            modelAndView.addObject("parentOffices",editOfficeInformationController.getParentDetails(formBean.getLevelId()));
            }
            modelAndView.addObject("officeTypes",editOfficeInformationController.getOfficeTypes(formBean.getLevelId()));
            modelAndView.addObject("officeFormBean", formBean);
            modelAndView.addObject("view", "enable");
            }
/*Code for update office in else -vishnu*/
        return modelAndView;
    }
}
