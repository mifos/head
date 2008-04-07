package org.mifos.framework.util.helpers;

import java.util.ArrayList;
import java.util.List;

public class TestObjectsHolder {

	List testObjects = new ArrayList();

	public void addObject(Object testObject){
		if (testObject != null) {
			testObjects.add(testObject);
		}
	}

	public void removeObjects()
	{
		while (testObjects.size() != 0){
			testObjects.remove(0);
		}
		
		// It is already zero size, so I suspect this
		// doesn't do anything...
		testObjects = new ArrayList();
	}


}
