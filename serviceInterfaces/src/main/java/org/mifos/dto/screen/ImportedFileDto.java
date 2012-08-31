package org.mifos.dto.screen;

import org.joda.time.DateTime;

public class ImportedFileDto {

    private String importFileName;
    private DateTime submittedOn;
    private Boolean phaseOut;
    
    public ImportedFileDto(String importFileName, DateTime submittedOn, Boolean phaseOut) {
        this.importFileName = importFileName;
        this.submittedOn = submittedOn;
        this.phaseOut = phaseOut;
    }

    public String getImportedFileName() {
        return importFileName;
    }

    public void setImportedFileName(String importFileName) {
        this.importFileName = importFileName;
    }

    public DateTime getSubmittedOn() {
        return submittedOn;
    }

    public void setSubmittedOn(DateTime submittedOn) {
        this.submittedOn = submittedOn;
    }

    public Boolean getPhaseOut() {
        return phaseOut;
    }

    public void setPhaseOut(Boolean phaseOut) {
        this.phaseOut = phaseOut;
    }

}
