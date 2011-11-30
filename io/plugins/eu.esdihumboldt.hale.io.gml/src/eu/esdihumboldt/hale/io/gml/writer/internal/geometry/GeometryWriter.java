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

package eu.esdihumboldt.hale.io.gml.writer.internal.geometry;

import java.util.Set;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.opengis.feature.type.Name;

import com.vividsolutions.jts.geom.Geometry;

import eu.esdihumboldt.hale.schemaprovider.model.TypeDefinition;

/**
 * Geometry writer interface. A geometry holds information about compatibility
 * and encoding patterns for a certain geometry type.
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 * @param <T> the geometry type
 */
public interface GeometryWriter<T extends Geometry> {

	/**
	 * Get the geometry type represented by the writer
	 * 
	 * @return the geometry type
	 */
	public Class<T> getGeometryType();
	
	/**
	 * Get the compatible types' names for the geometry type that can be handled
	 * by this writer. The compatible types serve as entry points for the 
	 * matching.
	 * 
	 * @return the type names, a <code>null</code> namespace in a name
	 * references the GML namespace
	 */
	public Set<Name> getCompatibleTypes();
	
	/**
	 * Matches the type against the encoding patterns.
	 * 
	 * @param type the type definition
	 * @param basePath the definition path
	 * @param gmlNs the GML namespace
	 * 
	 * @return the new path if there is a match, <code>null</code> otherwise
	 */
	public DefinitionPath match(TypeDefinition type, DefinitionPath basePath,
			String gmlNs);
	
	/**
	 * Write a geometry.
	 * 
	 * At this point we can assume that the wrapping element matches one of 
	 * the base patterns. The corresponding element name and its type 
	 * definition are given.
	 * 
	 * @param writer the XML stream writer
	 * @param geometry the geometry to write
	 * @param elementType the last type definition in the matching path
	 * @param elementName the corresponding element name
	 * @param gmlNs the GML namespace
	 * @throws XMLStreamException if an error occurs writing the geometry
	 */
	public void write(XMLStreamWriter writer, T geometry, 
			TypeDefinition elementType, Name elementName, String gmlNs) throws XMLStreamException;
	
}