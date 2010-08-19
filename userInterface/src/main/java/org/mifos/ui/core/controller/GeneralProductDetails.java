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

import java.util.Map;

public class GeneralProductDetails {

    private String name;
    private String shortName;
    private String description;

    private String selectedCategory;
    private Map<String, String> categoryOptions;

    private Integer startDateDay;
    private Integer startDateMonth;
    private String startDateYear;

    private Integer endDateDay;
    private Integer endDateMonth;
    private String endDateYear;

    private String selectedApplicableFor;
    private Map<String, String> applicableForOptions;

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShortName() {
        return this.shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSelectedCategory() {
        return this.selectedCategory;
    }

    public void setSelectedCategory(String selectedCategory) {
        this.selectedCategory = selectedCategory;
    }

    public Map<String, String> getCategoryOptions() {
        return this.categoryOptions;
    }

    public void setCategoryOptions(Map<String, String> categoryOptions) {
        this.categoryOptions = categoryOptions;
    }

    public Integer getStartDateDay() {
        return this.startDateDay;
    }

    public void setStartDateDay(Integer startDateDay) {
        this.startDateDay = startDateDay;
    }

    public Integer getStartDateMonth() {
        return this.startDateMonth;
    }

    public void setStartDateMonth(Integer startDateMonth) {
        this.startDateMonth = startDateMonth;
    }

    public String getStartDateYear() {
        return this.startDateYear;
    }

    public void setStartDateYear(String startDateYear) {
        this.startDateYear = startDateYear;
    }

    public Integer getEndDateDay() {
        return this.endDateDay;
    }

    public void setEndDateDay(Integer endDateDay) {
        this.endDateDay = endDateDay;
    }

    public Integer getEndDateMonth() {
        return this.endDateMonth;
    }

    public void setEndDateMonth(Integer endDateMonth) {
        this.endDateMonth = endDateMonth;
    }

    public String getEndDateYear() {
        return this.endDateYear;
    }

    public void setEndDateYear(String endDateYear) {
        this.endDateYear = endDateYear;
    }

    public String getSelectedApplicableFor() {
        return this.selectedApplicableFor;
    }

    public void setSelectedApplicableFor(String selectedApplicableFor) {
        this.selectedApplicableFor = selectedApplicableFor;
    }

    public Map<String, String> getApplicableForOptions() {
        return this.applicableForOptions;
    }

    public void setApplicableForOptions(Map<String, String> applicableForOptions) {
        this.applicableForOptions = applicableForOptions;
    }
}
