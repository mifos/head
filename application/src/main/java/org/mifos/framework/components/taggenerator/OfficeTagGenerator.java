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

package org.mifos.framework.components.taggenerator;

import org.mifos.customers.office.business.OfficeBO;
import org.mifos.framework.business.BusinessObject;
import org.mifos.framework.struts.tags.MifosTagUtils;

public class OfficeTagGenerator extends TagGenerator {

    public OfficeTagGenerator() {
    }

    @Override
    protected StringBuilder build(BusinessObject obj, Object randomNum) {
        return build(obj, false, randomNum);
    }

    @Override
    protected StringBuilder build(BusinessObject obj, boolean selfLinkRequired, Object randomNum) {
        OfficeBO office = (OfficeBO) obj;
        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append("<a href=\"custSearchAction.do?method=getOfficeHomePage&officeId=");
        strBuilder.append(office.getOfficeId());
        strBuilder.append("&officeName=");
        strBuilder.append(MifosTagUtils.xmlEscape(office.getOfficeName()));
        strBuilder.append("&randomNum=");
        strBuilder.append(randomNum);
        strBuilder.append("\">");
        strBuilder.append(MifosTagUtils.xmlEscape(office.getOfficeName()));
        strBuilder.append("</a>");
        return strBuilder;
    }
}
