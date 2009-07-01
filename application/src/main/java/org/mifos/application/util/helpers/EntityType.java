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

package org.mifos.application.util.helpers;

/**
 * This enum mirrors values contained in the ENTITY_MASTER table.
 */
public enum EntityType {

    CLIENT(1), LOANPRODUCT(2), SAVINGSPRODUCT(3), PRODUCT_CATEGORY(4), PRODUCT_CONFIGURATION(5), FEES(6), ACCOUNTS(7), ADMIN(
            8), CHECKLIST(9), CONFIGURATION(10), CUSTOMER(11), GROUP(12), LOGIN(13), MEETING(14), OFFICE(15), PENALTY(
            16), PERSONNEL(17), PROGRAM(18), ROLE_AND_PERMISSION(19), CENTER(20), SAVINGS(21), LOAN(22), BULK_ENTRY(23);

    Short value;

    EntityType(int value) {
        this.value = (short) value;
    }

    public Short getValue() {
        return value;
    }

    public static Short getEntityValue(String entity) {
        for (EntityType entityType : EntityType.values())
            if (entityType.name().equals(entity))
                return entityType.getValue();
        return null;
    }

    public static EntityType fromInt(int type) {
        for (EntityType candidate : EntityType.values()) {
            if (candidate.getValue() == type) {
                return candidate;
            }
        }
        throw new RuntimeException("no entity type " + type);
    }

}
