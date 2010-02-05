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

package eu.esdihumboldt.hale.task.schema;

import java.util.Collection;

import eu.esdihumboldt.hale.models.SchemaService.SchemaType;
import eu.esdihumboldt.hale.schemaprovider.model.AttributeDefinition;
import eu.esdihumboldt.hale.schemaprovider.model.TypeDefinition;
import eu.esdihumboldt.hale.task.Task;
import eu.esdihumboldt.hale.task.TaskFactory;

/**
 * Nil attribute mapping tasks
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class NilAttributeTaskProvider extends AbstractSchemaTaskProvider {
	
	private TaskFactory mapNilAttribute;

	/**
	 * Default constructor
	 */
	public NilAttributeTaskProvider() {
		super(null, SchemaType.TARGET);
		
		setReactOnCellAddOrUpdate(true);
		
		addFactory(mapNilAttribute = new MapNilAttributeTaskFactory());
	}

	/**
	 * @see AbstractSchemaTaskProvider#generateAttributeTasks(AttributeDefinition, Collection)
	 */
	@Override
	protected void generateAttributeTasks(AttributeDefinition attribute,
			Collection<Task> taskList) {
		Task attrTask = mapNilAttribute.createTask(serviceProvider, attribute);
		if (attrTask != null) {
			taskList.add(attrTask);
		}
	}

	/**
	 * @see AbstractSchemaTaskProvider#generateTypeTasks(TypeDefinition, Collection)
	 */
	@Override
	protected void generateTypeTasks(TypeDefinition type,
			Collection<Task> taskList) {
		// ignore
	}

}
