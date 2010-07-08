package org.mifos.ui.core.controller;

import java.util.LinkedList;
import java.util.List;
import org.mifos.application.admin.servicefacade.ViewOrganizationSettingsServiceFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.Properties;

@Controller
@RequestMapping("/viewOrganizationSettings")
public class ViewOrganizationSettingsController {

    @Autowired
    private ViewOrganizationSettingsServiceFacade viewOrganizationSettingsServiceFacade;

    private final BreadCrumbsLinks crumb1 = new BreadCrumbsLinks();
    private final BreadCrumbsLinks crumb2 = new BreadCrumbsLinks();
    private final List<BreadCrumbsLinks> linksList = new LinkedList<BreadCrumbsLinks>();

    protected ViewOrganizationSettingsController() {
        super();
        crumb1.setLink("admin.ftl");
        crumb1.setMessage("admin");
        crumb2.setLink("viewOrganizationSettings.ftl");
        crumb2.setMessage("viewOrganizationSettings");
        linksList.add(crumb1);
        linksList.add(crumb2);
    }

    @edu.umd.cs.findbugs.annotations.SuppressWarnings(value="NP_UNWRITTEN_FIELD", justification="request is not null")
    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView handleRequestInternal(HttpServletRequest request) {

        ModelAndView modelAndView = new ModelAndView("viewOrganizationSettings");
        Properties p = viewOrganizationSettingsServiceFacade.getOrganizationSettings(request.getSession());
        modelAndView.addObject("properties", p);
        modelAndView.addObject("breadcrumbs", linksList);

        return modelAndView;
    }

    public String getPageToDisplay(HttpServletRequest request) {
        return request.getRequestURI().replace("mifos/", "").replace("/", "").replace(".ftl", "");
    }
}