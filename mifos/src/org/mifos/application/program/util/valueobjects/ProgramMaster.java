package org.mifos.application.program.util.valueobjects;

import org.mifos.framework.util.valueobjects.ValueObject;

public class ProgramMaster extends ValueObject{

	private Short programId;

	private String programName;

	public ProgramMaster() {
	}

	public ProgramMaster(Short programId, String programName) {

		this.programId = programId;
		this.programName = programName;
	}

	public void setProgramId(Short programId) {
		this.programId = programId;
	}

	public Short getProgramId() {
		return programId;
	}

	public void setProgramName(String programName) {
		this.programName = programName;
	}

	public String getProgramName() {
		return programName;
	}

}
