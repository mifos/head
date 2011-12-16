package org.mifos.platform.rest.ui.controller;

import java.util.List;

import org.mifos.application.admin.servicefacade.PersonnelServiceFacade;
import org.mifos.dto.screen.PersonnelInformationDto;
import org.mifos.rest.approval.domain.ApprovalState;
import org.mifos.rest.approval.domain.RESTApprovalEntity;
import org.mifos.rest.approval.service.ApprovalService;
import org.mifos.ui.core.controller.AdminBreadcrumbBuilder;
import org.mifos.ui.core.controller.BreadCrumbsLinks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ApprovalController {

    @Autowired
    ApprovalService approvalService;

    @Autowired
    PersonnelServiceFacade personnelServiceFacade;

    @RequestMapping("restApprovalList.ftl")
    public ModelAndView list() {
        ModelAndView mav = new ModelAndView("approval/list");
        List<BreadCrumbsLinks> breadcrumbs = new AdminBreadcrumbBuilder().withLink("View REST Approval List", "").build();
        mav.addObject("breadcrumbs", breadcrumbs);
        mav.addObject("waitingForApprovalList", approvalService.getAllWaiting());
        mav.addObject("approvedList", approvalService.getAllNotWaiting());
        return mav;
    }

    @RequestMapping("restApproval/id-{id}/details.ftl")
    public ModelAndView details(@PathVariable Long id) {
        ModelAndView mav = new ModelAndView("approval/details");
        mav.addObject("waitingForApprovalList", approvalService.getAllWaiting());
        RESTApprovalEntity approval = approvalService.getDetails(id);
        mav.addObject("approval", approval);

        PersonnelInformationDto p = personnelServiceFacade.getPersonnelInformationDto(approval.getCreatedBy().longValue(), null);
        mav.addObject("createdBy", p.getDisplayName());

        if (!approval.getState().equals(ApprovalState.WAITING)) {
            p = personnelServiceFacade.getPersonnelInformationDto(approval.getApprovedBy().longValue(), null);
            mav.addObject("approvedBy", p.getDisplayName());
        }
        return mav;
    }
}
