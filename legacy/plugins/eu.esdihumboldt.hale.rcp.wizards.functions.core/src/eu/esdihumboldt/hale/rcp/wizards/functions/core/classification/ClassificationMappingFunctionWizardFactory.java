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
package eu.esdihumboldt.hale.rcp.wizards.functions.core.classification;

import eu.esdihumboldt.cst.corefunctions.ClassificationMappingFunction;
import eu.esdihumboldt.hale.ui.model.functions.AlignmentInfo;
import eu.esdihumboldt.hale.ui.model.functions.FunctionWizard;
import eu.esdihumboldt.hale.ui.model.functions.FunctionWizardFactory;
import eu.esdihumboldt.hale.ui.model.schema.SchemaItem;
import eu.esdihumboldt.specification.cst.align.ICell;

/**
 * Classification mapping wizard factory
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class ClassificationMappingFunctionWizardFactory implements
		FunctionWizardFactory {

	/**
	 * @see FunctionWizardFactory#createWizard(AlignmentInfo)
	 */
	@Override
	public FunctionWizard createWizard(AlignmentInfo selection) {
		return new ClassificationMappingFunctionWizard(selection);
	}

	/**
	 * @see FunctionWizardFactory#supports(AlignmentInfo)
	 */
	@Override
	public boolean supports(AlignmentInfo selection) {
		// must be exactly one source and target item
		if (selection.getSourceItemCount() != 1 || selection.getTargetItemCount() != 1) {
			return false;
		}
		
		// source item must be a property
		SchemaItem source = selection.getFirstSourceItem();
		if (!source.isAttribute()) {
			return false;
		}
		
		// target item must be a property
		SchemaItem target = selection.getFirstTargetItem();
		if (!target.isAttribute()) {
			return false;
		}
		
		// if a cell already exists it must be of the right transformation
		ICell cell = selection.getAlignment(
				source, target);
		if (cell != null) {
			// only allow editing matching transformation
			try {
				return cell.getEntity1().getTransformation().getService().getLocation().equals(
						ClassificationMappingFunction.class.getName());
			} catch (NullPointerException e) {
				return false;
			}
		}
		
		return true;
	}

}