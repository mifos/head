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

package org.mifos.application.accounts.financial.business;

/**
 * A "category" is a top-level general ledger account. These are the categories
 * that must be present in a Mifos instance.
 * <p>
 * Note that some subcategories must also exist in the chart of accounts, see
 * the <a
 * href="http://www.mifos.org/knowledge/functional-specs/accounting-in-mifos"
 * >documentation on accounting in Mifos</a> for more details.
 */
public enum GLCategoryType {
    ASSET, LIABILITY, INCOME, EXPENDITURE;

    /**
     * Convert a given string to the corresponding enum type.
     */
    public static GLCategoryType fromString(String typeAsString) {
        for (GLCategoryType t : values()) {
            if (t.toString().equals(typeAsString))
                return t;
        }
        throw new IllegalArgumentException("given string does not correspond" + " to any known category type");
    }
}
