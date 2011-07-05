/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2011.
 */

package eu.esdihumboldt.hale.ui.io.source.internal;

import de.fhg.igd.eclipse.util.extension.ExtensionObjectFactory;
import eu.esdihumboldt.hale.core.io.IOProviderFactory;
import eu.esdihumboldt.hale.ui.io.ImportSource;

/**
 * Interface for {@link ImportSource} factories provided by the 
 * {@link ImportSourceExtension}.
 * 
 * @author Simon Templer
 */
public interface ImportSourceFactory extends ExtensionObjectFactory<ImportSource<?, ?>> {

	/**
	 * Get the I/O provider factory type supported by the import source.
	 * @return the I/O provider factory type
	 */
	public Class<? extends IOProviderFactory<?>> getProviderFactoryType();
	
	/**
	 * Get the source description.
	 * @return the description or <code>null</code>
	 */
	public String getDescription();
	
}