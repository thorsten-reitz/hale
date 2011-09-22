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

package eu.esdihumboldt.hale.ui.views.properties.definition;

import org.eclipse.jface.viewers.IFilter;

import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.schema.model.Definition;

/**
 * Filter that lets only {@link Definition}s with a description that is not 
 * <code>null</code> pass.
 * @author Simon Templer
 */
public class DefinitionDescriptionFilter implements IFilter {

	/**
	 * @see IFilter#select(Object)
	 */
	@Override
	public boolean select(Object toTest) {
		if (toTest instanceof EntityDefinition) {
			return ((EntityDefinition) toTest).getDefinition().getDescription() != null;
		}
		
		return false;
	}

}