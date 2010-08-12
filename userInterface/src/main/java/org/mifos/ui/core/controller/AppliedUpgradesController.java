package org.mifos.ui.core.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.mifos.application.admin.servicefacade.AppliedUpgradesServiceFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller

public class AppliedUpgradesController {

    @Autowired
    protected AppliedUpgradesServiceFacade appliedUpgradesServiceFacade;

    @RequestMapping("/appliedUpgrades.ftl")
    public String viewAppliedUpgrades(ModelMap model, HttpServletRequest httpServletRequest) {
        List<Integer> list = appliedUpgradesServiceFacade.getAppliedUpgrades();

        model.put("upgrades", list);

        return "appliedUpgrades";
    }

}
