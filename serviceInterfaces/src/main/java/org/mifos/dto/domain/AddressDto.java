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

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;

@SuppressWarnings("PMD")
@edu.umd.cs.findbugs.annotations.SuppressWarnings(value="SE_NO_SERIALVERSIONID", justification="should disable at filter level and also for pmd - not important for us")
public class AddressDto implements Serializable {

    private final String line1;
    private final String line2;
    private final String line3;
    private final String city;
    private final String state;
    private final String country;
    private final String zip;
    private final String phoneNumber;

    public AddressDto(String line1, String line2, String line3, String city, String state, String country, String zip, String phoneNumber) {
        this.line1 = StringUtils.defaultIfEmpty(line1, "");
        this.line2 = StringUtils.defaultIfEmpty(line2, "");
        this.line3 = StringUtils.defaultIfEmpty(line3, "");
        this.city = StringUtils.defaultIfEmpty(city, "");
        this.state = StringUtils.defaultIfEmpty(state, "");
        this.country = StringUtils.defaultIfEmpty(country, "");
        this.zip = StringUtils.defaultIfEmpty(zip, "");
        this.phoneNumber = StringUtils.defaultIfEmpty(phoneNumber, "");
    }

    public String getLine1() {
        return this.line1;
    }


    public String getLine2() {
        return this.line2;
    }


    public String getLine3() {
        return this.line3;
    }


    public String getCity() {
        return this.city;
    }


    public String getState() {
        return this.state;
    }


    public String getCountry() {
        return this.country;
    }


    public String getZip() {
        return this.zip;
    }


    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    public String getDisplayAddress() {
        String displayAddress = "";
        if (StringUtils.isNotBlank(getLine1())) {
            displayAddress = getLine1();
        }
        if (StringUtils.isNotBlank(getLine2()) && StringUtils.isNotBlank(getLine1())) {
            displayAddress += ", " + getLine2();
        } else if (StringUtils.isNotBlank(getLine2())) {
            displayAddress = getLine2();
        }
        if (StringUtils.isNotBlank(getLine3()) && StringUtils.isNotBlank(getLine2())
                || (StringUtils.isNotBlank(getLine3()) && StringUtils.isNotBlank(getLine1()))) {
            displayAddress += ", " + getLine3();
        } else if (StringUtils.isNotBlank(getLine3())) {
            displayAddress += getLine3();
        }
        return displayAddress;
    }
}
