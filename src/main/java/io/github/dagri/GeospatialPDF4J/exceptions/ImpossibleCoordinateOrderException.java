package io.github.dagri.GeospatialPDF4J.exceptions;

import io.github.dagri.GeospatialPDF4J.geo.BoundingBox;

/**
 * Exception to be thrown if given corners are in an impossible order for a
 * {@link BoundingBox}.
 * 
 * @author DaGri
 * @since 11.01.2017
 *
 */
public class ImpossibleCoordinateOrderException extends Exception {

	/**
	 * The serial version UID.
	 */
	private static final long serialVersionUID = 4106286855548934860L;

	/**
	 * Constructor for a {@link ImpossibleCoordinateOrderException}.
	 */
	public ImpossibleCoordinateOrderException() {
		super("Error on the coordinate order: Creation of a BoundingBox is not possible!");
	}

}
