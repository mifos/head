package org.mifos.application.importexport.servicefacade;

import java.io.InputStream;

import org.mifos.dto.domain.ParsedLoansDto;
import org.mifos.dto.domain.ParsedSavingsDto;
import org.springframework.security.access.prepost.PreAuthorize;

public interface ImportLoansSavingsFacade {
    //TODO: ROLE_CAN_IMPORT_CLIENTS is temporary, loans and savings should have own roles
    /**
     * Parse loan accounts data.
     * @param stream
     * @return
     */
    @PreAuthorize("isFullyAuthenticated() and hasRole('ROLE_CAN_IMPORT_CLIENTS')")
    ParsedLoansDto parseImportLoans(InputStream stream);
    /**
     * Saves parsed loan accounts data.
     * @param parsedLoansDto
     * @return
     */
    @PreAuthorize("isFullyAuthenticated() and hasRole('ROLE_CAN_IMPORT_CLIENTS')")
    ParsedLoansDto saveLoans(ParsedLoansDto parsedLoansDto);
    @PreAuthorize("isFullyAuthenticated() and hasRole('ROLE_CAN_IMPORT_CLIENTS')")
    ParsedLoansDto createLoansDtoFromSingleError(String error);
    
    ParsedSavingsDto parseImportSavings(InputStream stream);
    
    ParsedSavingsDto saveSavings(ParsedSavingsDto parsedSavingsDto);

    ParsedSavingsDto createSavingsDtoFromSingleError(String error);
}
