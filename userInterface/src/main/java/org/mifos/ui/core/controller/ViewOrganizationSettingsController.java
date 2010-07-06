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
    BreadCrumbsLinks crumb1=new BreadCrumbsLinks();
    BreadCrumbsLinks crumb2=new BreadCrumbsLinks();
    List<BreadCrumbsLinks> linksList=new LinkedList<BreadCrumbsLinks> ();

    ViewOrganizationSettingsController(){
        super();
        crumb1.setLink("admin.ftl");
        crumb1.setMessage("admin");
        crumb2.setLink("viewOrganizationSettings.ftl");
        crumb2.setMessage("viewOrganizationSettings");
        linksList.add(crumb1);
        linksList.add(crumb2);
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
            modelAndView.addObject("breadcrumbs",linksList);
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
