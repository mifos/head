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

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;

@SuppressWarnings("PMD")
public class GeneralProductBean {

    private Integer id;

    @NotEmpty
    private String name;

    @Size(max=4)
    @NotEmpty
    private String shortName;

    private String description;

    @NotEmpty
    private String selectedCategory;
    private Map<String, String> categoryOptions;

    @Min(value=1)
    @Max(value=31)
    @NotNull
    private Integer startDateDay;

    @Min(value=1)
    @Max(value=12)
    @NotNull
    private Integer startDateMonth;

    @Size(max=4)
    @NotEmpty
    private String startDateYear;

    @Min(value=1)
    @Max(value=31)
    private Integer endDateDay;

    @Min(value=1)
    @Max(value=12)
    private Integer endDateMonth;

    @Size(max=4)
    private String endDateYear;

    @NotEmpty
    private String selectedApplicableFor;
    private Map<String, String> applicableForOptions;

    private String selectedStatus;
    private Map<String, String> statusOptions;

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

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSelectedStatus() {
        return this.selectedStatus;
    }

    public void setSelectedStatus(String selectedStatus) {
        this.selectedStatus = selectedStatus;
    }

    public Map<String, String> getStatusOptions() {
        return this.statusOptions;
    }

    public void setStatusOptions(Map<String, String> statusOptions) {
        this.statusOptions = statusOptions;
    }
}