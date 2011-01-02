package org.mifos.server;

import java.util.List;

import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.webapp.FragmentConfiguration;
import org.eclipse.jetty.webapp.MetaData;
import org.eclipse.jetty.webapp.WebAppContext;

/**
 * FragmentFolderConfiguration.
 * 
 * Extension of FragmentConfiguration to support web-fragment.xml
 * in folders as well as inside JARs.
 * 
 * @see FragmentConfiguration
 * 
 * @see https://bugs.eclipse.org/bugs/show_bug.cgi?id=330189
 * 
 * @author Michael Vorburger
 */
public class FragmentFolderConfiguration extends FragmentConfiguration {

	/**
	 * Overriden method which, contrary to the original implementation in the parent class,
	 * add directly the web-fragment.xml resource to the MetaData, instead of re-creating it
	 * with a forced jar prefix.
	 */
	@Override
    @SuppressWarnings("unchecked")
	public void findWebFragments(WebAppContext context, MetaData metaData) throws Exception {
		final List<Resource> frags = (List<Resource>)context.getAttribute(FRAGMENT_RESOURCES);
        if (frags!=null)
        {
            for (final Resource frag : frags)
            {
            	final Resource parentResource = Util.chop(frag.getURL(), "/META-INF/web-fragment.xml");
                metaData.addFragment(parentResource, frag);
            }
        }
	}

}
