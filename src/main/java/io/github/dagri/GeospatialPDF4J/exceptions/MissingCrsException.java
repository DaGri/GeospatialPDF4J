package io.github.dagri.GeospatialPDF4J.exceptions;

/**
 * Exception to be thrown if a DirectPosition has no attached CRS.
 * 
 * @author DaGri
 * @since 10.08.2017
 *
 */
public class MissingCrsException extends Exception {

	/**
	 * The serial version UID.
	 */
	private static final long serialVersionUID = -8353281656630614310L;

	/**
	 * Constructor for a {@link CoordinateCreationException}.
	 */
	public MissingCrsException() {
		super("Used DirectPosition has no attached CoordinateRefrenceSystem!");
	}
}
