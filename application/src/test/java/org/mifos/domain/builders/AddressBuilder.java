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

package org.mifos.domain.builders;

import org.mifos.framework.business.util.Address;

/**
 *
 */
public class AddressBuilder {

    private String line1 = "line1";
    private String line2 = "line2";
    private String line3 = "line3";
    private String city = "some city";
    private String state = "some state";
    private String country = "some country";
    private String zip = "n/a";
    private String phoneNumber = "555-9191919";

    public static Address anAddress() {
        return new AddressBuilder().build();
    }

    public Address build() {
        return new Address(line1, line2, line3, city, state, country, zip, phoneNumber);
    }
}