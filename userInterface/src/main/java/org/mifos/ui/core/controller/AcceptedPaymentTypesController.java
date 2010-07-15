package org.mifos.ui.core.controller;

import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;


@Controller
@RequestMapping("/defineAcceptedPaymentTypes.ftl")
@SessionAttributes("breadcrumbs")
public class AcceptedPaymentTypesController {

    private static final String CANCEL_PARAM = "CANCEL";
    @RequestMapping(method = RequestMethod.GET)
    @ModelAttribute("breadcrumbs")
    public List<BreadCrumbsLinks> showBreadCrumbs() {
        return new AdminBreadcrumbBuilder().withLink("defineAcceptedPaymentTypes", "defineAcceptedPaymentTypes.ftl").build();
    }

    @RequestMapping(method = RequestMethod.POST)
    public String processFormSubmit(@RequestParam(value = CANCEL_PARAM, required = false) String cancel,
            AcceptedPaymentTypesBean Bean) {
       return "defineAcceptedPaymentTypes";
    }




}