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
import org.mifos.accounts.business.AccountActionDateEntity;
import org.mifos.accounts.business.AccountPenaltiesEntity;
import org.mifos.accounts.loan.business.LoanBO;
import org.mifos.accounts.loan.business.LoanScheduleEntity;
import org.mifos.accounts.penalties.business.AmountPenaltyBO;
import org.mifos.accounts.penalties.business.RatePenaltyBO;
import org.mifos.accounts.penalties.util.helpers.PenaltyPeriod;
import org.mifos.accounts.penalties.util.helpers.PenaltyStatus;
import org.mifos.application.NamedQueryConstants;
import org.mifos.framework.components.batchjobs.SchedulerConstants;
import org.mifos.framework.components.batchjobs.TaskHelper;
import org.mifos.framework.components.batchjobs.exceptions.BatchJobException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.DateTimeService;
import org.mifos.framework.util.helpers.Money;

public class ApplyPenaltyToLoanAccountsHelper extends TaskHelper {
    
    public ApplyPenaltyToLoanAccountsHelper() {
        super();
    }
    
    @Override
    public void execute(final long timeInMillis) throws BatchJobException {
        final List<String> errorList = new ArrayList<String>();
        List<LoanBO> loanAccounts = null;

        try {
            Query select = StaticHibernateUtil.getSessionTL().getNamedQuery(NamedQueryConstants.GET_ALL_LOAN_ACCOUNTS_WITH_PENALTIES);
            select.setDate("currentDate", new Date(timeInMillis));
            
            loanAccounts =  new ArrayList<LoanBO>(castList(LoanBO.class, select.list()));
        } catch (Exception e) {
            throw new BatchJobException(e);
        }

        if (loanAccounts != null && !loanAccounts.isEmpty()) {
            Integer loanAccountId = null;

            try {
                for (LoanBO loanAccount : loanAccounts) {
                    loanAccountId = loanAccount.getAccountId();
                    final List<AccountPenaltiesEntity> penaltyEntities = new ArrayList<AccountPenaltiesEntity>(loanAccount.getAccountPenalties());
                    final List<LoanScheduleEntity> schedule = new ArrayList<LoanScheduleEntity>(loanAccount.getLoanScheduleEntities());
                    List<AccountActionDateEntity> lateInstallments = loanAccount.getDetailsOfLateInstallmentsOn(new Date(timeInMillis));
                    final Short installmentId = lateInstallments.get(0).getInstallmentId();
                    
                    for (AccountPenaltiesEntity penaltyEntity : penaltyEntities) {
                        Days days = Days.daysBetween(new LocalDate(schedule.get(installmentId - 1).getActionDate().getTime()),
                                                     new LocalDate(timeInMillis));
                        
                        if(penaltyEntity.hasPeriodType()) {
                            PenaltyPeriod penaltyPeriod = penaltyEntity.getPenalty().getPeriodType().getPenaltyPeriod();
                            Integer duration = penaltyEntity.getPenalty().getPeriodDuration();
                            
                            if(penaltyPeriod == PenaltyPeriod.DAYS && days.getDays() < duration
                                    || penaltyPeriod == PenaltyPeriod.INSTALLMENTS && lateInstallments.size() < duration + 1) {
                                continue;
                            }
                        }
                        
                        if (!penaltyEntity.isOneTime()) {
                            if (penaltyEntity.isDailyTime() && days.getDays() == penaltyEntity.getCalculativeCount()
                                    || penaltyEntity.isMonthlyTime() && days.getDays() % 31 != 1
                                    || penaltyEntity.isWeeklyTime() && days.getDays() % 7 != 1) {
                                continue;
                            }
                        } else {
                            if(penaltyEntity.getCalculativeCount() >= 1) {
                                continue;
                            }
                        }
                        
                        if (penaltyEntity.isAmountPenalty()) {
                            addAmountPenalty(penaltyEntity, loanAccount, schedule.get(installmentId - 1));
                        } else {
                            addRatePenalty(penaltyEntity, loanAccount, schedule.get(installmentId - 1));
                        }
                    }
                }
            } catch (Exception e) {
                getLogger().error(
                        "ApplyPenaltyToLoanAccountsTask execute failed with exception " + e.getClass().getName() + ": "
                                + e.getMessage() + " at loan account " + loanAccountId.toString(), e);

                StaticHibernateUtil.rollbackTransaction();
                errorList.add(loanAccountId.toString());
            } finally {
                StaticHibernateUtil.closeSession();
            }
        }

        if (!errorList.isEmpty()) {
            throw new BatchJobException(SchedulerConstants.FAILURE, errorList);
        }
    }

    private void addAmountPenalty(final AccountPenaltiesEntity penaltyEntity, final LoanBO loanAccount, final LoanScheduleEntity loanScheduleEntity) {
        final AmountPenaltyBO penalty = (AmountPenaltyBO) penaltyEntity.getPenalty();
        
        final Money charge = penaltyEntity.getAccountPenaltyAmount();
        
        loanAccount.applyPenalty(penalty.getPenaltyName(), charge, loanScheduleEntity);
        penaltyEntity.incrementCalculativeCount();
        
        try {
            StaticHibernateUtil.startTransaction();
            StaticHibernateUtil.getSessionTL().update(loanAccount);
            StaticHibernateUtil.commitTransaction();
        } catch (Exception e) {
            getLogger().error(e.getMessage());
            StaticHibernateUtil.rollbackTransaction();
        }
    }
    
    private void addRatePenalty(final AccountPenaltiesEntity penaltyEntity, final LoanBO loanAccount, final LoanScheduleEntity loanScheduleEntity) {
        final RatePenaltyBO penalty = (RatePenaltyBO) penaltyEntity.getPenalty();
        final Double radio = penaltyEntity.getAccountPenaltyAmount().getAmount().doubleValue() / 100.0d;
        Money charge = null;
        
        if(penalty.isOutstandingPrincipalAmount()) {
            charge = loanAccount.getLoanSummary().getOriginalPrincipal().multiply(radio);
        } else if(penalty.isOutstandingLoanAmount()) {
            charge = loanAccount.getLoanSummary().getOutstandingBalance().multiply(radio);
        } else if(penalty.isOverdueAmountDue()) {
            charge = loanScheduleEntity.getTotalDue().multiply(radio);
        } else if(penalty.isOverduePrincipal()) {
            charge = loanScheduleEntity.getPrincipalDue().multiply(radio);
        }
        
        loanAccount.applyPenalty(penalty.getPenaltyName(), charge, loanScheduleEntity);
        
        try {
            StaticHibernateUtil.startTransaction();
            StaticHibernateUtil.getSessionTL().update(loanAccount);
            StaticHibernateUtil.commitTransaction();
        } catch (Exception e) {
            getLogger().error(e.getMessage());
            StaticHibernateUtil.rollbackTransaction();
        }
    }

    private <T> List<T> castList(final Class<? extends T> clazz, final Collection<?> collection) {
        List<T> results = new ArrayList<T>(collection.size());

        for (Object item : collection) {
            results.add(clazz.cast(item));
        }

        return results;
    }

}
