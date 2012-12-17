package org.mifos.ui.core.controller;

import org.mifos.application.admin.servicefacade.CoaServiceFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller

public class CoaAdminController {

    private CoaServiceFacade coaServiceFacade;

    
    @RequestMapping(value = "/coaAdmin", method = RequestMethod.GET)
    public ModelAndView showChartOfAccounts() {
        ModelAndView modelAndView = new ModelAndView("coaAdmin");
        modelAndView.addObject("COAlist", coaServiceFacade.getList(null));
        modelAndView.addObject("canModifyCOA", coaServiceFacade.canModifyCOA());
        return modelAndView;
    }

    @RequestMapping(value = "/coaAdminAjax", method = RequestMethod.GET)
    public @ResponseBody CoaListViewModel getString(@RequestParam(value = "id", required=true) Short id) {
        CoaListViewModel coaListViewModel = new CoaListViewModel();
        coaListViewModel.setCoaList(coaServiceFacade.getList(id));
        coaListViewModel.setModifiable(coaServiceFacade.canModifyCOA());
        return coaListViewModel;
    }
    
    @Autowired
    public void setCoaServiceFacade(CoaServiceFacade coaServiceFacade) {
        this.coaServiceFacade = coaServiceFacade;
    }
 
}
