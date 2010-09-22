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
@RequestMapping("/officePreview")
public class PreviewOfficeDetailsController {

    @Autowired
    OfficeServiceFacade officeServiceFacade;

    protected PreviewOfficeDetailsController() {
        // TODO Auto-generated constructor stub
    }

    public PreviewOfficeDetailsController(final OfficeServiceFacade officeServiceFacade){
        this.officeServiceFacade=officeServiceFacade;
    }

    private static final String REDIRECT_TO_ADMIN_SCREEN = "redirect:/AdminAction.do?method=load";
    private static final String EDIT_PARAM = "EDIT";
    private static final String CANCEL_PARAM = "CANCEL";


    @RequestMapping(method=RequestMethod.GET)
    public ModelAndView populateForm(OfficeFormBean officeFormBean){
        ModelAndView modelAndView=new ModelAndView("officePreview");
        modelAndView.addObject("formBean", officeFormBean);
        return  modelAndView;
    }

    @RequestMapping(method = RequestMethod.POST)
    public ModelAndView processFormSubmit(@RequestParam(value = CANCEL_PARAM, required = false) String cancel,@RequestParam(value = EDIT_PARAM, required = false) String edit,
                                    @ModelAttribute("officeFormBean") OfficeFormBean formBean,
                                    BindingResult result,
                                    SessionStatus status) {

        ModelAndView mav = new ModelAndView(REDIRECT_TO_ADMIN_SCREEN);

        if (StringUtils.isNotBlank(cancel)) {
            status.setComplete();
        } else if (result.hasErrors()) {
            mav = new ModelAndView("officePreview");
            mav.addObject("officeFormBean", formBean);
        }
        else if (StringUtils.isNotBlank(edit)) {
            EditOfficeInformationController editOfficeInformationController = new EditOfficeInformationController(officeServiceFacade);
            mav = new ModelAndView("editOfficeInformation");
            if(!formBean.getLevelId().equals("1")){
            mav.addObject("parentOffices",editOfficeInformationController.getParentDetails(formBean.getLevelId()));
            }
            mav.addObject("officeTypes",editOfficeInformationController.getOfficeTypes(formBean.getLevelId()));
            mav.addObject("officeFormBean", formBean);
            mav.addObject("view", "enable");
            }
/*Code for update office in else -vishnu*/

        return mav;
    }
}
