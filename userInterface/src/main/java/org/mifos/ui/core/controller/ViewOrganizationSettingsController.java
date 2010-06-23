package org.mifos.ui.core.controller;

import org.mifos.application.admin.servicefacade.ViewOrganizationSettingsServiceFacade;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Properties;

@SuppressWarnings( { "PMD.SystemPrintln", "PMD.SingularField", "PMD.AvoidPrintStackTrace" })
@Controller
public class ViewOrganizationSettingsController extends AbstractController{

    private ViewOrganizationSettingsServiceFacade viewOrganizationSettingsServiceFacade;

    public ViewOrganizationSettingsServiceFacade getViewOrganzationSettingsSeviceFacade(){
        return this.viewOrganizationSettingsServiceFacade;
    }

    public void setViewOrganizationSettingsServiceFacade(ViewOrganizationSettingsServiceFacade viewOrganizationServiceFacade){
        this.viewOrganizationSettingsServiceFacade=viewOrganizationServiceFacade;
    }

    @Override
    @RequestMapping("/viewOrganizationSettings.ftl")
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response)
     {
         ModelAndView modelAndView = new ModelAndView("viewOrganizationSettings");
         try {
             Properties p = viewOrganizationSettingsServiceFacade.getOrganizationSettings(request.getSession());
             modelAndView.addObject("properties", p);
         } catch (Exception e) {
             e.printStackTrace();
         }
         return modelAndView;
    }

}
