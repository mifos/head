package org.mifos.platform.rest.ui.controller;

import org.mifos.rest.approval.service.ApprovalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ApprovalController {

    @Autowired
    ApprovalService approvalService;

    @RequestMapping("approvalList.ftl")
    public ModelAndView list() {
        ModelAndView mav = new ModelAndView("approval/list");
        mav.addObject("approvalList", approvalService.getWaitingForApproval());
        return mav;
    }

}
