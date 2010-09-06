package org.mifos.dto.screen;

import java.io.Serializable;

@SuppressWarnings("PMD")
@edu.umd.cs.findbugs.annotations.SuppressWarnings(value="SE_NO_SERIALVERSIONID", justification="should disable at filter level and also for pmd - not important for us")
public class CheckBoxItemDto implements Serializable{

    private final Short checklistId;
    private final String checklistName;
    private final Short checklistStatus;

    public CheckBoxItemDto(Short checklistId, String checklistName, Short checklistStatus) {
        super();
        this.checklistId = checklistId;
        this.checklistName = checklistName;
        this.checklistStatus = checklistStatus;
    }

    public Short getChecklistId() {
        return this.checklistId;
    }
    public String getChecklistName() {
        return this.checklistName;
    }
    public Short getChecklistStatus() {
        return this.checklistStatus;
    }
}
