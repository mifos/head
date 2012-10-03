package org.mifos.ui.core.controller;

import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang.StringUtils;
import org.mifos.application.admin.servicefacade.RolesPermissionServiceFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;


@Controller
@RequestMapping("/modifyRole")
public class ModifyRoleController {

    @Autowired
    RolesPermissionServiceFacade rolesPermissionServiceFacade;

    public ModifyRoleController(final RolesPermissionServiceFacade rolesPermissionServiceFacade){
        this.rolesPermissionServiceFacade=rolesPermissionServiceFacade;
    }

    protected ModifyRoleController(){
        // empty constructor for spring wiring
    }

     @RequestMapping(method = RequestMethod.GET)
     public ModelAndView handleRequestInternal(HttpServletRequest request){
        ModelAndView modelAndView = new ModelAndView("modifyRole");
        modelAndView.addObject("user", request.getParameter("user"));
        modelAndView.addObject("roleId", request.getParameter("roleId"));
        modelAndView.addObject("breadcrumbs", new AdminBreadcrumbBuilder().withLink("admin.modifyRole",
        "modifyRole.ftl").build());
        return modelAndView;
    }


    @RequestMapping(method = RequestMethod.POST)
     public ModelAndView onSubmit(HttpServletRequest request){
        ModelAndView modelAndView = new ModelAndView("manageRolesAndPermissions");
        List<Short> activityList = new ArrayList<Short>();
         for(int i=0;i<181;i++){
             if(StringUtils.isNotEmpty(request.getParameter("activity("+i+")")) && !request.getParameter("activity("+i+")").equals("checkbox")){
                 activityList.add(Short.parseShort(request.getParameter("activity("+i+")")));
             }
         }
        /* UserContext userContext = (UserContext)request.getSession().getAttribute("UserContext");
         try {
            this.rolesPermissionServiceFacade.updateRole(Short.parseShort(request.getParameter("roleId")), userContext.getId(), request.getParameter("roleName"), activityList);
        } catch (Exception e) {
            modelAndView.addObject("errorMessage", "An error has occurred. Please try again.");
        }*/
        modelAndView.addObject("roles",rolesPermissionServiceFacade.retrieveAllRoles());
        return modelAndView;
    }


}
