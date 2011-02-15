package org.mifos.ui.core.controller;

import org.mifos.application.admin.servicefacade.AppliedUpgradesServiceFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@Controller

public class AppliedUpgradesController {

    @Autowired
    protected AppliedUpgradesServiceFacade appliedUpgradesServiceFacade;

    @RequestMapping("/appliedUpgrades.ftl")
    public String viewChangeSets(ModelMap model, HttpServletRequest httpServletRequest) {
        model.put("appliedChangeSets", appliedUpgradesServiceFacade.getAppliedUpgrades());
        model.put("unRunChangeSets", appliedUpgradesServiceFacade.getUnRunChangeSets());
        return "appliedUpgrades";
    }

}
