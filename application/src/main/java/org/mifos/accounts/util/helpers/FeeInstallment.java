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

package org.mifos.accounts.util.helpers;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.mifos.accounts.business.AccountFeesEntity;
import org.mifos.accounts.fees.util.helpers.FeeFrequencyType;
import org.mifos.application.holiday.business.Holiday;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.config.FiscalCalendarRules;
import org.mifos.framework.util.helpers.Money;
import org.mifos.schedule.ScheduledDateGeneration;
import org.mifos.schedule.ScheduledEvent;
import org.mifos.schedule.ScheduledEventFactory;
import org.mifos.schedule.internal.HolidayAndWorkingDaysScheduledDateGeneration;

public class FeeInstallment {

    private Short installmentId;
    private Money accountFee;
    private AccountFeesEntity accountFeesEntity = null;

    public static FeeInstallment buildFeeInstallment(final Short installmentId, final Money accountFeeAmount,
            final AccountFeesEntity accountFee) {
        FeeInstallment feeInstallment = new FeeInstallment();
        feeInstallment.setInstallmentId(installmentId);
        feeInstallment.setAccountFee(accountFeeAmount);
        feeInstallment.setAccountFeesEntity(accountFee);
        accountFee.setAccountFeeAmount(accountFeeAmount);
        return feeInstallment;
    }

    public static List<FeeInstallment> createFeeInstallments(final List<InstallmentDate> installmentDates,
            List<AccountFeesEntity> accountFees) {

        List<FeeInstallment> feeInstallmentList = new ArrayList<FeeInstallment>();

        for (AccountFeesEntity accountFeesEntity : accountFees) {
            if (accountFeesEntity.isActive()) {
                Short accountFeeType = accountFeesEntity.getFees().getFeeFrequency().getFeeFrequencyType().getId();
                if (accountFeeType.equals(FeeFrequencyType.ONETIME.getValue())) {

                    feeInstallmentList.add(FeeInstallment.handleOneTime(accountFeesEntity, installmentDates));

                } else if (accountFeeType.equals(FeeFrequencyType.PERIODIC.getValue())) {
                    feeInstallmentList.addAll(FeeInstallment.handlePeriodic(accountFeesEntity, installmentDates));
                }
            }
        }

        return feeInstallmentList;
    }

    public static FeeInstallment handleOneTime(final AccountFeesEntity accountFee,
            final List<InstallmentDate> installmentDates) {
        Money accountFeeAmount = accountFee.getAccountFeeAmount();
        Date feeDate = installmentDates.get(0).getInstallmentDueDate();
        Short installmentId = InstallmentDate.findMatchingInstallmentId(installmentDates, feeDate);
        return FeeInstallment.buildFeeInstallment(installmentId, accountFeeAmount, accountFee);
    }

    public static List<FeeInstallment> handlePeriodic(final AccountFeesEntity accountFee,
            final List<InstallmentDate> installmentDates) {

        Money accountFeeAmount = accountFee.getAccountFeeAmount();
        MeetingBO feeMeetingFrequency = accountFee.getFees().getFeeFrequency().getFeeMeetingFrequency();

        DateTime startFromMeetingDate = new DateTime(feeMeetingFrequency.getMeetingStartDate());
        ScheduledEvent scheduledEvent = ScheduledEventFactory.createScheduledEventFrom(feeMeetingFrequency);

        List<Days> workingDays = new FiscalCalendarRules().getWorkingDaysAsJodaTimeDays();
        List<Holiday> noHolidays = new ArrayList<Holiday>();

        ScheduledDateGeneration dateGeneration = new HolidayAndWorkingDaysScheduledDateGeneration(workingDays,
                noHolidays);
        // FIXME - #00003 - keithw - should generate dates upto last repayment date got from non-holiday adjusted scheduledDates
        List<DateTime> feeDates = dateGeneration.generateScheduledDates(10, startFromMeetingDate, scheduledEvent);

        List<FeeInstallment> feeInstallmentList = new ArrayList<FeeInstallment>();
        for (DateTime feeDate : feeDates) {
            Short installmentId = InstallmentDate.findMatchingInstallmentId(installmentDates, feeDate.toDate());
            feeInstallmentList.add(FeeInstallment.buildFeeInstallment(installmentId, accountFeeAmount, accountFee));
        }

        return feeInstallmentList;
    }

    public static List<FeeInstallment> mergeFeeInstallments(final List<FeeInstallment> feeInstallmentList) {
        List<FeeInstallment> newFeeInstallmentList = new ArrayList<FeeInstallment>();
        for (Iterator<FeeInstallment> iterator = feeInstallmentList.iterator(); iterator.hasNext();) {
            FeeInstallment feeInstallment = iterator.next();
            iterator.remove();
            FeeInstallment feeInstTemp = null;
            for (FeeInstallment feeInst : newFeeInstallmentList) {
                if (feeInst.getInstallmentId().equals(feeInstallment.getInstallmentId())
                        && feeInst.getAccountFeesEntity().equals(feeInstallment.getAccountFeesEntity())) {
                    feeInstTemp = feeInst;
                    break;
                }
            }
            if (feeInstTemp != null) {
                newFeeInstallmentList.remove(feeInstTemp);
                feeInstTemp.setAccountFee(feeInstTemp.getAccountFee().add(feeInstallment.getAccountFee()));
                newFeeInstallmentList.add(feeInstTemp);
            } else {
                newFeeInstallmentList.add(feeInstallment);
            }
        }
        return newFeeInstallmentList;
    }

    public Money getAccountFee() {
        return accountFee;
    }

    public void setAccountFee(Money accountFee) {
        this.accountFee = accountFee;
    }

    public AccountFeesEntity getAccountFeesEntity() {
        return accountFeesEntity;
    }

    public void setAccountFeesEntity(AccountFeesEntity accountFeesEntity) {
        this.accountFeesEntity = accountFeesEntity;
    }

    public Short getInstallmentId() {
        return installmentId;
    }

    public void setInstallmentId(Short installmentId) {
        this.installmentId = installmentId;
    }

}
