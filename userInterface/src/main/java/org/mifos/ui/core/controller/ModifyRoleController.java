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
     public ModelAndView handleRequestInternal(HttpServletRequest request){
        ModelAndView modelAndView = new ModelAndView("modifyRole");
        modelAndView.addObject("user", request.getParameter("user"));
        //System.out.println(request.getParameter("user"));
        modelAndView.addObject("breadcrumbs", new AdminBreadcrumbBuilder().withLink("admin.modifyRole",
        "modifyRole.ftl").build());
        return modelAndView;
    }

     @RequestMapping(method = RequestMethod.POST)
     public ModelAndView onSubmit(){
        //ModelAndView modelAndView = new ModelAndView("redirect:/admin.ftl");
        return new ModelAndView("redirect:/admin.ftl");
    }

}
