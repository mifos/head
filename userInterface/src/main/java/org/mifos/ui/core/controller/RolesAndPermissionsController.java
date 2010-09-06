package org.mifos.ui.core.controller;

import org.mifos.application.admin.servicefacade.RolesPermissionServiceFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;


@Controller
@RequestMapping("/manageRolesAndPermissions")
@SessionAttributes("formBean")
public class RolesAndPermissionsController {
    @Autowired
    private RolesPermissionServiceFacade rolesPermissionServiceFacade;

    protected RolesAndPermissionsController(){
     // empty constructor for spring wiring
    }
    public RolesAndPermissionsController(final RolesPermissionServiceFacade rolesPermissionServiceFacade){
        this.rolesPermissionServiceFacade=rolesPermissionServiceFacade;
    }

    @RequestMapping(method = RequestMethod.GET)
     public ModelAndView handleRequestInternal() {
        ModelAndView modelAndView = new ModelAndView("manageRolesAndPermissions");
        modelAndView.addObject("roles",this.rolesPermissionServiceFacade.retrieveAllRoles());
        modelAndView.addObject("breadcrumbs", new AdminBreadcrumbBuilder().withLink("admin.manageRolesAndPermissions",
        "manageRolesAndPermissions.ftl").build());
        return modelAndView;
    }
    }

