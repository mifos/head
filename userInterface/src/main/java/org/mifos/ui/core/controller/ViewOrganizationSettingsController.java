package org.mifos.ui.core.controller;



import java.util.Properties;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.mifos.application.admin.servicefacade.ViewOrganizationSettingsServiceFacade;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

@SuppressWarnings( { "PMD.SystemPrintln", "PMD.SingularField" })
public class ViewOrganizationSettingsController extends AbstractController{

    private ViewOrganizationSettingsServiceFacade viewOrganizationSettingsServiceFacade;

    BreadCrumbsLinks linksList;

    public ViewOrganizationSettingsServiceFacade getViewOrganzationSettingsSeviceFacade(){
        return this.viewOrganizationSettingsServiceFacade;
    }

    public void setViewOrganizationSettingsServiceFacade(ViewOrganizationSettingsServiceFacade viewOrganizationServiceFacade){
        this.viewOrganizationSettingsServiceFacade=viewOrganizationServiceFacade;
    }




    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response)
     {
        HttpSession httpsession=request.getSession();
       //creating links object
        linksList=new BreadCrumbsLinks();
        //adding the present page's message into the list
        linksList.setWay(getPageToDisplay(request));
        Properties p=null;
        //adding the list to the modelAndView.
        ModelAndView modelAndView=new ModelAndView("viewOrganizationSettings","breadcrumbs",linksList.getWay());
        try {
            p=viewOrganizationSettingsServiceFacade.getOrganizationSettings(httpsession);
            modelAndView.addObject("properties",p);
            } catch (Exception e) {
        }
        return modelAndView;
    }

    public String getPageToDisplay(HttpServletRequest request) {
        System.out.println(request.getRequestURI());
        return request.getRequestURI().replace("mifos/","").replace("/", "").replace(".ftl", "");
    }

}
