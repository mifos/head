/*
 * Copyright Grameen Foundation USA
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

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;
import org.springframework.binding.message.MessageBuilder;
import org.springframework.binding.message.MessageContext;

@SuppressWarnings("PMD")
@edu.umd.cs.findbugs.annotations.SuppressWarnings(value="SE_NO_SERIALVERSIONID", justification="required for spring web flow storage at a minimum - should disable at filter level and also for pmd")
public class AddressBean implements Serializable {

    private String address1;
    private String address2;
    private String address3;
    private String cityDistrict;
    private String state;
    private String country;
    private String postalCode;
    private String telephoneNumber;
    private boolean address1Mandatory;
    private boolean address2Hidden;
    private boolean address3Hidden;
    private boolean cityDistrictHidden;
    private boolean stateHidden;
    private boolean countryHidden;
    private boolean postalCodeHidden;

    public String getAddress1() {
        return this.address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return this.address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getAddress3() {
        return this.address3;
    }

    public void setAddress3(String address3) {
        this.address3 = address3;
    }

    public String getCityDistrict() {
        return this.cityDistrict;
    }

    public void setCityDistrict(String cityDistrict) {
        this.cityDistrict = cityDistrict;
    }

    public String getState() {
        return this.state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return this.country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPostalCode() {
        return this.postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getTelephoneNumber() {
        return this.telephoneNumber;
    }

    public void setTelephoneNumber(String telephoneNumber) {
        this.telephoneNumber = telephoneNumber;
    }

    public boolean isAddress1Mandatory() {
        return this.address1Mandatory;
    }

    public void setAddress1Mandatory(boolean address1Mandatory) {
        this.address1Mandatory = address1Mandatory;
    }

    public boolean isAddress2Hidden() {
        return this.address2Hidden;
    }

    public void setAddress2Hidden(boolean address2Hidden) {
        this.address2Hidden = address2Hidden;
    }

    public boolean isAddress3Hidden() {
        return this.address3Hidden;
    }

    public void setAddress3Hidden(boolean address3Hidden) {
        this.address3Hidden = address3Hidden;
    }

    public boolean isCityDistrictHidden() {
        return this.cityDistrictHidden;
    }

    public void setCityDistrictHidden(boolean cityDistrictHidden) {
        this.cityDistrictHidden = cityDistrictHidden;
    }

    public boolean isStateHidden() {
        return this.stateHidden;
    }

    public void setStateHidden(boolean stateHidden) {
        this.stateHidden = stateHidden;
    }

    public boolean isCountryHidden() {
        return this.countryHidden;
    }

    public void setCountryHidden(boolean countryHidden) {
        this.countryHidden = countryHidden;
    }

    public boolean isPostalCodeHidden() {
        return this.postalCodeHidden;
    }

    public void setPostalCodeHidden(boolean postalCodeHidden) {
        this.postalCodeHidden = postalCodeHidden;
    }

    public void validate(MessageContext messages, String parentObjectName) {
        if (this.address1Mandatory && StringUtils.isBlank(this.address1)) {
            messages.addMessage(new MessageBuilder().error().source("address1").code("NotEmpty." + parentObjectName + ".address.address1").defaultText("address1 is mandatory.").build());
        }
    }

    public String getDisplayAddress() {
        StringBuilder displayAddressBuilder = new StringBuilder();
        if (StringUtils.isNotBlank(this.address1)) {
            displayAddressBuilder.append(this.address1);
        }

        if (StringUtils.isNotBlank(this.address2) && StringUtils.isNotBlank(this.address1)) {
            displayAddressBuilder.append(", ").append(this.address2);
        } else if (StringUtils.isNotBlank(this.address2)) {
            displayAddressBuilder.append(this.address2);
        }

        if (StringUtils.isNotBlank(this.address3) && StringUtils.isNotBlank(this.address2)
                || (StringUtils.isNotBlank(this.address3) && StringUtils.isNotBlank(this.address1))) {
            displayAddressBuilder.append(", ").append(this.address3);
        } else if (StringUtils.isNotBlank(this.address3)) {
            displayAddressBuilder.append(", ").append(this.address3);
        }

        return displayAddressBuilder.toString();
    }
}