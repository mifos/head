package org.mifos.reports.pentaho.service;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.apache.commons.io.FileUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.mifos.accounts.business.AccountStateEntity;
import org.mifos.accounts.util.helpers.AccountState;
import org.mifos.application.admin.servicefacade.ViewOrganizationSettingsServiceFacade;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.reports.admindocuments.business.AdminDocAccStateMixBO;
import org.mifos.reports.admindocuments.business.AdminDocumentBO;
import org.mifos.reports.admindocuments.persistence.LegacyAdminDocumentDao;
import org.mifos.reports.pentaho.PentahoReport;
import org.mifos.reports.pentaho.params.AbstractPentahoParameter;
import org.mifos.reports.pentaho.params.PentahoInputParameter;
import org.mifos.reports.pentaho.util.PentahoOutputType;
import org.springframework.beans.factory.annotation.Autowired;

// TODO: Configure Jetty to properly bind SourceDB for Pentaho .prpt report testing

@Ignore
public class PentahoReportsServiceIntegrationTest extends MifosIntegrationTestCase {
    @Autowired
    PentahoReportsServiceImpl pentahoReportsServiceImpl;
    @Autowired
    LegacyAdminDocumentDao legacyAdminDocumentDao;
    @Autowired
    ViewOrganizationSettingsServiceFacade viewOrganizationSettingsServiceFacade;

    @Test
    public void testGetAdminReport() throws Exception {
        String adminDocumentUploadPath = viewOrganizationSettingsServiceFacade.getAdminDocumentStorageDirectory();
        String adminDocumentPath = "IntegrationTest_Loan_template.prpt";

        File adminDocumentSrc = new File(this.getClass().getResource("/Loan_template.prpt").toString()
                .replace("file:", ""));
        File adminDocumentDst = new File(adminDocumentUploadPath + "/" + adminDocumentPath);

        try {
            FileUtils.copyFile(adminDocumentSrc, adminDocumentDst);

            AdminDocumentBO adminDocumentBO = new AdminDocumentBO();
            adminDocumentBO.setAdmindocId(Short.valueOf("1"));
            adminDocumentBO.setAdminDocumentName("IntegrationTest_Loan_template");
            adminDocumentBO.setIsActive(Short.valueOf("1"));
            adminDocumentBO.setAdminDocumentIdentifier(adminDocumentPath);

            AdminDocAccStateMixBO adminDocAccStateMixBO = new AdminDocAccStateMixBO();
            AccountStateEntity accountStateEntity = new AccountStateEntity(AccountState.LOAN_APPROVED);
            adminDocAccStateMixBO.setAccountStateID(accountStateEntity);
            adminDocAccStateMixBO.setAdminDocumentID(adminDocumentBO);
            legacyAdminDocumentDao.createOrUpdate(adminDocumentBO);

            Map<String, AbstractPentahoParameter> params = new HashMap<String, AbstractPentahoParameter>();
            PentahoInputParameter entityIdParameter = new PentahoInputParameter();
            entityIdParameter.setParamName("entity_id");
            entityIdParameter.setValue("000100000000002");
            params.put("entity_id", entityIdParameter);

            Integer adminDocId = Integer.parseInt(adminDocumentBO.getAdmindocId().toString());

            for (PentahoOutputType outputType : PentahoOutputType.values()) {
                PentahoReport report = pentahoReportsServiceImpl.getAdminReport(adminDocId, outputType.getId(), params);

                Assert.assertTrue(report.getFileExtension() == outputType.getFileExtension());
                Assert.assertTrue(report.getContentType() == outputType.getContentType());
                Assert.assertTrue(report.getErrors().isEmpty());
            }
        } catch (Exception e) {
            throw e;
        } finally {
            if (adminDocumentDst.exists()) {
                adminDocumentDst.delete();
            }
        }
    }

}
