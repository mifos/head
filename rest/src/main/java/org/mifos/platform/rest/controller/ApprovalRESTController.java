package org.mifos.platform.rest.controller;

import java.util.List;

import org.mifos.rest.approval.domain.ApprovalMethod;
import org.mifos.rest.approval.domain.RESTApprovalEntity;
import org.mifos.rest.approval.service.ApprovalService;
import org.mifos.ui.core.controller.AdminBreadcrumbBuilder;
import org.mifos.ui.core.controller.BreadCrumbsLinks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Special controller for approval of REST API
 *
 * This should never be intercept by {@link AspectJRESTApprovalInterceptor}
 */
@Controller
@RequestMapping("rest/API/approval/")
public class ApprovalRESTController {

    @Autowired
    ApprovalService approvalService;

    @RequestMapping(value="id-{id}/approve", method=RequestMethod.POST)
    public ModelMap approve(@PathVariable(value="id") Long id) throws Exception {
        ModelMap model = new ModelMap();
        List<BreadCrumbsLinks> breadcrumbs = new AdminBreadcrumbBuilder().build();
        model.addAttribute("breadcrumbs", breadcrumbs);

        Object result = approvalService.approve(id);
        if(result.toString().startsWith("Error")) {
            model.addAttribute("status", "error");
        } else {
            model.addAttribute("status", "success");
        }
        model.addAttribute("result", result);
        return model;
    }

    @RequestMapping(value="id-{id}/reject", method=RequestMethod.POST)
    public ModelMap reject(@PathVariable(value="id") Long id) throws Exception {
        ModelMap model = new ModelMap();
        approvalService.reject(id);
        model.addAttribute("status", "success");
        return model;
    }

    @RequestMapping(value="id-{id}/details", method=RequestMethod.GET)
    public @ResponseBody RESTApprovalEntity getDetails(@PathVariable(value="id") Long id) throws Exception {
        return approvalService.getDetails(id);
    }

    @RequestMapping(value="id-{id}/methodData", method=RequestMethod.POST)
    public ModelMap updateMethodData(@PathVariable(value="id") Long id,
    		                         @RequestBody ApprovalMethod content) throws Exception {
        ModelMap model = new ModelMap();
        approvalService.updateMethodContent(id, content);
        model.addAttribute("status", "success");
        return model;
    }
}
