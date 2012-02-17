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

package org.mifos.framework.components.taggenerator;

import org.mifos.customers.api.CustomerLevel;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.persistence.CustomerPersistence;
import org.mifos.framework.business.AbstractBusinessObject;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.struts.tags.MifosTagUtils;

public class CustomerTagGenerator extends TagGenerator {
    public CustomerTagGenerator() {
        setAssociatedGenerator(new OfficeTagGenerator());
    }

    @Override
    protected StringBuilder build(AbstractBusinessObject obj, Object randomNum) {
        return build(obj, true, randomNum);
    }

    @Override
    protected StringBuilder build(AbstractBusinessObject obj, boolean selfLinkRequired, Object randomNum) {
        CustomerBO customer = (CustomerBO) obj;

        try {
            CustomerBO customerReloaded = new CustomerPersistence().getCustomer(customer.getCustomerId());
            StringBuilder strBuilder = getAssociatedGenerator().build(customerReloaded.getOffice(), randomNum);
            if (strBuilder == null) {
                strBuilder = new StringBuilder();
            }

            buildLink(strBuilder, customerReloaded, customerReloaded, selfLinkRequired, randomNum);
            return strBuilder;

        } catch (PersistenceException e) {
            throw new RuntimeException(e);
        }
    }

    private void buildLink(StringBuilder strBuilder, CustomerBO customer, CustomerBO originalCustomer,
            boolean selfLinkRequired, Object randomNum) {
        if (customer == null) {
            return;
        }
        try {
            CustomerBO customerReloaded = new CustomerPersistence().getCustomer(customer.getCustomerId());
            buildLink(strBuilder, customerReloaded.getParentCustomer(), originalCustomer, selfLinkRequired, randomNum);
            strBuilder.append(" / ");
            createCustomerLink(strBuilder, customer, originalCustomer, selfLinkRequired, randomNum);
        } catch (PersistenceException e) {
            throw new RuntimeException(e);
        }
    }

    private void createCustomerLink(StringBuilder strBuilder, CustomerBO customer, CustomerBO originalCustomer,
            boolean selfLinkRequired, Object randomNum) {
        if (!customer.equals(originalCustomer)) {
            strBuilder.append("<a href=\"");
            strBuilder.append(getAction(customer));
            strBuilder.append(customer.getGlobalCustNum());
            strBuilder.append("&randomNum=");
            strBuilder.append(randomNum);
            strBuilder.append("\">");
            strBuilder.append(MifosTagUtils.xmlEscape(customer.getDisplayName()));
            strBuilder.append("</a>");
        } else if (selfLinkRequired) {
            strBuilder.append("<a href=\"");
            strBuilder.append(getAction(customer));
            strBuilder.append(customer.getGlobalCustNum());
            strBuilder.append("&randomNum=");
            strBuilder.append(randomNum);
            strBuilder.append("\">");
            strBuilder.append(MifosTagUtils.xmlEscape(customer.getDisplayName()));
            strBuilder.append("</a>");
        } else if (!selfLinkRequired) {
            strBuilder.append("<b>" + MifosTagUtils.xmlEscape(customer.getDisplayName()) + "</b>");
        }

    }

    private String getAction(CustomerBO customer) {
        if (customer.getCustomerLevel().getId().shortValue() == CustomerLevel.CENTER.getValue()) {
            return "viewCenterDetails.ftl?globalCustNum=";
        } else if (customer.getCustomerLevel().getId().shortValue() == CustomerLevel.GROUP.getValue()) {
            return "viewGroupDetails.ftl?globalCustNum=";
        } else if (customer.getCustomerLevel().getId().shortValue() == CustomerLevel.CLIENT.getValue()) {
            return "viewClientDetails.ftl?globalCustNum=";
        }
        return "";
    }

}
