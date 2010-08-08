package org.mifos.dto.screen;

import org.joda.time.DateTime;

public class PersonnelNoteDto {

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
