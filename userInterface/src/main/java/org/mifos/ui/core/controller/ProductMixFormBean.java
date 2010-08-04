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

import java.util.LinkedHashMap;
import java.util.Map;

@SuppressWarnings("PMD")
@edu.umd.cs.findbugs.annotations.SuppressWarnings(value="EI_EXPOSE_REP", justification="..")
public class ProductMixFormBean {

    private String productTypeId;
    private String productId;
    private String[] allowed;
    private String[] notAllowed;
    private Map<String, String> productTypeOptions = new LinkedHashMap<String, String>();
    private Map<String, String> productNameOptions = new LinkedHashMap<String, String>();
    private Map<String, String> allowedProductOptions = new LinkedHashMap<String, String>();
    private Map<String, String> notAllowedProductOptions = new LinkedHashMap<String, String>();

    public String getProductTypeId() {
        return this.productTypeId;
    }

    public void setProductTypeId(String productTypeId) {
        this.productTypeId = productTypeId;
    }

    public Map<String, String> getProductNameOptions() {
        return this.productNameOptions;
    }

    public void setProductNameOptions(Map<String, String> productNameOptions) {
        this.productNameOptions = productNameOptions;
    }

    public Map<String, String> getProductTypeOptions() {
        return this.productTypeOptions;
    }

    public void setProductTypeOptions(Map<String, String> productTypeOptions) {
        this.productTypeOptions = productTypeOptions;
    }

    public String getProductId() {
        return this.productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public Map<String, String> getAllowedProductOptions() {
        return this.allowedProductOptions;
    }

    public void setAllowedProductOptions(Map<String, String> allowedProductOptions) {
        this.allowedProductOptions = allowedProductOptions;
    }

    public Map<String, String> getNotAllowedProductOptions() {
        return this.notAllowedProductOptions;
    }

    public void setNotAllowedProductOptions(Map<String, String> notAllowedProductOptions) {
        this.notAllowedProductOptions = notAllowedProductOptions;
    }

    public String[] getAllowed() {
        return this.allowed;
    }

    public void setAllowed(String[] allowed) {
        this.allowed = allowed;
    }

    public String[] getNotAllowed() {
        return this.notAllowed;
    }

    public void setNotAllowed(String[] notAllowed) {
        this.notAllowed = notAllowed;
    }
}