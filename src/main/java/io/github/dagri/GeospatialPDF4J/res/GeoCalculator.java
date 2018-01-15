package io.github.dagri.GeospatialPDF4J.res;

import org.geotools.geometry.DirectPosition2D;

import io.github.dagri.GeospatialPDF4J.exceptions.CoordinateTransformException;
import io.github.dagri.GeospatialPDF4J.exceptions.MissingCrsException;

/**
 * Class to provide different calculations on {@link Coordinate2D}s and
 * {@link Coordinate3D}.
 * 
 * @author DaGri
 * @since 10.01.2017
 *
 */
public class GeoCalculator {

	// ATTRIBUTES

	/**
	 * The instance of this {@link GeoCalculator} according to the singleton
	 * pattern.
	 */
	private static GeoCalculator instance;

	// CONSTRUCTORS

	/**
	 * Empty constructor for a {@link GeoCalculator}.
	 */
	private GeoCalculator() {
		// NOTHING
	}

	// METHODS

	/**
	 * Returns an instance of this {@link GeoCalculator}.
	 *
	 * @return the instance
	 */
	public static GeoCalculator getInstance() {
		if (instance == null)
			instance = new GeoCalculator();
		return instance;
	}

	/**
	 * Calculates the 2D distance in meters between two {@link Coordinate2D}s in
	 * CRS:EPSG:25832, and returns it as {@link Double}.
	 *
	 * @param pos1
	 *            the first {@link Coordinate2D}
	 * @param pos2
	 *            the second {@link Coordinate2D}
	 * @return the 2D distance in meters
	 * @throws MissingCrsException 
	 */
	public double pythagoras2D(DirectPosition2D c1, DirectPosition2D c2) throws CoordinateTransformException, MissingCrsException{
		CoordinateTransformer t = CoordinateTransformer.getInstance();
		if(c1.getCoordinateReferenceSystem() != t.getUtmCrs())
			c1 = t.transform(c1, t.getUtmCrs());
		if(c2.getCoordinateReferenceSystem() != t.getUtmCrs())
			c2 = t.transform(c2, t.getUtmCrs());
		double erg = Math.sqrt(
				Math.pow(c1.getOrdinate(0) - c2.getOrdinate(0), 2.0) + Math.pow(c1.getOrdinate(1) - c2.getOrdinate(1), 2.0));
		return erg;
	}

	// /**
	// * Calculates the 3D distance in meters between two {@link Coordinate2D}s
	// in
	// * CRS:EPSG:25832, and returns it as {@link Double}.
	// *
	// * @param pos1
	// * the first {@link Coordinate3D}
	// * @param pos2
	// * the second {@link Coordinate3D}
	// * @return the 3D distance in meters
	// */
	// public double pythagoras3D(DirectPosition2D c1, DirectPosition2D c2) {
	// return Math.sqrt(Math.pow(c1.getOrdinate(0) - c2.getOrdinate(0), 2.0)
	// + Math.pow(c1.getOrdinate(1) - c2.getOrdinate(1), 2.0) +
	// Math.pow((c1.getZvalue() - c2.getZvalue()), 2.0));
	// }

	// GETTERS AND SETTERS

	// OTHERS
}
