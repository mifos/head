package org.mifos.dto.screen;

import java.io.Serializable;
import java.util.Locale;

import org.joda.time.DateTime;

@SuppressWarnings("PMD")
@edu.umd.cs.findbugs.annotations.SuppressWarnings(value="SE_NO_SERIALVERSIONID", justification="should disable at filter level and also for pmd - not important for us")
public class PersonnelNoteDto implements Serializable {

    private final DateTime commentDate;
    private final String comment;
    private final String personnelName;

    public PersonnelNoteDto(DateTime commentDate, String comment, String personnelName) {
        this.commentDate = commentDate;
        this.comment = comment;
        this.personnelName = personnelName;
    }

    public String getCommentDateFormatted() {
        return org.joda.time.format.DateTimeFormat.forPattern("dd/MM/yyyy").withLocale(Locale.getDefault()).print(this.commentDate);
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