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
package eu.esdihumboldt.cst.transformer.service.rename;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.FeatureType;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

/**
 * The FeatureSplitter is used by the {@link RenameFeatureFunction} to handle
 * rename cells with a defined split condition. The split condition is expressed
 * using the following grammar:
 * 
 * <pre>
 * &lt;SplitRule&gt;                  ::= &lt;Operation&gt;:&lt;Operator&gt;
 * &lt;Operation&gt;                  ::= split
 * &lt;Operator&gt;                   ::= extractSubgeometry(&lt;extractGeometryParameters&gt;) | extractSubstring(&lt;extractSubstringParameters&gt;)
 * &lt;extractGeometryParameters&gt;  ::= Point | LineString | Polygon
 * &lt;extractSubstringParameters&gt; ::= &lt;RegularExpression&gt;
 * </pre>
 * 
 * @author Thorsten Reitz, Ulrich Schaeffler
 */
public class FeatureSplitter {
	
	private String onAttributeName = null;
	private String targetAttribute = null;
	
	private Class<? extends Geometry> geometryAtomType = null;
	
	private Pattern pattern = null;


	/**
	 * Default constructor.
	 * @param onAttributeName the local name of the Attribute whose value is 
	 * to be used in the execution of the split rule. If null, the 
	 * DefaultGeometry will be used for a extractSubgeometry case.
	 * @param splitRule the rule how to split the source {@link Feature}.
	 */
	public FeatureSplitter(String onAttributeName, String splitRule, String targetAttribute) {
		this.onAttributeName = onAttributeName;
		this.targetAttribute = targetAttribute;
		
		String[] splitrule = splitRule.split(":"); //$NON-NLS-1$
		if (splitrule[0].equals("split")) { //$NON-NLS-1$
			if (splitrule[1].startsWith("extractSubgeometry")) { //$NON-NLS-1$
				String extractGeometryParameter = splitrule[1].substring(
						splitrule[1].indexOf("(") + 1,  //$NON-NLS-1$
						splitrule[1].indexOf(")")); //$NON-NLS-1$
				if (extractGeometryParameter.equals("Point")) { //$NON-NLS-1$
					this.geometryAtomType = Point.class;
				}
				else if (extractGeometryParameter.equals("LineString")) { //$NON-NLS-1$
					this.geometryAtomType = LineString.class;
				}
				else if (extractGeometryParameter.equals("Polygon")) { //$NON-NLS-1$
					this.geometryAtomType = Polygon.class;
				}
				else {
					throw new RuntimeException("You can only extract Points, " + //$NON-NLS-1$
							"Polygons and LineStrings."); //$NON-NLS-1$
				}
			}
			else if (splitrule[1].startsWith("extractSubstring")) { //$NON-NLS-1$
				String extractSubstringParameters = splitrule[1].substring(
						splitrule[1].indexOf("("),  //$NON-NLS-1$
						splitrule[1].indexOf(")")); //$NON-NLS-1$
				this.pattern = Pattern.compile(extractSubstringParameters);
			}
		}
		else {
			throw new RuntimeException("You can only create a " + //$NON-NLS-1$
				"FeatureSplitter from a split rule."); //$NON-NLS-1$
		}
	}
	
	/**
	 * Perform the spit operation on the given source {@link Feature}.
	 * @param source the {@link Feature} to split.
	 * @param targetFT the {@link FeatureType} for which to create new 
	 * {@link Feature}s
	 * @return a List of {@link Feature}s that have a new geometry and a ID set,
	 * but nothing else.
	 */
	public List<Feature> split(Feature source, FeatureType targetFT) {
		List<Feature> result = new ArrayList<Feature>();
		if (this.geometryAtomType != null) {
			result.addAll(this.handleGeometrySplit(source, targetFT));
		}
		else {
			result.addAll(this.handleStringSplit(source, targetFT));
		}
		return result;
	}
	
