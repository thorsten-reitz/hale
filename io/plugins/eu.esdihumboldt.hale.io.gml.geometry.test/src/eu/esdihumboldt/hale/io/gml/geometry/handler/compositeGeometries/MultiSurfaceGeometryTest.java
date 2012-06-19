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

package eu.esdihumboldt.hale.io.gml.geometry.handler.compositeGeometries;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Collection;

import javax.xml.namespace.QName;

import org.junit.Test;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;

import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.instance.model.ResourceIterator;
import eu.esdihumboldt.hale.common.schema.geometry.GeometryProperty;
import eu.esdihumboldt.hale.io.gml.geometry.handler.internal.AbstractHandlerTest;

/**
 * Test for reading multi-surface geometries
 * 
 * @author Patrick Lieb
 */
public class MultiSurfaceGeometryTest extends AbstractHandlerTest {

	private MultiPolygon reference;

	/**
	 * @see eu.esdihumboldt.hale.io.gml.geometry.handler.internal.AbstractHandlerTest#init()
	 */
	@Override
	public void init() {
		super.init();

		LinearRing shell = geomFactory.createLinearRing(new Coordinate[] {
				new Coordinate(-122.44, 37.80), new Coordinate(-122.45, 37.80),
				new Coordinate(-122.45, 37.78), new Coordinate(-122.44, 37.78),
				new Coordinate(-122.44, 37.80) });

		LinearRing[] holes = new LinearRing[1];
		LinearRing hole1 = geomFactory.createLinearRing(new Coordinate[] {
				new Coordinate(-122.24, 37.60), new Coordinate(-122.25, 37.60),
				new Coordinate(-122.25, 37.58), new Coordinate(-122.24, 37.58),
				new Coordinate(-122.24, 37.60) });
		holes[0] = hole1;

		Polygon polygon2 = geomFactory.createPolygon(shell, holes);

		shell = geomFactory.createLinearRing(new Coordinate[] {
				new Coordinate(0.01, 3.2), new Coordinate(3.33, 3.33),
				new Coordinate(0.01, -3.2), new Coordinate(-3.33, -3.2),
				new Coordinate(0.01, 3.2) });

		holes = new LinearRing[2];
		hole1 = geomFactory.createLinearRing(new Coordinate[] {
				new Coordinate(0, 1), new Coordinate(1, 1),
				new Coordinate(0, -1), new Coordinate(-1, -1),
				new Coordinate(0, 1) });
		LinearRing hole2 = geomFactory.createLinearRing(new Coordinate[] {
				new Coordinate(0, 2), new Coordinate(2, 2),
				new Coordinate(0, -2), new Coordinate(-2, -2),
				new Coordinate(0, 2) });
		holes[0] = hole1;
		holes[1] = hole2;

		Polygon polygon1 = geomFactory.createPolygon(shell, holes);

		Polygon[] polygons = new Polygon[] { polygon1, polygon2 };

		reference = geomFactory.createMultiPolygon(polygons);
	}
	
	/**
	 * Test multi-surface geometries read from a GML 3 file
	 * 
	 * @throws Exception
	 *             if an error occurs
	 */
	@Test
	public void testMultiSurfaceGml3() throws Exception {
		InstanceCollection instances = AbstractHandlerTest
				.loadXMLInstances(getClass().getResource("/data/gml/geom-gml3.xsd")
						.toURI(),
						getClass().getResource("/data/surface/sample-multisurface-gml3.xml")
								.toURI());

		// one instance expected
		ResourceIterator<Instance> it = instances.iterator();
		try {
			// SurfaceMember with LinearRings defined through coordinates
			assertTrue("First sample feature missing", it.hasNext());
			Instance instance = it.next();
			checkSurfacePropertyInstance(instance);
		} finally {
			it.close();
		}
	}
	
	/**
	 * Test multi-surface geometries read from a GML 3.1 file
	 * 
	 * @throws Exception
	 *             if an error occurs
	 */
	@Test
	public void testMultiSurfaceGml31() throws Exception {
		InstanceCollection instances = AbstractHandlerTest
				.loadXMLInstances(getClass().getResource("/data/gml/geom-gml31.xsd")
						.toURI(),
						getClass().getResource("/data/surface/sample-multisurface-gml31.xml")
								.toURI());

		// one instance expected
		ResourceIterator<Instance> it = instances.iterator();
		try {
			// SurfaceMember with LinearRings defined through coordinates
			assertTrue("First sample feature missing", it.hasNext());
			Instance instance = it.next();
			checkSurfacePropertyInstance(instance);
		} finally {
			it.close();
		}
	}
	
	/**
	 * Test multi-surface geometries read from a GML 3.2 file
	 * 
	 * @throws Exception
	 *             if an error occurs
	 */
	@Test
	public void testMultiSurfaceGml32() throws Exception {
		InstanceCollection instances = AbstractHandlerTest
				.loadXMLInstances(getClass().getResource("/data/gml/geom-gml32.xsd")
						.toURI(),
						getClass().getResource("/data/surface/sample-multisurface-gml32.xml")
								.toURI());

		// one instance expected
		ResourceIterator<Instance> it = instances.iterator();
		try {
			// SurfaceMember with LinearRings defined through coordinates
			assertTrue("First sample feature missing", it.hasNext());
			Instance instance = it.next();
			checkSurfacePropertyInstance(instance);
		} finally {
			it.close();
		}
	}

	private void checkSurfacePropertyInstance(Instance instance) {
		Object[] geomVals = instance
				.getProperty(new QName(NS_TEST, "geometry"));
		assertNotNull(geomVals);
		assertEquals(1, geomVals.length);

		Object geom = geomVals[0];
		assertTrue(geom instanceof Instance);

		Instance geomInstance = (Instance) geom;
		checkGeomInstance(geomInstance);
	}

	private void checkGeomInstance(Instance geomInstance) {
		assertTrue(geomInstance.getValue() instanceof Collection<?>);
		for (Object instance : ((Collection<?>) geomInstance.getValue())) {
			assertTrue(instance instanceof GeometryProperty<?>);
			@SuppressWarnings("unchecked")
			MultiPolygon multipolygon = ((GeometryProperty<MultiPolygon>) instance)
					.getGeometry();
			assertTrue("Read geometry does not match the reference geometry",
					multipolygon.equalsExact(reference));
		}
	}
}