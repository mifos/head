package org.mifos.framework.util.helpers;

import java.util.ArrayList;
import java.util.List;

public class TestObjectsHolder {

	List l = new ArrayList();
	public void addObject(Object obj){
		if(obj != null)
		l.add(obj);
	}
	
	public void removeObjects()
	{
		
		while(l.size() != 0){
			Object o = l.get(0);
			l.remove(0);
			o = null;
		}
		l = null;
		l = new ArrayList();
	}


}
