package io.github.dagri.GeospatialPDF4J.exceptions;

/**
 * Exception to be thrown if a {@link BoundingBox} could not be created.
 * 
 * @author DaGri
 * @since 10.01.2017
 *
 */
public class BoundingboxNotCreatableException extends Exception {

	/**
	 * The serial version UID.
	 */
	private static final long serialVersionUID = -4178943844469025759L;

	/**
	 * Constructor for a {@link BoundingboxNotCreatableException}.
	 */
	public BoundingboxNotCreatableException() {
		super("Error on creating the BoundingBox: BoundingBox could not be instanciated!");
	}

}
