package org.mifos.server;

import java.io.File;
import java.io.FilenameFilter;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarEntry;

import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.webapp.MetaInfConfiguration;
import org.eclipse.jetty.webapp.WebAppContext;

/**
 * MetaInfFolderConfiguration.
 * 
 * Extension of MetaInfConfiguration, which also scans META-INF of
 * all folders (not only JARs) on the classpath to find resources, web-fragment.xml, tld.
 * 
 * @see MetaInfConfiguration
 * 
 * @see https://bugs.eclipse.org/bugs/show_bug.cgi?id=330189
 * 
 * @author Michael Vorburger
 */
public class MetaInfFolderConfiguration extends MetaInfConfiguration {

	@Override
	public void preConfigure(final WebAppContext context) throws Exception {
		super.preConfigure(context); // let original MetaInfConfiguration do it's thing first
		
        final ArrayList<Resource> resources = new ArrayList<Resource>();
        final List<Resource> containerJARResources = context.getMetaData().getOrderedContainerJars();
		resources.addAll(containerJARResources);

        for (final Resource resource : resources) {
        	if (!resource.isDirectory()) {
        		// If it's not a directory (but a JAR), then the parent already handled it, so skip
        		continue;
        	}
        	
        	{
	        	final Resource metaInfResourcesResource = resource.addPath("META-INF/resources/");
				if (metaInfResourcesResource.exists() && context.isConfigurationDiscovered()) {
					addResource(context, METAINF_RESOURCES, metaInfResourcesResource);
				}
        	}
        	
        	{
	        	final Resource webFragmentResource = resource.addPath("META-INF/web-fragment.xml");
				if (webFragmentResource.exists() && context.isConfigurationDiscovered()) {
	                addResource(context, METAINF_FRAGMENTS, webFragmentResource);     
				}
        	}
        	
			// TODO TEST "*.tld" handling...
			final File dir = resource.getFile();
			final File[] tldFiles = dir.listFiles(new FilenameFilter() {
				@Override
				public boolean accept(File dir, String name) {
					return name.toLowerCase().endsWith(".tld");
				}
			});
			for (final File tldFile : tldFiles) {
                addResource(context, METAINF_TLDS, Resource.newResource(tldFile.toURI()));     
			}
		}
	}

	/**
	 * Patched version of original super.processEntry().
	 * 
	 * Same as original except that for META-INF/web-fragment.xml,
	 * it adds the actual resource, not the JAR or directory.
	 * 
	 * Only called by super.preConfigure(), not used by preConfigure() above.
	 */
	@Override
	protected void processEntry(WebAppContext context, URI jarUri, JarEntry entry) {
		final String name = entry.getName();

		if (!name.startsWith("META-INF/"))
			return;

		try {
			if (name.equals("META-INF/web-fragment.xml") && context.isConfigurationDiscovered()) {
				addResource(context, METAINF_FRAGMENTS, Resource.newResource("jar:" + jarUri + "!/META-INF/web-fragment.xml")); // CHANGED!!!
			} else if (name.equals("META-INF/resources/") && context.isConfigurationDiscovered()) {
				addResource(context, METAINF_RESOURCES, Resource.newResource("jar:" + jarUri + "!/META-INF/resources"));
			} else {
				final String lcname = name.toLowerCase();
				if (lcname.endsWith(".tld")) {
					addResource(context, METAINF_TLDS, Resource.newResource("jar:" + jarUri + "!/" + name));
				}
			}
		} catch (final Exception e) {
			context.getServletContext().log(jarUri + "!/" + name, e);
		}
	}
	
}
