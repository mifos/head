package org.mifos.ui.core.controller;

import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;



@Controller
@RequestMapping("defineMandatoryHiddenFields.ftl")
public class DefineMandatoryHiddenFieldsController {

    @RequestMapping(method = RequestMethod.GET)
    @ModelAttribute("breadcrumbs")
    public List<BreadCrumbsLinks> showBreadCrumbs() {
        return new AdminBreadcrumbBuilder().withLink("definemandatory/hiddenfields", "defineMandatoryHiddenFields.ftl").build();
    }

    @RequestMapping(method = RequestMethod.POST)
    public String processFormSubmit(@RequestParam(value = "CANCEL", required = false) String cancel,
                                   DefineMandatoryHiddenFieldsFormBean bean) {
        return "defineMandatoryHiddenFields";
    }

}
