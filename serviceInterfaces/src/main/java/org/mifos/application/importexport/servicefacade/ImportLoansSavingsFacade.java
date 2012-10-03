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
    @PreAuthorize("isFullyAuthenticated() and hasRole('ROLE_CAN_IMPORT_LOANS')")
    ParsedLoansDto parseImportLoans(InputStream stream);
    /**
     * Saves parsed loan accounts data.
     * @param parsedLoansDto
     * @return
     */
    @PreAuthorize("isFullyAuthenticated() and hasRole('ROLE_CAN_IMPORT_LOANS')")
    ParsedLoansDto saveLoans(ParsedLoansDto parsedLoansDto);
    @PreAuthorize("isFullyAuthenticated() and hasRole('ROLE_CAN_IMPORT_LOANS')")
    ParsedLoansDto createLoansDtoFromSingleError(String error);
    
    @PreAuthorize("isFullyAuthenticated() and hasRole('ROLE_CAN_IMPORT_SAVINGS')")
    ParsedSavingsDto parseImportSavings(InputStream stream);
    
    @PreAuthorize("isFullyAuthenticated() and hasRole('ROLE_CAN_IMPORT_SAVINGS')")
    ParsedSavingsDto saveSavings(ParsedSavingsDto parsedSavingsDto);
    
    @PreAuthorize("isFullyAuthenticated() and hasRole('ROLE_CAN_IMPORT_SAVINGS')")
    ParsedSavingsDto createSavingsDtoFromSingleError(String error);
}
