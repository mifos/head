package org.mifos.application.util.helpers;

import java.util.Comparator;

import org.mifos.dto.screen.AccountPaymentDto;

public class AccountPaymentDtoComperator implements Comparator<AccountPaymentDto> {

    @Override
    public int compare(AccountPaymentDto o1, AccountPaymentDto o2) {
        return (o1.getPaymentId().compareTo(o2.getPaymentId()) > 0 ? 1 : 0);
    }

}
