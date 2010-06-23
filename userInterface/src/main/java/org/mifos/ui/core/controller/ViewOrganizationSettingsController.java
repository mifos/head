package org.mifos.ui.core.controller;

import java.util.LinkedList;
import java.util.List;
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

    //BreadCrumbsLinks linksList;
    List<String> linksList=new LinkedList<String> ();
    List<String> messagesList=new LinkedList<String> ();

    ViewOrganizationSettingsController(){
        this.linksList.add(this.linksList.size(),"admin.ftl");
        this.messagesList.add(this.messagesList.size(),"admin");
        this.messagesList.add(this.messagesList.size(),"viewOrganizationSettings");
    }


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
            modelAndView.addObject("bluecrumbmessages",messagesList);
         } catch (Exception e) {
             e.printStackTrace();
         }
         return modelAndView;
    }

    public String getPageToDisplay(HttpServletRequest request) {
        System.out.println(request.getRequestURI());
        return request.getRequestURI().replace("mifos/","").replace("/", "").replace(".ftl", "");
    }

}
