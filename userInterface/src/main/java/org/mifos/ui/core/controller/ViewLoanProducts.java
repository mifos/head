package org.mifos.ui.core.controller;

import java.util.List;

import org.mifos.application.admin.servicefacade.AdminServiceFacade;
import org.mifos.dto.screen.ProductDisplayDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


@Controller
@RequestMapping("/viewLoanProducts")
public class ViewLoanProducts {

    @Autowired
    AdminServiceFacade adminServiceFacade;

    protected ViewLoanProducts(){
        //for spring autowiring
    }
    public ViewLoanProducts(final AdminServiceFacade adminServicefacade){
        this.adminServiceFacade=adminServicefacade;
    }

    @RequestMapping(method = RequestMethod.GET)
    @ModelAttribute("breadcrumbs")
    public List<BreadCrumbsLinks> showBreadCrumbs() {
        return new AdminBreadcrumbBuilder().withLink("admin.viewLoanProducts", "viewLoanProducts.ftl").build();
    }

    @ModelAttribute("formBean")
    public List<ProductDisplayDto> showPopulatedForm() {

        return adminServiceFacade.retrieveLoanProducts();
    }


}
