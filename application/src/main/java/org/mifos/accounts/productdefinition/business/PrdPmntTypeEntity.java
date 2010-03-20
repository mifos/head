/*
 * Copyright (c) 2005-2010 Grameen Foundation USA
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

package org.mifos.accounts.productdefinition.business;

import org.mifos.application.master.business.PaymentTypeEntity;
import org.mifos.framework.business.PersistentObject;

public class PrdPmntTypeEntity extends PersistentObject {

    private Short prdPmntTypeId;

    private ProductTypeEntity prdType;

    private PaymentTypeEntity paymentType;

    private Short withdrawDepositFlag;

    private Short status;

    public PrdPmntTypeEntity() {
        super();

    }

    public Short getPrdPmntTypeId() {
        return prdPmntTypeId;
    }

    public void setPrdPmntTypeId(Short prdPmntTypeId) {

        this.prdPmntTypeId = prdPmntTypeId;
    }

    public ProductTypeEntity getPrdType() {
        return prdType;
    }

    public void setPrdType(ProductTypeEntity prdType) {
        this.prdType = prdType;
    }

    public PaymentTypeEntity getPaymentType() {
        return this.paymentType;
    }

    public void setPaymentType(PaymentTypeEntity paymentType) {
        this.paymentType = paymentType;
    }

    public Short getWithdrawDepositFlag() {
        return this.withdrawDepositFlag;
    }

    public void setWithdrawDepositFlag(Short withdrawDepositFlag) {
        this.withdrawDepositFlag = withdrawDepositFlag;
    }

    public Short getStatus() {
        return this.status;
    }

    public void setStatus(Short status) {
        this.status = status;
    }

}
