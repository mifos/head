package org.mifos.ui.core.controller;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.lang.StringUtils;
import org.mifos.application.admin.servicefacade.OfficeServiceFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/defineNewOffice")
@SuppressWarnings("PMD")
public class DefineNewOfficeController {

    private static final String REDIRECT_TO_ADMIN_SCREEN = "redirect:/AdminAction.do?method=load";
    private static final String CANCEL_PARAM = "CANCEL";
    private static final String PREVIEW_PARAM="PREVIEW";

    @Autowired
    OfficeServiceFacade officeServiceFacade;

    protected DefineNewOfficeController() {
        // spring auto wiring controller
    }

    public DefineNewOfficeController(final OfficeServiceFacade officeServiceFacade){
        this.officeServiceFacade=officeServiceFacade;
    }

    @RequestMapping(method=RequestMethod.GET)
    public ModelAndView populateForm(HttpServletRequest request,OfficeFormBean officeFormBean){
        ModelAndView modelAndView = new ModelAndView("defineNewOffice");
        EditOfficeInformationController editOfficeInformationController = new EditOfficeInformationController(officeServiceFacade);

        if(StringUtils.isNotBlank(request.getParameter("levelId"))){
        officeFormBean.setLevelId(request.getParameter("levelId"));
        }
        officeFormBean.setName("");
        officeFormBean.setLevelId("");
        officeFormBean.setOfficeShortName("");
        modelAndView.addObject("officeTypes", editOfficeInformationController.getOfficeTypes("new"));
        modelAndView.addObject("showError", "false");
        modelAndView.addObject("officeFormBean", officeFormBean);
        return modelAndView;
    }

    @RequestMapping(method = RequestMethod.POST)
    public ModelAndView processFormSubmit(@RequestParam(value = CANCEL_PARAM, required = false) String cancel,@RequestParam(value = PREVIEW_PARAM, required = false) String preview,
            @ModelAttribute("officeFormBean") @Valid OfficeFormBean officeFormBean, BindingResult result, SessionStatus status) {

        ModelAndView modelAndView = new ModelAndView(REDIRECT_TO_ADMIN_SCREEN);
        EditOfficeInformationController editOfficeInformationController = new EditOfficeInformationController(officeServiceFacade);

        if (StringUtils.isNotBlank(cancel)) {
            modelAndView.setViewName(REDIRECT_TO_ADMIN_SCREEN);
            status.setComplete();
        } else if(StringUtils.isNotBlank(preview)){
            if (result.hasErrors()) {
                modelAndView.setViewName("defineNewOffice");
                modelAndView.addObject("showError", "true");
                if( !((!officeFormBean.getLevelId().equals("1") && StringUtils.isNotBlank(officeFormBean.getParentId())) || (officeFormBean.getLevelId().equals("1") && !StringUtils.isNotBlank(officeFormBean.getParentId())))){
                    result.addError(new ObjectError("parentLevelId", "Please specify Parent Level name"));
                }

            }else{
                if(((!officeFormBean.getLevelId().equals("1") && StringUtils.isNotBlank(officeFormBean.getParentId())) || (officeFormBean.getLevelId().equals("1") && !StringUtils.isNotBlank(officeFormBean.getParentId())))){
                    result.addError(new ObjectError("parentLevelId", "Please specify Parent Level name"));
                    modelAndView = new ModelAndView("previewOfficeDetails");
                }else{
                    modelAndView = new ModelAndView("defineNewOffice");
                    result.addError(new ObjectError("parentLevelId", "Please specify Parent Level name"));
                    modelAndView.addObject("showError", "true");
                }
                switch(Integer.parseInt(officeFormBean.getLevelId())){
                case 1:
                    officeFormBean.setOfficeLevelName("Head Office");
                    break;
                case 2:
                    officeFormBean.setOfficeLevelName("Regional Office");
                    break;
                case 3:
                    officeFormBean.setOfficeLevelName("Divisional Office");
                    break;
                case 4:
                    officeFormBean.setOfficeLevelName("Area Office");
                    break;
                default:
                    officeFormBean.setOfficeLevelName("Branch Office");
                    break;
                }
            /*modelAndView.addObject("officeTypes", editOfficeInformationController.getOfficeTypes("new"));
            if(!officeFormBean.getLevelId().equals("1")){
                modelAndView.addObject("parentOffices", editOfficeInformationController.getParentDetails(officeFormBean.getLevelId()));
                officeFormBean.setParentOfficeName(officeServiceFacade.retrieveOfficeById(Short.parseShort(officeFormBean.getParentId())).getName());
            }
            modelAndView.addObject("officeFormBean", officeFormBean);*/
            }
            modelAndView.addObject("officeTypes", editOfficeInformationController.getOfficeTypes("new"));
            if(!officeFormBean.getLevelId().equals("1") && StringUtils.isNotBlank(officeFormBean.getLevelId()) || StringUtils.isNotBlank(officeFormBean.getParentId())){
                modelAndView.addObject("parentOffices", editOfficeInformationController.getParentDetails(officeFormBean.getLevelId()));
                officeFormBean.setParentOfficeName(officeServiceFacade.retrieveOfficeById(Short.parseShort(officeFormBean.getParentId())).getName());
            }
            modelAndView.addObject("officeFormBean", officeFormBean);
        }else{
                modelAndView.setViewName("defineNewOffice");
                modelAndView.addObject("officeTypes", editOfficeInformationController.getOfficeTypes("new"));
                if(!officeFormBean.getLevelId().equals("1")){
                    modelAndView.addObject("parentOffices", editOfficeInformationController.getParentDetails(officeFormBean.getLevelId()));
                }
                modelAndView.addObject("showError", "false");
            }
        return modelAndView;
    }


}
