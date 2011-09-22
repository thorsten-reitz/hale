/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */

package eu.esdihumboldt.hale.rcp.views.model.filtering;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import eu.esdihumboldt.hale.rcp.HALEActivator;
import eu.esdihumboldt.hale.rcp.views.model.ConfigurableModelContentProvider;
import eu.esdihumboldt.hale.Messages;

/**
 * TODO Explain the purpose of this type here.
 * 
 * @author Thorsten Reitz, Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class UseAggregationHierarchyAction 
	extends AbstractContentProviderAction {
	
	public UseAggregationHierarchyAction() {
		super.setIdentifier("UseAggregationHierarchyAction"); //$NON-NLS-1$
	}
	
	/**
	 * @see Action#getImageDescriptor()
	 */
	@Override
	public ImageDescriptor getImageDescriptor() {
		return AbstractUIPlugin.imageDescriptorFromPlugin(
				HALEActivator.PLUGIN_ID, "/icons/aggregation_hierarchy.png"); //$NON-NLS-1$
	}
	
	/**
	 * @see Action#getToolTipText()
	 */
	@Override
	public String getToolTipText() {
		return Messages.UseAggregationHierarchyAction_PropertyAggregationToolTipText;
	}

	/**
	 * @see AbstractContentProviderAction#updateContentProvider(ConfigurableModelContentProvider)
	 */
	@Override
	protected void updateContentProvider(
			ConfigurableModelContentProvider contentProvider) {
		contentProvider.setSuppressAggregation(!isChecked());
	}
}