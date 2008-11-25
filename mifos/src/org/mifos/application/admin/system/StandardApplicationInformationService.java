/**
 * 
 */
package org.mifos.application.admin.system;

import org.mifos.core.service.ApplicationInformationDto;
import org.mifos.core.service.ApplicationInformationService;

public class StandardApplicationInformationService implements
		ApplicationInformationService {

	private ApplicationInformationDto applicationInformationDto;
	
	public StandardApplicationInformationService() {
		applicationInformationDto = new ApplicationInformationDto();
	}
	public ApplicationInformationDto getApplicationInformation() {
		return applicationInformationDto;
	}

	public void setApplicationInformation(ApplicationInformationDto applicationInformationDto) {
		this.applicationInformationDto.setBuildId(applicationInformationDto.getBuildId());
		this.applicationInformationDto.setBuildTag(applicationInformationDto.getBuildTag());
		this.applicationInformationDto.setSvnRevision(applicationInformationDto.getSvnRevision());
	}

	public void setSystemInfo(SystemInfo info) {
		applicationInformationDto.setSvnRevision(info.getSvnRevision());
	}
}
