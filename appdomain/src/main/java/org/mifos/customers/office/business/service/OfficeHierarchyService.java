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

package org.mifos.customers.office.business.service;

import java.util.List;

import org.mifos.accounts.productdefinition.business.GracePeriodTypeEntity;
import org.mifos.application.master.business.LookUpEntity;
import org.mifos.application.master.business.LookUpValueEntity;
import org.mifos.customers.office.business.OfficeLevelEntity;
import org.mifos.dto.domain.UpdateConfiguredOfficeLevelRequest;

public interface OfficeHierarchyService {

    void updateOfficeHierarchyConfiguration(UpdateConfiguredOfficeLevelRequest updateRequest);

    void updateApplicationLabels(List<OfficeLevelEntity> changedOfficeLabels, List<LookUpEntity> lookupEntities, List<GracePeriodTypeEntity> gracePeriods, List<LookUpValueEntity> accountStatuses);
}
