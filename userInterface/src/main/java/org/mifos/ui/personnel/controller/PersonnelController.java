package org.mifos.ui.personnel.controller;

import org.mifos.application.admin.servicefacade.PersonnelServiceFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class PersonnelController {

    private static final String LOCALE_ID = "id";

    @Autowired
    private PersonnelServiceFacade personnelServiceFacade;

    @RequestMapping("/changeLocale.ftl")
    public ModelAndView changeUserLocale(@RequestParam(value = LOCALE_ID, required = false) Short id) {
        Short currentLocaleId = personnelServiceFacade.changeUserLocale(id);
        ModelAndView mav = new ModelAndView();
        mav.addObject("CURRENT_LOCALE_ID", currentLocaleId);
        mav.addObject("LOCALE_LIST", personnelServiceFacade.getDisplayLocaleList());
        mav.setViewName("personnel/changeLocale");
        return mav;

    }

}
