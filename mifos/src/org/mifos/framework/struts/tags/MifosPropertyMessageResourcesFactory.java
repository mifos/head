package org.mifos.framework.struts.tags;

import org.apache.struts.util.MessageResources;
import org.apache.struts.util.PropertyMessageResourcesFactory;

public class MifosPropertyMessageResourcesFactory extends PropertyMessageResourcesFactory{
    
   

	public MessageResources createResources(String config) {
        return new MifosPropertyMessageResources(this, config, this.returnNull);
    }
	
	 /**
	 * serial version UID for serailization
	 */
	private static final long serialVersionUID = 354254366564561L;
	
}
