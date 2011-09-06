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

package eu.esdihumboldt.hale.ui.function.generic.pages;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import eu.esdihumboldt.hale.align.model.EntityDefinition;
import eu.esdihumboldt.hale.align.model.impl.TypeEntityDefinition;
import eu.esdihumboldt.hale.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.ui.common.definition.viewer.DefinitionLabelProvider;
import eu.esdihumboldt.hale.ui.common.definition.viewer.TypesContentProvider;
import eu.esdihumboldt.hale.ui.service.schema.SchemaService;
import eu.esdihumboldt.hale.ui.service.schema.SchemaSpaceID;

/**
 * Dialog for selecting a {@link TypeEntityDefinition}
 * @author Simon Templer
 */
public class TypeEntityDialog extends EntityDialog {

	/**
	 * @param parentShell
	 * @param ssid
	 */
	public TypeEntityDialog(Shell parentShell, SchemaSpaceID ssid, String title) {
		super(parentShell, ssid, title);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see EntityDialog#setupViewer(TreeViewer)
	 */
	@Override
	protected void setupViewer(TreeViewer viewer) {
		viewer.setLabelProvider(new DefinitionLabelProvider());
		viewer.setContentProvider(new TypesContentProvider(viewer));
		
		SchemaService ss = (SchemaService) PlatformUI.getWorkbench().getService(SchemaService.class);
		
		viewer.setInput(ss.getSchemas(ssid));
		
		//TODO filter?
	}

	/**
	 * @see EntityDialog#getEntityFromSelection(ISelection)
	 */
	@Override
	protected EntityDefinition getEntityFromSelection(ISelection selection) {
		if (!selection.isEmpty() && selection instanceof IStructuredSelection) {
			Object element = ((IStructuredSelection) selection).getFirstElement();
			if (element instanceof TypeDefinition) {
				return new TypeEntityDefinition((TypeDefinition) element);
			}
		}
		
		return null;
	}

}