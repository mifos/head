package org.mifos.dto.domain;

import java.io.Serializable;
import java.util.List;
/**
 * Contains lists of parsed rows and rows with errors.
 * @author lgadomski
 */
public class ParsedLoansDto implements Serializable{

    private static final long serialVersionUID = 6217532551372070509L;
    private final List<String> parseErrors;
    private final List<ImportedLoanDetail> successfullyParsedRows;
    public ParsedLoansDto(List<String> parseErrors,
            List<ImportedLoanDetail> successfullyParsedRows) {
        super();
        this.parseErrors = parseErrors;
        this.successfullyParsedRows = successfullyParsedRows;
    }
    public int getParsedRowsCount(){
        return successfullyParsedRows==null ? 0 :successfullyParsedRows.size();
    }
    public int getErrorRowsCount(){
        return parseErrors==null ? 0 : parseErrors.size();
    }
    public List<String> getParseErrors() {
        return parseErrors;
    }
    public List<ImportedLoanDetail> getSuccessfullyParsedRows() {
        return successfullyParsedRows;
    }
    public boolean isInError() {
        return !parseErrors.isEmpty();
    }
    public boolean isReadyForSubmit() {
        return getParsedRowsCount() > 0;
    }
    public boolean isNoValidRows() {
        return successfullyParsedRows.isEmpty();
    }

}
