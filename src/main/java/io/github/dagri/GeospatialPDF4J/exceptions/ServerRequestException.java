package io.github.dagri.GeospatialPDF4J.exceptions;

/**
 * Exception to be thrown on server request errors.
 * 
 * @author DaGri
 * @since 12.01.2017
 *
 */
public class ServerRequestException extends Exception {

	/**
	 * The serial version UID.
	 */
	private static final long serialVersionUID = 1009536264555877812L;

	/**
	 * Constructor for a {@link ServerRequestException}.
	 */
	public ServerRequestException() {
		super("Server could not be reached!");
	}
}
