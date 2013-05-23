package org.mifos.platform.questionnaire.persistence;

import java.util.List;

import org.mifos.platform.persistence.GenericDao;
import org.mifos.platform.questionnaire.domain.Section;

public interface SectionDao extends GenericDao<Section, Integer>{
    List<Section> retrieveFromSectionId(Integer sectionId);
}
