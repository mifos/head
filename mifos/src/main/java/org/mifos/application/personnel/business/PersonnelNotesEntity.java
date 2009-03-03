package org.mifos.application.personnel.business;

import java.util.Date;

import org.mifos.framework.business.PersistentObject;
import org.mifos.framework.util.DateTimeService;

public class PersonnelNotesEntity extends PersistentObject {

	private final Integer commentId;

	private final Date commentDate;

	private final String comment;

	private final PersonnelBO officer;

	private final PersonnelBO personnel;

	public PersonnelNotesEntity(String comment, PersonnelBO officer,
			PersonnelBO personnel) {
		super();
		this.comment = comment;
		this.officer = officer;
		this.personnel = personnel;
		this.commentId = null;
		this.commentDate = new DateTimeService().getCurrentJavaDateTime();
	}

	protected PersonnelNotesEntity() {
		super();
		this.commentId = null;
		this.officer = null;
		this.personnel = null;
		this.comment = null;
		this.commentDate = null;
	}

	public String getCommentDateStr() {
		return (commentDate != null) ? this.commentDate.toString() : "";
	}

	public Date getCommentDate() {
		return this.commentDate;
	}

	public String getComment() {
		return this.comment;
	}

	public String getPersonnelName() {
		return officer.getDisplayName();
	}

}
