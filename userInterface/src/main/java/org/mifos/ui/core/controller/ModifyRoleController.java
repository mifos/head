package org.mifos.ui.core.controller;

import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;


@Controller
@RequestMapping("/modifyRole")
public class ModifyRoleController {
      protected ModifyRoleController(){
        // empty constructor for spring wiring
    }
     @RequestMapping(method = RequestMethod.GET)
     public ModelAndView handleRequestInternal(HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView("modifyRole");
        modelAndView.addObject("user", request.getParameter("user"));
        modelAndView.addObject("breadcrumbs", new AdminBreadcrumbBuilder().withLink("admin.modifyRole",
        "modifyRole.ftl").build());

        return modelAndView;
    }
}
