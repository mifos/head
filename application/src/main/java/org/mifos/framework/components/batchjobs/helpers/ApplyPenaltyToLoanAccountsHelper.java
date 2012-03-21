/*
 * Copyright (c) 2005-2011 Grameen Foundation USA
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

package org.mifos.framework.components.batchjobs.helpers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.hibernate.Query;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.mifos.accounts.business.AccountPenaltiesEntity;
import org.mifos.accounts.loan.business.LoanBO;
import org.mifos.accounts.loan.business.LoanPenaltyScheduleEntity;
import org.mifos.accounts.loan.business.LoanScheduleEntity;
import org.mifos.accounts.penalties.business.AmountPenaltyBO;
import org.mifos.accounts.penalties.business.RatePenaltyBO;
import org.mifos.accounts.penalties.util.helpers.PenaltyPeriod;
import org.mifos.application.NamedQueryConstants;
import org.mifos.framework.components.batchjobs.SchedulerConstants;
import org.mifos.framework.components.batchjobs.TaskHelper;
import org.mifos.framework.components.batchjobs.exceptions.BatchJobException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.helpers.Money;

public class ApplyPenaltyToLoanAccountsHelper extends TaskHelper {
    private LocalDate currentLocalDate;
    private Date currentDate;

    @Override
    public void execute(final long timeInMillis) throws BatchJobException {
        setCurrentDates(timeInMillis);
        List<String> errorList = new ArrayList<String>();
        List<LoanBO> loanAccounts;

        try {
            loanAccounts = getLoanAccounts();
        } catch (Exception e) {
            throw new BatchJobException(e);
        }

        if (loanAccounts != null && !loanAccounts.isEmpty()) {
            Integer loanAccountId = null;

            try {
                for (LoanBO loanAccount : loanAccounts) {
                    loanAccountId = loanAccount.getAccountId();
                    List<AccountPenaltiesEntity> penaltyEntities =new ArrayList<AccountPenaltiesEntity>(loanAccount.getAccountPenalties());
                    List<LoanScheduleEntity> lateInstallments = loanAccount.getDetailsOfLateInstallmentsOn(currentLocalDate);

                    for (AccountPenaltiesEntity penaltyEntity : penaltyEntities) {
                        for (LoanScheduleEntity entity : lateInstallments) {
                            if (lateInstallments.get(0).getInstallmentId().equals(entity.getInstallmentId())
                                    && checkGracePeriod(lateInstallments, penaltyEntity)) {
                                continue;
                            }
                            
                            LoanPenaltyScheduleEntity penaltySchedule = entity.getPenaltyScheduleEntity(penaltyEntity.getPenalty().getPenaltyId());

                            if (checkPeriod(penaltyEntity, new LocalDate(entity.getActionDate().getTime()))
                                    || (penaltySchedule != null && penaltySchedule.isOn(currentLocalDate))) {
                                continue;
                            }

                            if (penaltyEntity.isAmountPenalty()) {
                                addAmountPenalty(penaltyEntity, loanAccount, entity);
                            } else {
                                addRatePenalty(penaltyEntity, loanAccount, entity);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                if (loanAccountId != null) {
                    getLogger().error(String.format("ApplyPenaltyToLoanAccountsTask execute failed with exception %s: %s at loan account %s",
                                    e.getClass().getName(), e.getMessage(), loanAccountId.toString()), e);

                    errorList.add(loanAccountId.toString());
                }

                StaticHibernateUtil.rollbackTransaction();
            } finally {
                StaticHibernateUtil.closeSession();
            }
        }

        if (!errorList.isEmpty()) {
            throw new BatchJobException(SchedulerConstants.FAILURE, errorList);
        }
    }

    private boolean checkPeriod(AccountPenaltiesEntity penaltyEntity, LocalDate installmentDate) {
        int days = Days.daysBetween(installmentDate, currentLocalDate).getDays();
        boolean check = false;
        boolean oneTime = penaltyEntity.isOneTime();

        if (oneTime && penaltyEntity.getLastAppliedDate() != null) {
            check = true;
        } else if (!oneTime && ((penaltyEntity.isMonthlyTime() && days % 31 != 1) || penaltyEntity.isWeeklyTime() && days % 7 != 1)) {
            check = true;
        }

        return check;
    }

    private boolean checkGracePeriod(final List<LoanScheduleEntity> lateInstallments,
            final AccountPenaltiesEntity penaltyEntity) {
        boolean check = false;

        if (penaltyEntity.hasPeriodType()) {
            int days = Days.daysBetween(new LocalDate(lateInstallments.get(0).getActionDate().getTime()), currentLocalDate).getDays();
            PenaltyPeriod penaltyPeriod = penaltyEntity.getPenalty().getPeriodType().getPenaltyPeriod();
            int duration = penaltyEntity.getPenalty().getPeriodDuration();

            switch (penaltyPeriod) {
            case DAYS:
                check = days - 1 <= duration;
                break;
            case INSTALLMENTS:
                if(lateInstallments.size() - 1 < duration) {
                    check = true;
                } else if (lateInstallments.size() -1 == duration) {
                    check = lateInstallments.get(duration).isOn(currentLocalDate.minusDays(1));
                } else {
                    check = false;
                }
                break;
            }
        }

        return check;
    }

    private void addAmountPenalty(final AccountPenaltiesEntity penaltyEntity, final LoanBO loanAccount,
            final LoanScheduleEntity loanScheduleEntity) {
        AmountPenaltyBO penalty = (AmountPenaltyBO) penaltyEntity.getPenalty();
        Money accountPenaltyAmount = penaltyEntity.getAccountPenaltyAmount();
        Money charge = verifyLimits(loanAccount.getTotalPenalty(accountPenaltyAmount.getCurrency(), penalty.getPenaltyId()),
                accountPenaltyAmount, penalty.getMinimumLimit(), penalty.getMaximumLimit());

        if (charge != null && charge.isGreaterThanZero()) {
            loanAccount.applyPenalty(charge, loanScheduleEntity.getInstallmentId(), penaltyEntity, currentDate);
        }

        try {
            StaticHibernateUtil.startTransaction();
            StaticHibernateUtil.getSessionTL().update(loanAccount);
            StaticHibernateUtil.commitTransaction();
        } catch (Exception e) {
            getLogger().error(e.getMessage());
            StaticHibernateUtil.rollbackTransaction();
        }
    }

    private void addRatePenalty(final AccountPenaltiesEntity penaltyEntity, final LoanBO loanAccount,
            final LoanScheduleEntity loanScheduleEntity) {
        RatePenaltyBO penalty = (RatePenaltyBO) penaltyEntity.getPenalty();
        Double radio = penaltyEntity.getAccountPenaltyAmount().getAmount().doubleValue() / 100.0d;
        Money charge = null;

        if (penalty.isOutstandingPrincipalAmount()) {
            charge = loanAccount.getLoanSummary().getOriginalPrincipal().multiply(radio);
        } else if (penalty.isOutstandingLoanAmount()) {
            charge = loanAccount.getLoanSummary().getOutstandingBalance().multiply(radio);
        } else if (penalty.isOverdueAmountDue()) {
            charge = loanScheduleEntity.getTotalDue().multiply(radio);
        } else if (penalty.isOverduePrincipal()) {
            charge = loanScheduleEntity.getPrincipalDue().multiply(radio);
        } else {
            charge = Money.zero();
        }

        Money totalPenalty = loanAccount.getTotalPenalty(charge.getCurrency(), penalty.getPenaltyId());
        charge = verifyLimits(totalPenalty, charge, penalty.getMinimumLimit(), penalty.getMaximumLimit());

        if (charge.isGreaterThanZero()) {
            loanAccount.applyPenalty(charge, loanScheduleEntity.getInstallmentId(), penaltyEntity, currentDate);
        }

        try {
            StaticHibernateUtil.startTransaction();
            StaticHibernateUtil.getSessionTL().update(loanAccount);
            StaticHibernateUtil.commitTransaction();
        } catch (Exception e) {
            getLogger().error(e.getMessage());
            StaticHibernateUtil.rollbackTransaction();
        }
    }

    private Money verifyLimits(final Money total, final Money charge, final Double min, final double max) {
        Money cash = charge;
        boolean zero = total.isZero();
        
        if (zero && charge.getAmount().doubleValue() < min) {
            cash = new Money(charge.getCurrency(), min);
        } else if (!zero && total.add(charge).getAmount().doubleValue() > max) {
            cash = new Money(charge.getCurrency(), max - total.getAmount().doubleValue());
        }
        
        return cash;
    }

    private List<LoanBO> getLoanAccounts() {
        Query select = StaticHibernateUtil.getSessionTL().getNamedQuery(NamedQueryConstants.GET_ALL_LOAN_ACCOUNTS_WITH_PENALTIES);
        select.setDate("currentDate", currentDate);

        return castList(LoanBO.class, select.list());
    }

    private <T> List<T> castList(final Class<? extends T> clazz, final Collection<?> collection) {
        List<T> results = new ArrayList<T>(collection.size());

        for (Object item : collection) {
            results.add(clazz.cast(item));
        }

        return results;
    }

    private void setCurrentDates(long time) {
        currentLocalDate = new LocalDate(time);
        currentDate = new Date(time);
    }

}