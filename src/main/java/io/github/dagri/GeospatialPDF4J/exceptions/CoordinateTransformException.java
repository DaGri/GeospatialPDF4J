package io.github.dagri.GeospatialPDF4J.exceptions;

/**
 * Exception to be thrown at conversion errors.
 * 
 * @author DaGri
 * @since 10.01.2017
 */
public class CoordinateTransformException extends Exception {

	/**
	 * The serial version UID.
	 */
	private static final long serialVersionUID = 4506549576062200248L;

	/**
	 * Constructor for a {@link CoordinateCreationException}.
	 */
	public CoordinateTransformException() {
		super("Error on transforming a Coordinate to another CRS: The transformation could not be done!");
	}

}
