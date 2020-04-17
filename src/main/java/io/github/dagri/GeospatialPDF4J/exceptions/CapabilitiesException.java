package io.github.dagri.GeospatialPDF4J.exceptions;

/**
 * {@link Exception} to be thrown if an error occurs on receiving the
 * capabilities from a server.
 * 
 * @author DaGri
 * @since 09.10.2017
 */
public class CapabilitiesException extends Exception {

	/**
	 * The serial version UID.
	 */
	private static final long serialVersionUID = -6895341865707690050L;

	/**
	 * Constructor for a {@link CapabilitiesException}.
	 */
	public CapabilitiesException() {
		super("Could not receive the capabilities from the server!");
	}

}
