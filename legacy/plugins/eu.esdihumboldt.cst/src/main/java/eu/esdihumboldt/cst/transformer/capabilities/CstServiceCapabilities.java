/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                  01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */

package eu.esdihumboldt.cst.transformer.capabilities;

import java.util.List;

import eu.esdihumboldt.cst.transformer.CstService;

/**
 * This type provides a set of {@link FunctionDescription}s.
 * 
 * @author Thorsten Reitz 
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */ 
public interface CstServiceCapabilities{
	
	/**
	 * @return a {@link List} of {@link FunctionDescription}s for all schema 
	 * translation operations that this {@link CstService} supports.
	 */
	public List<FunctionDescription> getFunctionDescriptions(); 

}