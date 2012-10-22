package org.mifos.application.util.helpers;

import java.util.Comparator;

import org.mifos.dto.domain.LoanActivityDto;

public class LoanActivityDtoDataComperable implements Comparator<LoanActivityDto> {

    @Override
    public int compare(LoanActivityDto o1, LoanActivityDto o2) {
        return (o1.getActionDate().compareTo(o2.getActionDate()) > 0 ? 1 : 0);
    }

}
