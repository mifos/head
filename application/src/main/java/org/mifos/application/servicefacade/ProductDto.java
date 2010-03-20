/*
 * Copyright (c) 2005-2009 Grameen Foundation USA
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
package org.mifos.application.servicefacade;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;


/**
 * I am a DTO for PrdOfferingBO's
 */
public class ProductDto {

    private final Short id;
    private final String shortName;

    public ProductDto(final Short prdOfferingId, final String prdOfferingShortName) {
        this.id = prdOfferingId;
        this.shortName = prdOfferingShortName;
    }

    public Short getId() {
        return this.id;
    }

    public String getShortName() {
        return this.shortName;
    }

    @Override
    public boolean equals(final Object obj) {
        final ProductDto product = (ProductDto) obj;

        return new EqualsBuilder().reflectionEquals(this, product);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().reflectionHashCode(this);
    }
}
