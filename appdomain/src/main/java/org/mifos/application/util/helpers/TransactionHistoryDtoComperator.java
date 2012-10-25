package org.mifos.application.util.helpers;

import java.util.Comparator;

import org.mifos.dto.screen.TransactionHistoryDto;

public class TransactionHistoryDtoComperator implements Comparator<TransactionHistoryDto> {

    @Override
    public int compare(TransactionHistoryDto o1, TransactionHistoryDto o2) {
        return (o1.getPaymentId().compareTo(o2.getPaymentId()) > 0 ? 1 : 0);
    }

}
