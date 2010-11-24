/*
 * Copyright (c) 2005-2010 Grameen Foundation USA
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 * explanation of the license and how it is applied.
 */

package org.mifos.ui.core.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.mifos.application.admin.servicefacade.AdminServiceFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/previewProductMix")
@SessionAttributes("formBean")
@SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.NPathComplexity"})
public class PreviewProductMixController {

    private static final String REDIRECT_TO_ADMIN_SCREEN = "redirect:/AdminAction.do?method=load";
    private static final String CANCEL_PARAM = "CANCEL";
    private static final String EDIT_PARAM = "EDIT";

    @Autowired
    private AdminServiceFacade adminServiceFacade;

    protected PreviewProductMixController() {
        // default contructor for spring autowiring
    }

    public PreviewProductMixController(AdminServiceFacade adminServiceFacade) {
        this.adminServiceFacade = adminServiceFacade;
    }


    @RequestMapping(method = RequestMethod.POST)
    public ModelAndView processFormSubmit(@RequestParam(value = EDIT_PARAM, required = false) String edit,
                                          @RequestParam(value = CANCEL_PARAM, required = false) String cancel,
                                          @RequestParam(value = "FORMVIEW", required = true) String formView,
                                          @ModelAttribute("formBean") ProductMixFormBean formBean,
                                          BindingResult result,
                                          SessionStatus status) {

        ModelAndView mav = new ModelAndView(REDIRECT_TO_ADMIN_SCREEN);

        if (StringUtils.isNotBlank(edit)) {
            updateAllowedNotAllowedProductMix(formBean);
            resetAllowedAndNotAllowed(formBean);
            mav = new ModelAndView(formView);
            mav.addObject("formBean", formBean);

        } else if (StringUtils.isNotBlank(cancel)) {
            status.setComplete();
        } else if (result.hasErrors()) {
            mav = new ModelAndView("previewProductMix");
        } else {
            Integer productId = Integer.parseInt(formBean.getProductId());
            List<Integer> notAllowedProductIds = toIntegers(formBean.getNotAllowed());

            this.adminServiceFacade.createOrUpdateProductMix(productId, notAllowedProductIds);

            mav = new ModelAndView("confirmProductMix");
            mav.addObject("productId", productId);
        }

        return mav;
    }

    private List<Integer> toIntegers(String[] allowed) {
        List<Integer> allowedAsInts = new ArrayList<Integer>();
        if (null != allowed) {
            for (String productId : allowed) {
                if (null != productId) {
                    allowedAsInts.add(Integer.parseInt(productId));
                }
            }
        }
        return allowedAsInts;
    }

    private void updateAllowedNotAllowedProductMix(ProductMixFormBean formBean) {
        String[] allowed = formBean.getAllowed();
        if (null != allowed) {
            for (String allowedKey : allowed) {
                if (!formBean.getAllowedProductOptions().containsKey(allowedKey)) {
                    String productName = formBean.getNotAllowedProductOptions().get(allowedKey);
                    if (null != allowedKey && null != productName) {
                        formBean.getAllowedProductOptions().put(allowedKey, productName);
                        formBean.getNotAllowedProductOptions().remove(allowedKey);
                    }
                }
            }
        }

        String[] notAllowed = formBean.getNotAllowed();
        if (null != notAllowed) {
            for (String notAllowedKey : notAllowed) {
                if (!formBean.getNotAllowedProductOptions().containsKey(notAllowedKey)) {
                    String productName = formBean.getAllowedProductOptions().get(notAllowedKey);
                    if (null != notAllowedKey && null != productName) {
                        formBean.getNotAllowedProductOptions().put(notAllowedKey, productName);
                        formBean.getAllowedProductOptions().remove(notAllowedKey);
                    }
                }
            }
        }                        
    }

    /* Need to reset those attributes manually due to problem with reset them by Spring when select box is empty - morzechowski@soldevelo.com */
    private void resetAllowedAndNotAllowed(ProductMixFormBean formBean) {
        formBean.setAllowed(null);
        formBean.setNotAllowed(null);
    }
}