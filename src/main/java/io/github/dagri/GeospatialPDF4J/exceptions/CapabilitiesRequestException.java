package io.github.dagri.GeospatialPDF4J.exceptions;

/**
 * Exception to be thrown on errors while receiving capabilities.
 * 
 * @author DaGri
 * @since 06.02.2017
 *
 */
public class CapabilitiesRequestException extends Exception {

	// ATTRIBUTES

	/**
	 * The serial version UID.
	 */
	private static final long serialVersionUID = 2040682618113829824L;

	// CONSTRUCTORS

	/**
	 * Constructor for a {@link CapabilitiesRequestException}
	 */
	public CapabilitiesRequestException() {
		super("Error on receiving of the capabilities of the server!");
	}

	// METHODS

	// GETTERS AND SETTERS

	// OTHERS
}
