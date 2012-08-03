package org.mifos.reports.admindocuments;

import java.util.List;

import org.mifos.dto.domain.AdminDocumentDto;
import org.springframework.security.access.prepost.PreAuthorize;

public interface AdminDocumentsServiceFacade {

    @PreAuthorize("isFullyAuthenticated()")
    List<AdminDocumentDto> getAdminDocumentsForAccountPayment(Integer paymentId);

}
