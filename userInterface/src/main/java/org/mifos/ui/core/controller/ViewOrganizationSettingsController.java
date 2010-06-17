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
        Properties p=null;
        // TODO Auto-generated method stub
        ModelAndView modelAndView=new ModelAndView("viewOrganizationSettings");
        try {
            p=viewOrganizationSettingsServiceFacade.getOrganizationSettings(httpsession);
            modelAndView.addObject("properties",p);
            } catch (Exception e) {
            // TODO Auto-generated catch block
           System.out.println("in catch of try");
        }
        return modelAndView;
    }

}
