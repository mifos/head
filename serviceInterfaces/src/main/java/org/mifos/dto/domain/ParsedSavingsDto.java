package org.mifos.dto.domain;

import java.io.Serializable;
import java.util.List;

public class ParsedSavingsDto implements Serializable {

    private static final long serialVersionUID = -2481444295586262427L;

    private final List<String> parseErrors;
    private final List<ImportedSavingDetail> successfullyParsedRows;

    public ParsedSavingsDto(List<String> parseErrors, List<ImportedSavingDetail> successfullyParsedRows) {
        super();
        this.parseErrors = parseErrors;
        this.successfullyParsedRows = successfullyParsedRows;
    }

    public int getParsedRowsCount() {
        return successfullyParsedRows == null ? 0 : successfullyParsedRows.size();
    }

    public int getErrorRowsCount() {
        return parseErrors == null ? 0 : parseErrors.size();
    }

    public List<String> getParseErrors() {
        return parseErrors;
    }

    public List<ImportedSavingDetail> getSuccessfullyParsedRows() {
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
