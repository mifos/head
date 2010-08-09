package org.mifos.dto.screen;

import java.io.Serializable;

import org.joda.time.DateTime;

@SuppressWarnings("PMD")
@edu.umd.cs.findbugs.annotations.SuppressWarnings(value="SE_NO_SERIALVERSIONID", justification="should disable at filter level and also for pmd - not important for us")
public class PersonnelNoteDto implements Serializable {

    private final DateTime commentDate;
    private final String comment;
    private final String personnelName;

    public PersonnelNoteDto(DateTime commentDate, String comment, String personnelName) {
        super();
        this.commentDate = commentDate;
        this.comment = comment;
        this.personnelName = personnelName;
    }

    public DateTime getCommentDate() {
        return this.commentDate;
    }

    public String getComment() {
        return this.comment;
    }

    public String getPersonnelName() {
        return this.personnelName;
    }
}
