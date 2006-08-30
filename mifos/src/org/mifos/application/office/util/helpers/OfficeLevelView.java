package org.mifos.application.office.util.helpers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mifos.framework.business.View;

/**
 * It is helper class used for showing the list of offices
 */
public class OfficeLevelView extends View {
	
	private Map<Short, List> levelOffices = new HashMap<Short, List>();

	public OfficeLevelView() {
	}

	public void setLevelInfo(Short levelId, List parentOffices) {
		levelOffices.put(levelId, parentOffices);
	}

	public List getParentOffices(Short levelId) {
		return levelOffices.get(levelId);
	}

}
