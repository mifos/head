package org.mifos.reports.admindocuments.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.BooleanUtils;
import org.mifos.accounts.business.AccountPaymentEntity;
import org.mifos.accounts.business.AccountTrxnEntity;
import org.mifos.accounts.persistence.LegacyAccountDao;
import org.mifos.core.MifosRuntimeException;
import org.mifos.dto.domain.AdminDocumentDto;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.reports.admindocuments.AdminDocumentsServiceFacade;
import org.mifos.reports.admindocuments.business.AdminDocumentBO;
import org.mifos.reports.admindocuments.persistence.LegacyAdminDocumentDao;
import org.springframework.beans.factory.annotation.Autowired;

public class AdminDocumentsServiceImpl implements AdminDocumentsServiceFacade {

    @Autowired
    private LegacyAccountDao legacyAccountDao;

    @Autowired
    private LegacyAdminDocumentDao legacyAdminDocumentDao;

    @Override
    public List<AdminDocumentDto> getAdminDocumentsForAccountPayment(Integer paymentId) {
        try {
            List<AdminDocumentDto> adminDocuments = new ArrayList<AdminDocumentDto>();
            AccountPaymentEntity accountPaymentEntity = legacyAccountDao.findPaymentById(paymentId);
            Set<AccountTrxnEntity> accountTrxnEntities = accountPaymentEntity.getAccountTrxns();

            for (AccountTrxnEntity accountTrxnEntity : accountTrxnEntities) {
                List<AdminDocumentBO> adminDocumentBOs = legacyAdminDocumentDao
                        .getAdminDocumentsByAccountActionId(accountTrxnEntity.getAccountActionEntity().getId());
                if (adminDocumentBOs != null && !adminDocumentBOs.isEmpty()) {
                    for (AdminDocumentBO adminDocumentBO : adminDocumentBOs) {
                        AdminDocumentDto adminDocumentDto = new AdminDocumentDto(adminDocumentBO.getAdmindocId().intValue(),
                                adminDocumentBO.getAdminDocumentName(), adminDocumentBO.getAdminDocumentIdentifier(),
                                BooleanUtils.toBoolean(adminDocumentBO.getIsActive().intValue()));
                        if (!adminDocuments.contains(adminDocumentDto)){
                            adminDocuments.add(adminDocumentDto);
                        }
                    }
                }
            }

            return adminDocuments;
        } catch (PersistenceException e) {
            throw new MifosRuntimeException(e);
        }
    }

}
