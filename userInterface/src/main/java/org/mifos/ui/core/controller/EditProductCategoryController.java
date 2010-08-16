package org.mifos.ui.core.controller;


import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.lang.StringUtils;
import org.mifos.application.admin.servicefacade.AdminServiceFacade;
import org.mifos.dto.screen.ProductCategoryDetailsDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
//import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/editCategoryInformation")
@SessionAttributes("formBean")
public class EditProductCategoryController {
    //private static final String REDIRECT_TO_VIEW_CATEGORIES = "redirect:/viewProductCategories.ftl";
    //private static final String TO_CATEGORY_PREVIEW = "categoryPreview";
    private static final String CANCEL_PARAM = "CANCEL";
    private static final String REDIRECT_TO_ADMIN_SCREEN = "redirect:/AdminAction.do?method=load";

    @Autowired
    private AdminServiceFacade adminServiceFacade;

    protected EditProductCategoryController(){
        //empty controller for spring auto wiring
    }

    public EditProductCategoryController(final AdminServiceFacade adminServiceFacade){
        this.adminServiceFacade=adminServiceFacade;
    }

    @edu.umd.cs.findbugs.annotations.SuppressWarnings
    @RequestMapping(method = RequestMethod.POST)
    public ModelAndView showPopulatedForm(@RequestParam(value = CANCEL_PARAM, required = false) String cancel, @ModelAttribute("formBean") @Valid ProductCategoryFormBean formBean, BindingResult result) {
        ModelAndView modelAndView = new ModelAndView();
        if (StringUtils.isNotBlank(cancel)) {
            modelAndView.setViewName(REDIRECT_TO_ADMIN_SCREEN);
        } else if (result.hasErrors()) {
            modelAndView.setViewName("editCategoryInformation");
            modelAndView.addObject("formBean", formBean);
        }else{
            modelAndView.setViewName("categoryPreview");
            modelAndView.addObject("formBean", formBean);
        }
        modelAndView.addObject("breadcrumbs", new AdminBreadcrumbBuilder().withLink("admin.viewproductcategories", "viewProductCategories.ftl").withLink(formBean.getProductCategoryName(),"").build());
        return modelAndView;
    }


    @edu.umd.cs.findbugs.annotations.SuppressWarnings
    @RequestMapping(method = RequestMethod.GET)
    @ModelAttribute("formBean")
    public ProductCategoryFormBean showCategory(HttpServletRequest request) {
        ProductCategoryDetailsDto productCategoryDetailsDto=adminServiceFacade.retrieveProductCateogry(request.getParameter("globalPrdCategoryNum"));
        ProductCategoryFormBean productCategoryFormBean=new ProductCategoryFormBean();
        productCategoryFormBean.setGlobalPrdCategoryNum(request.getParameter("globalProductCategoryNumber"));
        productCategoryFormBean.setProductCategoryDesc(productCategoryDetailsDto.getProductCategoryDesc());
        productCategoryFormBean.setProductCategoryName(productCategoryDetailsDto.getProductCategoryName());
        productCategoryFormBean.setProductCategoryStatusId(productCategoryDetailsDto.getProductCategoryStatusId().toString());
        productCategoryFormBean.setProductType(productCategoryDetailsDto.getProductTypeName());
        productCategoryFormBean.setProductTypeId(productCategoryDetailsDto.getProductTypeId().toString());
        return productCategoryFormBean;
    }
}
