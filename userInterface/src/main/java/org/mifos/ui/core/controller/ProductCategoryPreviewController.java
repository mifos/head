package org.mifos.ui.core.controller;


import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/categoryPreview")
@SessionAttributes("formBean")
public class ProductCategoryPreviewController {
    private static final String REDIRECT_TO_ADMIN_SCREEN = "redirect:/AdminAction.do?method=load";
    private static final String CANCEL_PARAM = "CANCEL";
    private static final String EDIT_PARAM = "EDIT";
    /*    @Autowired
    private AdminServiceFacade adminServiceFacade;

    public ProductCategoryPreviewController(final AdminServiceFacade adminServiceFacade){
        this.adminServiceFacade=adminServiceFacade;
    }*/
    protected ProductCategoryPreviewController(){
        // empty constructor for spring autowiring
    }

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView showPopulatedForm() {
        return new ModelAndView(REDIRECT_TO_ADMIN_SCREEN);
    }


    @RequestMapping(method = RequestMethod.POST)
    public ModelAndView processFormSubmit(@RequestParam(value = EDIT_PARAM, required = false) String edit,
            @RequestParam(value = CANCEL_PARAM, required = false) String cancel, ProductCategoryFormBean formBean,
            BindingResult result,HttpServletRequest request) {
        String viewName = REDIRECT_TO_ADMIN_SCREEN;
        ModelAndView modelAndView = new ModelAndView();
        if (StringUtils.isNotBlank(edit)) {
            viewName = "editCategoryInformation";
            modelAndView.setViewName(viewName);
            modelAndView.addObject("formBean", formBean);
            modelAndView.addObject("breadcrumbs", new AdminBreadcrumbBuilder().withLink("admin.viewproductcategories", "viewProductCategories.ftl").withLink(formBean.getProductCategoryName(),"").build());
        } else if (StringUtils.isNotBlank(cancel)) {
            viewName = REDIRECT_TO_ADMIN_SCREEN;
            modelAndView.setViewName(viewName);
        } else if (result.hasErrors()) {
            viewName = "editCategoryInformation";
            modelAndView.setViewName(viewName);
            modelAndView.addObject("formBean", formBean);
        }else{
            //FIX ME: code for updating the product category
            /*UserContext userContext=((UserContext)request.getSession().getAttribute("UserContext"));
            Integer productStatusId=Integer.parseInt(formBean.getProductCategoryStatusId());
            Integer productTypeId=Integer.parseInt(formBean.getProductTypeId());
            CreateOrUpdateProductCategory createOrUpdateProductCategory=new CreateOrUpdateProductCategory(userContext.getId(), userContext.getBranchId(), productTypeId.shortValue(), formBean.getProductCategoryName(), formBean.getProductCategoryDesc(), productStatusId.shortValue());
            this.adminServiceFacade.updateProductCategory(createOrUpdateProductCategory);*/
            modelAndView.setViewName(REDIRECT_TO_ADMIN_SCREEN);
            modelAndView.addObject("formBean", formBean);
        }
        return modelAndView;
    }
}
