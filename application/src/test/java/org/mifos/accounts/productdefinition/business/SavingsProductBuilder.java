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
package org.mifos.accounts.productdefinition.business;

import java.util.Date;

import org.joda.time.DateTime;
import org.mifos.accounts.financial.business.GLCodeEntity;
import org.mifos.application.collectionsheet.persistence.MeetingBuilder;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.util.helpers.MeetingType;
import org.mifos.accounts.productdefinition.util.helpers.ApplicableTo;
import org.mifos.accounts.productdefinition.util.helpers.InterestCalcType;
import org.mifos.accounts.productdefinition.util.helpers.PrdStatus;
import org.mifos.accounts.productdefinition.util.helpers.SavingsType;
import org.mifos.framework.TestUtils;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.helpers.Money;

/**
 *
 */
public class SavingsProductBuilder {

    private final MeetingBO scheduleForInterestCalculationMeeting = new MeetingBuilder()
            .savingsInterestCalulationSchedule()
            .monthly().every(1).build();
    private final MeetingBO scheduleForInterestPostingMeeting = new MeetingBuilder().savingsInterestPostingSchedule()
            .monthly().every(1).build();
    private Money maxAmountOfWithdrawal = new Money(Money.getDefaultCurrency(), "50.0");
    private final Double interestRate = Double.valueOf("2.0");
    private SavingsType savingsType = SavingsType.VOLUNTARY;
    private final InterestCalcType interestCalcType = InterestCalcType.MINIMUM_BALANCE;
    
    // PRD_OFFERING FIELDS
    private String globalProductNumber = "XXXXX-1111";
    private final Date startDate = new DateTime().minusDays(14).toDate();
    private String name = "testProduct";
    private String shortName = "VS2";
    private final Date createdDate = new DateTime().minusDays(14).toDate();
    private final Short createdByUserId = TestUtils.makeUserWithLocales().getId();
    private final GLCodeEntity depositGLCode = new GLCodeEntity(Short.valueOf("1"), "10000");
    private final GLCodeEntity interesetGLCode = new GLCodeEntity(Short.valueOf("2"), "11000");
    private ApplicableTo applicableToCustomer = ApplicableTo.CENTERS;
    private ProductCategoryBO category = new ProductCategoryBO(Short.valueOf("1"), "savtest");
    private final PrdStatus productStatus = PrdStatus.SAVINGS_ACTIVE;
    private PrdStatusEntity productStatusEntity;
    
    public SavingsOfferingBO buildForUnitTests() {
        return build();
    }
    
    public SavingsOfferingBO buildForIntegrationTests() {
        category = (ProductCategoryBO) StaticHibernateUtil.getSessionTL().get(ProductCategoryBO.class,
                Short.valueOf("2"));
        
        productStatusEntity = (PrdStatusEntity) StaticHibernateUtil.getSessionTL().get(PrdStatusEntity.class,
                this.productStatus.getValue());

        return build();
    }

    private SavingsOfferingBO build() {
        final SavingsOfferingBO savingsProduct = new SavingsOfferingBO(savingsType, name, shortName,
                globalProductNumber, startDate, applicableToCustomer, category, productStatusEntity, interestCalcType,
                interestRate, maxAmountOfWithdrawal, depositGLCode, interesetGLCode, createdDate, createdByUserId);

        final PrdOfferingMeetingEntity scheduleForInstcalc = new PrdOfferingMeetingEntity(
                scheduleForInterestCalculationMeeting, savingsProduct,
                MeetingType.SAVINGS_INTEREST_CALCULATION_TIME_PERIOD);

        final PrdOfferingMeetingEntity scheduleForInterestPosting = new PrdOfferingMeetingEntity(
                scheduleForInterestPostingMeeting, savingsProduct, MeetingType.SAVINGS_INTEREST_POSTING);

        savingsProduct.setTimePerForInstcalc(scheduleForInstcalc);
        savingsProduct.setTimePerForInstcalc(scheduleForInterestPosting);
        return savingsProduct;
    }

    public SavingsProductBuilder withName(final String withName) {
        this.name = withName;
        this.globalProductNumber = "XXX-" + withName;
        return this;
    }

    public SavingsProductBuilder withShortName(final String withShortName) {
        this.shortName = withShortName;
        return this;
    }

    public SavingsProductBuilder appliesToCentersOnly() {
        this.applicableToCustomer = ApplicableTo.CENTERS;
        return this;
    }

    public SavingsProductBuilder appliesToGroupsOnly() {
        this.applicableToCustomer = ApplicableTo.GROUPS;
        return this;
    }

    public SavingsProductBuilder appliesToClientsOnly() {
        this.applicableToCustomer = ApplicableTo.CLIENTS;
        return this;
    }
    
    public SavingsProductBuilder mandatory() {
        this.savingsType = SavingsType.MANDATORY;
        return this;
    }

    public SavingsProductBuilder voluntary() {
        this.savingsType = SavingsType.VOLUNTARY;
        return this;
    }

    public SavingsProductBuilder withMaxWithdrawalAmount(final Money withMaxWithdrawal) {
        this.maxAmountOfWithdrawal = withMaxWithdrawal;
        return this;
    }
}
