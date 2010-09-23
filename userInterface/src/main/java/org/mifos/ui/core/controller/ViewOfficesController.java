package org.mifos.ui.core.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.mifos.application.admin.servicefacade.OfficeServiceFacade;
import org.mifos.dto.domain.OfficeDto;

@Controller
@RequestMapping("/viewOffices.ftl")
public class ViewOfficesController {

    @Autowired
    private OfficeServiceFacade officeServiceFacade;

    protected ViewOfficesController(){
        // default contructor for spring autowiring
    }

    @RequestMapping(method = RequestMethod.GET)
    @ModelAttribute("breadcrumbs")
    public List<BreadCrumbsLinks> showBreadCrumbs() {
       return new AdminBreadcrumbBuilder().withLink("viewOffices", "viewOffices.ftl").build();
    }
    @ModelAttribute("formBean")
    public Map<String,List<OfficeDto>> showPopulatedForm(){
        Map<String,List<OfficeDto>> res=new HashMap<String, List<OfficeDto>>();
        res.put("regionalOffices",officeServiceFacade.retrieveAllOffices().getRegionalOffices());
        res.put("headOffices",officeServiceFacade.retrieveAllOffices().getHeadOffices());
        res.put("areaOffices",officeServiceFacade.retrieveAllOffices().getAreaOffices());
        res.put("branchOffices",officeServiceFacade.retrieveAllOffices().getBranchOffices());
        res.put("divisionalOffices",officeServiceFacade.retrieveAllOffices().getDivisionalOffices());
        return res;
     }
}