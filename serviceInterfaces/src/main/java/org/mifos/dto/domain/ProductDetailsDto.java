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

package org.mifos.dto.domain;

import org.joda.time.DateTime;

public class ProductDetailsDto {

    private Integer id;
    private String globalNumber;
    private Integer status;
    private final String name;
    private final String shortName;
    private final String description;
    private final Integer category;
    private String categoryName;
    private final DateTime startDate;
    private String startDateFormatted;
    private final DateTime endDate;
    private String endDateFormatted;
    private final Integer applicableFor;

    /**
     * minimal legal constructor for product creation request.
     */
    public ProductDetailsDto(String name, String shortName, String description, Integer category, DateTime startDate,
            DateTime endDate, Integer applicableFor) {
        this.name = name;
        this.shortName = shortName;
        this.description = description;
        this.category = category;
        this.startDate = startDate;
        this.endDate = endDate;
        this.applicableFor = applicableFor;
    }

    public String getName() {
        return this.name;
    }

    public String getShortName() {
        return this.shortName;
    }

    public String getDescription() {
        return this.description;
    }

    public Integer getCategory() {
        return this.category;
    }

    public DateTime getStartDate() {
        return this.startDate;
    }

    public DateTime getEndDate() {
        return this.endDate;
    }

    public Integer getApplicableFor() {
        return this.applicableFor;
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getGlobalNumber() {
        return this.globalNumber;
    }

    public void setGlobalNumber(String globalNumber) {
        this.globalNumber = globalNumber;
    }

    public String getCategoryName() {
        return this.categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public Integer getStatus() {
        return this.status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getStartDateFormatted() {
        return this.startDateFormatted;
    }

    public void setStartDateFormatted(String startDateFormatted) {
        this.startDateFormatted = startDateFormatted;
    }

    public String getEndDateFormatted() {
        return this.endDateFormatted;
    }

    public void setEndDateFormatted(String endDateFormatted) {
        this.endDateFormatted = endDateFormatted;
    }
}