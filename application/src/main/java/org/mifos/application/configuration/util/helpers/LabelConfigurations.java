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

package org.mifos.application.configuration.util.helpers;

public enum LabelConfigurations {
    CLIENT_PARTIAL_APPLICATION(1), CLIENT_PENDING_APPROVAL(2), CLIENT_ACTIVE(3), CLIENT_ON_HOLD(4), CLIENT_CANCEL(5), CLIENT_CLOSED(
            6),

    GROUP_PARTIAL_APPLICATION(7), GROUP_PENDING_APPROVAL(8), GROUP_ACTIVE(9), GROUP_ON_HOLD(10), GROUP_CANCEL(11), GROUP_CLOSED(
            12),

    CENTER_ACTIVE(13), CENTER_INACTIVE(14),

    OFFICE_ACTIVE(15), OFFICE_INACTIVE(16),

    LOAN_PARTIAL_APPLICATION(17), LOAN_PENDING_APPROVAL(18), LOAN_APPROVED(19), LOAN_ACTIVE_GOOD_STANDING(21), LOAN_CLOSED_OBLIGATION_MET(
            22), LOAN_CLOSED_WRITTEN_OFF(23), LOAN_CLOSED_RESCHEDULE(24), LOAN_ACTIVE_BAD_STANDING(25), LOAN_CANCEL(141),

    SAVINGS_PARTIAL_APPLICATION(181), SAVINGS_PENDING_APPROVAL(182), SAVINGS_ACTIVE(184), SAVINGS_INACTIVE(210), SAVINGS_CANCEL(
            183), SAVINGS_CLOSED(185),

    FEE_CATEGORY_CLIENT(82), FEE_CATEGORY_GROUP(83), FEE_CATEGORY_CENTER(84), FEE_CATEGORY_LOAN(86),

    FEE_ACTIVE(165), FEE_INACTIVE(166),

    PERSONNEL_ACTIVE(152), PERSONNEL_INACTIVE(153),

    PRD_CATEGORY_ACTIVE(114), PRD_CATEGORY_INACTIVE(113),

    PRD_ACTIVE(115), PRD_INACTIVE(116);

    Integer value;

    LabelConfigurations(Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }
}
