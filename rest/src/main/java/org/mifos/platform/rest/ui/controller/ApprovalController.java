package org.mifos.platform.rest.ui.controller;

import java.util.List;

import org.mifos.rest.approval.domain.RESTApprovalEntity;
import org.mifos.rest.approval.service.ApprovalService;
import org.mifos.ui.core.controller.AdminBreadcrumbBuilder;
import org.mifos.ui.core.controller.BreadCrumbsLinks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ApprovalController {

    @Autowired
    ApprovalService approvalService;

    @RequestMapping("restApprovalList.ftl")
    public ModelAndView list() {
        ModelAndView mav = new ModelAndView("approval/list");
        List<BreadCrumbsLinks> breadcrumbs = new AdminBreadcrumbBuilder().withLink("View REST Approval List", "").build();
        mav.addObject("breadcrumbs", breadcrumbs);
        mav.addObject("waitingForApprovalList", approvalService.getWaitingForApproval());
        List<RESTApprovalEntity> approvedOrRejected = approvalService.getApproved();
        approvedOrRejected.addAll(approvalService.getRejected());
        mav.addObject("approvedList", approvedOrRejected);
        return mav;
    }
}
