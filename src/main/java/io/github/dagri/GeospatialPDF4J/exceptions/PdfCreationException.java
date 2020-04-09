package io.github.dagri.GeospatialPDF4J.exceptions;

/**
 * Exception to be thrown if an error occurs during the creation of a PDF
 * document.
 * 
 * @author DaGri
 * @since 14.01.2017
 *
 */
public class PdfCreationException extends Exception {

	/**
	 * The serial version UID.
	 */
	private static final long serialVersionUID = -8163780756298637011L;

	/**
	 * Constructor for a {@link PdfCreationException}.
	 * 
	 */
	public PdfCreationException() {
		super("An Error occured during the creation of the geospatial-PDF.");
	}
}
