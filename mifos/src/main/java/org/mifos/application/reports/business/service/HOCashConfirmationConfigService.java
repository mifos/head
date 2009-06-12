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

package org.mifos.application.reports.business.service;

import static org.mifos.framework.util.TransformerUtils.TRANSFORM_STRING_TO_SHORT;

import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.framework.components.configuration.business.Configuration;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.util.helpers.DateUtils;
import org.springframework.core.io.Resource;

public class HOCashConfirmationConfigService extends CashConfirmationConfigService {

    private static final String PRODUCT_OFFERING_DISBURSEMENT_IDS = "product.offering.disbursement.ids";
    private static final String CENTER_RECOVERY_PRODUCT_OFFERING_IDS = "product.offering.recovery.ids";
    private static final String CENTER_ISSUE_PRODUCT_OFFERING_IDS = "product.offering.issue.ids";

    public HOCashConfirmationConfigService(Resource resource) {
        super(resource);
    }

    public Date getActionDate() {
        return DateUtils.currentDate();
    }

    public List<Short> getProductOfferingsForRecoveries() throws ServiceException {
        return getProductIds(CENTER_RECOVERY_PRODUCT_OFFERING_IDS);
    }

    public List<Short> getProductOfferingsForIssues() throws ServiceException {
        return getProductIds(CENTER_ISSUE_PRODUCT_OFFERING_IDS);
    }

    public List<Short> getProductOfferingsForDisbursements() throws ServiceException {
        return getProductIds(PRODUCT_OFFERING_DISBURSEMENT_IDS);
    }

    public MifosCurrency getCurrency() {
        return Configuration.getInstance().getSystemConfig().getCurrency();
    }

    private List<Short> getProductIds(String productIdKey) throws ServiceException {
        return (List<Short>) CollectionUtils.collect(getPropertyValues(productIdKey), TRANSFORM_STRING_TO_SHORT);
    }
}
