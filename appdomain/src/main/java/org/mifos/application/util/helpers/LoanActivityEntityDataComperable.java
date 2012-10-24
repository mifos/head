package org.mifos.application.util.helpers;

import java.util.Comparator;

import org.mifos.accounts.loan.business.LoanActivityEntity;

public class LoanActivityEntityDataComperable implements Comparator<LoanActivityEntity> {

    @Override
    public int compare(LoanActivityEntity o1, LoanActivityEntity o2) {
        return (o1.getId().compareTo(o2.getId()) > 0 ? 1 : 0);
    }

}
