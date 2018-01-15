package io.github.dagri.GeospatialPDF4J.junit;

import static org.junit.Assert.*;

import org.geotools.geometry.DirectPosition2D;
import org.junit.BeforeClass;
import org.junit.Test;

import io.github.dagri.GeospatialPDF4J.geo.BoundingBox;
import io.github.dagri.GeospatialPDF4J.res.CoordinateTransformer;
import io.github.dagri.GeospatialPDF4J.res.TileArray;

/**
 * Junit test case to test the creation of the {@link TileArray}.
 * 
 * @author DaGri
 * @since 22.01.2017
 *
 */
public class TileArrayJunitTest {

	static TileArray array;

	/**
	 * The BeforeTest method.
	 *
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		BoundingBox layerBBox = new BoundingBox(new DirectPosition2D(CoordinateTransformer.getInstance().getUtmCrs(), 0.0, 0.0), new DirectPosition2D(CoordinateTransformer.getInstance().getUtmCrs(), 550.0, 550.0));
		array = new TileArray(100, layerBBox, 550, 550, 6, 6);
		array.prepareArray();
	}

	@Test
	public final void widht() {
		assertEquals(6, array.getColumns(), 0);
	}

	@Test
	public final void height() {
		assertEquals(6, array.getRows(), 0);
	}
}
