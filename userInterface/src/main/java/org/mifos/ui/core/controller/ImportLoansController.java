package org.mifos.ui.core.controller;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import org.mifos.application.importexport.servicefacade.ImportLoansSavingsFacade;
import org.mifos.core.MifosRuntimeException;
import org.mifos.dto.domain.ParsedLoansDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
@Controller
public class ImportLoansController {
    private final ImportLoansSavingsFacade importLSServiceFacade;

    @Autowired
    public ImportLoansController(ImportLoansSavingsFacade importLoansSavingFacade) {
        this.importLSServiceFacade = importLoansSavingFacade;
    }
    /**
     * Parse xls file.
     * @param importLoansFormBean
     * @return
     */
    public ParsedLoansDto parseFile(ImportLoansFormBean importLoansFormBean) {
        ParsedLoansDto result = null;
        CommonsMultipartFile file = importLoansFormBean.getFile();
        InputStream is = null;
        if (file == null) {
            throw new MifosRuntimeException("File cannot be null");
        }

        try {
            is = file.getInputStream();
            result = importLSServiceFacade.parseImportLoans(is);
        } catch (IOException ex) {
            result = importLSServiceFacade.createLoansDtoFromSingleError(ex.getMessage());

        } finally {
            closeStream(is);
            importLoansFormBean.setFile(null);
        }
        return result;
    }
    /**
     * Persists imported accounts' data
     * @param parsedLoansDto
     * @return
     */
    public ParsedLoansDto save(ParsedLoansDto parsedLoansDto) {
        //save loans and generate account numbers
        importLSServiceFacade.saveLoans(parsedLoansDto);
        //TODO: save loans with predefined account numbers
        return parsedLoansDto;
    }

    private void closeStream(Closeable stream) {
        if (stream != null) {
            try {
                stream.close();
            } catch (IOException ex) {
                throw new MifosRuntimeException(ex);
            }
        }
    }
}
