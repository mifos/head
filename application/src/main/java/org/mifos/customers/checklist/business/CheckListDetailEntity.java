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

package org.mifos.customers.checklist.business;

import org.mifos.application.master.business.SupportedLocalesEntity;
import org.mifos.framework.business.PersistentObject;

public class CheckListDetailEntity extends PersistentObject {

    private final Integer detailId;

    private String detailText;

    private Short answerType;

    private final CheckListBO checkListBO;

    private SupportedLocalesEntity supportedLocales;

    public CheckListDetailEntity() {
        this.detailId = null;
        this.checkListBO = null;
    }

    public CheckListDetailEntity(String detailText, Short answerType, CheckListBO checkListBO, Short localeId) {
        this.detailId = null;
        this.detailText = detailText;
        this.answerType = answerType;
        this.checkListBO = checkListBO;
        this.supportedLocales = new SupportedLocalesEntity(localeId);
    }

    public Integer getDetailId() {
        return detailId;
    }

    public String getDetailText() {
        return this.detailText;
    }

    @SuppressWarnings("unused")
    // see .hbm.xml file
    private void setDetailText(String detailText) {
        this.detailText = detailText;
    }

    public Short getAnswerType() {
        return this.answerType;
    }

    @SuppressWarnings("unused")
    // see .hbm.xml file
    private void setAnswerType(Short answerType) {
        this.answerType = answerType;
    }

    public SupportedLocalesEntity getSupportedLocales() {
        return this.supportedLocales;
    }

    @SuppressWarnings("unused")
    // see .hbm.xml file
    private void setSupportedLocales(SupportedLocalesEntity supportedLocales) {
        this.supportedLocales = supportedLocales;
    }

    public CheckListBO getCheckListBO() {
        return checkListBO;
    }

}
