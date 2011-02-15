/*
 * Copyright (c) 2005-2011 Grameen Foundation USA
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

package org.mifos.dto;

import junit.framework.Assert;

import org.junit.Test;
import org.mifos.dto.domain.CustomFieldDto;

@SuppressWarnings("PMD.SignatureDeclareThrowsException")
public class CustomFieldDtoTest {

    @Test
    public void shouldBeEqual() throws Exception {
        // The main point here is that we shouldn't get
        // NullPointerException for these operations.

        CustomFieldDto view = new CustomFieldDto();
        Assert.assertEquals("org.mifos.dto.domain.CustomFieldDto@0", view.toString());
        view.hashCode();

        CustomFieldDto view2 = new CustomFieldDto();
        Assert.assertTrue(view.equals(view2));
    }

}
