package io.github.dagri.GeospatialPDF4J.exceptions;

import io.github.dagri.GeospatialPDF4J.pdf.PdfHandler;

/**
 * Exception to be thrown if an error occurs in a {@link PdfHandler}.
 * 
 * @author DaGri
 * @since 14.01.2017
 *
 */
public class PdfHandlerException extends Exception {

	/**
	 * The serial version UID.
	 */
	private static final long serialVersionUID = -5838080821786548695L;

	/**
	 * Constructor for a {@link PdfHandlerException}.
	 */
	public PdfHandlerException() {
		super("Error in using a PdfHandler.");
	}

}
