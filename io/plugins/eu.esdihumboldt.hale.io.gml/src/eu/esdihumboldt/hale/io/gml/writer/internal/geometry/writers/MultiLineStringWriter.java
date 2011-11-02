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

package eu.esdihumboldt.hale.io.gml.writer.internal.geometry.writers;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.geotools.feature.NameImpl;
import org.opengis.feature.type.Name;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;

import eu.esdihumboldt.hale.io.gml.writer.internal.geometry.Descent;
import eu.esdihumboldt.hale.io.gml.writer.internal.geometry.GeometryWriter;
import eu.esdihumboldt.hale.schemaprovider.model.TypeDefinition;

/**
 * Writes {@link MultiLineString} in gml:MultiLineStringTypes
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class MultiLineStringWriter extends
		AbstractGeometryWriter<MultiLineString> {

	/**
	 * Default constructor
	 */
	public MultiLineStringWriter() {
		super(MultiLineString.class);
		
		// compatible types to serve as entry point
		addCompatibleType(new NameImpl("http://www.opengis.net/gml", "MultiLineStringType")); // restrict to "old" gml namespace (is depreceated since 3.0) -> use Curve instead in GML 3.2 //$NON-NLS-1$ //$NON-NLS-2$
		
		// patterns for matching inside compatible types
		addBasePattern("*/lineStringMember"); //$NON-NLS-1$
		
		// verification patterns
		addVerificationPattern("*/LineString"); //$NON-NLS-1$
	}

	/**
	 * @see GeometryWriter#write(XMLStreamWriter, Geometry, TypeDefinition, Name, String)
	 */
	@Override
	public void write(XMLStreamWriter writer, MultiLineString geometry,
			TypeDefinition elementType, Name elementName, String gmlNs)
			throws XMLStreamException {
		for (int i = 0; i < geometry.getNumGeometries(); i++) {
			if (i > 0) {
				writer.writeStartElement(elementName.getNamespaceURI(), elementName.getLocalPart());
			}
			
			Descent descent = descend(writer, Pattern.parse("*/LineString"),  //$NON-NLS-1$
					elementType, elementName, gmlNs, false);
			
			LineString line = (LineString) geometry.getGeometryN(i);
			writeCoordinates(writer, line.getCoordinates(), 
					descent.getPath().getLastType(), gmlNs);
			
			descent.close();
			
			if (i < geometry.getNumGeometries() - 1) {
				writer.writeEndElement();
			}
		}
	}

}