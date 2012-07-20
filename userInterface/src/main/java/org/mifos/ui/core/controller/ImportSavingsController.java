package org.mifos.ui.core.controller;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

import org.mifos.application.importexport.servicefacade.ImportLoansSavingsFacade;
import org.mifos.core.MifosRuntimeException;
import org.mifos.dto.domain.ParsedSavingsDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

@Controller
public class ImportSavingsController {
	private final ImportLoansSavingsFacade importLoansSavingsFacade;
	
	@Autowired
    public ImportSavingsController(ImportLoansSavingsFacade importLoansSavingFacade) {
        this.importLoansSavingsFacade = importLoansSavingFacade;
    }
	
	public ParsedSavingsDto parseFile(ImportSavingsFormBean importSavingsFormBean) {
        ParsedSavingsDto result = null;
        CommonsMultipartFile file = importSavingsFormBean.getFile();
        InputStream is = null;
        if (file == null) {
            throw new MifosRuntimeException("File cannot be null");
        }

        try {
            is = file.getInputStream();
            result = importLoansSavingsFacade.parseImportSavings(is);
        } catch (IOException ex) {
            result = importLoansSavingsFacade.createSavingsDtoFromSingleError(ex.getMessage());
        } finally {
            closeStream(is);
            importSavingsFormBean.setFile(null);
        }

        return result;
    }

    public ParsedSavingsDto save(ParsedSavingsDto parsedSavingsDto) {
    	importLoansSavingsFacade.saveSavings(parsedSavingsDto);
        return parsedSavingsDto;
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
