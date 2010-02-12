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

package org.mifos.accounts.fees.business;

import org.mifos.framework.business.PersistentObject;

/**
 * A class that represents a row in the 'fee_payments_categories_type' table.
 * This class may be customized as it is never re-generated after being created.
 */
public class FeePaymentsCategoriesTypeEntity extends PersistentObject {

    private Short feePaymentsCategoryTypeId;

    private FeeTypeEntity feeType;

    private CategoryTypeEntity categoryType;

    private FeePaymentEntity feePayment;

    public FeePaymentsCategoriesTypeEntity() {
    }

    public Short getFeePaymentsCategoryTypeId() {
        return feePaymentsCategoryTypeId;
    }

    public void setFeePaymentsCategoryTypeId(Short feePaymentsCategoryTypeId) {

        this.feePaymentsCategoryTypeId = feePaymentsCategoryTypeId;
    }

    public FeePaymentEntity getFeePayment() {
        return this.feePayment;
    }

    public void setFeePayment(FeePaymentEntity feePayment) {
        this.feePayment = feePayment;
    }

    public CategoryTypeEntity getCategoryType() {
        return this.categoryType;
    }

    public void setCategoryType(CategoryTypeEntity categoryType) {
        this.categoryType = categoryType;
    }

    public FeeTypeEntity getFeeType() {
        return this.feeType;
    }

    public void setFeeType(FeeTypeEntity feeType) {
        this.feeType = feeType;
    }

}
