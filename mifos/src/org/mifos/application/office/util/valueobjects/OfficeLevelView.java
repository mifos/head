
package org.mifos.application.office.util.valueobjects;

import java.io.Serializable;
import java.util.Map;
import java.util.HashMap;
import java.util.List;

/**
 * @author rajenders
 *
 */
public class OfficeLevelView implements Serializable{
	public static final long serialVersionUID=033;


	private Map<Short,List> levelOffices = new HashMap<Short,List>();


	public OfficeLevelView()
	{

	}


	public void setLevelInfo(Short levelId , List parentOffices)
	{
		levelOffices.put(levelId , parentOffices);
	}


	public List getParentOffices(Short levelId)
	{
		return levelOffices.get(levelId);
	}


}
