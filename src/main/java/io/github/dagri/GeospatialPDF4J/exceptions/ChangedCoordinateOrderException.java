package io.github.dagri.GeospatialPDF4J.exceptions;

/**
 * Exception to be thrown if the coordinates used to create a boundingbox are
 * exchanged.
 * 
 * @author DaGri
 * @since 13.01.2017
 *
 */
public class ChangedCoordinateOrderException extends Exception {

	/**
	 * The serial verion UID.
	 */
	private static final long serialVersionUID = -5215761371563305250L;

	/**
	 * Constructor for a {@link ChangedCoordinateOrderException}.
	 */
	public ChangedCoordinateOrderException() {
		super("Error on creating the Boundingbx: Coordinates are exchanged!");
	}

}
