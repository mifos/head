package org.mifos.ui.core.controller;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mifos.application.admin.servicefacade.AdminServiceFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/viewProductMix")
public class ProductMixController {

    @Autowired
    AdminServiceFacade adminservicefacade;

    protected ProductMixController(){
        //spring autowiring
    }

    public ProductMixController(final AdminServiceFacade adminServiceFacade){
        this.adminservicefacade=adminServiceFacade;

    }


    @RequestMapping(method = RequestMethod.GET)
    @ModelAttribute("breadcrumbs")
    public List<BreadCrumbsLinks> showBreadCrumbs() {
        return new AdminBreadcrumbBuilder().withLink("viewProductMix", "viewProductMix.ftl").build();
    }
    @ModelAttribute("mixList")
    public Map<String, Object> getList() {
        Map<String, Object> model = new HashMap<String, Object>();
        try {
            model.put("mix", adminservicefacade.retrieveAllProductMix().getProductMixList());
        } catch (Exception e) {
           model.put("mix", "List could not be retrieved.");
        }
        return model;
    }

}