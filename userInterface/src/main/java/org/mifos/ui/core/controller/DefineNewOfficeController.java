package org.mifos.ui.core.controller;

import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang.StringUtils;
import org.mifos.application.admin.servicefacade.OfficeServiceFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/defineNewOffice")
public class DefineNewOfficeController {

    @Autowired
    OfficeServiceFacade officeServiceFacade;

    protected DefineNewOfficeController() {
        // TODO Auto-generated constructor stub
    }

    public DefineNewOfficeController(final OfficeServiceFacade officeServiceFacade){
        this.officeServiceFacade=officeServiceFacade;
    }

    @RequestMapping(method=RequestMethod.GET)
    public ModelAndView populateForm(HttpServletRequest request,OfficeFormBean officeFormBean){
        ModelAndView modelAndView = new ModelAndView("defineNewOffice");
        if(StringUtils.isNotBlank(request.getParameter("levelId"))){
        officeFormBean.setLevelId(request.getParameter("levelId"));
        }
        EditOfficeInformationController editOfficeInformationController = new EditOfficeInformationController(officeServiceFacade);
        modelAndView.addObject("officeTypes", editOfficeInformationController.getOfficeTypes("new"));
        modelAndView.addObject("officeFormBean", officeFormBean);
        return modelAndView;
    }
}
