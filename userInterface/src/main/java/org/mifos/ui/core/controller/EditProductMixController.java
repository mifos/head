package org.mifos.ui.core.controller;
import java.util.List;

import org.mifos.application.admin.servicefacade.AdminServiceFacade;
import org.mifos.dto.screen.ProductMixDetailsDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/viewProductMix/{productId}")
public class EditProductMixController {

    @Autowired
    private AdminServiceFacade adminServiceFacade;

    protected EditProductMixController(){
        //spring autowiring
    }

    public EditProductMixController(final AdminServiceFacade adminServiceFacade){
        this.adminServiceFacade=adminServiceFacade;
    }

    @RequestMapping(method = RequestMethod.GET)
    public String displayProductMix(@PathVariable Integer productId, Model model) {
        ProductMixDetailsDto productMixDetailsDto = this.adminServiceFacade.retrieveProductMixDetails(productId.shortValue(), "1");
        model.addAttribute("productMixDetails", productMixDetailsDto);
        model.addAttribute("breadcrumbs", new AdminBreadcrumbBuilder().withLink("viewProductMix", "viewProductMix.ftl").build());
        return "redirect:login.ftl";
    }
}