	private Collection<? extends Feature> handleGeometrySplit(Feature source,
			FeatureType targetFT) {
		List<Feature> result = null;
		
		Object attribute_value = null;
		if (this.onAttributeName == null) {
			attribute_value = ((SimpleFeature)source).getDefaultGeometry();
		}
		else {
			attribute_value = ((SimpleFeature)source).getAttribute(
					this.onAttributeName);
		}
		
		if (Geometry.class.isAssignableFrom(attribute_value.getClass())) {
			String preparedErrorMsg = "It is not possible to extract " //$NON-NLS-1$
					+ this.geometryAtomType + " from the " //$NON-NLS-1$
					+ "given geometry type: " //$NON-NLS-1$
					+ attribute_value.getClass().getName();
			
			if (attribute_value.getClass().equals(MultiLineString.class)) {
				if (this.geometryAtomType.equals(Point.class)) {
					result = this.makeNewPointFeatures(source, targetFT, 
					((MultiLineString)attribute_value).getCoordinates());
				}
				else if (this.geometryAtomType.equals(LineString.class)) {
					MultiLineString linestring = (MultiLineString) attribute_value;
					LineString[] lineStrings = new LineString[linestring.getNumGeometries()];
					for (int n = 0; n < linestring.getNumGeometries(); n++) {
						lineStrings[n] = (LineString) linestring.getGeometryN(n);
					}
					result = this.makeNewLineStringFeatures(source, targetFT, 
							lineStrings);
				}
				else {
					throw new RuntimeException(preparedErrorMsg);
				}
			}
			else if (attribute_value.getClass().equals(MultiPolygon.class)) {
				MultiPolygon multipolygon = (MultiPolygon) attribute_value;
				if (this.geometryAtomType.equals(Point.class)) {
					result = new ArrayList<Feature>();
					for (int n = 0; n < multipolygon.getNumGeometries(); n++) {
						Coordinate[] coords = 
							((Polygon)multipolygon.getGeometryN(n)).getCoordinates();
						coords = Arrays.copyOfRange(coords, 1, coords.length);
						result.addAll(this.makeNewPointFeatures(source, targetFT, 
								coords));
					}
				}
				else if (this.geometryAtomType.equals(Polygon.class)) {
					Polygon[] interiorPolys = new Polygon[
					                          multipolygon.getNumGeometries()];
					for (int n = 0; n < multipolygon.getNumGeometries(); n++) {
						interiorPolys[n] = (Polygon) multipolygon.getGeometryN(n);
					}
					result = this.makeNewPolygonFeatures(source, targetFT, 
							interiorPolys);
				}
				else {
					throw new RuntimeException(preparedErrorMsg);
				}
			}
			else if (attribute_value.getClass().equals(MultiPoint.class)) {
				if (this.geometryAtomType.equals(Point.class)) {
					result = this.makeNewPointFeatures(source, targetFT, 
					((MultiPoint)attribute_value).getCoordinates());
				}
				else {
					throw new RuntimeException(preparedErrorMsg);
				}
			}
			else if (attribute_value.getClass().equals(Polygon.class)) {
				if (this.geometryAtomType.equals(Point.class)) {
					Coordinate[] coords = ((Polygon)attribute_value).getCoordinates();
					coords = Arrays.copyOfRange(coords, 1, coords.length);
					result = this.makeNewPointFeatures(source, targetFT, 
							coords);
				}
				else {
					throw new RuntimeException(preparedErrorMsg);
				}
			}
			else if (attribute_value.getClass().equals(LineString.class)) {
				if (this.geometryAtomType.equals(Point.class)) {
					result = this.makeNewPointFeatures(source, targetFT, 
					((LineString)attribute_value).getCoordinates());
				}
				else {
					throw new RuntimeException(preparedErrorMsg);
				}
			}
			else if (attribute_value.getClass().equals(Point.class)) {
				if (this.geometryAtomType.equals(Point.class)) {
					result = this.makeNewPointFeatures(source, targetFT, 
					((LineString)attribute_value).getCoordinates());
				}
				else {
					throw new RuntimeException(preparedErrorMsg);
				}
			}
			else {
				throw new RuntimeException("The geometry attribute to " + //$NON-NLS-1$
						"split is of an unknow type: " +  //$NON-NLS-1$
						attribute_value.getClass().getName());
			}
		}
		else {
			throw new RuntimeException("An extractSubgeometry rule was " + //$NON-NLS-1$
					"defined on a non-Geometry property."); //$NON-NLS-1$
		}
		return result;
	}

	private List<Feature> makeNewPointFeatures(Feature source, FeatureType targetType,
			Coordinate[] coordinates) {
		List<Feature> result = new ArrayList<Feature>();
		GeometryFactory geomFactory = new GeometryFactory();
		int i = 0;
		for (Coordinate c : coordinates) {
			SimpleFeature target = SimpleFeatureBuilder.build(
					(SimpleFeatureType) targetType, new Object[]{}, 
					source.getIdentifier().getID() + "[" + i++ + "]"); //$NON-NLS-1$ //$NON-NLS-2$
			target.getProperty(
					this.targetAttribute).setValue(geomFactory.createPoint(c));
//			target.setDefaultGeometry(geomFactory.createPoint(c));
			result.add(target);
		}
		return result;
	}

	private List<Feature> makeNewLineStringFeatures(Feature source, FeatureType targetType,
			LineString[] lineStrings) {
		List<Feature> result = new ArrayList<Feature>();
		GeometryFactory geomFactory = new GeometryFactory();
		int i = 0;
		for (LineString ls : lineStrings) {
			SimpleFeature target = SimpleFeatureBuilder.build(
					(SimpleFeatureType) targetType, new Object[]{}, 
					source.getIdentifier().getID() + "[" + i++ + "]"); //$NON-NLS-1$ //$NON-NLS-2$
			target.getProperty(this.targetAttribute).setValue(geomFactory.createLineString(
					ls.getCoordinates()));
			result.add(target);
		}
		return result;
	}
	
	private List<Feature> makeNewPolygonFeatures(Feature source, FeatureType targetType,
			Polygon[] polygons) {
		List<Feature> result = new ArrayList<Feature>();
		GeometryFactory geomFactory = new GeometryFactory();
		int i = 0;
		for (Polygon polygon : polygons) {
			SimpleFeature target = SimpleFeatureBuilder.build(
					(SimpleFeatureType) targetType, new Object[]{}, 
					source.getIdentifier().getID() + "[" + i++ + "]"); //$NON-NLS-1$ //$NON-NLS-2$
			LinearRing[] interiorPolys = new LinearRing[polygon.getNumInteriorRing()];
			for (int n = 0; n < polygon.getNumInteriorRing(); n++) {
				interiorPolys[n] = geomFactory.createLinearRing(
						polygon.getInteriorRingN(n).getCoordinates());
			}
			target.getProperty(
					this.targetAttribute).setValue(geomFactory.createPolygon(
					geomFactory.createLinearRing(
							polygon.getExteriorRing().getCoordinates()), 
							interiorPolys));
			result.add(target);
		}
		return result;
	}
	
	
	private Collection<? extends Feature> handleStringSplit(Feature source,
			FeatureType targetFT) {
		// TODO Auto-generated method stub
		return null;
	}

}